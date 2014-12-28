package de.hpi.smm.features;

public class BlankLineAndParagraphFeature extends AbstractTextFeature {

    Float[] features = new Float[2];

    public BlankLineAndParagraphFeature(float weight) {
        super(weight);
    }

    @Override
    public void feedText(String text) {
        // TODO: ADAPT FOR HANA DATA SET
        int blankLinesCount = 0;
        int paragraphLength = 0;
        int paragraphCount = 0;

        if (text.startsWith("\\n"))
            blankLinesCount++;

        if (text.contains("\\n\\n")) {
            String[] paragraphs = text.split("\\\\n\\\\n");
            blankLinesCount += paragraphs.length - 1;
            paragraphCount = paragraphs.length;

            for (String paragraph : paragraphs) {
                paragraphLength += paragraph.length();
            }

        }

        // Number of blank lines
        if (blankLinesCount != 0)
            features[0] =  1 - (1f / (float) blankLinesCount);
        else
            features[0] = 0f;

        // Average blank lines
        if (paragraphLength != 0)
            features[1] =  (1 - (1 / ((float) paragraphLength / (float) paragraphCount)));
        else
            features[1] = 0f;
    }

    @Override
    public Float[] getFeatures() {
        return features;
    }

    @Override
    public int getNumberOfFeatures() {
        return 2;
    }

    @Override
    public String getName() {
        return "BlankLineAndParagraphFeature";
    }
}
