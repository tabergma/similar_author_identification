package de.hpi.smm.clustering;

import org.apache.mahout.math.Vector;

import java.util.ArrayList;
import java.util.List;

public class ClusterCentroid {
    private int id;

    private String name;
    private List<String> labels;
    private List<Double> values;
    private List<Double> labelSignificance; 

    public ClusterCentroid(int id, String name, List<Double> values) {
        this.id = id;
        this.name = name;
        this.values = values;
        this.labels = new ArrayList<>();
        this.labelSignificance = new ArrayList<Double>();
        
    }

    public static ClusterCentroid createFromVector(int id, String name, Vector centerVector) {
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
    
    public void addLabel(String label, double significance) {
    	addLabel(label);
    	significance = Math.abs(significance);
    	if (significance < 0.3 && significance >= 0.0)
    		significance = 0.3 - significance;
    	this.labelSignificance.add(significance);
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
    
    public List<String> getMostSignificantLabels(int count) {
    	List<String> significantLabels = new ArrayList<String>();
    	
    	if (count >= labels.size()) return labels;
    	if (count == 0) return significantLabels;
    	
    	List<Integer> moreSignificant = new ArrayList<Integer>();
		
    	//find *count* most significant labels
    	for (double currentSignificance : labelSignificance) {
    		for (int i = 0; i < labelSignificance.size(); i++) {
    			if (labelSignificance.get(i) > currentSignificance)
    				moreSignificant.add(i);
    		}
    		if (moreSignificant.size() == count)
    			break;
    		else if (moreSignificant.size() == count - 1){
    			moreSignificant.add(labelSignificance.indexOf(currentSignificance));
    			break;
    		}
    		else
    			moreSignificant.clear();
    	}
    	if (moreSignificant.size() != count) {
    		System.out.println("couldn't get the right number of feature labels for " + this.name);
    		significantLabels = labels.subList(0, count);
    	}
    	else {
	    	for (int i : moreSignificant){
	    		significantLabels.add(labels.get(i));
	    	}
    	}
    	
    	if (significantLabels.size() != count)
    		significantLabels.add("!! Incorrect amount of Labels !!");
 
    	return significantLabels;
    }
}
