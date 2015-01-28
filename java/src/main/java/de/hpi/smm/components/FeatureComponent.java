package de.hpi.smm.components;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import de.hpi.smm.Config;
import de.hpi.smm.features.FeatureExtractor;

import java.util.ArrayList;
import java.util.List;

public class FeatureComponent {

    public static void main(String[] args) throws Exception {
        run();
    }

    /**
     * Get all documents where the features were not yet calculated,
     * calculate the features and
     * write them into the database.
     */
    public static void run() throws LangDetectException {
        /**
         * GET DOCUMENTS
         */
        List<String> documents = new ArrayList<>();
        // TODO: Clean document content

        /**
         * EXTRACTING FEATURES
         */
        FeatureExtractor featureExtractor = new FeatureExtractor();

        // Create language detector
        DetectorFactory.loadProfile(Config.PROFILES_DIR);
        Detector detector = DetectorFactory.create();

        int i = 0;
        List<Float> features;
        for (String content : documents) {
            if (content != null) {
                // detect language
                detector.append(content);
                String lang = detector.detect();

                // extract features
                features = featureExtractor.getFeatures(content, lang);

                // write features
                // TODO
            }
        }
    }

}
