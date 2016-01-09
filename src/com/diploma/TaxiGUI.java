package com.diploma;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by arsen on 20.12.2015.
 */
public class TaxiGUI extends JFrame{

    private Taxi agent;

    private JPanel contentPanel;

    public TaxiGUI(Taxi taxi) {

        agent = taxi;

        setContentPane(contentPanel);
        setSize(300, 200);
        setTitle(agent.getName());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                agent.doDelete();
                e.getWindow().dispose();
            }
        });


    }

}
