package de.hpi.smm;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


public class Tokenizer {

    private Map<String, Integer> words = new HashMap<String, Integer>();

    public Map<String, Integer> tokenizeText(String text) throws IOException {
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);

        TokenStream ts = analyzer.tokenStream("text", new StringReader(text));
        CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
        ts.reset();

        while (ts.incrementToken()) {
            if (termAtt.length() > 0) {
                String word = ts.getAttribute(CharTermAttribute.class).toString();

                if (this.words.containsKey(word)) {
                    this.words.put(word, this.words.get(word) + 1);
                } else {
                    this.words.put(word, 1);
                }
            }
        }

        ts.end();
        ts.close();

        return this.words;
    }

}
