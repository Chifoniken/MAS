package com.diploma;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Created by arsen on 20.12.2015.
 */
public class Dispatcher extends GuiAgent {

    transient private DispatcherGUI gui;

    private HashMap<String, Taxi> taxiAgents;
    private HashMap<String, Client> clientAgents;

    private Integer taxiId = 0;
    private Integer clientId = 0;

    protected void setup() {

        taxiAgents = new HashMap<>();
        clientAgents = new HashMap<>();

        addBehaviour(new DispatcherBehavior(this));

        gui = new DispatcherGUI(this);
        gui.setVisible(true);
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
                taxiId++;
                taxi = getContainerController().createNewAgent("Taxi_" + taxiId, "com.diploma.Taxi", arg);
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
                clientId++;
                client = getContainerController().createNewAgent("Client_" + clientId, "com.diploma.Client", arg);
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

            Taxi taxi = entry.getValue();
            double path = map.getShortestPath(taxi.getCoordinate(), client.getCoordinate());

            if (path < bestPath || bestPath < 0) {
                bestPath = path;
                taxiName = entry.getValue().getName();
            }
        }


        if (bestPath > 0) {
//            map.drawPath(taxiAgents.get(taxiName).getCoordinate(), client.getCoordinate()); // for debug only


            // Ask TaxiAgent if he accepts current ClientAgent
            //
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addUserDefinedParameter(Helper.AGENT_ROLE, Dispatcher.agentType());
            msg.addReceiver(taxiAgents.get(taxiName).getAID());

            ContentData content = new ContentData(null, client);
            content.setPath(map.getPathCoordinates(taxiAgents.get(taxiName).getCoordinate(), client.getCoordinate()));
            try {
                msg.setContentObject(content);
            } catch (IOException e) {
                e.printStackTrace();
            }

            send(msg);
        }

    }


    // Find ClientAgent for TaxiAgent
    //
    private void findClientForTaxi(Taxi taxi) {

        double bestPath = -1;
        String clientName = null;

        Map map = gui.getMap();

        for (Entry<String, Client> entry : clientAgents.entrySet()) {

            if (entry.getValue().isBusy()) {
                continue;
            }

            Client client = entry.getValue();
            double path = map.getShortestPath(taxi.getCoordinate(), client.getCoordinate());

            if (path < bestPath || bestPath < 0) {
                bestPath = path;
                clientName = entry.getValue().getName();
            }
        }


        if (bestPath > 0) {
//            map.drawPath(taxi.getCoordinate(), clientAgents.get(clientName).getCoordinate()); // for debug only


            // Ask TaxiAgent if he accepts current ClientAgent
            //
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addUserDefinedParameter(Helper.AGENT_ROLE, Dispatcher.agentType());
            msg.addReceiver(taxi.getAID());

            ContentData content = new ContentData(null, clientAgents.get(clientName));
            content.setPath(map.getPathCoordinates(taxi.getCoordinate(), clientAgents.get(clientName).getCoordinate()));
            try {
                msg.setContentObject(content);
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
                    if (msg.getUserDefinedParameter(Helper.AGENT_ROLE).equals(Helper.TAXI)) {
                        actionTaxiAgentRegistration(msg);
                    }
                    else if (msg.getUserDefinedParameter(Helper.AGENT_ROLE).equals(Helper.CLIENT)) {
                        actionClientAgentRegistration(msg);
                    }
                }
                else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                    if (msg.getUserDefinedParameter(Helper.AGENT_ROLE).equals(Helper.TAXI)) {
                        actionTaxiAcceptedProposal(msg);
                    }
                }else if (msg.getPerformative() == ACLMessage.INFORM) {
                    if (msg.getUserDefinedParameter(Helper.AGENT_ROLE).equals(Helper.TAXI)) {
                        actionTaxiUpdateStatus(msg);
                    }
                }
            }
        }

        public boolean done() {
            return finished;
        }
    }


    private void actionClientAgentRegistration(ACLMessage msg) {
        try {
            ContentData content = (ContentData) msg.getContentObject();
            Client client = (Client) content.getAgent();
            clientAgents.put(client.getName(), client);
            gui.addClientAgent(client.getCoordinate(), client.getName());
            System.out.println( client.getName() + " is registered!" );
            findTaxiForClient(client);
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }


    private void actionTaxiAgentRegistration(ACLMessage msg) {
        try {
            ContentData content = (ContentData) msg.getContentObject();
            Taxi taxi = (Taxi) content.getAgent();
            taxiAgents.put(taxi.getName(), taxi);
            gui.addTaxiAgent(taxi.getCoordinate(), taxi.getName());
            System.out.println( taxi.getName() + " is registered!" );

            if (clientAgents.size() > 0) {
                findClientForTaxi(taxi);
            }

        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }


    private void actionTaxiAcceptedProposal(ACLMessage msg) {
        try {
            ContentData content = (ContentData) msg.getContentObject();
            Taxi taxi = (Taxi) content.getAgent();
            String clientName = content.getMessage();
            taxiAgents.put(taxi.getName(), taxi);
            clientAgents.remove(clientName);
            gui.removeClientAgent(clientName);
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }


    private void actionTaxiUpdateStatus(ACLMessage msg) {
        try {
            ContentData content = (ContentData) msg.getContentObject();
            Taxi taxi = (Taxi) content.getAgent();
            taxiAgents.put(taxi.getName(), taxi);
            gui.getMap().setTaxi(taxi.getCoordinate(), taxi.getName());
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
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


    public static String agentType() {
        return "dispatcher";
    }
}
