package de.hpi.smm.helper;


import de.hpi.smm.Config;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class FeatureWriter {

    private PrintWriter writer;

    public FeatureWriter(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        File dir = new File(Config.RESULT_PATH);
        if (!dir.exists())
            dir.mkdir();

        this.writer = new PrintWriter(Config.RESULT_PATH + fileName, "UTF-8");
    }

    public void writeFeaturesForAllDocuments(List<List<Float>> features) {
        for(List<Float> documentFeatures: features) {
            String line = StringUtils.join(documentFeatures, Config.FEATURE_SEPERATOR);
            this.writer.println(line);
        }

        close();
    }

    public void writeFeaturesForDocument(List<Float> features) {
        String line = StringUtils.join(features, Config.FEATURE_SEPERATOR);
        this.writer.println(line);
    }

    public void close() {
        this.writer.close();
    }

}

