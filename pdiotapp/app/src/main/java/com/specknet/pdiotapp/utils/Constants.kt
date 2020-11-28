package com.specknet.pdiotapp.utils

import com.specknet.pdiotapp.R
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

object Constants {

//     var AVERAGE_TIME_DIFFERENCE_BETWEEN_RESPECK_PACKETS: Long

    // Respeck extras
    const val EXTRA_RESPECK_LIVE_X = "respeck_x"
    const val EXTRA_RESPECK_LIVE_Y = "respeck_y"
    const val EXTRA_RESPECK_LIVE_Z = "respeck_z"
    const val EXTRA_RESPECK_PACKET_SEQ = "respeck_seq"
    const val EXTRA_INTERPOLATED_TS = "interpolated_timestamp"
    const val NUMBER_OF_SAMPLES_PER_BATCH = 32
    const val SAMPLING_FREQUENCY = 12.7f
    // https://stackoverflow.com/a/60600758/9184658
    @JvmField val AVERAGE_TIME_DIFFERENCE_BETWEEN_RESPECK_PACKETS =
        (NUMBER_OF_SAMPLES_PER_BATCH / SAMPLING_FREQUENCY * 1000.0).roundToLong()

    const val MAXIMUM_MILLISECONDS_DEVIATION_ACTUAL_AND_CORRECTED_TIMESTAMP = 400
    const val ACTION_INNER_RESPECK_BROADCAST = "com.specknet.pdiotapp.RESPECK_BROADCAST"
    const val ACTION_RESPECK_CONNECTED = "com.specknet.pdiotapp.RESPECK_CONNECTED"
    const val ACTION_RESPECK_DISCONNECTED = "com.specknet.pdiotapp.RESPECK_DISCONNECTED"
    const val PREFERENCES_FILE = "com.specknet.pdiotapp.PREFERENCE_FILE"
    const val RESPECK_MAC_ADDRESS_PREF = "respeck_id_pref"
    const val RESPECK_VERSION = "respeck_version"

    //The REQUEST_ENABLE_BT constant passed to startActivityForResult(android.content.Intent, int)
    // is a locally-defined integer (which must be greater than 0) that the system passes back
    // to you in your onActivityResult(int, int, android.content.Intent) implementation as the requestCode parameter.
    const val RESPECK_CHARACTERISTIC_UUID = "00001524-1212-efde-1523-785feabcd125"
    const val SERVICE_UUID = "0000180a-0000-1000-8000-00805f9b34fb"
    const val REQUEST_CODE_PERMISSIONS = 4
    const val RECORDING_CSV_HEADER = "timestamp,seq,accel_x,accel_y,accel_z"

    //
//    // activity mappings
    val ACTIVITY_NAME_TO_CODE_MAPPING = mapOf(
        "Sitting" to 0,
        "Sitting bent forward" to 4,
        "Sitting bent backward" to 5,
        "Standing" to 100,
        "Walking at normal speed" to 1,
        "Lying down on back" to 2,
        "Lying down left" to 7,
        "Lying down right" to 6,
        "Lying down on stomach" to 8,
        "Movement" to 9,
        "Walking slow" to 10,
        "Running" to 11,
        "Climbing stairs" to 12,
        "Descending stairs" to 13,
        "Riding a bike" to 14,
        "Sit to stand (PR)" to 21,
        "Knee extension (PR)" to 22,
        "Squats (PR)" to 23,
        "Heel raises (PR)" to 24,
        "Bicep curls (PR)" to 25,
        "Shoulder press (PR)" to 26,
        "Wall push offs (PR)" to 27,
        "Leg slides (PR)" to 28,
        "Step ups" to 29,
        "Driving in car or bus" to 30,
        "Desk work" to 31,
    )

