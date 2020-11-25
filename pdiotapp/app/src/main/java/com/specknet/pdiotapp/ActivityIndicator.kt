package com.specknet.pdiotapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val CHANNEL_ID = "PDIoT.ActivityNotificationChannel"
private const val TAG = "ActivityNotification"

/**
 * TODO: document your custom view class.
 */
class ActivityIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val activityLevelProgressIndicator: SeekBar
    private val activityIndicatorIconSitting: ImageView
    private val activityIndicatorIconStanding: ImageView
    private val activityIndicatorIconRunning: ImageView

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

        activityIndicatorIconSitting = findViewById(R.id.activityIndicatorIconSitting)
        activityIndicatorIconStanding = findViewById(R.id.activityIndicatorIconStanding)
        activityIndicatorIconRunning = findViewById(R.id.activityIndicatorIconRunning)

        createNotificationChannel()
        setClickListeners()
    }

    private fun setClickListeners() {
        // these listeners should be more modular...
        activityIndicatorIconSitting.apply {
            setOnTouchListener { v, event ->
                val n = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_airline_seat_recline_normal_24)
                    .setContentTitle(context.getString(R.string.time_for_a_break))
                    .setContentText(context.getString(R.string.sitting_activity_description))
                    .setStyle(NotificationCompat.BigTextStyle())
                with(NotificationManagerCompat.from(context)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(0, n.build())
                }
                performClick()
                true
            }
        }
        activityIndicatorIconStanding.apply {
            setOnTouchListener { v, event ->
                val n = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_accessibility_new_24)
                    .setContentTitle(context.getString(R.string.almost_there))
                    .setContentText(context.getString(R.string.movement_activity_description))
                    .setStyle(NotificationCompat.BigTextStyle())
                with(NotificationManagerCompat.from(context)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(0, n.build())
                }
                performClick()
                true
            }
        }
        activityIndicatorIconRunning.apply {
            setOnTouchListener { v, event ->
                val n = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                    .setContentTitle(context.getString(R.string.awesome_moves))
                    .setContentText(context.getString(R.string.goal_reached_activity_description))
                    .setStyle(NotificationCompat.BigTextStyle())
                with(NotificationManagerCompat.from(context)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(0, n.build())
                }
                performClick()
                true
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Activity alerts"
            val descriptionText = "Shows activity reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
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