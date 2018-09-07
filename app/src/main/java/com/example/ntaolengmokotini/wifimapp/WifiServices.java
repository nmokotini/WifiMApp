package com.example.ntaolengmokotini.wifimapp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/*
This class provides Wifi Services to the App. It provides the functionality
to determine the device's current wifi signal level, as well as to determine whether
the device is connected to the eduroam network.
*/
public class WifiServices {

    private Context context;
    private WifiManager wifiManager;

    /*

    */
    public WifiServices(Context context){
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /*
    This method determines and returns the Wifi Signal Level in terms of
    the number of RSSI Levels outlined. For example, if rssiLevels = 5, then
    the Wifi levels will be determined out of 5 (thus 5/5 = excellent, 4/5 = good, 3/5 = moderate etc.).
    It utilises the Wifi Service of the device and uses it's wifi information to get the
    required information, and calculate the signal level.
    @Parameter int rssiLevels: The number of RSSI Levels that the Wifi strength will be rated out of.
    */
    public int getWifiSignalLevel(int rssiLevels){

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        int wifiSignalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), rssiLevels);

        return wifiSignalLevel;
    }

    /*
    This method is used to determine whether the wifi connection is valid
    in the context of the App - ultimately determining whether the currently
    connected Wifi network is eduroam. It uses the Wifi Services of the device.
    It does a check by obtaining the SSID of the currently connected wifi network
    in order to check if it is equal to 'eduroam'.
    */
    public boolean checkWifiValidity(){

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
