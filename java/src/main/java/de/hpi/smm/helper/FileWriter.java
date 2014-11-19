package de.hpi.smm.helper;


import org.apache.commons.lang.StringUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class FileWriter {

    public static String OUT_PATH = "../output/";
    public static String FEATURE_SEPERATOR= " ";

    public static void writeFeaturesToFile(List<List<Float>> features, String fileName)
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(OUT_PATH + fileName, "UTF-8");

        for(List<Float> documentFeatures: features) {
            String line = StringUtils.join(documentFeatures, FEATURE_SEPERATOR);
            writer.println(line);
        }

        writer.close();
    }

}

