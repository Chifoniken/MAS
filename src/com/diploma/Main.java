package com.diploma;

import de.cm.osm2po.routing.DefaultRouter;
import de.cm.osm2po.routing.Graph;
import de.cm.osm2po.routing.RoutingResultSegment;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.File;
import java.util.Properties;

/**
 * Created by arsen on 20.12.2015.
 */
public class Main {

    public static void main(String[] args) {

//        Runtime runtime = Runtime.instance();
//        Profile profile = new ProfileImpl();
//        profile.setParameter(Profile.MAIN_HOST, "localhost");
//        profile.setParameter(Profile.GUI, "true");
//
//        ContainerController containerController = runtime.createMainContainer(profile);
//
//        AgentController dispatcher;
//
//        try {
//            dispatcher = containerController.createNewAgent("Dispatcher", "com.diploma.Dispatcher", null);
//            dispatcher.start();
//        } catch (StaleProxyException e) {
//            e.printStackTrace();
//        }

        MapGUI map = new MapGUI();
        map.setVisible(true);

        readOSM();
    }

    private static void readOSM() {

        File graphFile = new File("D:\\MSU\\Diploma\\Projects\\mas\\uz_2po.gph");

        Graph graph = new Graph(graphFile);
        DefaultRouter router = new DefaultRouter();

// Somewhere in Uzb
        int sourceId = graph.findClosestVertexId(41.289211f, 69.263533f);
        int targetId = graph.findClosestVertexId(41.300194f, 69.282250f);

// additional params for DefaultRouter
        Properties params = new Properties();
        params.setProperty("findShortestPath", "true");
        params.setProperty("ignoreRestrictions", "false");
        params.setProperty("ignoreOneWays", "false");
        params.setProperty("heuristicFactor", "0.0"); // 0.0 Dijkstra, 1.0 good A*

        int[] path = router.findPath(graph, sourceId, targetId, Float.MAX_VALUE, params);

        if (path != null) { // Found!
            for (int i = 0; i < path.length; i++) {
                RoutingResultSegment rrs = graph.lookupSegment(path[i]);
                int segId = rrs.getId();
                int from = rrs.getSourceId();
                int to = rrs.getTargetId();
                String segName = rrs.getName().toString();
                System.out.println(from + "-" + to + "  " + segId + "/" + path[i] + " " + segName);
            }
        }

        graph.close();

    }

}
