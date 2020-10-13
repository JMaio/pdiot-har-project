package com.specknet.pdiotapp.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.common.collect.EvictingQueue
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.DelayRespeck
import com.specknet.pdiotapp.utils.RespeckData
import com.specknet.pdiotapp.utils.zeroRespeckData
import kotlinx.android.synthetic.main.activity_live_data.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.DelayQueue
import kotlin.math.roundToInt
import kotlin.math.sqrt


class LiveDataActivity : AppCompatActivity() {

    // display queue to update the graph smoothly
    private var mDelayRespeckQueue: BlockingQueue<DelayRespeck> = DelayQueue<DelayRespeck>()

    // global graph variables
    lateinit var dataSet_x: LineDataSet
    lateinit var dataSet_y: LineDataSet
    lateinit var dataSet_z: LineDataSet
    lateinit var dataSet_mag: LineDataSet

    var time = 0f
    lateinit var allAccelData: LineData
    lateinit var chart: LineChart

    // global broadcast receiver so we can unregister it
    lateinit var respeckLiveUpdateReceiver: BroadcastReceiver
    lateinit var looper: Looper

    val filterTest = IntentFilter(Constants.ACTION_INNER_RESPECK_BROADCAST)

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<*>
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager

    // EvictingQueue from Guava, alternative could be Apache CircularFifoQueue
    private lateinit var respeckDataQueue: EvictingQueue<RespeckData>

    private var dummyClassificationResults = ActivityClassifier.ClassificationResults(
        (0 until ActivityClassifier.OUTPUT_CLASSES_COUNT).mapIndexed { _, i ->
            ClassificationResult(
                Constants.ACTIVITY_CODE_TO_NAME_MAPPING
                    .getOrDefault(Constants.TFCODE_TO_ACTIVITY_CODE[i], "Unknown"),
                0f
            )
        }
    )

    // https://www.tensorflow.org/lite/guide/inference#load_and_run_a_model_in_java
    // load tf lite model
    private var activityClassifier = ActivityClassifier(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_data)

        // get the accel fields
        val accelX = findViewById<TextView>(R.id.accel_x)
        val accelY = findViewById<TextView>(R.id.accel_y)
        val accelZ = findViewById<TextView>(R.id.accel_z)
        val magTextView = findViewById<TextView>(R.id.magTextView)

        activityClassifier
            .initialize()
            .addOnSuccessListener {
                val w = activityClassifier.windowSize
                respeckDataQueue = EvictingQueue.create(w)
                // fill with zero packets
                respeckDataQueue.addAll((1..w).map { zeroRespeckData })
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error setting up activity classifier.", e)
            }


        setupChart()

        setupRecyclerView()

