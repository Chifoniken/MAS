package com.diploma;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by arsen on 05.02.2016.
 */
public abstract class PrimitiveAgent extends GuiAgent implements Serializable {

    public static final String DO_DELETE = "doDelete";

    protected AID dispatcher;
    protected SerializableCoordinate coordinate;
    protected Boolean busy;


    protected void setup() {
        busy = false;
    }


    public void setCoordinate(SerializableCoordinate c) {
        coordinate = c;
    }


    public Coordinate getCoordinate() {
        return new Coordinate(coordinate.getLat(), coordinate.getLon());
    }


    protected void setBusy(boolean val) {
        busy = val;
    }


    public boolean isBusy() {
        return busy;
    }


    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }


    protected void actionRegistration() {

        dispatcher = new AID((String) getArguments()[0], true);
        coordinate = (SerializableCoordinate) getArguments()[1];

        ContentData messageContent = new ContentData(null, this);
        ACLMessage msg = composeMessage(dispatcher, ACLMessage.SUBSCRIBE, messageContent);
        send(msg);

        System.out.println( getLocalName() + " is ready! " + coordinate );
    }


    protected void actionDelete(ACLMessage msg) {
        System.out.println(getName() + " is deleted!" );
        doDelete();
    }


    protected ACLMessage composeMessage(AID receiver, int performative, ContentData content) {

        ACLMessage msg = new ACLMessage(performative);
        msg.addUserDefinedParameter(Helper.AGENT_ROLE, role());
        msg.addReceiver(receiver);

        try {
            msg.setContentObject(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return msg;
    }


    protected String role() {
        return "primitive_agent";
    }

}
