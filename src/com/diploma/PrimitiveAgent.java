package com.diploma;

import jade.core.AID;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import java.io.Serializable;

/**
 * Created by arsen on 05.02.2016.
 */
public class PrimitiveAgent extends GuiAgent implements Serializable {

    public static final String DO_DELETE = "doDelete";

    protected AID dispatcher;
    protected SerializableCoordinate coordinate;
    protected Boolean busy;


    protected void setup() {
        busy = false;
    }


    public Coordinate getCoordinate() {
        return new Coordinate(coordinate.getLat(), coordinate.getLon());
    }


    protected void setBusy(boolean val) {
        busy = val;
    }


    public boolean isBusy() {
        return busy;
    }


    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
