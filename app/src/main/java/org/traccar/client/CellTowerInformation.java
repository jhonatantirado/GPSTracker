package org.traccar.client;

/**
 * Created by Nathan on 21/05/2017.
 */

public class CellTowerInformation {

    private String mobileCountryCode;
    public String getMobileCountryCode(){return mobileCountryCode;}
    public void setMobileCountryCode(String mobileCountryCode){this.mobileCountryCode=mobileCountryCode;}

    private String mobileNetworkCode;
    public String getMobileNetworkCode(){return mobileNetworkCode;}
    public void setMobileNetworkCode(String mobileNetworkCode){this.mobileNetworkCode=mobileNetworkCode;}

    private String locationAreaCode;
    public String getLocationAreaCode(){return locationAreaCode;}
    public void setLocationAreaCode(String locationAreaCode){this.locationAreaCode=locationAreaCode;}

    private String cellId;
    public String getCellId(){return cellId;}
    public void setCellId (String cellId){this.cellId=cellId;}

    private String radioType;
    public String getRadioType() {return radioType;}

    public void setRadioType(String radioType) {
        this.radioType = radioType;
    }

    public CellTowerInformation(){
    }

    public CellTowerInformation(String mobileCountryCode, String mobileNetworkCode, String locationAreaCode, String cellId, String radioType){
        this.mobileCountryCode = mobileCountryCode;
        this.mobileNetworkCode = mobileNetworkCode;
        this.locationAreaCode = locationAreaCode;
        this.cellId = cellId;
        this.radioType = radioType;
    }

}
