package com.diploma;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;

/**
 * Created by arsen on 20.12.2015.
 */
public class Dispatcher extends GuiAgent {
    transient private DispatcherGUI gui;

    ArrayList<AID> taxiAgents;

    protected void setup() {

        taxiAgents = new ArrayList<>();

        addBehaviour(new DispatcherBehavior(this));

        gui = new DispatcherGUI(this);
        gui.setVisible(true);
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

        int type = guiEvent.getType();

        if (type == DispatcherGUI.ADD_TAXI_AGENT_EVENT) {
            AgentController taxi;
            try {
                Object[] arg = new Object[1];
                arg[0] = getName();

                taxi = getContainerController().createNewAgent("Taxi_" + (taxiAgents.size() + 1), "com.diploma.Taxi", arg);
                taxi.start();
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
                    taxiAgents.add(msg.getSender());
                    gui.addTaxiAgent(msg.getSender().getName());
                    System.out.println( msg.getSender().getName() + " is registered!" );
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
