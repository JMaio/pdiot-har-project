package com.specknet.pdiotapp.tracking

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.specknet.pdiotapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

typealias TimeSpentList = List<Pair<Int, Int>>

private val startData: TimeSpentList = listOf(
    (R.drawable.ic_baseline_airline_seat_flat_24 to 23),
    (R.drawable.ic_baseline_airline_seat_recline_normal_24 to 43),
    (R.drawable.ic_baseline_accessibility_new_24 to 5),
    (R.drawable.ic_baseline_directions_walk_24 to 24),
    (R.drawable.ic_baseline_directions_run_24 to 10),
)

/**
 * A simple [Fragment] subclass.
 * Use the [ActivityTrackingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActivityTrackingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var ctx: Context

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerView.Adapter<*>
    private lateinit var recyclerViewManager: RecyclerView.LayoutManager


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

    private fun setupRecyclerViews(view: View) {
        // https://stackoverflow.com/a/17516998/9184658
        // create a layout manager that does not scroll
        recyclerViewManager = object : LinearLayoutManager(ctx) {
            override fun canScrollVertically(): Boolean = false
        }
        recyclerViewAdapter = ActivityTimeSpentRecyclerAdapter(startData)
        recyclerView = view.findViewById<RecyclerView>(R.id.activityBreakdownRecyclerView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = recyclerViewManager
            // specify an viewAdapter (see also next example)
            adapter = recyclerViewAdapter
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
    class ActivityTimeSpentRecyclerAdapter(private val timeSpent: TimeSpentList) :
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
            timeSpent[position].let { (icon, prog) ->
                holder.apply {
                    activityItemIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            holder.activityItemIcon.context,
                            icon
                        )
                    )
                    activityItemTimeSpent.progress = prog
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ActivityTrackingFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ActivityTrackingFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
    }
}