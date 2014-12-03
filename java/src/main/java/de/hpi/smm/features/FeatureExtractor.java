package de.hpi.smm.features;

import de.hpi.smm.Config;
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
        List<AbstractFeature> featureList = new ArrayList<AbstractFeature>();
        addAllFeatures(featureList);

        // Tokenize text
        List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new StringReader(text),
                PTBTokenizer.PTBTokenizerFactory.newWordTokenizerFactory("untokenizable=noneDelete, normalizeParentheses=false, normalizeOtherBrackets=false"));

        for (List<HasWord> s : sentences) {
            List<TaggedWord> taggedWords = tagger.tagSentence(s);
            for (TaggedWord taggedWord : taggedWords) {
                String tag = taggedWord.tag();
                String word = taggedWord.word();

                for (AbstractFeature feature : featureList) {
                    feature.feedToken(word, tag);
                }
            }
        }

        // Add features to feature list
        ArrayList<Float> featureValues = new ArrayList<Float>();
        for (AbstractFeature feature : featureList){
            featureValues.addAll(Arrays.asList(feature.getFeatures()));
        }

        return featureValues;
    }

    public void addAllFeatures(List<AbstractFeature> featureList) {
//        featureList.add(new WordLengthFeature(1.0f));
        featureList.add(new CharacterFrequencyFeature(1.0f));
        featureList.add(new FunctionWordFeature(1.0f));
//        featureList.add(new CapitalLetterFeature(1.0f));
        featureList.add(new UpperCaseFeature(1.0f));
//        featureList.add(new WordFrequencyFeature(1.0f));
//        featureList.add(new PosTagFeature(1.0f, Util.asSortedList(tagger.getTags().tagSet())));
        featureList.add(new EmoticonFeature(1.0f));
        featureList.add(new PostLengthFeature(1.0f));
    }
}
