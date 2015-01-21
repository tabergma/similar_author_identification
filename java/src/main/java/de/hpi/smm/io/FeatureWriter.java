package de.hpi.smm.io;


import de.hpi.smm.Config;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
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
            String line = StringUtils.join(documentFeatures, Config.FEATURE_SEPARATOR);
            this.writer.println(line);
        }

        close();
    }

    public void writeFeaturesForDocument(List<Float> features, int authorId) {
        String line = StringUtils.join(features, Config.FEATURE_SEPARATOR);
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
        this.svmWriter.close();
    }

    public static List<List<Float>> readFeatureFile() throws IOException {
        List<List<Float>> list = new ArrayList<List<Float>>();

        BufferedReader br = new BufferedReader(new FileReader(Config.FEATURE_FILE));
        String line = br.readLine();

        while (line != null) {
            List<Float> l = new ArrayList<Float>();

            String[] fs = line.split(" ");
            for (String f : fs)
                l.add(Float.parseFloat(f));

            list.add(l);
            line = br.readLine();
        }
        br.close();

        return list;
    }
}

