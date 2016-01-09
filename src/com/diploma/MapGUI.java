package com.diploma;

import javafx.application.Platform;
import org.jdesktop.swingx.JXMapViewer;

import javax.swing.*;

/**
 * Created by arsen on 09.01.2016.
 */
public class MapGUI extends JFrame {

    private JPanel contentPanel;

    public MapGUI() {

        setContentPane(contentPanel);
        setTitle("Map");
        setSize(500, 500);


//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                JXMapViewer mapViewer = new JXMapViewer();
//                contentPanel.add(mapViewer);
//            }
//        });


    }

}
