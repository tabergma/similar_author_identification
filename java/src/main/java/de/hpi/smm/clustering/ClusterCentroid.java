package de.hpi.smm.clustering;

import org.apache.mahout.math.Vector;

import java.util.ArrayList;
import java.util.List;

public class ClusterCentroid {
    private int id;

    private String name;
    private List<String> labels;
    private List<Double> values;

    public ClusterCentroid(int id, String name, List<Double> values) {
        this.id = id;
        this.name = name;
        this.values = values;
        this.labels = new ArrayList<>();
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

    public void setName(String name) {
        this.name = name;
    }

    public void addLabel(String label) {
        this.labels.add(label);
    }

    public List<Double> getValues() {
        return values;
    }

    public List<String> getLabels() {
        return labels;
    }

    public int getId() {
        return id;
    }
}
