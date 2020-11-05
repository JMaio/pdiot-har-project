package com.specknet.pdiotapp.utils;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    // Respeck extras
    public static final String EXTRA_RESPECK_LIVE_X = "respeck_x";
    public static final String EXTRA_RESPECK_LIVE_Y = "respeck_y";
    public static final String EXTRA_RESPECK_LIVE_Z = "respeck_z";
    public static final String EXTRA_RESPECK_PACKET_SEQ = "respeck_seq";
    public static final String EXTRA_INTERPOLATED_TS = "interpolated_timestamp";
    public static final int NUMBER_OF_SAMPLES_PER_BATCH = 32;
    public static final float SAMPLING_FREQUENCY = 12.7f;
    public static final int AVERAGE_TIME_DIFFERENCE_BETWEEN_RESPECK_PACKETS = (int) Math.round(
            NUMBER_OF_SAMPLES_PER_BATCH / SAMPLING_FREQUENCY * 1000.);
    public static final int MAXIMUM_MILLISECONDS_DEVIATION_ACTUAL_AND_CORRECTED_TIMESTAMP = 400;

    public static final String ACTION_INNER_RESPECK_BROADCAST = "com.specknet.pdiotapp.RESPECK_BROADCAST";
    public static final String ACTION_RESPECK_CONNECTED = "com.specknet.pdiotapp.RESPECK_CONNECTED";
    public static final String ACTION_RESPECK_DISCONNECTED = "com.specknet.pdiotapp.RESPECK_DISCONNECTED";

    public static final String PREFERENCES_FILE = "com.specknet.pdiotapp.PREFERENCE_FILE";
    public static final String RESPECK_MAC_ADDRESS_PREF = "respeck_id_pref";
    public static final String RESPECK_VERSION = "respeck_version";

    //The REQUEST_ENABLE_BT constant passed to startActivityForResult(android.content.Intent, int)
    // is a locally-defined integer (which must be greater than 0) that the system passes back
    // to you in your onActivityResult(int, int, android.content.Intent) implementation as the requestCode parameter.

    public static final String RESPECK_CHARACTERISTIC_UUID = "00001524-1212-efde-1523-785feabcd125";
    public static final String SERVICE_UUID = "0000180a-0000-1000-8000-00805f9b34fb";

    public static final int REQUEST_CODE_PERMISSIONS = 4;

    public static final String RECORDING_CSV_HEADER = "timestamp,seq,accel_x,accel_y,accel_z";

    // activity mappings
    public static final Map<String, Integer> ACTIVITY_NAME_TO_CODE_MAPPING = new HashMap<String, Integer>() {{
        put("Sitting", 0);
        put("Sitting bent forward", 4);
        put("Sitting bent backward", 5);
        put("Standing", 100);
        put("Walking at normal speed", 1);
        put("Lying down on back", 2);
        put("Lying down left", 7);
        put("Lying down right", 6);
        put("Lying down on stomach", 8);
        put("Movement", 9);
        put("Walking slow", 10);
        put("Running", 11);
        put("Climbing stairs", 12);
        put("Descending stairs", 13);
        put("Riding a bike", 14);
        put("Sit to stand (PR)", 21);
        put("Knee extension (PR)", 22);
        put("Squats (PR)", 23);
        put("Heel raises (PR)", 24);
        put("Bicep curls (PR)", 25);
        put("Shoulder press (PR)", 26);
        put("Wall push offs (PR)", 27);
        put("Leg slides (PR)", 28);
        put("Step ups", 29);
        put("Driving in car or bus", 30);
        put("Desk work", 31);
    }};

    public static final Map<Integer, String> ACTIVITY_CODE_TO_NAME_MAPPING = new HashMap<Integer, String>() {{
        put(0, "Sitting");
        put(4, "Sitting bent forward");
        put(5, "Sitting bent backward");
        put(1, "Walking at normal speed");
        put(2, "Lying down on back");
        put(7, "Lying down left");
        put(6, "Lying down right");
        put(8, "Lying down on stomach");
        put(9, "Movement");
        put(10, "Walking slow");
        put(11, "Running");
        put(12, "Climbing stairs");
        put(13, "Descending stairs");
        put(14, "Riding a bike");
        put(21, "Sit to stand (PR)");
        put(22, "Knee extension (PR)");
        put(23, "Squats (PR)");
        put(24, "Heel raises (PR)");
        put(25, "Bicep curls (PR)");
        put(26, "Shoulder press (PR)");
        put(27, "Wall push offs (PR)");
        put(28, "Leg slides (PR)");
        put(29, "Step ups");
        put(30, "Driving in car or bus");
        put(31, "Desk work");
        put(100, "Standing");
    }};

    public static final Map<String, Integer> SS_NAME_TO_CODE_MAPPING = new HashMap<String, Integer>() {{
        put("Coughing", 15);
        put("Talking", 16);
        put("Eating", 17);
        put("Singing", 18);
        put("Laughing", 19);
        put("Breathing", 20);
    }};

    public static final Map<Integer, String> SS_CODE_TO_NAME_MAPPING = new HashMap<Integer, String>() {{
        put(15, "Coughing");
        put(16, "Talking");
        put(17, "Eating");
        put(18, "Singing");
        put(19, "Laughing");
        put(20, "Breathing");
    }};


    public static final ArrayList<String> AVAILABLE_ACTIVITIES = new ArrayList<String>() {{
        add("Sitting");
        add("Sitting bent forward");
        add("Sitting bent backward");
        add("Walking at normal speed");
        add("Standing");
        add("Lying down on back");
        add("Lying down left");
        add("Lying down right");
        add("Lying down on stomach");
        add("Movement");
        add("Running");
        add("Climbing stairs");
        add("Descending stairs");
        add("Desk work");
    }};

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

    public static final ArrayList<String> ACTIVITY_CATEGORIES = new ArrayList<String>() {{
        add("Sitting");
        add("Walking");
        add("Standing");
        add("Lying");
        add("Movement");
        add("Running");
        add("Climbing Stairs");
        add("Descending stairs");
    }};


    public static final String PREF_USER_FIRST_TIME = "user_first_time";

}
