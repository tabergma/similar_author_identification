package de.hpi.smm.helper;


import org.apache.commons.lang.StringUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class FileWriter {

    public static String OUT_PATH = "../output/";
    public static String FEATURE_SEPERATOR= " ";

    private PrintWriter writer;

    public FileWriter(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
        this.writer = new PrintWriter(OUT_PATH + fileName, "UTF-8");
    }

    public void writeFeaturesForAllDocuments(List<List<Float>> features) {
        for(List<Float> documentFeatures: features) {
            String line = StringUtils.join(documentFeatures, FEATURE_SEPERATOR);
            this.writer.println(line);
        }

        close();
    }

    public void writeFeaturesForDocument(List<Float> features) {
        String line = StringUtils.join(features, FEATURE_SEPERATOR);
        this.writer.println(line);
    }

    public void close() {
        this.writer.close();
    }


}
