package com.zitech.audio.gaia;

import android.util.Log;

import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import com.zitech.audio.utils.Consts;

public class LEDGaiaManager extends AGaiaManager {
    /**
     * <p>To represent the boolean value <code>true</code> as a payload of one parameter for GAIA commands.</p>
     */
    private static final byte[] PAYLOAD_BOOLEAN_TRUE = { 0x01 };
    /**
     * <p>To represent the boolean value <code>false</code> as a payload of one parameter for GAIA commands.</p>
     */
    private static final byte[] PAYLOAD_BOOLEAN_FALSE = { 0x00 };
    private final String TAG = "LEDGaiaManager";
    private final LEDGaiaManagerListener mListener;

    public LEDGaiaManager(LEDGaiaManagerListener myListener, @GAIA.Transport int transport) {
        super(transport);
        this.mListener = myListener;
    }

    @Override
    protected void receiveSuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        Log.i(TAG, "LED GAIA  receiveSuccessfulAcknowledgement: ");
        receiveACK(gaiaPacket);
    }

    @Override
    protected void receiveUnsuccessfulAcknowledgement(GaiaPacket gaiaPacket) {
        Log.i(TAG, "LED GAIA receiveUnsuccessfulAcknowledgement: ");
    }

    @Override
    protected boolean manageReceivedPacket(GaiaPacket gaiaPacket) {
        Log.i(TAG, "LED GAIA manageReceivedPacket: ");
        return false;
    }

    @Override
    protected void hasNotReceivedAcknowledgementPacket(GaiaPacket gaiaPacket) {
        Log.i(TAG, "LED GAIA  hasNotReceivedAcknowledgementPacket: ");
    }

    @Override
    protected void onSendingFailed(GaiaPacket gaiaPacket) {
        Log.i(TAG, "LED GAIA onSendingFailed: ");
    }
    @Override
    protected boolean sendGAIAPacket(byte[] bytes) {
        return false;
    }

    public void setLEDAlertCommand(boolean activate) {
        byte[] payload = activate ? PAYLOAD_BOOLEAN_TRUE : PAYLOAD_BOOLEAN_FALSE;
        createRequest(createPacket(GAIA.COMMAND_ALERT_LEDS, payload));
    }
    public void getVoiceCommand() {
        createRequest(createPacket(GAIA.COMMAND_GET_LED_CONFIGURATION));
    }
    private void receiveACK (GaiaPacket packet) {
        byte[] payload = packet.getPayload();
        final int PAYLOAD_VALUE_OFFSET = 1;
        final int PAYLOAD_VALUE_LENGTH = 1;
        final int PAYLOAD_MIN_LENGTH = PAYLOAD_VALUE_LENGTH + 1; // ACK status length is 1

        if (payload.length >= PAYLOAD_MIN_LENGTH) {
            boolean activate = payload[PAYLOAD_VALUE_OFFSET] == 0x01;
            mListener.operationFeedback(activate);
        }
    }


    // ====== INTERFACES ===========================================================================

    /**
     * <p>This interface allows this manager to dispatch messages or events to a listener.</p>
     */
    public interface LEDGaiaManagerListener {
        //boolean sendLEDGAIAPacket(byte[] packet);
        void operationFeedback(boolean isSuccessful);
    }
    // ====== INNER CLASSES ===========================================================================


}
