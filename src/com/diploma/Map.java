package com.diploma;

import de.cm.osm2po.model.LatLon;
import de.cm.osm2po.routing.DefaultRouter;
import de.cm.osm2po.routing.Graph;
import de.cm.osm2po.routing.RoutingResultSegment;
import org.openstreetmap.gui.jmapviewer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by arsen on 09.01.2016.
 */
public class Map extends JFrame {

    public interface MapCallBack {
        void onMapClicked(SerializableCoordinate coordinate, int agentType);
    }

    private JPanel contentPanel;
    private JMapViewer mapViewer;
    private MapCallBack callBack;

    File graphFile;

    static public final int TAXI_AGENT = 1;
    static public final int CLIENT_AGENT = 2;

    private boolean isTaxi;

    public Map() {

        setContentPane(contentPanel);
        setTitle("Map");
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);


        Coordinate tashkent = new Coordinate(41.289211f, 69.263533f);
        mapViewer.setDisplayPosition(tashkent, 16);

        isTaxi = true;

        new DefaultMapController(mapViewer){

            @Override
            public void mouseClicked(MouseEvent e) {
                SerializableCoordinate coordinate = new SerializableCoordinate(map.getPosition(e.getPoint()).getLat(), map.getPosition(e.getPoint()).getLon());

                MapMarkerDot mapMarkerDot = new MapMarkerDot(new Coordinate(coordinate.getLat(), coordinate.getLon()));

                if (isTaxi) {
                    mapMarkerDot.setBackColor(Color.yellow);
                    map.addMapMarker(mapMarkerDot);
                    callBack.onMapClicked(coordinate, TAXI_AGENT);
                }
                else {
                    mapMarkerDot.setBackColor(Color.green);
                    map.addMapMarker(mapMarkerDot);
                    callBack.onMapClicked(coordinate, CLIENT_AGENT);
                }

            }
        };

        readOSM();
    }

    public void setCallBack(MapCallBack c) {
        callBack = c;
    }

    private void readOSM() {
        graphFile = new File("D:\\MSU\\Diploma\\Projects\\mas\\uz_2po.gph");
    }

    public double getShortestPath(Coordinate source, Coordinate target) {

        Graph graph = new Graph(graphFile);
        DefaultRouter router = new DefaultRouter();

        int sourceId = graph.findClosestVertexId((float) source.getLat(), (float) source.getLon());
        int targetId = graph.findClosestVertexId((float) target.getLat(), (float) target.getLon());

        // additional params for DefaultRouter
        Properties params = new Properties();
        params.setProperty("findShortestPath", "true");
        params.setProperty("ignoreRestrictions", "false");
        params.setProperty("ignoreOneWays", "false");
        params.setProperty("heuristicFactor", "1.0"); // 0.0 Dijkstra, 1.0 good A*

        int[] path = router.findPath(graph, sourceId, targetId, Float.MAX_VALUE, params);

        double pathLength = graph.calcPathLength(path);

        graph.close();

        return pathLength;
    }


    public void drawPath(Coordinate source, Coordinate target) {

        Graph graph = new Graph(graphFile);
        DefaultRouter router = new DefaultRouter();

        int sourceId = graph.findClosestVertexId((float) source.getLat(), (float) source.getLon());
        int targetId = graph.findClosestVertexId((float) target.getLat(), (float) target.getLon());

        // additional params for DefaultRouter
        Properties params = new Properties();
        params.setProperty("findShortestPath", "true");
        params.setProperty("ignoreRestrictions", "false");
        params.setProperty("ignoreOneWays", "false");
        params.setProperty("heuristicFactor", "0.0"); // 0.0 Dijkstra, 1.0 good A*

        int[] path = router.findPath(graph, sourceId, targetId, Float.MAX_VALUE, params);

        if (path != null) { // Found!

            List<Coordinate> coordinates = new ArrayList<>();
//            coordinates.add(source);

            for (int i = 0; i < path.length; i++) {
                RoutingResultSegment rrs = graph.lookupSegment(path[i]);
                for (long coord : rrs.getCoords().getCoords()) {
                    Coordinate coordinate = new Coordinate(LatLon.latOf(coord), LatLon.lonOf(coord));
                    coordinates.add(coordinate);
                }
            }

//            coordinates.add(target);

            MapPath polyLine = new MapPath(coordinates);
            mapViewer.addMapPolygon(polyLine);
        }

        graph.close();
    }


    public void enableTaxiMarker() {
        isTaxi = true;
    }

    public void enableClientMarker() {
        isTaxi = false;
    }

}
