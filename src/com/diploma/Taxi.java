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
import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * Created by arsen on 20.12.2015.
 */
public class Taxi extends GuiAgent {

    public static String DO_DELETE = "doDelete";

    transient private TaxiGUI gui;
    AID dispatcher;
    Coordinate coordinate;

    protected void setup() {

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

            dispatcher = new AID((String) getArguments()[0], true);
            ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
            msg.setContent(Dispatcher.SUBSCRIBE_TAXI);
            msg.addReceiver(dispatcher);
            send(msg);

            coordinate = (Coordinate) getArguments()[1];

            System.out.println( myAgent.getLocalName() + " is ready! " + coordinate );
        }

        public boolean done() {
            return finished;
        }

    }
}
