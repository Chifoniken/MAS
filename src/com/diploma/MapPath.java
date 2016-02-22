package com.diploma;

import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arsen on 25.01.2016.
 */
public class MapPath extends MapPolygonImpl {

//    private ArrayList<SerializableCoordinate>

    public MapPath(List<? extends ICoordinate> points) {
        super(null, null, points);
    }

    @Override
    public void paint(Graphics g, List<Point> points) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.red);
        g2d.setStroke(getStroke());
        Path2D path = buildPath(points);
        g2d.draw(path);
        g2d.dispose();
    }

    private Path2D buildPath(List<Point> points) {
        Path2D path = new Path2D.Double();
        if (points != null && points.size() > 0) {
            Point firstPoint = points.get(0);
            path.moveTo(firstPoint.getX(), firstPoint.getY());
            for (Point p : points) {
                path.lineTo(p.getX(), p.getY());
            }
        }
        return path;
    }

}
