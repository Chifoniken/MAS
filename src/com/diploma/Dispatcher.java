package com.diploma;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Created by arsen on 20.12.2015.
 */
public class Dispatcher extends GuiAgent {

    transient private DispatcherGUI gui;

    HashMap<String, Taxi> taxiAgents;
    HashMap<String, Client> clientAgents;

    protected void setup() {

        taxiAgents = new HashMap<>();
        clientAgents = new HashMap<>();

        addBehaviour(new DispatcherBehavior(this));

        gui = new DispatcherGUI(this);
        gui.setVisible(true);
    }

    @Override
    public void doDelete() {

        for (Entry<String, Taxi> entry : taxiAgents.entrySet()) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(PrimitiveAgent.DO_DELETE);
            msg.addReceiver(entry.getValue().getAID());
            send(msg);
        }

        for (Entry<String, Client> entry: clientAgents.entrySet()) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(PrimitiveAgent.DO_DELETE);
            msg.addReceiver(entry.getValue().getAID());
            send(msg);
        }

        super.doDelete();
    }


    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

        Integer type = guiEvent.getType();

        if (type.equals(Map.TAXI_AGENT)) {
            AgentController taxi;
            try {
                Object[] arg = new Object[2];
                arg[0] = getName(); // pass dispatcher name
                arg[1] = guiEvent.getParameter(0); // pass coordinates

                taxi = getContainerController().createNewAgent("Taxi_" + (taxiAgents.size() + 1), "com.diploma.Taxi", arg);
                taxi.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        } else
        if (type.equals(Map.CLIENT_AGENT)) {
            AgentController client;
            try {
                Object[] arg = new Object[2];
                arg[0] = getName(); // pass dispatcher name
                arg[1] = guiEvent.getParameter(0); // pass coordinates

                client = getContainerController().createNewAgent("Client_" + (clientAgents.size() + 1), "com.diploma.Client", arg);
                client.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
    }


    // Find TaxiAgent for ClientAgent
    //
    private void findTaxiForClient(Client client) {

        double bestPath = -1;
        String taxiName = null;

        Map map = gui.getMap();

        for (Entry<String, Taxi> entry : taxiAgents.entrySet()) {

            if (entry.getValue().isBusy()) {
                continue;
            }

            double path = map.getShortestPath(entry.getValue().getCoordinate(), client.getCoordinate());

            if (path < bestPath || bestPath < 0) {
                bestPath = path;
                taxiName = entry.getValue().getName();
            }
        }


        if (bestPath > 0) {
            map.drawPath(taxiAgents.get(taxiName).getCoordinate(), client.getCoordinate()); // for debug only


            // Ask TaxiAgent if he accepts current ClientAgent
            //
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addUserDefinedParameter(Data.AGENT_TYPE, Dispatcher.agentType());
            msg.addReceiver(taxiAgents.get(taxiName).getAID());

            try {
                msg.setContentObject(client);
            } catch (IOException e) {
                e.printStackTrace();
            }

            send(msg);
        }

    }


    class DispatcherBehavior extends SimpleBehaviour {

        private boolean finished = false;


        public DispatcherBehavior(Agent a) {
            super(a);
        }


        @Override
        public void onStart() {
            super.onStart();
            System.out.println( myAgent.getLocalName() + " is ready!" );
        }


        public void action() {

            ACLMessage msg = myAgent.receive();

            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.SUBSCRIBE) {
                    if (msg.getUserDefinedParameter(Data.AGENT_TYPE).equals(Taxi.agentType())) {
                        actionTaxiAgentRegistration(msg);
                    } else
                    if (msg.getUserDefinedParameter(Data.AGENT_TYPE).equals(Client.agentType())) {
                        actionClientAgentRegistration(msg);
                    }
                } else
                if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                    if (msg.getUserDefinedParameter(Data.AGENT_TYPE).equals(Taxi.agentType())) {
                        actionTaxiAcceptedProposal(msg);
                    }
                }
            }
        }


        private void actionTaxiAgentRegistration(ACLMessage msg) {
            try {
                Taxi taxi = (Taxi) msg.getContentObject();
                taxiAgents.put(taxi.getName(), taxi);
                gui.addTaxiAgent(msg.getSender().getName());
                System.out.println( msg.getSender().getName() + " is registered!" );
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }


        private void actionTaxiAcceptedProposal(ACLMessage msg) {
            try {
                Taxi taxi = (Taxi) msg.getContentObject();
                String clientName = msg.getContent();
                taxiAgents.put(taxi.getName(), taxi);
                clientAgents.remove(clientName);
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }


        private void actionClientAgentRegistration(ACLMessage msg) {
            try {
                Client client = (Client) msg.getContentObject();
                clientAgents.put(client.getName(), client);
                gui.addClientAgent(msg.getSender().getName());
                System.out.println( msg.getSender().getName() + " is registered!" );
                findTaxiForClient(client);
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }


        public boolean done() {
            return finished;
        }

    }

    public static String agentType() {
        return "dispatcher";
    }
}
