package de.hpi.smm.clustering;

import org.apache.mahout.math.Vector;

import java.util.ArrayList;
import java.util.List;

public class ClusterCentroid {
    private int id;

    private String name;
    private List<Double> values;

    public ClusterCentroid(int id, String name, List<Double> values) {
        this.id = id;
        this.name = name;
        this.values = values;
    }

    public static ClusterCentroid createFromVector(int id, String name, Vector centerVector){
        List<Double> values = new ArrayList<Double>();
        for (int i = 0; i < centerVector.size(); i++) {
            values.add(centerVector.getElement(i).get());
        }
        return new ClusterCentroid(id, name, values);
    }

    public String getName() {
        return name;
    }

    public void addName(String name) {
        this.name += "; " + name;
    }

    public List<Double> getValues() {
        return values;
    }

    public int getId() {
        return id;
    }
}
