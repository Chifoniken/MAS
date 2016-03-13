package com.diploma;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.Serializable;
import java.util.*;
import java.util.Timer;

/**
 * Created by arsen on 20.12.2015.
 */
public class Taxi extends PrimitiveAgent implements Serializable {

    transient private TaxiGUI gui;

    private ArrayList<Client> clients;
    private Taxi self;

    private ArrayList<SerializableCoordinate> path;

    @Override
    protected void setup() {
        super.setup();

        clients = new ArrayList<>();

        addBehaviour(new TaxiBehavior(this));
        self = this;

//        gui = new TaxiGUI(this);
//        gui.setVisible(true);
    }


    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }


    class TaxiBehavior extends SimpleBehaviour {

        public TaxiBehavior(Agent a) {
            super(a);
        }


        @Override
        public void onStart() {
            super.onStart();
            actionRegistration();
        }


        @Override
        public void action() {
            ACLMessage msg = receive();

            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    if (msg.getUserDefinedParameter(Helper.AGENT_ROLE).equals(Dispatcher.agentType())) {
                        actionAcceptClient(msg);
                    }
                }
                else if (msg.getPerformative() == ACLMessage.INFORM) {
                    if (msg.getUserDefinedParameter(Helper.AGENT_ROLE).equals(Dispatcher.agentType())) {
                        if (msg.getContent().equals(DO_DELETE)) {
                            actionDelete(msg);
                        }
                    }
                }

            }
        }

        @Override
        public boolean done() {
            return false;
        }

    }


    private void actionUpdateStatus() {
        ContentData messageContent = new ContentData(null, this);
        ACLMessage msg = composeMessage(dispatcher, ACLMessage.INFORM, messageContent);
        send(msg);
    }


    private void actionAcceptClient(ACLMessage msg) {

        ContentData content = null;
        try {
            content = (ContentData) msg.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
        }

        if (content != null) {
            Client client = (Client) content.getAgent();
            clients.add(client);
            path = content.getPath();
            setBusy(true);

            ContentData messageContent = new ContentData(client.getName(), this);
            ACLMessage reply = composeMessage(dispatcher, ACLMessage.ACCEPT_PROPOSAL, messageContent);
            send(reply);

            System.out.println(getName() + " accepted " + client.getName());

            go();
        }
    }


    private void go() {
        final int[] i = {0};
        final double[] lambda = {0};

        final Timer timer = new java.util.Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (lambda[0] >= 1) {
                    i[0]++;
                    lambda[0] = 0;
                }

                if (i[0] < path.size() - 1) {

                    SerializableCoordinate a = path.get(i[0]);
                    SerializableCoordinate b = path.get(i[0] + 1);
                    double length = 10000 * Math.sqrt((a.getLat() - b.getLat())*(a.getLat() - b.getLat()) + (a.getLon() - b.getLon())* (a.getLon() - b.getLon()));

                    lambda[0] += 1/length;

                    SerializableCoordinate step = getStep(a, b, lambda[0]);

                    self.setCoordinate(step);

                    self.actionUpdateStatus();

                } else {
                    timer.cancel();
                    timer.purge();
                }

            }
        }, 10, 10);
    }


    private SerializableCoordinate getStep(SerializableCoordinate a, SerializableCoordinate b, double lambda) {

        return SerializableCoordinate.sum(
                SerializableCoordinate.mul(a, 1-lambda),
                SerializableCoordinate.mul(b, lambda)
        );
    }


    @Override
    protected String role() {
        return Helper.TAXI;
    }


}
