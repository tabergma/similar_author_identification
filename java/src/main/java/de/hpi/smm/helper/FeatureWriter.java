package de.hpi.smm.helper;


import de.hpi.smm.Config;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class FeatureWriter {

    private PrintWriter svmWriter;
    private PrintWriter writer;

    public FeatureWriter() throws FileNotFoundException, UnsupportedEncodingException {
        File dir = new File(Config.RESULT_PATH);
        if (!dir.exists())
            dir.mkdir();

        this.writer = new PrintWriter(Config.FEATURE_FILE, "UTF-8");
        this.svmWriter = new PrintWriter(Config.SVM_FEATURE_FILE, "UTF-8");
    }

    public void writeFeaturesForAllDocuments(List<List<Float>> features) {
        for(List<Float> documentFeatures: features) {
            String line = StringUtils.join(documentFeatures, Config.FEATURE_SEPERATOR);
            this.writer.println(line);
        }

        close();
    }

    public void writeFeaturesForDocument(List<Float> features, int authorId) {
        String line = StringUtils.join(features, Config.FEATURE_SEPERATOR);
        this.writer.println(line);

        this.svmWriter.print(authorId);
        int i = 1;
        for (Float f : features){
            if (f != 0.0){
                this.svmWriter.print(Config.SVM_SEPARATOR);
                this.svmWriter.print(i);
                this.svmWriter.print(Config.KEY_VALUE_SEPARATOR);
                this.svmWriter.print(f);
            }
            i++;
        }
        this.svmWriter.println();
    }

    public void close() {
        this.writer.close();
    }

}

