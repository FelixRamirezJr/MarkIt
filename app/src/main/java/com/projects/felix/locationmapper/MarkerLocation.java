package com.projects.felix.locationmapper;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by felix on 12/23/16.
 */

public class MarkerLocation {

    public String address;
    public String name;
    public double lat;
    public double lon;
    public String id;
    public String date = "";


    MarkerLocation(){}


    void setData(String name, String address, double lat, double lon, String date){
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.date = date;
        generateKey();
    }

    void generateKey(){
        SecureRandom random = new SecureRandom();
        id =  new BigInteger(130, random).toString(32);
    }

}
