package com.zitech.audio.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.GaiaException;
import com.qualcomm.qti.libraries.gaia.GaiaUtils;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacketBLE;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacketBREDR;
import com.zitech.audio.R;

import com.zitech.audio.api.ProductFeatures;
import com.zitech.audio.api.Status;
import com.zitech.audio.application.audioApplication;
import com.zitech.audio.gaia.MainGaiaManager;
import com.zitech.audio.gaia.RemoteGaiaManager;
import com.zitech.audio.models.gatt.GATT;
import com.zitech.audio.models.gatt.GATTServices;
import com.zitech.audio.models.gatt.GattServiceBattery;
import com.zitech.audio.receivers.EnableNeverForgetReceiver;
import com.zitech.audio.receivers.RefreshBatteryLevelReceiver;
import com.zitech.audio.receivers.RefreshConnectionStateReceiver;
import com.zitech.audio.receivers.ShowFeaturesReceiver;
import com.zitech.audio.utils.Consts;
import com.zitech.audio.utils.SharedPref;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTestService extends Service implements MainGaiaManager.MainGaiaManagerListener, RemoteGaiaManager.GaiaManagerListener, ServiceConnection {

    private BluetoothService mService;
    private @BluetoothService.Transport int mTransport = BluetoothService.Transport.UNKNOWN;

    private SharedPref sharedPref;
    private ActivityHandler mHandler;
    public String deviceAddress, blueDeviceName;
    private boolean[] mGAIAFeatures = new boolean[MainGaiaManager.FEATURES_NUMBER];
    static final boolean DEBUG = Consts.DEBUG;

    private MainGaiaManager mGaiaManager;
    private RemoteGaiaManager mRemoteGaiaManager;

//    private int mBatteryLevel = -1;
    private int notifIdNeverForget = 274;
    private int notifIdFindPhone   = 291;
    private int notifIdBattery     = 190;
    private int notifIdConnection  = 923;

    private boolean vibro;
    private boolean isRssiSupported = true;
    private boolean isAlertSended;
    private boolean firstTime = true;
    private boolean ringingFindPhone = false;

    private Handler handler, batteryHandler;

    private final String TAG = "MyTestService";
    private final IBinder binder = new LocalBinder();

    public static int connState = BluetoothService.State.DISCONNECTED;
    public static boolean enableNeverForget = false;
    public static boolean isCharging = false;
    public static int lastBatteryLevel = 0;


    public void sendRemouteCommand(int volumeUp) {
        mRemoteGaiaManager.sendControlCommand(volumeUp);
    }

    public class LocalBinder extends Binder {
        public MyTestService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyTestService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "i_xchange_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Bluetooth audio service",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }

        handler = new Handler();
        batteryHandler = new Handler();

        init();

//        onBluetoothEnabled =>
        if (mService == null) {
            startService();
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (mService != null) {
            initService();
        }
    }

    @Override
    public void onDestroy() {

        stopForeground(true);
        stopSelf();

        super.onDestroy();

        if (mService != null) {
            getRSSINotifications(false);
            mService.removeHandler(mHandler);
            mService = null;
            unbindService(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    private void initService() {
        mService.addHandler(mHandler);
        if (mService.getDevice() == null) {
            // get the bluetooth information

            // get the device Bluetooth address
            deviceAddress = sharedPref.readValue(Consts.BLUETOOTH_ADDRESS_KEY, "");
            blueDeviceName = audioApplication.sharedPref.readValue(Consts.CURRENT_DEVICE_NAME_KEY, "audio");
            boolean done = mService.connectToDevice(deviceAddress);
            if (!done) Log.w(TAG, "connection failed");
        }
    }

    private boolean startService() {
        // get the bluetooth information

        // get the device Bluetooth address
        deviceAddress = sharedPref.readValue(Consts.BLUETOOTH_ADDRESS_KEY, "");
        blueDeviceName = audioApplication.sharedPref.readValue(Consts.CURRENT_DEVICE_NAME_KEY, "audio");
        if (deviceAddress.length() == 0 || !BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
            // no address, not possible to establish a connection
            return false;
        }

        // get the transport type
        int transport = sharedPref.readValue(Consts.TRANSPORT_KEY, BluetoothService.Transport.UNKNOWN);
        mTransport = transport == BluetoothService.Transport.BLE ? BluetoothService.Transport.BLE :
                transport == BluetoothService.Transport.BR_EDR ? BluetoothService.Transport.BR_EDR :
                        BluetoothService.Transport.UNKNOWN;
        if (mTransport == BluetoothService.Transport.UNKNOWN) {
            // transport unknown, not possible to establish a connection
            return false;
        }

        // get the service class to bind
        Class<?> serviceClass = mTransport == BluetoothService.Transport.BLE ? GAIAGATTBLEService.class :
                GAIABREDRService.class; // mTransport can only be BLE or BR EDR

        // bind the service
        Intent gattServiceIntent = new Intent(this, serviceClass);
        gattServiceIntent.putExtra(Consts.BLUETOOTH_ADDRESS_KEY, deviceAddress); // give address to the service
        return bindService(gattServiceIntent, this, BIND_AUTO_CREATE);
    }

    private void init() {
        sharedPref = new SharedPref(this);
        // the Handler to receive messages from the GAIAGATTBLEService once attached
        mHandler = new ActivityHandler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean sendGAIAPacket(byte[] packet) {
        return mService!= null && mService.sendGAIAPacket(packet);
    }

    @Override
    public void onRemoteControlNotSupported() {
        Log.e("reali", "remont controll not supported");
    }

//    @Override
//    public void onRSSINotSupported() {
//        isRssiSupported = false;
//    }

    @Override
    public void onGetLedControl(boolean activate) { }

    @Override
    public void onFeatureSupported(int feature) {

        switch (feature) {
            case MainGaiaManager.Features.LED:
                mGAIAFeatures[MainGaiaManager.Features.LED] = true;
                Log.e("reali", "LED supported");
                break;
            case MainGaiaManager.Features.EQUALIZER:
                mGAIAFeatures[MainGaiaManager.Features.EQUALIZER] = true;
                Log.e("reali", "EQUALIZER supported");
                break;
            case MainGaiaManager.Features.REMOTE_CONTROL:
                mGAIAFeatures[MainGaiaManager.Features.REMOTE_CONTROL] = true;
                Log.e("reali", "REMOTE_CONTROL supported");
                break;
            case MainGaiaManager.Features.UPGRADE:
                mGAIAFeatures[MainGaiaManager.Features.UPGRADE] = true;
                Log.e("reali", "UPGRADE supported");
                break;
        }

    }

    @Override
    public void onInformationNotSupported(int information) {
        switch (information) {

            case MainGaiaManager.Information.API_VERSION:
                Log.e("reali", "API_VERSION NotSupported");
                break;
            case MainGaiaManager.Information.BATTERY:
                Log.e("reali", "BATTERY NotSupported");
                break;
            case MainGaiaManager.Information.LED:
                Log.e("reali", "LED NotSupported");
                break;
            case MainGaiaManager.Information.RSSI:
                Log.e("reali", "RSSI NotSupported");
                break;
        }
    }

    @Override
    public void onChargerConnected(boolean isConnected) {
        isCharging = isConnected;

        Log.e("reali", "isCharging " + isCharging);

        if (mService != null) {
            mService.requestBatteryLevels();
        }
    }

    @Override
    public void onGetBatteryLevel(int level) {
//        mBatteryLevel = level;
//        refreshBatteryLevel();
    }

//    private void refreshBatteryLevel() {
//        int value = mBatteryLevel * 100 / Consts.BATTERY_LEVEL_MAX;
////        sendRefreshBatteryLvlBroadcast(value);
//        Log.e("reali", "battery " + value + "% perc");
//    }

    @Override
    public void onGetRSSILevel(int level) {
        mRssi = level;
//        Log.e("reali", "rssi " + level);
        updatePathLoss(level, mTXPowerLevel);
    }

    @Override
    public void onGetAPIVersion(int versionPart1, int versionPart2, int versionPart3) {
        String APIText = "API " + versionPart1 + "." + versionPart2 + "." + versionPart3;
        Log.e("reali", "API " + APIText);
    }

    @Override
    public void onFeaturesDiscovered() {
        getInformationFromDevice();
    }

    @Override
    public void onGetViberatorControl(boolean activate) {
        vibro = activate;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (name.getClassName().equals(GAIAGATTBLEService.class.getName())) {
            mService = ((GAIAGATTBLEService.LocalBinder) service).getService();
        } else if (name.getClassName().equals(GAIABREDRService.class.getName())) {
            mService = ((GAIABREDRService.LocalBinder) service).getService();
        }

        if (mService != null) {
            initService();
            initManagerAndOther(); // to inform subclass
        }
    }


    private void initManagerAndOther() {

        @GAIA.Transport int transport = mService.getTransport() == BluetoothService.Transport.BR_EDR ?
                GAIA.Transport.BR_EDR : GAIA.Transport.BLE;
        mGaiaManager = new MainGaiaManager(this, transport);
        mRemoteGaiaManager = new RemoteGaiaManager(this, transport);
//        mProxGaiaManager = new ProximityGaiaManager(this, transport);

        audioApplication.gaiaManager = mGaiaManager;

        if (mService != null) {
            GATTServices support = mService.getGattSupport();
            initInformation(support);
            if (support == null || !support.isGattProfileProximitySupported()) {
                displayLongToast(R.string.toast_proximity_not_supported);
            }

        }

        Log.e("reali", "initManagerAndOther");

        getGeneralDeviceInformation();
        refreshConnectionState(mService.getConnectionState(), false);
        refreshBondState(mService.getBondState(), false);
        getGAIAFeatures();
        getInformationFromDevice();
        enableGattFeatures(mService.getGattSupport());

    }

    private void enableGattFeatures(GATTServices gattServices) {
        if (gattServices == null) {
            Log.e("reali", "gattServices == null");
        } else {
            Log.e("reali", "gattServices != null");
        }
    }

    private void getInformationFromDevice() {
        if (mService != null && mService.getConnectionState() == BluetoothService.State.CONNECTED
                && mService.isGaiaReady()) {
            mGaiaManager.getInformation(MainGaiaManager.Information.API_VERSION);
            mGaiaManager.getInformation(MainGaiaManager.Information.RSSI);
            mGaiaManager.getInformation(MainGaiaManager.Information.BATTERY);
            mGaiaManager.getInformation(MainGaiaManager.Information.LED);
            mGaiaManager.getNotifications(MainGaiaManager.Information.BATTERY, true);
            getRSSINotifications(true);
        }
    }

    private void getRSSINotifications(boolean notify) {
        if (notify && !mService.startRssiUpdates(true)) {
            // it is not possible to use the BLE way so we use GAIA
            mGaiaManager.getNotifications(MainGaiaManager.Information.RSSI, true);
        } else if (!notify) {
            mService.startRssiUpdates(false);
            mGaiaManager.getNotifications(MainGaiaManager.Information.RSSI, false);
        }
        sendEnableNeverForgetBroadcast();
    }

    private void getGAIAFeatures() {
        int features = 0; // to get the number of supported GAIA products

        // if known: activates corresponding controls
        for (int i=0; i<mGAIAFeatures.length; i++) {
            if (mGAIAFeatures[i]) {
                onFeatureSupported(i);
                features++;
            }
        }

        // no feature is supported: probably support not checked yet
        if (features == 0 && mService != null && mService.getConnectionState() == BluetoothService.State.CONNECTED
                && mService.isGaiaReady()) {
            mGaiaManager.getSupportedFeatures();
        } else if (features != 0) {
            Log.i(TAG, "App is ready to be used: all GAIA products have been restored.");
        }
    }

    private void refreshBondState(int bondState, boolean notify) {
        // display the bond state
        String label = bondState == BluetoothDevice.BOND_BONDED ? getString(R.string.device_state_bonded)
                : bondState == BluetoothDevice.BOND_BONDING ? getString(R.string.device_state_bonding)
                : getString(R.string.device_state_not_bonded);
        Log.e("reali", "pairing " + label);
    }

    private void getGeneralDeviceInformation() {
        BluetoothDevice device = mService.getDevice();
        String deviceName = device == null ? "" : device.getName();

        Log.e("reali", "deviceName " + deviceName);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (name.getClassName().equals(GAIAGATTBLEService.class.getName())) {
            mService = null;
            Log.e("reali", "onServiceDisconnected");
        }
    }

    void handleMessageFromService(Message msg) {

        String handleMessage = "Handle a message from Bluetooth service: ";

        switch (msg.what) {
            case BluetoothService.Messages.CONNECTION_STATE_HAS_CHANGED:
                @BluetoothService.State int connectionState = (int) msg.obj;
                refreshConnectionState(connectionState, true);
                if (DEBUG) {
                    String stateLabel = connectionState == BluetoothService.State.CONNECTED ? "CONNECTED"
                            : connectionState == BluetoothService.State.CONNECTING ? "CONNECTING"
                            : connectionState == BluetoothService.State.DISCONNECTING ? "DISCONNECTING"
                            : connectionState == BluetoothService.State.DISCONNECTED ? "DISCONNECTED"
                            : "UNKNOWN";
                    Log.d(TAG, handleMessage + "CONNECTION_STATE_HAS_CHANGED: " + stateLabel);
                }
                if (connectionState == BluetoothService.State.CONNECTED) {
                    getGeneralDeviceInformation();
                }
                break;

            case BluetoothService.Messages.DEVICE_BOND_STATE_HAS_CHANGED:
                int bondState = (int) msg.obj;
                refreshBondState(bondState, true);
                if (DEBUG) {
                    String bondStateLabel = bondState == BluetoothDevice.BOND_BONDED ? "BONDED"
                            : bondState == BluetoothDevice.BOND_BONDING ? "BONDING"
                            : "BOND NONE";
                    Log.d(TAG, handleMessage + "DEVICE_BOND_STATE_HAS_CHANGED: " + bondStateLabel);
                }
                break;
            case BluetoothService.Messages.GATT_SUPPORT:
                GATTServices gattServices = (GATTServices) msg.obj;
                onGattSupport(gattServices);
                if (!gattServices.gattServiceGaia.isSupported()) {
                    displayLongToast(R.string.toast_gaia_not_supported);
                }
                if (mService != null && mService.isGattReady()) {
                    enableGattFeatures(gattServices);
                }
                if (DEBUG) Log.d(TAG, handleMessage + "GATT_SUPPORT");
                break;
            case BluetoothService.Messages.GAIA_PACKET:

                byte[] data = (byte[]) msg.obj;

                mGaiaManager.onReceiveGAIAPacket(data);
//                mProxGaiaManager.onReceiveGAIAPacket(data);
                mRemoteGaiaManager.onReceiveGAIAPacket(data);

                ////////////////////////////////////////////////////////

                String msg1 = GaiaUtils.getHexadecimalStringFromBytes(data);

                try {
                    Object packet = mGaiaManager.getTransportType() == 0 ? new GaiaPacketBLE(data) : new GaiaPacketBREDR(data);

                    if (((GaiaPacket)packet).getCommandId() == 16387 && audioApplication.sharedPref.readValue(Consts.FIND_PHONE_NAME_KEY, false)) {
                        Log.e(TAG, "FIND PHONE");

                        if (firstTime) {
                            firstTime = false;
                            break;
                        }

                        if (ringingFindPhone) {
                            break;
                        }

                        notificationDialog("Find phone alert", "Headset is searching for you", notifIdFindPhone, true, true, true);
                        ringingFindPhone = true;
                        handler.postDelayed(startCancelNotification, 1000);

//                        createVibration();

                    } else if (((GaiaPacket)packet).getCommandId() == 647) {
                        Log.e(TAG, " COMMAND_GET_LED_CONTROL");
                    } else if (((GaiaPacket)packet).getCommandId() == 16385) {
                        Log.e(TAG, " COMMAND_REGISTER_NOTIFICATION");
                    }

                } catch (GaiaException var4) {
                    Log.e("GaiaManager", "!!! " + GaiaUtils.getHexadecimalStringFromBytes(data));
                }

                if (DEBUG) Log.e(TAG, msg1 + " GAIA_PACKET");
                // no log as these will be logged by the GAIA manager
                break;
            case BluetoothService.Messages.GAIA_READY:
                getGAIAFeatures();
                if (DEBUG) Log.d(TAG, handleMessage + "GAIA_READY");
                break;
            case BluetoothService.Messages.GATT_READY:
                onGattReady();
                if (mService != null) {
                    enableGattFeatures(mService.getGattSupport());
                }
                if (DEBUG) Log.d(TAG, handleMessage + "GATT_READY");
                break;
            case BluetoothService.Messages.GATT_MESSAGE:
                @GAIAGATTBLEService.GattMessage int gattMessage = msg.arg1;
                Object content = msg.obj;
                onReceiveGattMessage(gattMessage, content);
                if (DEBUG) Log.d(TAG, handleMessage + "GATT_MESSAGE > " + gattMessage);
                break;
            default:
                if (DEBUG)
                    Log.d(TAG, handleMessage + "UNKNOWN MESSAGE: " + msg.what);
                break;
        }

    }

    Runnable startCancelNotification = new Runnable() {
        public void run() {
            cancelNotification(notifIdFindPhone);
            ringingFindPhone = false;
        }
    };

    private void onGattReady() {
        if (mService.getConnectionState() == BluetoothService.State.CONNECTED) {
            sendEnableNeverForgetBroadcast();

            batteryHandler.post(getBatterry);
        }
    }

    Runnable getBatterry = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "charger updating battery");
            if (mService != null) {
                mService.requestBatteryLevels();

                batteryHandler.postDelayed(getBatterry, 60000); //get battery level every 1 minute
            }
        }
    };

    private static class Default {
        /**
         * <p>The default threshold value for the MILD immediate alert level.</p>
         */
        private static final int THRESHOLD_MILD = 60;
        /**
         * <p>The default threshold value for the HIGH immediate alert level.</p>
         */
        private static final int THRESHOLD_HIGH = 90;
        /**
         * <p>The default value for the TX Power level.</p>
         */
        private static final int TX_POWER_LEVEL = 0;
        /**
         * <p>The default value for the RSSI level.</p>
         */
        private static final int RSSI = 0;
        /**
         * <p>The default value for the path loss.</p>
         */
        private static final int PATH_LOSS = TX_POWER_LEVEL - RSSI;
        /**
         * <p>The default value for Alert Levels.</p>
         */
        private static final int ALERT_LEVEL = GATT.AlertLevel.Levels.NONE;
    }

    private int mTXPowerLevel = Default.TX_POWER_LEVEL;
    private int mRssi = Default.RSSI;
    private int mPathLoss = Default.PATH_LOSS;

    private void onReceiveGattMessage(@GAIAGATTBLEService.GattMessage int gattMessage, Object content) {

        if (gattMessage == GAIAGATTBLEService.GattMessage.RSSI_LEVEL) {
            mRssi = (int) content;
            onGetRSSILevel(mRssi);
            updatePathLoss(mRssi, mTXPowerLevel);

        } else if (gattMessage == GAIAGATTBLEService.GattMessage.TX_POWER_LEVEL) {

            mTXPowerLevel = (int) content;
            Log.e("reali", "!!!!!!!!!! " + mTXPowerLevel + " mTXPowerLevel");
            updatePathLoss(mRssi, mTXPowerLevel);

        } else if (gattMessage == GAIAGATTBLEService.GattMessage.GATT_STATE) {
            @GAIAGATTBLEService.GattState int state = (int) content;
            switch (state) {
                case GAIAGATTBLEService.GattState.IN_USE_BY_SYSTEM:
//                    showSnackBar(R.color.snack_bar_warning, R.string.snack_bar_gatt_busy, true);
                    break;
                case GAIAGATTBLEService.GattState.DISCOVERING_SERVICES:
//                    showSnackBar(R.color.snack_bar_warning, R.string.snack_bar_discovering_gatt_services, false);
                    break;
            }
        } else if (gattMessage == GAIAGATTBLEService.GattMessage.BATTERY_LEVEL_UPDATE) {
            int instance = (int) content;
            updateBatteryLevels(instance);
        } else {

            Log.e("reali", "gattMessage " + gattMessage);

        }
    }

    private void updateBatteryLevels(int instance) {

        Log.e("reali", "NEW updateBatteryLevels");

        GattServiceBattery service = mService.getGattSupport().gattServiceBatteries.get(instance);
        // get the index of the Battery Service to know its place in the grid
        int index = mService.getGattSupport().gattServiceBatteries.indexOfKey(instance);

        // values shouldn't be empty and should match the Grid Layout information
        if (service != null && index >= 0) {// && index < mBatteryGrid.getChildCount()) {
            lastBatteryLevel = service.getBatteryLevel();
            Log.e("reali", "NEW " + lastBatteryLevel);
            sendRefreshBatteryLvlBroadcast(lastBatteryLevel);
            checkIfNeededNotify(lastBatteryLevel);
        }

    }

    private void checkIfNeededNotify(int batteryLevel) {

        if (isCharging) {

            if (audioApplication.sharedPref.readValue(Consts.SWITCH_HIGH_KEY, false)) {

                int val = audioApplication.sharedPref.readValue(Consts.SWITCH_HIGH_VALUE_KEY, 100);

                if (batteryLevel >= val) {
                    notificationDialog("Battery alert", getString(R.string.full_battery, batteryLevel + "%"), notifIdBattery, true, true, false);
                }

            }

        } else {

            if (audioApplication.sharedPref.readValue(Consts.SWITCH_LOW_KEY, false)) {

                int val = audioApplication.sharedPref.readValue(Consts.SWITCH_LOW_VALUE_KEY, 20);

                if (batteryLevel < val) {
                    notificationDialog("Battery alert", getString(R.string.low_battery_message), notifIdBattery, true, true, false);
                }

            }

        }

    }

    private void updatePathLoss(int rssi, int txPowerLevel) {
        mPathLoss = txPowerLevel - rssi;
        sendAlert();
    }

    private void onGattSupport(GATTServices services) {
        if (mService.getConnectionState() == BluetoothService.State.CONNECTED) {
            initInformation(services);
        }
    }

    private void initInformation(GATTServices support) {
        if (support != null && support.isGattProfileProximitySupported()) {
            // GATT service Link Loss is supported as it is mandatory for the Proximity profile
            mService.requestLinkLossAlertLevel();

            if (support.gattServiceimmediateAlert.isSupported() && support.gattServicetxPower.isSupported()
                    && isRssiSupported) {
                // if both services are supported they can be used
                mService.requestTxPowerLevel();
                getRSSINotifications(true);
            }
        }
    }

    boolean isNotificationSent = false;
    NotificationManager notificationManager;

//    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationDialog(String title, String content, int notifId, boolean led, boolean vibration, boolean sound) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);

            notificationChannel.setVibrationPattern(new long[]{0, 100, 50, 50});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setTicker("Tutorialspoint")
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(content);
//                .setContentInfo("Information");

        if (sound) {

            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getPackageName() + "/raw/alarm_beep");

//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
            r.play();
        }


        notificationManager.notify(notifId, notificationBuilder.build());
    }

    private void sendAlert() {

        if (!audioApplication.sharedPref.containsValue("NeverMissAlertDevice")) {
            audioApplication.sharedPref.writeValue("NeverMissAlertDevice", true);
        }

        boolean isNeverForget = audioApplication.sharedPref.readValue("NeverMissAlert", false);
        boolean isPhone = audioApplication.sharedPref.readValue("NeverMissAlertPhone", false);
        boolean isDevice = audioApplication.sharedPref.readValue("NeverMissAlertDevice", false);
        int distance = audioApplication.sharedPref.readValue("AlertDistance", 20);

        if (isNeverForget && (distance + 40) < mPathLoss && !isAlertSended) {

            if (isDevice) {
                isAlertSended = true;
                mGaiaManager.setLEDAlertState(2);
                mService.sendImmediateAlertLevel(GATT.AlertLevel.Levels.HIGH);
            }
            //onClickLedButton(true);
            if (!isNotificationSent && isPhone) {

                boolean led = audioApplication.sharedPref.readValue("NeverForgetFlash", false);
                boolean sound = audioApplication.sharedPref.readValue("NeverForgetRingtone", false);
                boolean vibration = audioApplication.sharedPref.readValue("NeverForgetVibro", false);

                notificationDialog("Never forget alert", "Device is far from you", notifIdNeverForget, led, vibration, sound);
                isNotificationSent = true;
            }
//            turnOnFlashSignals();
        } else if (isNeverForget && (distance + 40) >= mPathLoss) {

            if (isNotificationSent) {
                cancelNotification(notifIdNeverForget);
                isNotificationSent = false;
                //onClickLedButton(true);
            }

            if (isAlertSended) {
                isAlertSended = false;
                mService.sendImmediateAlertLevel(GATT.AlertLevel.Levels.NONE);
            }
        } else {
            if (isAlertSended) {
                isAlertSended = false;
                mService.sendImmediateAlertLevel(GATT.AlertLevel.Levels.NONE);
//                turnOffFlashSignals();
            }
        }

    }

    private void cancelNotification(int notifId) {
        notificationManager.cancel(notifId);
    }

    void displayLongToast(int textID) {
        Toast.makeText(this, textID, Toast.LENGTH_LONG).show();
    }

    private void refreshConnectionState(@BluetoothService.State int state, boolean notify) {
        // display the connection state
        int connectionLabel = state == BluetoothService.State.CONNECTING ? R.string.device_state_connecting
                : state == BluetoothService.State.DISCONNECTING ? R.string.device_state_disconnecting
                : state == BluetoothService.State.CONNECTED ? R.string.device_state_connected
                : state == BluetoothService.State.DISCONNECTED ? R.string.device_state_disconnected
                : R.string.device_state_connection_unknown;

        Log.e("reali", " " + connectionLabel);
        sendRefreshConnStateBroadcast(state);
        connState = state;

        switch (state) {
            case BluetoothService.State.CONNECTED:

                cancelNotification(notifIdConnection);

                sendSystemInfo();

                if (audioApplication.products != null && audioApplication.products.size() > 0) {
                    audioApplication.features = audioApplication.findProduct(blueDeviceName);
                    sendShowFeaturesBroadcast();
                } else {
                    getFeatures();
                }

                break;
            case BluetoothService.State.DISCONNECTED:
                notificationDialog("Connection", "Connection lost", notifIdConnection, true, true, false);
//                mService.reconnectToDevice();
                break;
            default:
                break;

        }

        // activate or deactivate buttons
        enableAllFeatures(state == BluetoothService.State.CONNECTED);

    }

    private void sendRefreshConnStateBroadcast(int state) {
        Intent i = new Intent(RefreshConnectionStateReceiver.REFRESH_CONNECTION_STATE);
        i.putExtra("state", state);
        sendBroadcast(i);
    }

    private void sendRefreshBatteryLvlBroadcast(int value) {
        Intent i = new Intent(RefreshBatteryLevelReceiver.REFRESH_BATTERY_LEVEL);
        i.putExtra("value", value);
        sendBroadcast(i);
    }

    private void sendShowFeaturesBroadcast() {
        Intent i = new Intent(ShowFeaturesReceiver.FEATURES_SHOW);
        sendBroadcast(i);
    }

    private void sendEnableNeverForgetBroadcast() {
        enableNeverForget = true;
        Intent i = new Intent(EnableNeverForgetReceiver.ENABLE_NEVER_FORGET);
        sendBroadcast(i);
    }

    private void sendSystemInfo() {

        String deviceAndroid = android.os.Build.MODEL;
        String op_sys = "Android " + Build.VERSION.RELEASE;

//        Log.e("reali", "deviceAndroid " + deviceAndroid + " op_sys " + op_sys);

        NetworkService.getInstance()
                .getJSONApi()
                .sendSystemInfo(deviceAddress.replaceAll(":", ""), blueDeviceName, deviceAndroid, op_sys)
                .enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(@NonNull Call<Status> call, @NonNull Response<Status> response) {

                        Status status = response.body();
                        Log.e("reali", "status " + status.getMsg());
                    }

                    @Override
                    public void onFailure(@NonNull Call<Status> call, @NonNull Throwable t) {

                        Log.e("reali", "sendSystemInfo Failure");
                        t.printStackTrace();
                    }
                });


    }

    private void getFeatures() {

//        new InternetCheck(internet -> {
//            Log.e("reali", "internet exist");
//        });

        NetworkService.getInstance()
                .getJSONApi()
                .getProductFeatures(blueDeviceName)
                .enqueue(new Callback<List<ProductFeatures>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ProductFeatures>> call, @NonNull Response<List<ProductFeatures>> response) {

//                        List<ProductFeatures> products = response.body();
//
//                        Log.e("reali", "getFeatures " + products.size());
//
//                        if (products.size() > 0) {
//                            Log.e("reali", "getFeatures " + products.get(0).getProduct_model() + " " + products.get(0).getAlert_led());
//                        }

                        audioApplication.products = response.body();

                        if (audioApplication.products != null && audioApplication.products.size() > 0) {
                            audioApplication.features = audioApplication.products.get(0);

                            sendShowFeaturesBroadcast();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ProductFeatures>> call, @NonNull Throwable t) {

                        Log.e("reali", "getFeatures onFailure");
//                        textView.append("Error occurred while getting request!");
                        t.printStackTrace();
                    }
                });

    }

    private void enableAllFeatures(boolean enabled) {
        if (enabled) {
            enableGaiaFeatures();
            if (mService != null) {
                enableGattFeatures(mService.getGattSupport());
            }
        }
        else {
//            enableChildView(mLayoutFeatures, false);
        }
    }

    private void enableGaiaFeatures() {
//        tv_led.setTextColor(Color.GREEN);
//        tv_eqw.setTextColor(Color.GREEN);
//        tv_remote.setTextColor(Color.GREEN); todo remote
//        tv_upg.setTextColor(Color.GREEN); todo remote

//        if (mGAIAFeatures[MainGaiaManager.Features.LED]) {
////            IconAlertPref
//        }

//        layoutAlertPref.setClickable(mGAIAFeatures[MainGaiaManager.Features.LED]);
//        layoutBassBooster.setClickable(mGAIAFeatures[MainGaiaManager.Features.EQUALIZER]);
    }

    private class ActivityHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            handleMessageFromService(msg);
        }
    }

}