    val ACTIVITY_CODE_TO_NAME_MAPPING = mapOf(
        0 to "Sitting",
        4 to "Sitting bent forward",
        5 to "Sitting bent backward",
        1 to "Walking at normal speed",
        2 to "Lying down on back",
        7 to "Lying down left",
        6 to "Lying down right",
        8 to "Lying down on stomach",
        9 to "Movement",
        10 to "Walking slow",
        11 to "Running",
        12 to "Climbing stairs",
        13 to "Descending stairs",
        14 to "Riding a bike",
        21 to "Sit to stand (PR)",
        22 to "Knee extension (PR)",
        23 to "Squats (PR)",
        24 to "Heel raises (PR)",
        25 to "Bicep curls (PR)",
        26 to "Shoulder press (PR)",
        27 to "Wall push offs (PR)",
        28 to "Leg slides (PR)",
        29 to "Step ups",
        30 to "Driving in car or bus",
        31 to "Desk work",
        100 to "Standing",
    )

    //        val SS_NAME_TO_CODE_MAPPING = mapOf(
//            "Coughing" to 15,
//            "Talking" to 16,
//            "Eating" to 17,
//            "Singing" to 18,
//            "Laughing" to 19,
//            "Breathing" to 20,
//        )
//    val SS_CODE_TO_NAME_MAPPING: Map<Int, String> = object : HashMap<Int?, String?>() {
//        init {
//            put(15, "Coughing")
//            put(16, "Talking")
//            put(17, "Eating")
//            put(18, "Singing")
//            put(19, "Laughing")
//            put(20, "Breathing")
//        }
//    }
    val AVAILABLE_ACTIVITIES = listOf(
        "Sitting",
        "Sitting bent forward",
        "Sitting bent backward",
        "Walking at normal speed",
        "Standing",
        "Lying down on back",
        "Lying down left",
        "Lying down right",
        "Lying down on stomach",
        "Movement",
        "Running",
        "Climbing stairs",
        "Descending stairs",
        "Desk work",
    )

    //    public static final Map<Integer, Integer> ACTIVITY_TO_CATEGORY_MAP = new HashMap<Integer, Integer>() {{
    //        put(0,  0);    // "Sitting",
    //        put(1,  0);    // "Sitting bent forward",
    //        put(2,  0);    // "Sitting bent backward",
    //        put(3,  1);    // "Walking at normal speed",
    //        put(4,  0);    // "Standing",
    //        put(5,  0);    // "Lying down on back",
    //        put(6,  0);    // "Lying down left",
    //        put(7,  0);    // "Lying down right",
    //        put(8,  0);    // "Lying down on stomach",
    //        put(9,  1);    // "Movement",
    //        put(10, 2);    // "Running",
    //        put(11, 1);   // "Climbing stairs",
    //        put(12, 1);   // "Descending stairs",
    //        put(13, 0);   // "Desk work"
    //    }};
    //    public static final Map<Integer, Integer> TFCODE_TO_ACTIVITY_CODE = new HashMap<Integer, Integer>() {{
    //        put(0,  0);    // "Sitting",
    //        put(1,  4);    // "Sitting bent forward",
    //        put(2,  5);    // "Sitting bent backward",
    //        put(3,  1);    // "Walking at normal speed",
    //        put(4,  100);  // "Standing",
    //        put(5,  2);    // "Lying down on back",
    //        put(6,  7);    // "Lying down left",
    //        put(7,  6);    // "Lying down right",
    //        put(8,  8);    // "Lying down on stomach",
    //        put(9,  9);    // "Movement",
    //        put(10, 11);   // "Running",
    //        put(11, 12);   // "Climbing stairs",
    //        put(12, 13);   // "Descending stairs",
    //        put(13, 31);   // "Desk work"
    //    }};

    val ACTIVITY_CATEGORIES = listOf(
        "Sitting",
        "Walking",
        "Standing",
        "Lying",
        "Running",
        "Climbing Stairs",
        "Descending stairs",
    )

    val ACTIVITY_TO_ICON_MAP = mapOf(
        "Sitting" to R.drawable.ic_baseline_airline_seat_recline_normal_24,
        "Walking" to R.drawable.ic_baseline_directions_walk_24,
        "Standing" to R.drawable.ic_baseline_accessibility_new_24,
        "Lying" to R.drawable.ic_baseline_airline_seat_flat_24,
        "Running" to R.drawable.ic_baseline_directions_run_24,
        "Climbing Stairs" to R.drawable.ic_baseline_stairs_24,
        "Descending stairs" to R.drawable.ic_baseline_stairs_24,
    )

    const val PREF_USER_FIRST_TIME = "user_first_time"
}