package com.specknet.pdiotapp

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_live_data.view.*
import kotlinx.android.synthetic.main.model_prediction_card.view.*
import kotlinx.android.synthetic.main.model_prediction_card.view.modelPredictionActivityText
import kotlinx.android.synthetic.main.model_prediction_card.view.modelPredictionConfidence
import kotlinx.android.synthetic.main.widget_activity_indicator.view.*

/**
 * TODO: document your custom view class.
 */
class PredictionTextIndicator constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    // default drawable
//    var iconDrawable: Int = R.drawable.ic_baseline_phonelink_setup_24
//        set(newDrawable: Int) {
//            field = newDrawable
//            drawIcon()
//        }

//    var description: Int = R.string.on_device_prediction
//        set(d: Int) {
//            field = d
//            updateDescription(resources.getString(d))
//        }

    init {
        inflate(context, R.layout.model_prediction_card, this)

//        outlineProvider = ViewOutlineProvider.PADDED_BOUNDS
//        this.clipChildren = false
//        this.clipToPadding = false
//        this.clipToOutline = false
    }

    fun setIcon(i: Int) {
        modelPredictionConfidence.setCompoundDrawablesWithIntrinsicBounds(
            0, i, 0, 0
        )
    }

    fun setConfidence(c: String) {
        modelPredictionConfidence.text = c
    }

    fun setDescription(d: String) {
        model_origin_text.text = d
    }

    fun setDescription(d: Int) {
        setDescription(resources.getString(d))
    }

    fun setActivity(a: String?) {
        modelPredictionActivityText.text = a ?: "?"
    }


    fun setProgress(n: Number) {
        // convert to int for progress bar
        val p: Int = if (n in 0..100) n as Int else -1
        var t = "?"
        when (p) {
            in 0..33 -> t = resources.getString(R.string.low)
            in 34..66 -> t = resources.getString(R.string.moderate)
            in 67..100 -> t = resources.getString(R.string.intense)
        }
        activityIndicatorTextView.text = t
        animateProgression(p)
//        currentProgress = p
//        activityLevelProgressIndicator.progress = p
    }

    // https://stackoverflow.com/a/49261958/9184658
    private fun animateProgression(to: Int) {
        val animation =
            ObjectAnimator.ofInt(activityLevelProgressIndicator, "progress", to * 10)
        animation.duration = 500
        animation.interpolator = DecelerateInterpolator()
        animation.start()
        activityLevelProgressIndicator.clearAnimation()
    }

}