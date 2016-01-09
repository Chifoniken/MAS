package com.diploma;

import javafx.application.Platform;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.josm.gui.layer.markerlayer.Marker;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by arsen on 09.01.2016.
 */
public class MapGUI extends JFrame {

    private JPanel contentPanel;
    private JMapViewer mapViewer;

    public MapGUI() {

        setContentPane(contentPanel);
        setTitle("Map");
        setSize(800, 600);

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

                System.out.println(coordinate);

            }
        };



    }

}
