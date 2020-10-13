package com.specknet.pdiotapp.live

import android.content.Context
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.specknet.pdiotapp.R

public class ActivityConfidenceView(val ctx: Context) : ConstraintLayout(ctx) {


    val activityNameTextView = findViewById<TextView>(R.id.activityName)
    val confidenceIndicatorProgressBar = findViewById<ProgressBar>(R.id.confidenceIndicator)

//    public fun setActivityName(name: String) {
//        activityNameTextView.text = name
//    }
//
//    public fun setProgress(prog: Float) {
//        confidenceIndicatorProgressBar.progress = 20
//    }
}