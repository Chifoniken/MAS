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

        public ClientBehavior(Agent a) {
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

                if (msg.getPerformative() == ACLMessage.INFORM) {
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


    @Override
    protected String role() {
        return Helper.CLIENT;
    }

}
