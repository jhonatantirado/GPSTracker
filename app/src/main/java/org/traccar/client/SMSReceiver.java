package org.traccar.client;

import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.app.AlarmManager;

import static org.traccar.client.R.xml.preferences;

/**
 * Created by Nathan on 26/02/2017.
 */

public class SMSReceiver extends BroadcastReceiver {
    /**
     * The Action fired by the Android-System when a SMS was received.
     * We are using the Default Package-Visibility
     */

    private SharedPreferences preferences;
    private String cellphone;
    private static final String SMS_EXTRA_NAME = "pdus";
    private String format = "3gpp";

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public static final String KEY_DEVICE = "id";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PORT = "port";
    public static final String KEY_SECURE = "secure";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_PROVIDER = "provider";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CELLPHONE = "cellphone";
    public static final String KEY_SENDTOSERVER = "SendToServer";
    public static final String KEY_SENDSMS ="SendSMS";

    @Override
    public void onReceive(Context context, Intent intent) {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        cellphone = preferences.getString(MainActivity.KEY_CELLPHONE, Constants.CELLPHONE);

        SmsMessage[] messages = null;
        String strMessage = "";
        Bundle myBundle;

        Log.d("REMOTE", "Message received");

        String action="";
        action = intent.getAction();

        if (action.equals(Constants.ACTION)) {
            myBundle = intent.getExtras();

            if (myBundle != null) {
                Object[] pdus = (Object[]) myBundle.get("pdus");

                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < messages.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = myBundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    strMessage += "SMS From: " + messages[i].getOriginatingAddress();
                    strMessage += " : ";
                    strMessage += messages[i].getMessageBody();
                    strMessage += "\n";
                }
                Log.d("SMS content", strMessage);

                if (strMessage.toString().contains(Constants.START_MESSAGE)) {
                    Log.d("REMOTE", "Starting service");
                    context.startService(new Intent(context, TrackingService.class));
                }

                if (strMessage.toString().contains(Constants.STOP_MESSAGE)) {
                    Log.d("REMOTE", "Stopping service");
                    stopTrackingService(context);
                }

            }
        }
    }

    private void stopTrackingService(Context context) {
        context.stopService(new Intent(context, TrackingService.class));
    }

}


