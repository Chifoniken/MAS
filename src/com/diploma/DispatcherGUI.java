package com.diploma;

import jade.gui.GuiEvent;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import javax.swing.*;
import java.awt.event.*;

/**
 * Created by arsen on 20.12.2015.
 */
public class DispatcherGUI extends JFrame implements Map.MapCallBack{

    private Dispatcher agent;

    private JPanel contentPanel;
    private JList<String> taxiAgentsList;
    private JList<String> clientAgentList;
    private Map map;

    private DefaultListModel<String> taxiListModel;
    private DefaultListModel<String> clientListModel;

    static public final int ADD_TAXI_AGENT_EVENT = 1;
    static public final int ADD_CLIENT_AGENT_EVENT = 2;

    public DispatcherGUI(Dispatcher dispatcher) {

        map.setCallBack(this);

        agent = dispatcher;

        setContentPane(contentPanel);
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setTitle(agent.getName());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                agent.doDelete();
                e.getWindow().dispose();
            }
        });

        taxiListModel = new DefaultListModel<>();
        taxiAgentsList.setModel(taxiListModel);

        clientListModel = new DefaultListModel<>();
        clientAgentList.setModel(clientListModel);

    }

    public void addTaxiAgent(String taxiName) {
        taxiListModel.addElement(taxiName);
    }

    public void addClientAgent(String clientName) {
        taxiListModel.addElement(clientName);
    }

    @Override
    public void onMapClicked(Coordinate coordinate) {
        System.out.println(coordinate);
        GuiEvent guiEvent = new GuiEvent(this, ADD_TAXI_AGENT_EVENT);
        guiEvent.addParameter(coordinate);
        agent.postGuiEvent(guiEvent);
    }
}
