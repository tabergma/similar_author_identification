package de.hpi.smm.features;

public class BlankLinesFeature extends AbstractTextFeature {

    Float[] features = new Float[1];

    public BlankLinesFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedText(String text) {
        // TODO: ADAPT FOR HANA DATA SET
        int count = 0;

        if (text.startsWith("\\n"))
            count++;

        if (text.contains("\\n\\n"))
            count += text.split("\\\\n\\\\n").length - 1;

        if (count != 0)
            features[0] = (float) 1 - (1 / count);
        else
            features[0] = 0f;
    }

    @Override
    public Float[] getFeatures() {
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 1;
    }
}
