package com.diploma;

import de.cm.osm2po.model.LatLon;
import de.cm.osm2po.routing.DefaultRouter;
import de.cm.osm2po.routing.Graph;
import de.cm.osm2po.routing.RoutingResultSegment;
import org.openstreetmap.gui.jmapviewer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.*;
import java.util.List;

/**
 * Created by arsen on 09.01.2016.
 */
public class Map extends JFrame {

    public interface MapCallBack {
        void onMapClicked(SerializableCoordinate coordinate, int agentType);
    }

    private JPanel contentPanel;
    private JMapViewer map;
    private MapCallBack callBack;

    static public final int TAXI_AGENT = 1;
    static public final int CLIENT_AGENT = 2;

    private boolean isTaxi;

    private HashMap<String, MapMarkerDot> taxiMarkers;
    private HashMap<String, MapMarkerDot> clientMarkers;

    public Map() {

        taxiMarkers = new HashMap<>();
        clientMarkers = new HashMap<>();

        setContentPane(contentPanel);
        setTitle("Map");
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        map.setTileSource(new OfflineOsmTileSource(Helper.mapTiles, 13, 16));

        Coordinate tashkent = new Coordinate(41.289211f, 69.263533f);
        map.setDisplayPosition(tashkent, 13);
        map.setMapRectanglesVisible(true);

        isTaxi = true;

        new DefaultMapController(map){

            @Override
            public void mouseClicked(MouseEvent e) {
                SerializableCoordinate coordinate = new SerializableCoordinate(map.getPosition(e.getPoint()).getLat(), map.getPosition(e.getPoint()).getLon());

                MapMarkerDot markerDot = new MapMarkerDot(new Coordinate(coordinate.getLat(), coordinate.getLon()));

                if (isTaxi) {
                    markerDot.setBackColor(Color.yellow);
                    map.addMapMarker(markerDot);
                    callBack.onMapClicked(coordinate, TAXI_AGENT);
                }
                else {
                    markerDot.setBackColor(Color.green);
                    map.addMapMarker(markerDot);
                    callBack.onMapClicked(coordinate, CLIENT_AGENT);
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent var1) {
                this.map.setZoom(this.map.getZoom() - var1.getWheelRotation(), var1.getPoint());
            }

        };


        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateMarkers();
            }
        }, 100, 100);

    }

    public void setCallBack(MapCallBack c) {
        callBack = c;
    }


    public void updateMarkers() {

        map.removeAllMapMarkers();

        for (java.util.Map.Entry<String, MapMarkerDot> entry : taxiMarkers.entrySet()) {
            map.addMapMarker(entry.getValue());
        }

        for (java.util.Map.Entry<String, MapMarkerDot> entry : clientMarkers.entrySet()) {
            map.addMapMarker(entry.getValue());
        }
    }


    public void setTaxi(Coordinate coordinate, String name) {
        MapMarkerDot markerDot = new MapMarkerDot(coordinate);
        markerDot.setBackColor(Color.yellow);
        markerDot.setName(name.split("@")[0]);
        taxiMarkers.put(name.split("@")[0], markerDot);
    }


    public void setClient(Coordinate coordinate, String name) {
        MapMarkerDot markerDot = new MapMarkerDot(coordinate);
        markerDot.setBackColor(Color.green);
        markerDot.setName(name.split("@")[0]);
        clientMarkers.put(name.split("@")[0], markerDot);
    }


    public double getShortestPath(Coordinate source, Coordinate target) {

        DefaultRouter router = new DefaultRouter();

        int sourceId = Helper.getInstance().getGraph().findClosestVertexId((float) source.getLat(), (float) source.getLon());
        int targetId = Helper.getInstance().getGraph().findClosestVertexId((float) target.getLat(), (float) target.getLon());

        // additional params for DefaultRouter
        Properties params = new Properties();
        params.setProperty("findShortestPath", "true");
        params.setProperty("ignoreRestrictions", "false");
        params.setProperty("ignoreOneWays", "false");
        params.setProperty("heuristicFactor", "1.0"); // 0.0 Dijkstra, 1.0 good A*

        int[] path = router.findPath(Helper.getInstance().getGraph(), sourceId, targetId, Float.MAX_VALUE, params);

        if (path != null) {
            return Helper.getInstance().getGraph().calcPathLength(path);
        }

        return -1;
    }


    public ArrayList<SerializableCoordinate> getPathCoordinates(Coordinate source, Coordinate target) {

        ArrayList<SerializableCoordinate> coordinates = new ArrayList<>();

        DefaultRouter router = new DefaultRouter();

        int sourceId = Helper.getInstance().getGraph().findClosestVertexId((float) source.getLat(), (float) source.getLon());
        int targetId = Helper.getInstance().getGraph().findClosestVertexId((float) target.getLat(), (float) target.getLon());

        // additional params for DefaultRouter
        Properties params = new Properties();
        params.setProperty("findShortestPath", "true");
        params.setProperty("ignoreRestrictions", "false");
        params.setProperty("ignoreOneWays", "false");
        params.setProperty("heuristicFactor", "0.0"); // 0.0 Dijkstra, 1.0 good A*

        int[] path = router.findPath(Helper.getInstance().getGraph(), sourceId, targetId, Float.MAX_VALUE, params);

        if (path != null) { // Found!

            for (int i = 0; i < path.length; i++) {
                RoutingResultSegment rrs = Helper.getInstance().getGraph().lookupSegment(path[i]);
                for (long coord : rrs.getCoords().getCoords()) {
                    SerializableCoordinate coordinate = new SerializableCoordinate(LatLon.latOf(coord), LatLon.lonOf(coord));
                    coordinates.add(coordinate);
                }
            }

        }

        return coordinates;
    }


    public void drawPath(Coordinate source, Coordinate target) {

        DefaultRouter router = new DefaultRouter();

        int sourceId = Helper.getInstance().getGraph().findClosestVertexId((float) source.getLat(), (float) source.getLon());
        int targetId = Helper.getInstance().getGraph().findClosestVertexId((float) target.getLat(), (float) target.getLon());

        // additional params for DefaultRouter
        Properties params = new Properties();
        params.setProperty("findShortestPath", "true");
        params.setProperty("ignoreRestrictions", "false");
        params.setProperty("ignoreOneWays", "false");
        params.setProperty("heuristicFactor", "0.0"); // 0.0 Dijkstra, 1.0 good A*

        int[] path = router.findPath(Helper.getInstance().getGraph(), sourceId, targetId, Float.MAX_VALUE, params);

        if (path != null) { // Found!

            List<Coordinate> coordinates = new ArrayList<>();
//            coordinates.add(source);

            for (int i = 0; i < path.length; i++) {
                RoutingResultSegment rrs = Helper.getInstance().getGraph().lookupSegment(path[i]);
                for (long coord : rrs.getCoords().getCoords()) {
                    Coordinate coordinate = new Coordinate(LatLon.latOf(coord), LatLon.lonOf(coord));
                    coordinates.add(coordinate);
                }
            }

//            coordinates.add(target);

            MapPath polyLine = new MapPath(coordinates);
            map.addMapPolygon(polyLine);
        }

    }


    public void enableTaxiMarker() {
        isTaxi = true;
    }


    public void enableClientMarker() {
        isTaxi = false;
    }

}
