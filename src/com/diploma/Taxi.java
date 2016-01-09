package com.diploma;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

/**
 * Created by arsen on 20.12.2015.
 */
public class Taxi extends GuiAgent {

    transient private TaxiGUI gui;
    AID dispatcher;

    protected void setup() {

        addBehaviour(new TaxiBehavior(this));

        gui = new TaxiGUI(this);
        gui.setVisible(true);
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }


    class TaxiBehavior extends SimpleBehaviour {

        private boolean finished = false;

        public TaxiBehavior(Agent a) {
            super(a);
        }

        public void action() {

        }

        @Override
        public void onStart() {
            super.onStart();

            dispatcher = new AID((String) getArguments()[0]);
            ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
            msg.setContent("registration");
            msg.addReceiver(dispatcher);
            send(msg);

            System.out.println( myAgent.getLocalName() + " is ready!" );
        }

        public boolean done() {
            return finished;
        }

    }
}
