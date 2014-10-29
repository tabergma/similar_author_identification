package de.hpi.smm;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder;
import org.apache.mahout.vectorizer.encoders.StaticWordValueEncoder;

import java.io.IOException;
import java.io.StringReader;

public class Main {

    public static void main(String[] args) {

        try {
            FeatureVectorEncoder encoder = new StaticWordValueEncoder("text");
            Analyzer analyzer =
                    new StandardAnalyzer(Version.LUCENE_46);
            StringReader in = new StringReader("text to magically vectorize");
            TokenStream ts = analyzer.tokenStream("body", in);
            CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
            Vector v1 = new RandomAccessSparseVector(100);
            ts.reset();

            while (ts.incrementToken()) {
                char[] termBuffer = termAtt.buffer();
                int termLen = termAtt.length();
                String w = new String(termBuffer, 0, termLen);
                encoder.addToVector(w, 1, v1);
            }

            System.out.printf("%s\n", new SequentialAccessSparseVector(v1));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
