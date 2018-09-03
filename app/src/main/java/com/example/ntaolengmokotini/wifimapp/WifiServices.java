package com.example.ntaolengmokotini.wifimapp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiServices {


    public int getWifiSignalLevel(Context context, int rssiLevels){
        //gets access to WIFI service through permissions
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        //gets information about the wifi connection and stores it in a WifiInfo object
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        //calulates Signal level
        int wifiSignalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), rssiLevels);
        return wifiSignalLevel;
    }

    public boolean checkWifiValidity(Context context){
        //gets access to WIFI service through permissions
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        //gets information about the wifi connection and stores it in a WifiInfo object
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID().toString();

        if(ssid.replace("\"", "").equals("eduroam")){
            return true;
        }
        else{
            return false;
        }
    }


}
