package com.inspirado.kuber;

/**
 *
 */

/**
 * @author navnit
 *
 */
public class TrackPoint {

    double lat;
    double lng;
    String gpsTime;
    float direction;
    String gpsSpeed;
    String posType;

    public double getLat() {
        return lat;
    }
    public void setLat(float lat) {
        this.lat = lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLng(float lng) {
        this.lng = lng;
    }
    public String getGpsTime() {
        return gpsTime;
    }
    public void setGpsTime(String gpsTime) {
        this.gpsTime = gpsTime;
    }
    public float getDirection() {
        return direction;
    }
    public void setDirection(float direction) {
        this.direction = direction;
    }
    public String getGpsSpeed() {
        return gpsSpeed;
    }
    public void setGpsSpeed(String gpsSpeed) {
        this.gpsSpeed = gpsSpeed;
    }
    public String getPosType() {
        return posType;
    }
    public void setPosType(String posType) {
        this.posType = posType;
    }





}
