package de.hpi.smm.features;

public abstract class AbstractFeature {

    public abstract void feedToken(String token);

    public abstract Float[] getFeatures();

    public abstract int getNumberOfFeatures();

}
