package com.zitech.audio.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qualcomm.qti.libraries.gaia.GAIA;
import com.zitech.audio.R;
import com.zitech.audio.application.audioApplication;
import com.zitech.audio.gaia.CustomEqualizerGaiaManager;
import com.zitech.audio.gaia.EqualizerGaiaManager;
import com.zitech.audio.models.equalizer.Band;
import com.zitech.audio.models.equalizer.Bank;
import com.zitech.audio.models.equalizer.parameters.Filter;
import com.zitech.audio.models.equalizer.parameters.Parameter;
import com.zitech.audio.models.equalizer.parameters.ParameterType;
import com.zitech.audio.services.BluetoothService;
import com.zitech.audio.utils.Consts;
import com.zitech.audio.utils.EQListAdapter;
import com.zitech.audio.utils.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Equalizer extends ServiceActivity implements SliderLayout.SliderListener, EqualizerGaiaManager.GaiaManagerListener,
        CustomEqualizerGaiaManager.GaiaManagerListener {

    @BindView(R.id.Toolbar_Top)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.seekbar_1)
    SliderLayout seekBar1;

    @BindView(R.id.seekbar_2)
    SliderLayout seekBar2;

    @BindView(R.id.seekbar_3)
    SliderLayout seekBar3;

    @BindView(R.id.seekbar_4)
    SliderLayout seekBar4;

    @BindView(R.id.seekbar_5)
    SliderLayout seekBar5;

    @BindView(R.id.layout_equalizer_1)
    LinearLayout layout1;

    @BindView(R.id.text_seek1)
    TextView text_seek1;

    @BindView(R.id.text_seek2)
    TextView text_seek2;

    @BindView(R.id.text_seek3)
    TextView text_seek3;

    @BindView(R.id.text_seek4)
    TextView text_seek4;

    @BindView(R.id.text_seek5)
    TextView text_seek5;
    private int value1, value2, value3, value4, value5;
    private int value1s, value2s, value3s, value4s, value5s;
    private boolean setUserData = false;

