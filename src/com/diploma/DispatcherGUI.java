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
    private JComboBox<String> agentSelector;

    private DefaultListModel<String> taxiListModel;
    private DefaultListModel<String> clientListModel;

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

        agentSelector.addItem("Taxi");
        agentSelector.addItem("Client");
        agentSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (((JComboBox) e.getSource()).getSelectedIndex() == 0) {
                    map.enableTaxiMarker();
                }
                else {
                    map.enableClientMarker();
                }
            }
        });

        taxiListModel = new DefaultListModel<>();
        taxiAgentsList.setModel(taxiListModel);

        clientListModel = new DefaultListModel<>();
        clientAgentList.setModel(clientListModel);

    }


    public void addTaxiAgent(Coordinate coordinate, String taxiName) {
        taxiListModel.addElement(taxiName);
        map.setTaxi(coordinate, taxiName);
    }


    public void addClientAgent(Coordinate coordinate, String clientName) {
        clientListModel.addElement(clientName);
        map.setClient(coordinate, clientName);
    }


    public void removeClientAgent(String clientName) {
        clientListModel.removeElement(clientName);
    }


    public Map getMap() {
        return map;
    }


    @Override
    public void onMapClicked(SerializableCoordinate coordinate, int agentType) {

        GuiEvent guiEvent = new GuiEvent(this, agentType);
        guiEvent.addParameter(coordinate);
        agent.postGuiEvent(guiEvent);
    }
}
