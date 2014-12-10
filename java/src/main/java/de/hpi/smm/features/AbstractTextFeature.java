package de.hpi.smm.features;


public abstract class AbstractTextFeature {

    private float weight = 0;

    public AbstractTextFeature(float weight) {
        this.weight = weight;
    }

    public abstract void feedText(String text);

    public abstract Float[] getFeatures();

    public Float[] getWeightedFeatures(){
        Float[] features = this.getFeatures();
        for (int i = 0; i < features.length; i++){
            features[i] *= this.weight;
        }
        return  features;
    }

    public abstract int getNumberOfFeatures();

}
