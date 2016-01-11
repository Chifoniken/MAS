package com.diploma;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Runtime;
import jade.core.behaviours.SimpleBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import java.util.ArrayList;

/**
 * Created by arsen on 20.12.2015.
 */
public class Dispatcher extends GuiAgent {

    public static String SUBSCRIBE_TAXI = "addTaxi";
    public static String SUBSCRIBE_CLIENT = "addClient";

    transient private DispatcherGUI gui;

    ArrayList<AID> taxiAgents;
    ArrayList<AID> clientAgents;

    protected void setup() {

        taxiAgents = new ArrayList<>();

        addBehaviour(new DispatcherBehavior(this));

        gui = new DispatcherGUI(this);
        gui.setVisible(true);
    }

    @Override
    public void doDelete() {
        for (AID taxiAgent : taxiAgents) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(Taxi.DO_DELETE);
            msg.addReceiver(taxiAgent);
            send(msg);
        }

        super.doDelete();
    }


    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

        Integer type = guiEvent.getType();

        if (type.equals(DispatcherGUI.ADD_TAXI_AGENT_EVENT)) {
            AgentController taxi;
            try {
                Object[] arg = new Object[2];
                arg[0] = getName();
                arg[1] = guiEvent.getParameter(0);

                taxi = getContainerController().createNewAgent("Taxi_" + (taxiAgents.size() + 1), "com.diploma.Taxi", arg);
                taxi.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(DispatcherGUI.ADD_CLIENT_AGENT_EVENT)) {
            AgentController client;
            try {
                Object[] arg = new Object[2];
                arg[0] = getName();
                arg[1] = guiEvent.getParameter(0);

                client = getContainerController().createNewAgent("Client_" + (clientAgents.size() + 1), "com.diploma.Client", arg);
                client.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
    }


    class DispatcherBehavior extends SimpleBehaviour {

        private boolean finished = false;

        public DispatcherBehavior(Agent a) {
            super(a);
        }

        public void action() {

            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.SUBSCRIBE) {

                    if (msg.getContent().equals(SUBSCRIBE_TAXI)) {
                        taxiAgents.add(msg.getSender());
                        gui.addTaxiAgent(msg.getSender().getName());
                        System.out.println( msg.getSender().getName() + " is registered!" );
                    }
                    else if (msg.getContent().equals(SUBSCRIBE_CLIENT)) {
                        clientAgents.add(msg.getSender());
                        gui.addClientAgent(msg.getSender().getName());
                        System.out.println( msg.getSender().getName() + " is registered!" );
                    }

                }
            }

        }

        @Override
        public void onStart() {
            super.onStart();
            System.out.println( myAgent.getLocalName() + " is ready!" );
        }

        public boolean done() {
            return finished;
        }

    }
}
