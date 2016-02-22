package com.diploma;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by arsen on 20.12.2015.
 */
public class Taxi extends PrimitiveAgent implements Serializable {

    transient private TaxiGUI gui;

    ArrayList<Client> clients;

    @Override
    protected void setup() {
        super.setup();

        clients = new ArrayList<>();

        addBehaviour(new TaxiBehavior(this));

//        gui = new TaxiGUI(this);
//        gui.setVisible(true);
    }


    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }


    class TaxiBehavior extends SimpleBehaviour {

        private boolean finished = false;


        public TaxiBehavior(Agent a) {
            super(a);
        }

        @Override
        public void onStart() {
            super.onStart();
            actionRegistration();
        }

        public void action() {

            ACLMessage msg = receive();

            if (msg != null) {

                if (msg.getPerformative() == ACLMessage.INFORM) {
                    if (msg.getUserDefinedParameter(Data.AGENT_TYPE).equals(Dispatcher.agentType())) {
                        if (msg.getContent().equals(DO_DELETE)) {
                            actionDelete(msg);
                        }
                    }
                } else
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    if (msg.getUserDefinedParameter(Data.AGENT_TYPE).equals(Dispatcher.agentType())) {
                        actionAcceptClient(msg);
                    }
                }
            }
        }


        private void actionUpdateStatus() {
            ACLMessage msg = composeMessage(dispatcher, ACLMessage.INFORM, "");
            send(msg);
        }


        private void actionRegistration() {

            dispatcher = new AID((String) getArguments()[0], true);
            coordinate = (SerializableCoordinate) getArguments()[1];

            ACLMessage msg = composeMessage(dispatcher, ACLMessage.SUBSCRIBE, "");
            send(msg);

            System.out.println( getLocalName() + " is ready! " + coordinate );
        }


        private void actionAcceptClient(ACLMessage msg) {
            try {
                Client client = (Client) msg.getContentObject();
                clients.add(client);
                setBusy(true);

                ACLMessage reply = composeMessage(dispatcher, ACLMessage.ACCEPT_PROPOSAL, client.getName());
                send(reply);

                System.out.println( getName() + " accepted " + client.getName() );

            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        }


        private void actionDelete(ACLMessage msg) {
            System.out.println(getName() + " is deleted!" );
            doDelete();
        }


        private ACLMessage composeMessage(AID receiver, int performative, String content) {

            ACLMessage msg = new ACLMessage(performative);
            msg.addUserDefinedParameter(Data.AGENT_TYPE, agentType());
            msg.addReceiver(receiver);
            msg.setContent(content);

            try {
                msg.setContentObject(myAgent);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return msg;
        }


        public boolean done() {
            return finished;
        }

    }

    public static String agentType() {
        return "taxi";
    }

}
