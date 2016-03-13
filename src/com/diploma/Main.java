package com.diploma;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by arsen on 20.12.2015.
 */
public class Main {

    public static ContainerController containerController;

    public static void main(String[] args) {

        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true");

        containerController = runtime.createMainContainer(profile);

        AgentController dispatcher;

        try {
            dispatcher = containerController.createNewAgent("Dispatcher", "com.diploma.Dispatcher", null);
            dispatcher.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }

}
