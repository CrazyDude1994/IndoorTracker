package com.crazydude.indoortracker.models;

/**
 * Created by Crazy on 11.11.2016.
 */

public class WifiPoint {

    private String pointName;
    private String BSSID;
    private Position position;

    public WifiPoint(String pointName, String bssid, float x, float y) {
        this.pointName = pointName;
        this.BSSID = bssid;
        this.position = new Position(x, y);
    }

    public WifiPoint(String pointName, String BSSID, Position position) {
        this.pointName = pointName;
        this.BSSID = BSSID;
        this.position = position;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WifiPoint wifiPoint = (WifiPoint) o;

        if (BSSID != null ? !BSSID.equals(wifiPoint.BSSID) : wifiPoint.BSSID != null) return false;
        return position != null ? position.equals(wifiPoint.position) : wifiPoint.position == null;

    }

    @Override
    public int hashCode() {
        int result = BSSID != null ? BSSID.hashCode() : 0;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }
}
