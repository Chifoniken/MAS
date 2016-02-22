package com.diploma;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by arsen on 20.12.2015.
 */
public class Client extends PrimitiveAgent implements Serializable {


    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new ClientBehavior(this));
    }


    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }


    class ClientBehavior extends SimpleBehaviour {

        private boolean finished = false;


        public ClientBehavior(Agent a) {
            super(a);
        }


        public void action() {

            ACLMessage msg = myAgent.receive();

            if (msg != null) {

                if (msg.getPerformative() == ACLMessage.INFORM) {

                    if (msg.getContent().equals(DO_DELETE)) {
                        System.out.println(getName() + " is deleted!" );
                        doDelete();
                    }

                }
            }

        }


        @Override
        public void onStart() {
            super.onStart();

            coordinate = (SerializableCoordinate) getArguments()[1];

            dispatcher = new AID((String) getArguments()[0], true);
            ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
            msg.addUserDefinedParameter(Data.AGENT_TYPE, agentType());
            msg.addReceiver(dispatcher);

            try {
                msg.setContentObject(myAgent);
            } catch (IOException e) {
                e.printStackTrace();
            }

            send(msg);

            System.out.println( myAgent.getLocalName() + " is ready! " + coordinate );
        }


        public boolean done() {
            return finished;
        }

    }

    public static String agentType() {
        return "client";
    }

}
