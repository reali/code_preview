package com.zitech.audio.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import com.qualcomm.qti.libraries.ble.BLEService;

public class Consts {
    ////////////////IMPORTED/////////////////////

    public static final int ACTION_REQUEST_ENABLE_BLUETOOTH = 101;

    /**
     * The code to use for the methods {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * onRequestPermissionsResult} and
     * {@link android.support.v4.app.ActivityCompat#requestPermissions(Activity, String[], int) requestPermissions}
     * when requesting to the user to enable needed permissions.
     */
    public static final int ACTION_REQUEST_PERMISSIONS = 102;

    /**
     * The default time to scan for BLE devices - this is used as a timeout to call
     * {@link BluetoothAdapter#stopLeScan(BluetoothAdapter.LeScanCallback) stopLeScan}.
     */
    public static final int SCANNING_TIME = 10000;

    /**
     * The battery level max for the board device.
     */
    public static final int BATTERY_LEVEL_MAX = 3700;

    /**
     * The unit to use to display file size.
     */
    public static final String UNIT_FILE_SIZE = " KB";

    /**
     * <p>To define the format of a date.</p>
     */
    public static final String DATE_FORMAT = "HH:mm - d MMM yyyy";

    /**
     * The character which represents a percentage.
     */
    static final String PERCENTAGE_CHARACTER = "%";

    /**
     * <p>The time to wait prior to request again the RSSI value from the device.</p>
     */
    public static final int DELAY_TIME_FOR_RSSI = 1000;
    /**
     * The file name to use to save information in the shared preferences.
     */
    public static final String PREFERENCES_FILE = "GaiaControlPreferences";
    /**
     * The key to use for the shared preferences to store the Bluetooth address of a device.
     */
    public static final String BLUETOOTH_ADDRESS_KEY = "Device Bluetooth address";

    public static final String CURRENT_DEVICE_NAME_KEY = "CurrentDeviceName";
    public static final String FIND_PHONE_NAME_KEY = "findPhone";


    public static final String SWITCH_LOW_KEY = "switch_low";
    public static final String SWITCH_HIGH_KEY = "switch_high";
    public static final String SWITCH_LOW_VALUE_KEY = "low_value";
    public static final String SWITCH_HIGH_VALUE_KEY = "high_value";

    /**
     * The key to use for the shared preferences to store the type of transport which is used.
     */
    public static final String TRANSPORT_KEY = "Transport type";
    /**
     * To display or hide the debug logs of the application.
     */
    public static final boolean DEBUG = false;

    public static final class MTU {
        public static final int MINIMUM = BLEService.MTU_SIZE_MINIMUM;
        public static final int MAXIMUM = BLEService.MTU_SIZE_MAXIMUM;
    }
    ///////////////////////////////////////////////////////

    public static String TAG = "audio";

    public static final String LAST_CONNECTED = "lastConnected";



}
