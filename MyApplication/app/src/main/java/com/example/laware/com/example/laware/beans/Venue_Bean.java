package com.example.laware.com.example.laware.beans;

import java.io.Serializable;

/**
 * Created by Arslanyasinwattoo on 8/2/2017.
 */

public class Venue_Bean implements Serializable{
    private String id;
    private String name;
    private String address;
    private String lng;
    private String lat;
    private String category[];
public  Venue_Bean(){

}
    public Venue_Bean(String id, String name, String address, String lng, String lat, String[] category) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lng = lng;
        this.lat = lat;
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    public String[] getCategory() {
        return category;
    }
}
