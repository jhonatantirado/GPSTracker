package org.traccar.client;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private String cellTowerInfo = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        cellphone = preferences.getString(MainActivity.KEY_CELLPHONE, Constants.CELLPHONE);

        if (cellphone.equals(Constants.CELLPHONE)){
            return;
        }

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

                outerloop:
                for (int i = 0; i < messages.length; i++) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = myBundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }

                    String sender="";
                    //sender = messages[i].getOriginatingAddress();
                    sender = messages[i].getDisplayOriginatingAddress();
                    String messageBody = "";
                    messageBody = messages[i].getMessageBody();

                    strMessage += "SMS From: " + sender;
                    strMessage += " : ";
                    strMessage += messageBody;
                    strMessage += "\n";

                    if (sender.toUpperCase().contains(cellphone.toUpperCase()))
                    {
                        if (messageBody.toUpperCase().contains(Constants.START_MESSAGE.toUpperCase())) {
                            Log.d("REMOTE", "Starting service");
                            context.startService(new Intent(context, TrackingService.class));
                            break outerloop;
                        }

                        if (messageBody.toUpperCase().contains(Constants.STOP_MESSAGE.toUpperCase())) {
                            Log.d("REMOTE", "Stopping service");
                            context.stopService(new Intent(context, TrackingService.class));
                            break outerloop;
                        }

                        if (messageBody.toUpperCase().contains(Constants.CELLTOWER_POSITION.toUpperCase())) {
                            Log.d("REMOTE", "Celltower position");
                            getCellTowerInfo (context);
                            break outerloop;
                        }
                    }
                }
                Log.d("SMS content", strMessage);
            }
        }
    }

    private void getCellTowerInfo(Context context) {
        JSONArray cellList;
        CellTowerPositionProvider cellTowers = new CellTowerPositionProvider(context);
        cellList = cellTowers.getCellTowerInformation();
        int len = cellList.length();
        if (len>0)
        {
            for (int i = 0; i < len; i++) {
                try {
                    //celltower = cellList.getString(i);
                    JSONObject tower = cellList.getJSONObject(i);
                    String cellId = tower.getString("cellId");
                    if (cellId != null && !cellId.equals("") && !cellId.equals("0")){
                        String tac = tower.getString("tac");
                        String mcc = tower.getString("mcc");
                        String mnc = tower.getString("mnc");
                        cellTowerInfo = "cellId:" + cellId + "-" + "tac:" + tac + "-" + "mcc:" + mcc + "-" + "mnc:" + mnc;
                        Log.d("Cell Towers Information",cellTowerInfo);
                        sendBySMS(cellTowerInfo);
                    }
                }
                catch (Exception ex)
                {
                    Log.d("Exception","Array not accessible");
                }
            }
        }
    }

    private void sendBySMS(String celltower) {
            if (cellphone != null && !cellphone.equals(""))
            {
                TextMessageManager.sendSMS(cellphone, celltower);
                Log.d("Sent by SMS", celltower);
            }
    }

}


