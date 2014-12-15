package de.hpi.smm.features;

import de.hpi.smm.Config;
import de.hpi.smm.helper.Util;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeatureExtractor {

    MaxentTagger tagger = null;

    public List<Float> getFeatures(String text, String lang) {
        // Create POS tagger
        tagger = Config.lang2tagger.get(lang);

        // Initialize features
        List<AbstractTokenFeature> tokenFeatureList = new ArrayList<AbstractTokenFeature>();
        addAllTokenFeatures(tokenFeatureList);
        List<AbstractTextFeature> textFeatureList = new ArrayList<AbstractTextFeature>();
        addAllTextFeatures(textFeatureList);

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

    public void addAllTokenFeatures(List<AbstractTokenFeature> featureList) {
        featureList.add(new WordLengthFeature(1.0f));
        featureList.add(new CharacterFrequencyFeature(1.0f));
        featureList.add(new FunctionWordFeature(1.0f));
        featureList.add(new CapitalLetterFeature(1.0f));
        featureList.add(new UpperCaseFeature(1.0f));
        featureList.add(new WordFrequencyFeature(1.0f));
        featureList.add(new PosTagFeature(1.0f, Util.asSortedList(tagger.getTags().tagSet())));
        featureList.add(new EmoticonFeature(1.0f));
        featureList.add(new PostLengthFeature(1.0f));
        featureList.add(new PrefixSuffixFeature(1.0f));
    }

    public void addAllTextFeatures(List<AbstractTextFeature> featureList) {
        featureList.add(new BlankLineAndParagraphFeature(1.0f));
        featureList.add(new SentenceLengthFeature(1.0f));
    }
}
