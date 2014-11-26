package de.hpi.smm.drawing;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Drawing {
    public static void drawInWindow(final List<Point> points) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                CartesianFrame frame = new CartesianFrame(points);
                frame.showUI();
            }
        });
    }

    public static void main (String[] args) {
        List<Point> points = new ArrayList<Point>();
        points.add(new Point(-0.5, -0.5));
        points.add(new Point(-0.5, 0.5));
        points.add(new Point(0.5, -0.5));
        points.add(new Point(0.3, 0.6));
        drawInWindow(points);
    }
}
