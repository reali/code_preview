package com.zitech.audio.activities;

import android.bluetooth.BluetoothDevice;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.qualcomm.qti.libraries.gaia.GAIA;
import com.zitech.audio.R;
import com.zitech.audio.application.audioApplication;
import com.zitech.audio.gaia.EqualizerGaiaManager;
import com.zitech.audio.gaia.VoiceCommandGaiaManager;
import com.zitech.audio.services.BluetoothService;
import com.zitech.audio.utils.Consts;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class VoiceCommand extends ServiceActivity implements VoiceCommandGaiaManager.GaiaManagerListener  {
    @BindView(R.id.Toolbar_Top)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.switch_voice_command)
    Switch switchVoiceCommand;
    private VoiceCommandGaiaManager mGaiaManager;
    private String TAG = "VoiceCommand";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_command);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
        toolbarTitle.setText(audioApplication.sharedPref.readValue(Consts.CURRENT_DEVICE_NAME_KEY, "audio"));
        switchVoiceCommand.setChecked(audioApplication.sharedPref.readValue("VoiceCommand", false));
    }

    @OnCheckedChanged(R.id.switch_voice_command)void setVoiceCommand(boolean isChecked){
        audioApplication.sharedPref.writeValue("VoiceCommand", isChecked);
        if(mGaiaManager!=null)
            mGaiaManager.setVoiceCommand(isChecked);
    }

    @Override // ServiceActivity
    protected void handleMessageFromService(Message msg) {
        //noinspection UnusedAssignment
        String handleMessage = "Handle a message from BLE service: ";

        switch (msg.what) {
            case BluetoothService.Messages.CONNECTION_STATE_HAS_CHANGED:
                @BluetoothService.State int connectionState = (int) msg.obj;
                String stateLabel = connectionState == BluetoothService.State.CONNECTED ? "CONNECTED"
                        : connectionState == BluetoothService.State.CONNECTING ? "CONNECTING"
                        : connectionState == BluetoothService.State.DISCONNECTING ? "DISCONNECTING"
                        : connectionState == BluetoothService.State.DISCONNECTED ? "DISCONNECTED"
                        : "UNKNOWN";
                displayLongToast(getString(R.string.toast_device_information) + stateLabel);
                if (DEBUG) Log.d(TAG, handleMessage + "CONNECTION_STATE_HAS_CHANGED: " + stateLabel);
                break;

            case BluetoothService.Messages.DEVICE_BOND_STATE_HAS_CHANGED:
                int bondState = (int) msg.obj;
                String bondStateLabel = bondState == BluetoothDevice.BOND_BONDED ? "BONDED"
                        : bondState == BluetoothDevice.BOND_BONDING ? "BONDING"
                        : "BOND NONE";
                displayLongToast(getString(R.string.toast_device_information) + bondStateLabel);
                if (DEBUG) Log.d(TAG, handleMessage + "DEVICE_BOND_STATE_HAS_CHANGED: " + bondStateLabel);
                break;

            case BluetoothService.Messages.GATT_SUPPORT:
                if (DEBUG) Log.d(TAG, handleMessage + "GATT_SUPPORT");
                break;

            case BluetoothService.Messages.GAIA_PACKET:
                byte[] data = (byte[]) msg.obj;
                mGaiaManager.onReceiveGAIAPacket(data);
                break;

            case BluetoothService.Messages.GAIA_READY:
                getInformation();
                if (DEBUG) Log.d(TAG, handleMessage + "GAIA_READY");
                break;

            case BluetoothService.Messages.GATT_READY:
                if (DEBUG) Log.d(TAG, handleMessage + "GATT_READY");
                break;

            default:
                if (DEBUG)
                    Log.d(TAG, handleMessage + "UNKNOWN MESSAGE: " + msg.what);
                break;
        }
    }

    @Override // ServiceActivity
    protected void onServiceConnected() {
        @GAIA.Transport int transport = getTransport() == BluetoothService.Transport.BR_EDR ?
                GAIA.Transport.BR_EDR : GAIA.Transport.BLE;
        mGaiaManager = new VoiceCommandGaiaManager(this, transport);
        getInformation();
    }

    @Override // ServiceActivity
    protected void onServiceDisconnected() {

    }

    @Override
    public boolean sendGAIAPacket(byte[] packet) {
        return mService!= null && mService.isGaiaReady()
                && mService.sendGAIAPacket(packet);
    }

    @Override
    public void operationFeedback(boolean isSuccessful) {
        switchVoiceCommand.setChecked(isSuccessful);
    }

    private void getInformation() {
        mGaiaManager.getVoiceCommand();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
