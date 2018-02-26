package com.android.internal.telephony;

/**
 * Created by Elena on 29.08.2017.
 */

public interface ITelephony {
    boolean endCall();

    void answerRingingCall();

    void silenceRinger();
}