//    private ArrayAdapter adapter;
    EQListAdapter adapter1;
    private List<String> bandsList;

    @BindView(R.id.bandsList)
    ListView listView;
    int selected;

    // ====== CONSTS ===============================================================================

    /**
     * For the debug mode, the tag to display for logs.
     */
    private static final String TAG = "EqualizerActivity";
    /**
     * To manage the GAIA packets which has been received from the device and which will be send to the device.
     */

    private EqualizerGaiaManager mGaiaManager;
    private CustomEqualizerGaiaManager mCustomEqualizerManager;
    /**
     * All the values displayed to the user for the selected band.
     */
    private final Bank mBank = new Bank(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbarTitle.setText(audioApplication.sharedPref.readValue(Consts.CURRENT_DEVICE_NAME_KEY, "audio"));

        initObj();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position >= 7) {
                    final TextView tv = (TextView) view.findViewById(R.id.text123);
                    final ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btn_delete);
                    final ImageButton btnEdit = (ImageButton) view.findViewById(R.id.btn_edit);
                    btnDelete.setVisibility(ImageButton.VISIBLE);
                    btnEdit.setVisibility(ImageButton.VISIBLE);
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //change the saved custom eq index
                            Log.i(TAG, "EQULIZER onClick delete ");
                            if (position == audioApplication.lastEqulizerValue) {
                                audioApplication.lastEqulizerValue--;
                            } else {
                                int index = position;
                                for (int i = position + 1; i < audioApplication.lastEqulizerValue; i++) {
                                    String val = audioApplication.sharedPref.readValue(mService.getDevice().getAddress() + "_" + i, "");
                                    audioApplication.sharedPref.writeValue(mService.getDevice().getAddress() + "_" + index, val);
                                    index++;
                                }
                                audioApplication.lastEqulizerValue--;
                                audioApplication.sharedPref.writeValue("LAST_EQ_INDX", audioApplication.lastEqulizerValue);
                            }
                            bandsList.remove(position);
                            displayLongToast(tv.getText() + " deleted!");
                            btnDelete.setVisibility(ImageButton.GONE);
                            btnEdit.setVisibility(ImageButton.GONE);


                        }
                    });

                    btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("EqualizerActivity", "EQULIZER onItemClick: " + position);
                            getExtras(position, 1, tv.getText().toString());
                            btnDelete.setVisibility(ImageButton.GONE);
                            btnEdit.setVisibility(ImageButton.GONE);
                        }
                    });

                    adapter1.notifyDataSetChanged();
                }
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.text123);
                audioApplication.sharedPref.writeValue(mService.getDevice().getAddress() + "_selected", "" + position);
                if (position < 7) {
                    if (position == 0) {
                        mGaiaManager.setPreset(1);
                        getExtras(-1, 1, "");
//                        Intent intent = new Intent(Equalizer.this, Equalizer.class);
//                        intent.putExtra("bankIndex", -1);
//                        startActivity(intent);
                    } else {
                        int freq[] = {180, 690, 2714, 10837, 41951};
                        for (int i = 0; i < listView.getChildCount(); i++) {
                            View listItem = listView.getChildAt(i);
                            TextView tv1 = (TextView) listItem.findViewById(R.id.text123);
                            tv1.setTextColor(Color.WHITE);
                        }
                        getExtras(position, 1, "");
                        tv.setTextColor(getResources().getColor(R.color.themeYellow));
                        switch (position) {
                            case 1: {
                                int value[] = {240, 58, 538, 182, 0};
                                ActivatePreset(value, freq);
                                Toast.makeText(Equalizer.this, tv.getText() + " activated!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 2: {
                                int value[] = {240, 123, -117, 123, 298};
                                ActivatePreset(value, freq);
                                Toast.makeText(Equalizer.this, tv.getText() + " activated!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 3: {
                                int value[] = {298, 182, -58, 182, 298};
                                ActivatePreset(value, freq);
                                Toast.makeText(Equalizer.this, tv.getText() + " activated!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 4: {
                                int value[] = {357, 0, 123, -117, 58};
                                ActivatePreset(value, freq);
                                Toast.makeText(Equalizer.this, tv.getText() + " activated!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 5: {
                                int value[] = {-58, 123, 298, 58, -117};
                                ActivatePreset(value, freq);
                                Toast.makeText(Equalizer.this, tv.getText() + " activated!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 6: {
                                int value[] = {240, 58, 538, 182, 0};
                                ActivatePreset(value, freq);
                                Toast.makeText(Equalizer.this, tv.getText() + " activated!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                    }
                } else {

                    int freq[] = {180, 690, 2714, 10837, 41951};
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        View listItem = listView.getChildAt(i);
                        TextView tv1 = (TextView) listItem.findViewById(R.id.text123);
                        tv1.setTextColor(Color.WHITE);
                    }
                    tv.setTextColor(getResources().getColor(R.color.themeYellow));
                    String val = audioApplication.sharedPref.readValue(mService.getDevice().getAddress() + "_" + position, "");
                    if (!val.equals("")) {

                        String[] splited = val.split("_");

                        int value[] = {Integer.parseInt(splited[1]), Integer.parseInt(splited[2]), Integer.parseInt(splited[3]), Integer.parseInt(splited[4]), Integer.parseInt(splited[5])};
                        Log.i("EqualizerActivity", "EQULIZER activating: " + tv.getText() + value[0]+", "+value[1]+", "+value[2]+", "+value[3]+", "+value[4]);
                        ActivatePreset(value, freq);
                        Toast.makeText(Equalizer.this, tv.getText() + " activated!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(Equalizer.this, "Something is wrong!", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void ActivatePreset(int[] value, int freq[]) {

        Log.e("reali", "ActivatePreset");

        for (int i = 1; i <= 5; i++) {

            Log.e("reali", "i " + i);

            mBank.setCurrentBand(i);
            mBank.getBand(i).hasToBeUpdated();
            // filter
            mCustomEqualizerManager.getEQParameter(i, ParameterType.FILTER.ordinal());
            Filter filter = Filter.PARAMETRIC_EQUALIZER;
            mBank.getBand(i).setFilter(filter, true);
            Parameter parameter = mBank.getBand(i).getGain();
            parameter.setValue(value[i - 1]);
            ParameterType parameterType = parameter.getParameterType();
            int parameterValue = (parameterType != null) ? parameterType.ordinal()
                    : CustomEqualizerGaiaManager.PARAMETER_MASTER_GAIN;
            // ParameterType.FILTER.ordinal();
            Log.i(TAG, "EQULIZER onStopTrackingTouch: changing band " + i + " gain value to " + parameter.getLabelValue() + " raw is " + parameter.getValue());
            mCustomEqualizerManager.setEQParameter(i, parameterValue, parameter.getValue());


            Parameter parameter2 = mBank.getBand(i).getFrequency();
            parameter2.setValue(freq[i - 1]);
            ParameterType parameterType2 = parameter2.getParameterType();
            int parameterValue2 = (parameterType2 != null) ? parameterType2.ordinal()
                    : CustomEqualizerGaiaManager.PARAMETER_MASTER_GAIN;
            // ParameterType.FILTER.ordinal();
            Log.i(TAG, "EQULIZER onStopTrackingTouch: changing band " + i + " freq bound is " + parameter2.getLabelMaxBound() + " value to " + parameter2.getLabelValue() + " raw is " + parameter2.getValue());
            mCustomEqualizerManager.setEQParameter(i, parameterValue2, parameter2.getValue());

        }

    }

    private void initObj() {
        bandsList = new ArrayList<>();
        //adapter = new ArrayAdapter(this, R.layout.item_simple_text_1, bandsList);
        adapter1 = new EQListAdapter(this, R.layout.item_simple_text_1, bandsList);
        listView.setAdapter(adapter1);
    }

    private void getExtras(int index, int isForEdit, String customEQ_name) {

        Log.e(TAG, "EQULIZER getExtras");

        if (index == -1) {
            setUserData = false;
            Log.i(TAG, "EQULIZER No Extras: ");

        } else {

            String str = audioApplication.sharedPref.readValue(mService.getDevice().getAddress() + "_" + index, "0");
            if (!str.equals("0")) {
                setUserData = true;
                String str2 = audioApplication.sharedPref.readValue(mService.getDevice().getAddress() + "_" + index + "_Label", "0");
                String[] splited_values = str2.split("_");
                String[] splited = str.split("_");
                value1 = Integer.parseInt(splited[1]);
                value2 = Integer.parseInt(splited[2]);
                value3 = Integer.parseInt(splited[3]);
                value4 = Integer.parseInt(splited[4]);
                value5 = Integer.parseInt(splited[5]);

                seekBar1.setSliderPosition(value1);
                seekBar2.setSliderPosition(value2);
                seekBar3.setSliderPosition(value3);
                seekBar4.setSliderPosition(value4);
                seekBar5.setSliderPosition(value5);

                Log.i(TAG, "EQULIZER getExtras: " + str2);
                //setting text view values of label frequency which we previously saved
                text_seek1.setText(splited_values[1]);
                text_seek2.setText(splited_values[2]);
                text_seek3.setText(splited_values[3]);
                text_seek4.setText(splited_values[4]);
                text_seek5.setText(splited_values[5]);
                Log.i(TAG, "EQULIZER getExtras: " + value1 + ", " + value2 + ", " + value3 + ", " + value4 + ", " + value5);
            } else
                Log.i(TAG, "EQULIZER No Extras: ");


        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mService != null) {
            updateDataInList();
        }
        for (int i = 1; i <= 5; i++) {
            getInformation(i);
        }
    }

    private String getName(String str) {
        String[] splited = str.split("_");
        return splited[0];
    }

    private void updateDataInList() {

        bandsList.clear();
        bandsList.add("Custom");
        bandsList.add(getResources().getString(R.string.heavy_metal));
        bandsList.add(getResources().getString(R.string.jazz));
        bandsList.add(getResources().getString(R.string.rock));
        bandsList.add(getResources().getString(R.string.dance));
        bandsList.add(getResources().getString(R.string.pop));
        bandsList.add(getResources().getString(R.string.folk));

        Log.e("reali", "!!! updateDataInList");

        if (audioApplication.lastEqulizerValue > -1) {
            int index = audioApplication.lastEqulizerValue + 1;
            for (int i = 0; i < index; i++) {
                String str = audioApplication.sharedPref.readValue(
                        mService.getDevice().getAddress() + "_" + i, "0");
                if (!str.equals("0")) {
                    bandsList.add(getName(str));
                }
            }
        }

        adapter1.notifyDataSetChanged();
        selected = Integer.parseInt(audioApplication.sharedPref.readValue(mService.getDevice().getAddress() + "_selected", "1"));
        View listItem = listView.getChildAt(selected);
        if (listItem != null) {
            TextView tv1 = (TextView) listItem.findViewById(R.id.text123);
            tv1.setTextColor(getResources().getColor(R.color.themeYellow));
        } else
            Log.i(TAG, "EQULIZER item is  " + selected + " is null size is " + listView.getChildCount());
        //adapter.notifyDataSetChanged();
    }

    // ====== SERVICE METHODS =======================================================================

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
                if (DEBUG)
                    Log.d(TAG, handleMessage + "CONNECTION_STATE_HAS_CHANGED: " + stateLabel);
                break;

            case BluetoothService.Messages.DEVICE_BOND_STATE_HAS_CHANGED:
                int bondState = (int) msg.obj;
                String bondStateLabel = bondState == BluetoothDevice.BOND_BONDED ? "BONDED"
                        : bondState == BluetoothDevice.BOND_BONDING ? "BONDING"
                        : "BOND NONE";
                displayLongToast(getString(R.string.toast_device_information) + bondStateLabel);
                if (DEBUG)
                    Log.d(TAG, handleMessage + "DEVICE_BOND_STATE_HAS_CHANGED: " + bondStateLabel);
                break;

            case BluetoothService.Messages.GATT_SUPPORT:
                if (DEBUG) Log.d(TAG, handleMessage + "GATT_SUPPORT");
                break;

            case BluetoothService.Messages.GAIA_PACKET:
                byte[] data = (byte[]) msg.obj;
                mCustomEqualizerManager.onReceiveGAIAPacket(data);
                mGaiaManager.onReceiveGAIAPacket(data);
                break;

            case BluetoothService.Messages.GAIA_READY:

                for (int i = 1; i <= 5; i++) {
                    getInformation(i);
                }
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
        mCustomEqualizerManager = new CustomEqualizerGaiaManager(this, transport);
        mGaiaManager = new EqualizerGaiaManager(this, transport);

//        initObj();
        updateDataInList();

        mGaiaManager.setActivationState(EqualizerGaiaManager.Controls.PRESETS, true);
        mGaiaManager.getPreset();

        //initSettingsComponents();

//        getExtras();
        for (int i = 1; i <= 5; i++) {
            getInformation(i);
        }
        setFrequesncies();
    }

    @Override // ServiceActivity
    protected void onServiceDisconnected() {

    }

    ///////////////////// UI LISTENERS//////////////////////////

    @OnClick(R.id.iv_equalizer_vc)
    void setVoulume() {
//        dialogAdjustVolume();
    }

    @OnClick(R.id.btn_equalizer_save)
    void setSave() {

//        int indexToSave=0;
//        if(isForEdit==1){
//            indexToSave=index;
//            int value1 = value1s;
//            int value2 = value2s;
//            int value3 = value3s;
//            int value4 = value4s;
//            int value5 = value5s;
//            audioApplication.sharedPref.writeValue(mService.getDevice().getAddress() + "_" + indexToSave, customEQ_name + "_" + value1 + "_" + value2 + "_" + value3 + "_" + value4 + "_" + value5);
//            audioApplication.sharedPref.writeValue(mService.getDevice().getAddress() + "_" + indexToSave + "_Label", customEQ_name + "_" + text_seek1.getText() + "_" + text_seek2.getText() + "_" + text_seek3.getText() + "_" + text_seek4.getText() + "_" + text_seek5.getText());
//            audioApplication.sharedPref.writeValue("LAST_EQ_INDX", audioApplication.lastEqulizerValue);
//            finish();
//        } else {
//            dialogSave();
//        }


    }

    // ====== GAIA MANAGER METHODS =======================================================================


    @Override // EqualizerGaiaManager.GaiaManagerListener
    public boolean sendGAIAPacket(byte[] packet) {
        return mService != null && mService.isGaiaReady()
                && mService.sendGAIAPacket(packet);
    }

    private void selectPreset(int selected) {
    }

    @Override
    public void onGetPreset(int preset) {
        selectPreset(preset);
    }

    @Override
    public void onGetControlActivationState(int control, boolean activated) {
        switch (control) {
            case EqualizerGaiaManager.Controls.PRESETS:
                refreshPresetsDisplay(activated);
                listView.setVisibility(View.VISIBLE);
                selected = Integer.parseInt(audioApplication.sharedPref.readValue(mService.getDevice().getAddress() + "_selected", "1"));
                View listItem = listView.getChildAt(selected);
                if (listItem != null) {
                    TextView tv1 = (TextView) listItem.findViewById(R.id.text123);
                    tv1.setTextColor(getResources().getColor(R.color.themeYellow));
                } else
                    Log.i(TAG, "EQULIZER item is  " + selected + " is null size is " + listView.getChildCount());
                break;
        }
    }

    private void refreshPresetsDisplay(boolean activated) {
        if (activated) {
            mGaiaManager.getPreset();
            listView.setVisibility(View.VISIBLE);
        } else {
//            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onControlNotSupported(int control) {
        switch (control) {
            case EqualizerGaiaManager.Controls.PRESETS:
//                switchEqualizer.setEnabled(false);
                break;
        }
    }

    @Override // EqualizerGaiaManager.GaiaManagerListener
    public void onControlNotSupported() {
        displayLongToast(R.string.customization_not_supported);
        finish();
    }

    @Override // EqualizerGaiaManager.GaiaManagerListener
    public void onGetMasterGain(int value) {
    }

    @Override // EqualizerGaiaManager.GaiaManagerListener
    public void onGetFilter(int band, Filter filter) {
    }

    @Override // EqualizerGaiaManager.GaiaManagerListener
    public void onGetFrequency(int band, int value) {
    }

    @Override // EqualizerGaiaManager.GaiaManagerListener
    public void onGetGain(int band, int value) {
        Log.i(TAG, "EQULIZER onGetGain: ");
        //refreshParameterValue(band, value, mBank.getBand(band).getGain());
    }

    @Override // EqualizerGaiaManager.GaiaManagerListener
    public void onGetQuality(int band, int value) {
    }

    @Override // EqualizerGaiaManager.GaiaManagerListener
    public void onIncorrectState() {
    }


    @Override // SliderLayout.SliderListener
    public void onProgressChangedByUser(int progress, int id) {
        Parameter parameter = null;
        SliderLayout sliderLayout = null;
        TextView tv = null;
        switch (id) {
            case R.id.seekbar_1:
                parameter = mBank.getBand(1).getGain();
                sliderLayout = seekBar1;
                tv = text_seek1;
                break;
            case R.id.seekbar_2:
                parameter = mBank.getBand(2).getGain();
                sliderLayout = seekBar2;
                tv = text_seek2;
                break;
            case R.id.seekbar_3:
                parameter = mBank.getBand(3).getGain();
                sliderLayout = seekBar3;
                tv = text_seek3;
                break;
            case R.id.seekbar_4:
                parameter = mBank.getBand(4).getGain();
                sliderLayout = seekBar4;
                tv = text_seek4;
                break;
            case R.id.seekbar_5:
                parameter = mBank.getBand(5).getGain();
                sliderLayout = seekBar5;
                tv = text_seek5;
                break;
        }

        if (parameter != null && sliderLayout != null) {
            parameter.setValueFromProportion(progress);
            Log.i(TAG, "EQULIZER onProgressChangedByUser: " + parameter.getLabelValue() + " raw is " + parameter.getValue());
            tv.setText(parameter.getLabelValue());
            updateDisplayParameterValue(sliderLayout, parameter);
        }
    }

    @Override // SliderLayout.SliderListener
    public void onStopTrackingTouch(int progress, int id) {
        Parameter parameter = null;
        SliderLayout sliderLayout = null;
        int band = 1;
        switch (id) {
            case R.id.seekbar_1:
                parameter = mBank.getBand(1).getGain();
                sliderLayout = seekBar1;
                band = 1;

                break;
            case R.id.seekbar_2:
                parameter = mBank.getBand(2).getGain();
                sliderLayout = seekBar2;
                band = 2;
                break;
            case R.id.seekbar_3:
                parameter = mBank.getBand(3).getGain();
                sliderLayout = seekBar3;
                band = 3;
                break;
            case R.id.seekbar_4:
                parameter = mBank.getBand(4).getGain();
                sliderLayout = seekBar4;
                band = 4;
                break;
            case R.id.seekbar_5:
                parameter = mBank.getBand(5).getGain();
                sliderLayout = seekBar5;
                band = 5;
                break;
        }

        if (parameter != null && sliderLayout != null) {
            parameter.setValueFromProportion(progress);
            updateDisplayParameterValue(sliderLayout, parameter);
            ParameterType parameterType = parameter.getParameterType();
            int parameterValue = (parameterType != null) ? parameterType.ordinal()
                    : CustomEqualizerGaiaManager.PARAMETER_MASTER_GAIN;
            // ParameterType.FILTER.ordinal();
            switch (id){
                case R.id.seekbar_1:
                {
                    value1s= parameter.getValue();
                    break;
                }
                case R.id.seekbar_2:
                {
                    value2s= parameter.getValue();
                    break;
                }
                case R.id.seekbar_3:
                {
                    value3s= parameter.getValue();
                    break;
                }
                case R.id.seekbar_4:
                {
                    value4s= parameter.getValue();
                    break;
                }
                case R.id.seekbar_5:
                {
                    value5s= parameter.getValue();
                    break;
                }

            }
            Log.i(TAG, "EQULIZER onStopTrackingTouch: saving "+"_" + value1s + "_" + value2s + "_" + value3s + "_" + value4s + "_" + value5s);
            Log.i(TAG, "EQULIZER onStopTrackingTouch: changing band " + band + " value to " + parameter.getLabelValue() + " raw is " + parameter.getValue());
            mCustomEqualizerManager.setEQParameter(band, parameterValue, parameter.getValue());
        }
    }

    private void updateDisplayParameterValue(SliderLayout sliderLayout, Parameter parameter) {
        if (parameter.isConfigurable()) {
            sliderLayout.setEnabled(true);
            sliderLayout.setSliderPosition(parameter.getPositionValue());
            sliderLayout.displayValue(parameter.getLabelValue());
        } else {
            sliderLayout.setEnabled(false);
            sliderLayout.displayValue(parameter.getLabelValue());
            displayLongToast("can't change the value");
        }
    }

    private void getInformation(int bandNumber) {
        if (mService != null && mService.isGaiaReady()) {
            // define the new band
            mBank.setCurrentBand(bandNumber);
            mBank.getBand(bandNumber).hasToBeUpdated();
            // filter
            mCustomEqualizerManager.getEQParameter(bandNumber, ParameterType.FILTER.ordinal());

            Band band = mBank.getBand(bandNumber);
            Filter filter = Filter.PARAMETRIC_EQUALIZER;
            band.setFilter(filter, true);
            SliderLayout sliderLayout = null;
            int value = band.getGain().getValue();
            switch (bandNumber) {
                case 1:
                    sliderLayout = seekBar1;
                    if (setUserData) {
                        value = value1;

                    }
                    break;
                case 2:
                    sliderLayout = seekBar2;
                    if (setUserData) {
                        value = value2;
                    }
                    break;
                case 3:
                    sliderLayout = seekBar3;
                    if (setUserData) {
                        value = value3;
                    }
                    break;
                case 4:
                    sliderLayout = seekBar4;
                    if (setUserData) {
                        value = value4;
                    }
                    break;
                case 5:
                    sliderLayout = seekBar5;
                    if (setUserData) {
                        value = value5;
                    }
                    break;
            }
            if (sliderLayout != null) {
                sliderLayout.initialize(getString(R.string.gain_title),
                        band.getGain().getLabelValue(), this);
                sliderLayout.setSliderBounds(band.getGain().getBoundsLength());
                sliderLayout.setSliderPosition(value);
                Log.i(TAG, "EQULIZER initSettingsComponents: set max bound of band " + bandNumber + " " + band.getGain().getLabelMaxBound() + " position value is " + value);

            }

        }
    }

    private void setFrequesncies(){
        int freq[] = {180, 690, 2714, 10837, 41951};
        for (int i = 1; i <= 5; i++) {
            Parameter parameter2 = mBank.getCurrentBand().getFrequency();
            parameter2.setValue(freq[i - 1]);
            ParameterType parameterType2 = parameter2.getParameterType();
            int parameterValue2 = (parameterType2 != null) ? parameterType2.ordinal()
                    : CustomEqualizerGaiaManager.PARAMETER_MASTER_GAIN;
            // ParameterType.FILTER.ordinal();
            Log.i(TAG, "EQULIZER onStopTrackingTouch: changing band " + i + " freq bound is "+parameter2.getLabelMaxBound()+" value to " + parameter2.getLabelValue() + " raw is " + parameter2.getValue());
            mCustomEqualizerManager.setEQParameter(i, parameterValue2, parameter2.getValue());

        }
    }
//    private void dialogSave() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                final AlertDialog.Builder alert = new AlertDialog.Builder(Equalizer.this);
//                final LayoutInflater inflater = getLayoutInflater();
//                final View view = inflater.inflate(R.layout.dailog_layout_single_input, null);
//                TextView tvTitle = view.findViewById(R.id.tv_title_input_dailog);
//                tvTitle.setText(mService.getDevice().getName());
//                final EditText et = view.findViewById(R.id.et_input_dialog);
//                Button btn = view.findViewById(R.id.btn_ok_input_dialog);
//                alert.setView(view);
//                final AlertDialog b = alert.create();
//                b.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                b.show();
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!et.getText().toString().isEmpty()) {
//                            int indexToSave=6;
//                            audioApplication.lastEqulizerValue++;
//                            indexToSave=audioApplication.lastEqulizerValue;
//
//                            int value1 = value1s;
//                            int value2 = value2s;
//                            int value3 = value3s;
//                            int value4 = value4s;
//                            int value5 = value5s;
//                            audioApplication.sharedPref.writeValue(mService.getDevice().getAddress() + "_" + indexToSave, et.getText().toString() + "_" + value1 + "_" + value2 + "_" + value3 + "_" + value4 + "_" + value5);
//                            audioApplication.sharedPref.writeValue(mService.getDevice().getAddress() + "_" + indexToSave + "_Label", et.getText().toString() + "_" + text_seek1.getText() + "_" + text_seek2.getText() + "_" + text_seek3.getText() + "_" + text_seek4.getText() + "_" + text_seek5.getText());
//                            audioApplication.sharedPref.writeValue("LAST_EQ_INDX", audioApplication.lastEqulizerValue);
//                            Log.i(TAG, "EQULIZER onClick: saved at " + audioApplication.lastEqulizerValue);
//                            Log.i("EqualizerActivity", "EQULIZER activating: " + et.getText() +  "_" + value1 + "_" + value2 + "_" + value3 + "_" + value4 + "_" + value5);
//                            b.dismiss();
//                            finish();
//                        } else {
//                            Toast.makeText(Equalizer.this, "Please enter name.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//    }
}
