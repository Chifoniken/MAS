package com.diploma;

import de.cm.osm2po.routing.Graph;

import java.io.File;

/**
 * Created by arsen on 12.01.2016.
 */
public class Helper {
    private static Helper instance = new Helper();

    public static Helper getInstance() {
        return instance;
    }

    private Helper() {
        graph = new Graph(new File(graphFile));
    }

    public static final String graphFile = "D:\\MSU\\Diploma\\Projects\\mas\\uz_2po.gph";
    public static final String mapTiles = "file:\\D:\\MSU\\Diploma\\jTileDownloader\\tiles\\";

    public static final String AGENT_ROLE = "agent_role";
    public static final String TAXI = "taxi";
    public static final String CLIENT = "client";

    private Graph graph;

    public Graph getGraph() {
        return graph;
    }
}
