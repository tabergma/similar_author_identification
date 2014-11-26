package de.hpi.smm.drawing;

public class Cluster {
    private final Point center;
    private final double radius;

    public Cluster(Point center, double radius){
        this.center = center;
        this.radius = radius;
    }

    public Point getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }
}
