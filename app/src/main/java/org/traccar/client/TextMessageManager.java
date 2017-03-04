package org.traccar.client;

import android.os.Build;
import android.telephony.SmsMessage;
import android.telephony.gsm.SmsManager;

/**
 * Created by Nathan on 05/02/2017.
 */

public class TextMessageManager {

    public static void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
