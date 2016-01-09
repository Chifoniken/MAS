package com.diploma;

import jade.gui.GuiEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;

/**
 * Created by arsen on 20.12.2015.
 */
public class DispatcherGUI extends JFrame{

    private Dispatcher agent;

    private JPanel contentPanel;
    private JList taxiAgentsList;
    private JButton addTaxiAgent;
    private DefaultListModel<String> listModel;

    static public int ADD_TAXI_AGENT_EVENT = 1;

    public DispatcherGUI(Dispatcher dispatcher) {

        agent = dispatcher;

        setContentPane(contentPanel);
        setSize(800, 600);
        setTitle(agent.getName());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                agent.doDelete();
                e.getWindow().dispose();
            }
        });

        listModel = new DefaultListModel<>();
        taxiAgentsList.setModel(listModel);

        addTaxiAgent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiEvent guiEvent = new GuiEvent(this, ADD_TAXI_AGENT_EVENT);
                agent.postGuiEvent(guiEvent);
            }
        });
    }

    public void addTaxiAgent(String taxiName) {
        listModel.addElement(taxiName);
    }

}
