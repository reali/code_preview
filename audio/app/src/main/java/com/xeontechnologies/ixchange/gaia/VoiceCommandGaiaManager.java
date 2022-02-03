/* ************************************************************************************************
 * Copyright 2017 Qualcomm Technologies International, Ltd.                                       *
 **************************************************************************************************/


package com.zitech.audio.gaia;

import com.qualcomm.qti.libraries.gaia.GAIA;
import com.qualcomm.qti.libraries.gaia.packets.GaiaPacket;
import com.zitech.audio.utils.Consts;


public class VoiceCommandGaiaManager extends AGaiaManager {

    // ====== STATIC FIELDS =======================================================================

    /**
     * To know if we are using the application in the debug mode.
     */
    @SuppressWarnings("unused")
    private static final boolean DEBUG = Consts.DEBUG;
    /**
     * <p>To represent the boolean value <code>true</code> as a payload of one parameter for GAIA commands.</p>
     */
    private static final byte[] PAYLOAD_BOOLEAN_TRUE = { 0x01 };
    /**
     * <p>To represent the boolean value <code>false</code> as a payload of one parameter for GAIA commands.</p>
     */
    private static final byte[] PAYLOAD_BOOLEAN_FALSE = { 0x00 };

    private final String TAG = "EqualizerGaiaManager";

    private final GaiaManagerListener mListener;

    public VoiceCommandGaiaManager(GaiaManagerListener myListener, @GAIA.Transport int transport) {
        super(transport);
        this.mListener = myListener;
    }

    public void setVoiceCommand(boolean activate) {
        byte[] payload = activate ? PAYLOAD_BOOLEAN_TRUE : PAYLOAD_BOOLEAN_FALSE;
        createRequest(createPacket(GAIA.COMMAND_SET_SPEECH_RECOGNITION_CONTROL, payload));
    }

    public void getVoiceCommand() {
        createRequest(createPacket(GAIA.COMMAND_GET_SPEECH_RECOGNITION_CONTROL));
    }

    // ====== PROTECTED METHODS ====================================================================

    @Override // extends GaiaManager
    protected void receiveSuccessfulAcknowledgement(GaiaPacket packet) {
        receiveACK(packet);
    }

    @Override // extends GaiaManager
    protected void receiveUnsuccessfulAcknowledgement(GaiaPacket packet) {

    }

    @Override // extends GaiaManager
    protected void hasNotReceivedAcknowledgementPacket(GaiaPacket packet) {
    }

    @Override // extends GaiaManager
    protected void onSendingFailed(GaiaPacket packet) {
    }

    @Override // extends GaiaManager
    protected boolean manageReceivedPacket(GaiaPacket packet) {
        return false;
    }

    @Override // extends GaiaManager
    protected boolean sendGAIAPacket(byte[] packet) {
        return mListener.sendGAIAPacket(packet);
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

    public interface GaiaManagerListener {
        boolean sendGAIAPacket(byte[] packet);

        void operationFeedback(boolean isSuccessful);
    }

}
