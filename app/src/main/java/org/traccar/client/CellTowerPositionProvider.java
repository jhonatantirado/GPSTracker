package org.traccar.client;

import android.content.Context;
import android.os.Build;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Nathan on 13/04/2017.
 */

public class CellTowerPositionProvider {
    private Context context;
    private TelephonyManager telephonyManager;
    private int mcc;
    private int mnc;

    public CellTowerPositionProvider(Context context)
    {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = telephonyManager.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
        }
    }

    public JSONArray getCellTowerInformation()
    {
        JSONArray cellList = new JSONArray();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
        {
            List<NeighboringCellInfo> neighCells = telephonyManager.getNeighboringCellInfo();
            for (int i = 0; i < neighCells.size(); i++) {
                try {
                    JSONObject cellObj = new JSONObject();
                    NeighboringCellInfo thisCell = neighCells.get(i);
                    cellObj.put("cellId", thisCell.getCid());
                    cellObj.put("lac", thisCell.getLac());
                    cellObj.put("rssi", thisCell.getRssi());
                    cellObj.put("mcc",mcc);
                    cellObj.put("mnc",mnc);
                    cellList.put(cellObj);
                }
                catch (Exception e) {
                    Log.d("Exception","Not supported");
                }
            }
        }
        else
        {
            List<CellInfo> cellTowers = telephonyManager.getAllCellInfo();
            for (int i = 0; i<cellTowers.size(); ++i) {
                try {
                    JSONObject cellObj = new JSONObject();
                    CellInfo info = cellTowers.get(i);
                    if (info instanceof CellInfoGsm){
                        CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                        CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                        cellObj.put("cellId", identityGsm.getCid());
                        cellObj.put("lac", identityGsm.getLac());
                        cellObj.put("dbm", gsm.getDbm());
                        cellObj.put("mcc",mcc);
                        cellObj.put("mnc",mnc);
                        cellList.put(cellObj);
                    } else if (info instanceof CellInfoLte) {
                        CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                        CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                        cellObj.put("cellId", identityLte.getCi());
                        cellObj.put("tac", identityLte.getTac());
                        cellObj.put("dbm", lte.getDbm());
                        cellObj.put("mcc",mcc);
                        cellObj.put("mnc",mnc);
                        cellList.put(cellObj);
                    }

                } catch (Exception ex) {
                    Log.d("Exception","Not supported");
                }
            }
        }

        return cellList;
    }
}
