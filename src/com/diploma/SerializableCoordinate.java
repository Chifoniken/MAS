package com.diploma;

import org.openstreetmap.gui.jmapviewer.Coordinate;

import java.io.Serializable;

/**
 * Created by arsen on 25.01.2016.
 */
public class SerializableCoordinate implements Serializable {

    private double lat;
    private double lon;


    public SerializableCoordinate() {
        lat = 0.0;
        lon = 0.0;
    }


    public SerializableCoordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }


    public void setCoordinates(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }


    public double getLat() {
        return lat;
    }


    public double getLon() {
        return lon;
    }


    public Coordinate toMapCoordinate() {
        return new Coordinate(lat, lon);
    }


    @Override
    public String toString() {
        return "Coordinate[" + lat + ", " + lon + ']';
    }


    public static SerializableCoordinate sum(SerializableCoordinate a, SerializableCoordinate b) {
        return new SerializableCoordinate(a.getLat() + b.getLat(), a.getLon() + b.getLon());
    }

    public static SerializableCoordinate mul(SerializableCoordinate a, double mul) {
        return new SerializableCoordinate(a.getLat() * mul, a.getLon() * mul);
    }

}
