package de.hpi.smm.drawing;

import javax.swing.*;
import java.util.List;

class CartesianFrame extends JFrame {
    CartesianPanel panel;

    public CartesianFrame(List<Point> points) {
        panel = new CartesianPanel(points);
        add(panel);
    }

    public void showUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Cartesian");
        setSize(700, 700);
        setVisible(true);
    }
}
