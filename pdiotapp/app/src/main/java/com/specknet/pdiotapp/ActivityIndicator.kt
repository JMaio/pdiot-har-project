package com.specknet.pdiotapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar

/**
 * TODO: document your custom view class.
 */
class ActivityIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val activityLevelProgressIndicator: SeekBar

    init {
        inflate(context, R.layout.widget_activity_indicator, this)

        activityLevelProgressIndicator = findViewById(R.id.activityLevelProgressIndicator)
//        activityLevelProgressIndicator.isEnabled = false
        activityLevelProgressIndicator.apply {
//            isClickable = false
//            isFocusable = false
            // "should performClick"?
            setOnTouchListener { _, _ ->
                performClick()
                true
            }
        }
    }


//    private fun init() {
//    }
    //        inflate(context, R.layout.widget_activity_indicator, this)


//
//    constructor(context: Context) : super(context) {
//        init(null, 0)
//    }
//
//    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        init(attrs, 0)
//    }
//
//    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
//        context,
//        attrs,
//        defStyle
//    ) {
//        init(attrs, defStyle)
//    }

//    private fun init(attrs: AttributeSet?, defStyle: Int) {
//
//        inflate(context, R.layout.widget_activity_indicator, this)
//
//        R.id.activityLevelProgressIndicator
//
//    }

}