package com.specknet.pdiotapp.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import com.google.common.collect.EvictingQueue
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.*
import kotlinx.android.synthetic.main.activity_live_data.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.openapitools.client.apis.DefaultApi
import org.openapitools.client.infrastructure.ApiClient
import retrofit2.HttpException
import retrofit2.await
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.BlockingQueue
import java.util.concurrent.DelayQueue
import kotlin.math.roundToInt
import kotlin.math.sqrt


class LiveDataFragment : Fragment() {
    companion object {
        const val API_BASE_PATH = "http://192.168.1.105:5000/api/v1"
        private const val TAG = "LiveDataActivity"
        const val floatFormat = "%.1f%%"
        var model: InferenceModel = ModelRepository.defaultModel

        // classify every n readings
        const val classificationInterval = 15 // roughly every 1.2 seconds

    }

    override fun setRetainInstance(retain: Boolean) {
        super.setRetainInstance(true)
    }

    // assets
    lateinit var assetManager: AssetManager
    lateinit var ctx: Context

    // display queue to update the graph smoothly
    private var mDelayRespeckQueue: BlockingQueue<DelayRespeck> = DelayQueue<DelayRespeck>()

    // global graph variables
    lateinit var dataSet_x: LineDataSet
    lateinit var dataSet_y: LineDataSet
    lateinit var dataSet_z: LineDataSet
    lateinit var dataSet_mag: LineDataSet

    var time = 0
    lateinit var allAccelData: LineData
    lateinit var chart: LineChart

    // global broadcast receiver so we can unregister it
    lateinit var respeckLiveUpdateReceiver: BroadcastReceiver
    lateinit var looper: Looper

    val filterTest = IntentFilter(Constants.ACTION_INNER_RESPECK_BROADCAST)

    //    private lateinit var modelSelector: Spinner
    private lateinit var modelPredictionActivityText: TextView
    private lateinit var modelPredictionConfidence: TextView
    private lateinit var networkModelPredictionActivityText: TextView
    private lateinit var networkModelPredictionConfidence: TextView
    private lateinit var modelChoiceAdapter: ArrayAdapter<String>
    private lateinit var models: List<String>

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<*>
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager

    // for movement categories
//    private lateinit var recyclerViewCategory: RecyclerView
//    private lateinit var recyclerViewCategoryAdapter: RecyclerView.Adapter<*>
//    private lateinit var recyclerViewCategoryManager: RecyclerView.LayoutManager

    private lateinit var flaskApi: DefaultApi
    private lateinit var respeckUUID: String

    // EvictingQueue from Guava, alternative could be Apache CircularFifoQueue
    // initialize zero-size queue to prevent errors
    private var respeckDataQueue: EvictingQueue<RespeckData> = EvictingQueue.create(0)

    private var dummyClassificationResults = ClassificationResults(
        Constants.ACTIVITY_CATEGORIES.map { c ->
            ClassificationResult(c, 0f)
        }
    )

    // https://www.tensorflow.org/lite/guide/inference#load_and_run_a_model_in_java
    // load tf lite model
    private lateinit var activityClassifier: ActivityClassifier

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assetManager = ctx.assets
        // grab files with tflite extensions
        models =
            assetManager.list(ActivityClassifier.MODEL_DIR)?.filter { f -> f.endsWith(".tflite") }
                .orEmpty()
        Log.i(TAG, "models found: $models")

        modelChoiceAdapter = ArrayAdapter<String>(
            ctx,
            android.R.layout.simple_spinner_dropdown_item,
            models
        )

        activityClassifier = ActivityClassifier(ctx)
    }

    // https://www.raywenderlich.com/1364094-android-fragments-tutorial-an-introduction-with-kotlin
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val view: View = inflater.inflate(R.layout.activity_live_data, container, false)

        activityClassifier
            .initialize(model)
            .addOnSuccessListener {
                val w = model.ws
                respeckDataQueue = EvictingQueue.create(w)
                // fill with zero packets
                respeckDataQueue.addAll((1..w).map { zeroRespeckData })
                Log.i(TAG, "Set up activity classifier '${model.filename}'")
                // https://stackoverflow.com/a/58665768/9184658
                Snackbar.make(
                    modelPredictionActivityText,
                    "Selected model '${model.filename}' (ws=${w})",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setAnchorView(R.id.bottom_nav_fab)
                }.show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error setting up activity classifier.", e)
            }

        modelPredictionActivityText = view.findViewById(R.id.modelPredictionActivityText)
        modelPredictionConfidence = view.findViewById(R.id.modelPredictionConfidence)

        networkModelPredictionActivityText =
            view.findViewById(R.id.networkModelPredictionActivityText)
        networkModelPredictionConfidence = view.findViewById(R.id.networkModelPredictionConfidence)

        connectToApi()


        // https://stackoverflow.com/q/60430697/9184658

        setupChart(view)
