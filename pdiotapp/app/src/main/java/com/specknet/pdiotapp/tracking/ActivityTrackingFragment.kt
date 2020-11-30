package com.specknet.pdiotapp.tracking

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.specknet.pdiotapp.R
import kotlin.math.min
import kotlin.math.roundToInt

// activity percent is calculated as the sum of the percent spent (standing*0.5) / (walking*1) / (running*2)
class TimeSpentList(val lay: Int, val sit: Int, val stand: Int, val walk: Int, val run: Int) {
    constructor(t: TimeSpentList) : this(t.lay, t.sit, t.stand, t.walk, t.run)

    fun toList() = listOf(lay, sit, stand, walk, run)

    fun getActivityPercent(): Int =
        min(.75 * stand + 1.5 * walk + 3 * run, 100.0).roundToInt()

    val size: Int get() = toList().size
}

private val activityIcons = listOf(
    R.drawable.ic_baseline_airline_seat_flat_24,
    R.drawable.ic_baseline_airline_seat_recline_normal_24,
    R.drawable.ic_baseline_accessibility_new_24,
    R.drawable.ic_baseline_directions_walk_24,
    R.drawable.ic_baseline_directions_run_24,
)

val activityVeryLowData = TimeSpentList(56, 42, 3, 4, 0)
val activityLowData = TimeSpentList(24, 59, 9, 7, 1)
val activityModerateData = TimeSpentList(14, 52, 10, 18, 6)
val activityIntenseData = TimeSpentList(11, 36, 14, 25, 14)


/**
 * A simple [Fragment] subclass.
 * Use the [ActivityTrackingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActivityTrackingFragment : Fragment() {

    private lateinit var ctx: Context

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<*>
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager

    private lateinit var activityIndicator: ActivityIndicator

//    var timeSpentList = TimeSpentList(activityVeryLowData)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_activity_tracking, container, false)
        setupRecyclerViews(view)

        return view
    }

//    override fun onStart() {
//        super.onStart()
//        activityIndicator.apply {
//            setProgress(activityVeryLowData)
//        }
//    }

    private fun setupRecyclerViews(view: View) {
        // https://stackoverflow.com/a/17516998/9184658
        // create a layout manager that does not scroll
        recyclerViewManager = object : LinearLayoutManager(ctx) {
            override fun canScrollVertically(): Boolean = false
        }
        // set the initial data
        recyclerViewAdapter = ActivityTimeSpentRecyclerAdapter(activityVeryLowData)
        recyclerView = view.findViewById<RecyclerView>(R.id.activityBreakdownRecyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = recyclerViewManager
            // specify an viewAdapter (see also next example)
            adapter = recyclerViewAdapter
        }

        activityIndicator = view.findViewById(R.id.activityTrackingCompoundIndicator)
        activityIndicator.apply {
            activityLevelRecyclerView = recyclerView
            setProgress(activityVeryLowData)
        }
//        // category view, almost equivalent to other recycler view
//        recyclerViewCategoryManager = object : LinearLayoutManager(view.context) {
//            override fun canScrollVertically(): Boolean = false
//        }
//        recyclerViewAdapter = ActivityTimeSpentRecyclerAdapter(ClassificationResults(emptyList()))
//        recyclerViewCategory =
//            view.findViewById<RecyclerView>(R.id.activityCategoriesRecyclerView).apply {
//                setHasFixedSize(true)
//                layoutManager = recyclerViewCategoryManager
//                adapter = recyclerViewAdapter
//            }
    }

    //
    class ActivityTimeSpentRecyclerAdapter(var timeSpent: TimeSpentList) :
        RecyclerView.Adapter<ActivityTimeSpentRecyclerAdapter.ActivityTimeSpentRecyclerViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class ActivityTimeSpentRecyclerViewHolder(
            private val v: View,
            val activityItemIcon: ImageView = v.findViewById(R.id.activityItemIcon),
            val activityItemTimeSpent: ProgressBar = v.findViewById(R.id.activityItemTimeSpent),
            val activityItemTimeSpentText: TextView = v.findViewById(R.id.activityItemTimeSpentText),
        ) : RecyclerView.ViewHolder(v) {
            init {
//                Log.d(TAG, "Starting recyclerViewHolder")
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ActivityTimeSpentRecyclerViewHolder {
            // create a new view
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_activity, parent, false) as LinearLayout
            // set the view's size, margins, padding and layout parameters

            return ActivityTimeSpentRecyclerViewHolder(v)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ActivityTimeSpentRecyclerViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            activityIcons.zip(timeSpent.toList())[position].let { (icon, prog) ->
                holder.apply {
                    activityItemIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            holder.activityItemIcon.context,
                            icon
                        )
                    )
//                    activityItemTimeSpent.progress = prog
                    animateProgression(activityItemTimeSpent, prog, stepMultiplier = 10)
                    activityItemTimeSpentText.text = String.format("%d%%", prog)
                }
//                activities.list[position].let { (name, c) ->
//                    activityName.text = name
//                    confidenceIndicator.apply {
//                        progress = (c * 100).roundToInt()
//                        progressTintList = ColorStateList.valueOf(
//                            if (position == activities.maxI) {
//                                resources.getColor(R.color.accent_500, null)
//                            } else {
//                                resources.getColor(R.color.primary_500, null)
//                            }
//                        )
//                    }
//                    Log.d(TAG, "activity - ${name} - ${c}")
//                }
            }
        }


        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount(): Int {
            return timeSpent.size
        }
    }

    companion object {
        private const val TAG = "ActivityTrackingFragment"
    }
}

// generic progress bar animator
// https://stackoverflow.com/a/49261958/9184658
fun animateProgression(bar: ProgressBar, to: Int, duration: Long = 500, stepMultiplier: Int = 1) {
    bar.max = 100 * stepMultiplier
    val animation =
        ObjectAnimator.ofInt(bar, "progress", bar.progress, to * stepMultiplier)
    animation.duration = duration
    animation.interpolator = DecelerateInterpolator()
    animation.start()
    bar.clearAnimation()
}