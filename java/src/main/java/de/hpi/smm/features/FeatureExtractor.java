package de.hpi.smm.features;

import de.hpi.smm.Config;
import de.hpi.smm.helper.Util;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.StringReader;
import java.util.*;

public class FeatureExtractor {

    MaxentTagger tagger = null;
    List<AbstractTokenFeature> tokenFeatureList;
    List<AbstractTextFeature> textFeatureList;

    public List<Float> getFeatures(String text, String lang) {
        // Create POS tagger
        tagger = Config.lang2tagger.get(lang);

        initializeFeatures();

        // Tokenize text
        List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new StringReader(text),
                PTBTokenizer.PTBTokenizerFactory.newWordTokenizerFactory("untokenizable=noneDelete, normalizeParentheses=false, normalizeOtherBrackets=false"));

        // Run token features
        for (List<HasWord> s : sentences) {
            List<TaggedWord> taggedWords = tagger.tagSentence(s);
            for (TaggedWord taggedWord : taggedWords) {
                String tag = taggedWord.tag();
                String word = taggedWord.word();

                for (AbstractTokenFeature feature : tokenFeatureList) {
                    feature.feedToken(word, tag);
                }
            }
        }
        // Run text features
        for (AbstractTextFeature feature : textFeatureList) {
            feature.feedText(text);
        }

        // Add features to feature list
        ArrayList<Float> featureValues = new ArrayList<Float>();
        for (AbstractTokenFeature feature : tokenFeatureList){
            featureValues.addAll(Arrays.asList(feature.getFeatures()));
        }
        for (AbstractTextFeature feature : textFeatureList) {
            featureValues.addAll(Arrays.asList(feature.getFeatures()));
        }

        return featureValues;
    }

    private void initializeFeatures(){
        this.tokenFeatureList = new ArrayList<AbstractTokenFeature>();
        this.textFeatureList = new ArrayList<AbstractTextFeature>();
        addAllTokenFeatures();
        addAllTextFeatures();
    }

    public static Map<Integer, Feature> getIndexToFeatureMap(){
        FeatureExtractor featureExtractor = new FeatureExtractor();
        featureExtractor.initializeFeatures();
        return featureExtractor.getIndex2FeatureMap();
    }

    public void addAllTokenFeatures() {
        this.tokenFeatureList.add(new WordLengthFeature(1.0f));
        this.tokenFeatureList.add(new PunctuationFrequencyFeature(1.0f));
        this.tokenFeatureList.add(new NumberFrequencyFeature(1.0f));
        this.tokenFeatureList.add(new FunctionWordFeature(1.0f));
        this.tokenFeatureList.add(new CapitalLetterFeature(1.0f));
        this.tokenFeatureList.add(new UpperCaseFeature(1.0f));
        this.tokenFeatureList.add(new WordFrequencyFeature(1.0f));
        this.tokenFeatureList.add(new SingleWordFrequencyFeature(1.0f));
        if (Config.USE_SVM_TO_CLUSTER) {
            this.tokenFeatureList.add(new PosTagFeature(1.0f, Util.asSortedList(tagger.getTags().tagSet())));
        }
        this.tokenFeatureList.add(new EmoticonFeature(1.0f));
        this.tokenFeatureList.add(new PostLengthFeature(1.0f));
        if (Config.USE_SVM_TO_CLUSTER) {
            this.tokenFeatureList.add(new PrefixSuffixFeature(1.0f));
        }
    }

    public void addAllTextFeatures() {
        this.textFeatureList.add(new ParagraphFeature(1.0f));
        this.textFeatureList.add(new SentenceLengthFeature(1.0f));
    }

    public List<AbstractTokenFeature> getTokenFeatureList() {
        return tokenFeatureList;
    }

    public List<AbstractTextFeature> getTextFeatureList() {
        return textFeatureList;
    }

    public Map<Integer, Feature> getIndex2FeatureMap() {
        Map<Integer, Feature> index2Feature = new HashMap<>();
        int offset = 0;
        int index = 0;

        for (AbstractTokenFeature tokenFeature : this.tokenFeatureList) {
            index2Feature.put(index, getFeature(
                    tokenFeature.getName(),
                    tokenFeature.getMaxName(),
                    tokenFeature.getMinName(),
                    tokenFeature.getHighName(),
                    tokenFeature.getLowName(),
                    tokenFeature.getMeaningfulName(),
                    offset,
                    offset + tokenFeature.getNumberOfFeatures()
            ));
            // Increment index and offset
            index += 1;
            offset += tokenFeature.getNumberOfFeatures();
        }
        for (AbstractTextFeature textFeature : this.textFeatureList) {
            index2Feature.put(index, getFeature(
                    textFeature.getName(),
                    textFeature.getMaxName(),
                    textFeature.getMinName(),
                    textFeature.getHighName(),
                    textFeature.getLowName(),
                    textFeature.getMeaningfulName(),
                    offset,
                    offset + textFeature.getNumberOfFeatures()
            ));
            // Increment index and offset
            index += 1;
            offset += textFeature.getNumberOfFeatures();
        }
        return index2Feature;
    }

    private Feature getFeature(String name,
                               String maxName,
                               String minName,
                               String highName,
                               String lowName,
                               String meaningfulName,
                               int start, int end) {
        // Create feature
        Feature feature = new Feature();
        feature.name = name;
        feature.maxName = maxName;
        feature.minName = minName;
        feature.highName = highName;
        feature.lowName = lowName;
        feature.meaningfulName = meaningfulName;
        feature.start = start;
        feature.end = end;
        return feature;
    }
}