//
        setupRecyclerViews(view)

        val sharedPreferences =
            ctx.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        respeckUUID = sharedPreferences.getString(Constants.RESPECK_MAC_ADDRESS_PREF, "").toString()

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


                    time += 1
                    updateGraph()

                    if (time % classificationInterval == 0) {
                        // only update every n data points
                        try {
                            classifyActivity(respeckDataQueue.toList())
                        } catch (e: Exception) {
                            // catch all exceptions here to keep it from crashing as much as possible
                            // can happen by switching tabs if the Respeck is still connected
                            // but the interpreter is closed
                            Log.e(TAG, e.toString())
                        }
                        // coroutine, runs asynchronously
                        // https://kotlinlang.org/docs/tutorials/coroutines/coroutines-basic-jvm.html
                        // https://developer.android.com/kotlin/coroutines

                        GlobalScope.launch {
                            try {
                                Log.d(
                                    "$TAG/flaskApiPost",
                                    "respeckDataQueue (${respeckDataQueue.size}) => ${respeckDataQueue.toList()}"
                                )
                                val startTime = System.nanoTime()
                                flaskApi.postRespeckData(
                                    respeckUUID.replace(':', '-'),
                                    org.openapitools.client.models.RespeckData(
                                        respeckDataQueue.toList().map { d ->
                                            if (d == null) {
                                                Log.e("$TAG/flaskApiPost/map", d.toString())
                                                listOf(0, 0, 0).map { it.toBigDecimal() }
                                            } else {
                                                listOf(
                                                    d.accel_x,
                                                    d.accel_y,
                                                    d.accel_z,
                                                ).map { it.toBigDecimal() }
                                            }
                                        }.toList()
                                    ),
                                    ""
                                ).await().let { pred ->
                                    Log.d(TAG, "prediction response => $pred")
                                    //      pred.predictions[pred.label].toFloat()
                                    val p = pred.label?.let { pred.predictions?.get(it) } ?: 0
                                    val conf = String.format(floatFormat, 100 * p.toFloat())
                                    Log.d(
                                        TAG,
                                        "prediction response => activity   = ${pred.activity}"
                                    )
                                    Log.d(TAG, "                       confidence = ${conf}")
                                    runOnUiThread {
                                        predictionTextIndicatorNetwork.apply {
                                            setActivity(pred.activity)
                                            setConfidence(conf)
                                        }
                                    }
                                }
                                val elapsedTime = (System.nanoTime() - startTime) / 1000000
                                Log.i(TAG, "Network request time = $elapsedTime ms")
                            } catch (e: Exception) {
                                Log.w(TAG, "API Exception: $e")
                                when (e) {
                                    // probably disconnected
                                    // connectToApi()
                                    is HttpException,
                                    is ConnectException,
                                    is SocketTimeoutException -> {
                                        // probably just timed out / disconnected
                                        Log.w("$TAG/flaskApiPost", e.toString())
                                    }
                                    else -> {
                                        Log.e("$TAG/flaskApiPost", e.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // register receiver on another thread
        val handlerThread = HandlerThread("bgThread")
        handlerThread.start()
        looper = handlerThread.looper
        val handler = Handler(looper)
        ctx.registerReceiver(respeckLiveUpdateReceiver, filterTest, null, handler)
//        return super.onCreateView(inflater, container, savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        predictionTextIndicatorOnDevice
        predictionTextIndicatorNetwork.apply {
            setIcon(R.drawable.ic_baseline_wifi_24)
            setDescription(R.string.network_prediction)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityClassifier.close()
        looper.quit()
        ctx.unregisterReceiver(respeckLiveUpdateReceiver)
    }

    private fun setupChart(view: View) {
        chart = view.findViewById<LineChart>(R.id.chart)

        time = 0
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
                ctx,
                R.color.red
            )
        )
        dataSet_y.setColor(
            ContextCompat.getColor(
                ctx,
                R.color.green
            )
        )
        dataSet_z.setColor(
            ContextCompat.getColor(
                ctx,
                R.color.blue
            )
        )
        dataSet_mag.setColor(
            ContextCompat.getColor(
                ctx,
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

//        val dataPointCount = findViewById<TextView>(R.id.dataPointsCount)

        val tf = time.toFloat()

        dataSet_x.addEntry(Entry(tf, respeckData.accel_x))
        dataSet_y.addEntry(Entry(tf, respeckData.accel_y))
        dataSet_z.addEntry(Entry(tf, respeckData.accel_z))
        dataSet_mag.addEntry(Entry(tf, respeckData.accel_mag))

        runOnUiThread {
            allAccelData.notifyDataChanged()
            chart.notifyDataSetChanged()
            chart.invalidate()
            chart.setVisibleXRangeMaximum(150f)
//            Log.i("Chart", "Lowest X = " + chart.lowestVisibleX.toString())
            chart.moveViewToX(chart.lowestVisibleX + 40)
//            Log.i("Chart", "Lowest X after = " + chart.lowestVisibleX.toString())
//            dataPointCount.text = getString(R.string.n_data_points, dataSet_mag.entryCount)
        }
    }

    private fun setupRecyclerViews(view: View) {
        // https://stackoverflow.com/a/17516998/9184658
        // create a layout manager that does not scroll
        recyclerViewManager = object : LinearLayoutManager(ctx) {
            override fun canScrollVertically(): Boolean = false
        }
        recyclerViewAdapter = ActivityRecyclerAdapter(dummyClassificationResults)
        recyclerView = view.findViewById<RecyclerView>(R.id.activityTypesRecyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = recyclerViewManager
            // specify an viewAdapter (see also next example)
            adapter = recyclerViewAdapter
        }

        // category view, almost equivalent to other recycler view
//        recyclerViewCategoryManager = object : LinearLayoutManager(view.context) {
//            override fun canScrollVertically(): Boolean = false
//        }
//        recyclerViewAdapter = ActivityRecyclerAdapter(ClassificationResults(emptyList()))
//        recyclerViewCategory =
//            view.findViewById<RecyclerView>(R.id.activityCategoriesRecyclerView).apply {
//                setHasFixedSize(true)
//                layoutManager = recyclerViewCategoryManager
//                adapter = recyclerViewAdapter
//            }
    }

    //
    class ActivityRecyclerAdapter(private val activities: ClassificationResults) :
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
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ActivityRecyclerViewHolder {
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
                        progress = (c * 100).roundToInt() // has been NaN before
                        progressTintList = ColorStateList.valueOf(
                            if (position == activities.maxI) {
                                resources.getColor(R.color.colorPrimaryDark, null)
                            } else {
                                resources.getColor(R.color.gray_600, null)
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


    private fun classifyActivity(data: List<RespeckData>) {
        if (activityClassifier.isInitialized) {
            activityClassifier
                .classifyAsync(data)
                .addOnSuccessListener { res ->
                    Log.d(TAG, "updated classification results")
                    recyclerView.adapter = ActivityRecyclerAdapter(res)

                    // massage the results
//                    val categorizedResults = ClassificationResults(
//                        // for each category
//                        Constants.ACTIVITY_CATEGORIES.mapIndexed { i, cat ->
//                            // make a pair of category to the confidence
//                            cat to res.list.filterIndexed { j, _ ->
//                                // if the category mapping puts this activity in this group
//                                Constants.ACTIVITY_TO_CATEGORY_MAP[j] == i
//                            }.map { (_, confidence) -> confidence }
//                                .sum() // sum the individual % confidence
//                        }
//                    )
//                    recyclerViewCategory.adapter = ActivityRecyclerAdapter(categorizedResults)

                    res.max.let { (activity, c) ->
                        predictionTextIndicatorOnDevice.apply {
                            setActivity(activity)
                            setConfidence(String.format(floatFormat, 100 * c))
                        }
//                        modelPredictionActivityText.text = name
//                        modelPredictionConfidence.text = String.format("%.2f%%", 100 * c)
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

    private fun connectToApi() {
        try {
            Log.i(TAG, "Connecting to API at $API_BASE_PATH ...")
            flaskApi = ApiClient(API_BASE_PATH).createService(DefaultApi::class.java)
            Log.i(TAG, "Connected to API! ($flaskApi)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed connection to API: $e")
        }
    }
}
