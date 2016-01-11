package com.diploma;

import de.cm.osm2po.routing.DefaultRouter;
import de.cm.osm2po.routing.Graph;
import de.cm.osm2po.routing.RoutingResultSegment;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Properties;

/**
 * Created by arsen on 09.01.2016.
 */
public class Map extends JFrame {

    public interface MapCallBack {
        void onMapClicked(Coordinate coordinate);
    }

    private JPanel contentPanel;
    private JMapViewer mapViewer;
    private MapCallBack callBack;

    private Graph graph;
    private DefaultRouter router;

    public Map() {

        setContentPane(contentPanel);
        setTitle("Map");
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);


        Coordinate tashkent = new Coordinate(41.289211f, 69.263533f);
        mapViewer.setDisplayPosition(tashkent, 16);

        new DefaultMapController(mapViewer){

            @Override
            public void mouseClicked(MouseEvent e) {
                Coordinate coordinate = new Coordinate(map.getPosition(e.getPoint()).getLat(), map.getPosition(e.getPoint()).getLon());

                MapMarkerDot mapMarkerDot = new MapMarkerDot(coordinate);

                if (e.getButton() == MouseEvent.BUTTON1) {
                    mapMarkerDot.setBackColor(Color.red);
                    map.addMapMarker(mapMarkerDot);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    mapMarkerDot.setBackColor(Color.green);
                    map.addMapMarker(mapMarkerDot);
                }

                callBack.onMapClicked(coordinate);
            }
        };

//        readOSM();
    }

    public void setCallBack(MapCallBack c) {
        callBack = c;
    }

    private void readOSM() {

        File graphFile = new File("D:\\MSU\\Diploma\\Projects\\mas\\uz_2po.gph");

        graph = new Graph(graphFile);
        router = new DefaultRouter();

        // Somewhere in Uzb
        int sourceId = graph.findClosestVertexId(41.289211f, 69.263533f);
        int targetId = graph.findClosestVertexId(41.300194f, 69.282250f);

        // additional params for DefaultRouter
        Properties params = new Properties();
        params.setProperty("findShortestPath", "true");
        params.setProperty("ignoreRestrictions", "false");
        params.setProperty("ignoreOneWays", "false");
        params.setProperty("heuristicFactor", "0.0"); // 0.0 Dijkstra, 1.0 good A*

        int[] path = router.findPath(graph, sourceId, targetId, Float.MAX_VALUE, params);

        if (path != null) { // Found!
            for (int i = 0; i < path.length; i++) {
                RoutingResultSegment rrs = graph.lookupSegment(path[i]);
                int segId = rrs.getId();
                int from = rrs.getSourceId();
                int to = rrs.getTargetId();
                String segName = rrs.getName().toString();
                System.out.println(from + "-" + to + "  " + segId + "/" + path[i] + " " + segName);
            }
        }

        graph.close();
    }


}
