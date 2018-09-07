package com.example.ntaolengmokotini.wifimapp;

/*
The Lwdata class is the data structure that stores the information required by
the App. It ultimately stores location inforamation, in the form of a latitude and longitude in degrees,
and the Wifi RSSI Level at that location.
Lwdata stands for Location Wifi Data.
*/
public class Lwdata {

    //Defining attributes
    private Double lat;
    private Double lng;
    private int rssilvl;

    //Set methods for all attributes

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setRssilvl(int rssilvl) {
        this.rssilvl = rssilvl;
    }

    //Get methods for all attributes

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public int getRssilvl() {
        return rssilvl;
    }


}
