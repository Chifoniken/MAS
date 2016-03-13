package com.diploma;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by arsen on 04.03.2016.
 */
public class ContentData implements Serializable {

    private String message;
    private PrimitiveAgent agent;
    private ArrayList<SerializableCoordinate> path;


    public ContentData() {
        message = null;
        agent = null;
        path = null;
    }


    public ContentData(String m, PrimitiveAgent a) {
        message = m;
        agent = a;
        path = null;
    }


    public void setMessage(String m) {
        message = m;
    }


    public  String getMessage() {
        return message;
    }


    public void setAgent(PrimitiveAgent a) {
        agent = a;
    }


    public PrimitiveAgent getAgent() {
        return agent;
    }


    public void setPath(ArrayList<SerializableCoordinate> p) {
        path = p;
    }


    public ArrayList<SerializableCoordinate> getPath() {
        return path;
    }

}