        // set up the broadcast receiver
        respeckLiveUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
//                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)
                val action = intent.action
                if (action == Constants.ACTION_INNER_RESPECK_BROADCAST) {
                    // get all relevant intent contents
                    val x = intent.getFloatExtra(Constants.EXTRA_RESPECK_LIVE_X, 0f)
                    val y = intent.getFloatExtra(Constants.EXTRA_RESPECK_LIVE_Y, 0f)
                    val z = intent.getFloatExtra(Constants.EXTRA_RESPECK_LIVE_Z, 0f)

                    val mag = sqrt((x * x + y * y + z * z).toDouble())
                    val data =
                        RespeckData(
                            timestamp = 0L,
                            accel_x = x,
                            accel_y = y,
                            accel_z = z,
                            accel_mag = mag.toFloat(),
                            breathingSignal = 0f
                        )
                    val delayRespeck =
                        DelayRespeck(
                            data,
                            79
                        )
                    mDelayRespeckQueue.add(delayRespeck)
                    respeckDataQueue.add(data)
                    Log.d(TAG, "respeckDataQueue head = ${respeckDataQueue.peek()}")

                    classifyActivity(respeckDataQueue.toList())

                    runOnUiThread {
                        accelX.text = "${getString(R.string.accel_x)} = ${x}"
                        accelY.text = "${getString(R.string.accel_y)} = ${y}"
                        accelZ.text = "${getString(R.string.accel_z)} = ${z}"
                        magTextView.text = "${getString(R.string.mag)} = ${mag}"
                    }
                    time += 1
                    updateGraph()
                }
            }
        }

        // register receiver on another thread
        val handlerThread = HandlerThread("bgThread")
        handlerThread.start()
        looper = handlerThread.looper
        val handler = Handler(looper)
        this.registerReceiver(respeckLiveUpdateReceiver, filterTest, null, handler)
    }

    fun setupChart() {
        chart = findViewById<LineChart>(R.id.chart)

        time = 0f
        var entries_x = ArrayList<Entry>()
        var entries_y = ArrayList<Entry>()
        var entries_z = ArrayList<Entry>()
        var entries_mag = ArrayList<Entry>()

        dataSet_x = LineDataSet(entries_x, "Accel X")
        dataSet_y = LineDataSet(entries_y, "Accel Y")
        dataSet_z = LineDataSet(entries_z, "Accel Z")
        dataSet_mag = LineDataSet(entries_mag, "Magnitude")

        dataSet_x.setDrawCircles(false)
        dataSet_y.setDrawCircles(false)
        dataSet_z.setDrawCircles(false)
        dataSet_mag.setDrawCircles(false)

        dataSet_x.setColor(
            ContextCompat.getColor(
                this,
                R.color.red
            )
        )
        dataSet_y.setColor(
            ContextCompat.getColor(
                this,
                R.color.green
            )
        )
        dataSet_z.setColor(
            ContextCompat.getColor(
                this,
                R.color.blue
            )
        )
        dataSet_mag.setColor(
            ContextCompat.getColor(
                this,
                R.color.yellow
            )
        )

        var dataSets = ArrayList<ILineDataSet>()
        dataSets.add(dataSet_x)
        dataSets.add(dataSet_y)
        dataSets.add(dataSet_z)
        dataSets.add(dataSet_mag)

        allAccelData = LineData(dataSets)
        chart.data = allAccelData
        chart.invalidate()
    }

    fun updateGraph() {
        // take the first element from the queue
        // and update the graph with it
        val respeckData = mDelayRespeckQueue.take().data

        val dataPointCount = findViewById<TextView>(R.id.dataPointsCount)

        dataSet_x.addEntry(Entry(time, respeckData.accel_x))
        dataSet_y.addEntry(Entry(time, respeckData.accel_y))
        dataSet_z.addEntry(Entry(time, respeckData.accel_z))
        dataSet_mag.addEntry(Entry(time, respeckData.accel_mag))

        runOnUiThread {
            allAccelData.notifyDataChanged()
            chart.notifyDataSetChanged()
            chart.invalidate()
            chart.setVisibleXRangeMaximum(150f)
//            Log.i("Chart", "Lowest X = " + chart.lowestVisibleX.toString())
            chart.moveViewToX(chart.lowestVisibleX + 40)
//            Log.i("Chart", "Lowest X after = " + chart.lowestVisibleX.toString())
            dataPointCount.text = getString(R.string.n_data_points, dataSet_mag.entryCount)
        }

    }

    fun setupRecyclerView() {
        // https://stackoverflow.com/a/17516998/9184658
        // create a layout manager that does not scroll
        recyclerViewManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean = false
        }
        recyclerViewAdapter = ActivityRecyclerAdapter(dummyClassificationResults)

        recyclerView = findViewById<RecyclerView>(R.id.activityTypesRecyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = recyclerViewManager
            // specify an viewAdapter (see also next example)
            adapter = recyclerViewAdapter
        }
    }

    class ActivityRecyclerAdapter(private val activities: ActivityClassifier.ClassificationResults) :
        RecyclerView.Adapter<ActivityRecyclerAdapter.ActivityRecyclerViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class ActivityRecyclerViewHolder(
            private val v: View,
            val activityName: TextView = v.findViewById(R.id.activityName),
            val confidenceIndicator: ProgressBar = v.findViewById(R.id.confidenceIndicator)
        ) : RecyclerView.ViewHolder(v) {
            init {
//                Log.d(TAG, "Starting recyclerViewHolder")
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ActivityRecyclerViewHolder {
            // create a new view
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_classification_confidence_row, parent, false) as View
            // set the view's size, margins, padding and layout parameters

            return ActivityRecyclerViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ActivityRecyclerViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.apply {
                activities.list[position].let { (name, c) ->
                    activityName.text = name
                    confidenceIndicator.apply {
                        progress = (c * 100).roundToInt()
                        progressTintList = ColorStateList.valueOf(
                            if (position == activities.maxI) {
                                resources.getColor(R.color.accent_500, null)
                            } else {
                                resources.getColor(R.color.primary_500, null)
                            }
                        )
                    }
                    Log.d(TAG, "activity - ${name} - ${c}")
                }
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount(): Int {
            return activities.list.size
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(respeckLiveUpdateReceiver)
        activityClassifier.close()
        looper.quit()
    }

    private fun classifyActivity(data: List<RespeckData>) {
        if (activityClassifier.isInitialized) {
            activityClassifier
                .classifyAsync(data)
                .addOnSuccessListener { res ->
                    Log.d(TAG, "updated classification results")
                    recyclerView.adapter = ActivityRecyclerAdapter(res)
                    res.max.let { (name, c) ->
                        modelPredictionActivityText.text = name
                        modelPredictionConfidence.text = String.format("%.2f%%", 100*c)
                    }
                }
                .addOnFailureListener { e ->
                    modelPredictionActivityText.text = getString(
                        R.string.tfe_dc_classification_error_message,
                        e.localizedMessage
                    )
                    Log.e(TAG, "Error classifying activity.", e)
                }
        }
    }

    companion object {
        private const val TAG = "LiveDataActivity"
    }
}
