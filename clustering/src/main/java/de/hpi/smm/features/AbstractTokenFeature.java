package de.hpi.smm.features;

public abstract class AbstractTokenFeature {

    private float weight = 0;

    public AbstractTokenFeature(float weight) {
        this.weight = weight;
    }

    public abstract void feedToken(String token, String tag);

    public abstract Float[] getFeatures();

    public Float[] getWeightedFeatures(){
        Float[] features = this.getFeatures();
        for (int i = 0; i < features.length; i++){
            features[i] *= this.weight;
        }
        return  features;
    }

    public abstract int getNumberOfFeatures();

    public abstract String getName();

    public abstract String getMaxName();

    public abstract String getMinName();

    public abstract String getHighName();

    public abstract String getLowName();

    public abstract String getVeryLowName();
    
    public abstract String getVeryHighName();
    
    public abstract String getAverageName();
    
    public abstract String getMeaningfulName();
}
