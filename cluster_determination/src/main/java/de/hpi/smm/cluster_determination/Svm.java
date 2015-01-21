package de.hpi.smm.cluster_determination;


import de.hpi.smm.Config;
import de.hpi.smm.libsvm.svm_predict;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

public class Svm {

    public static Integer getNearestCluster(List<Float> features) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(-1);
        int i = 1;
        for (Float f : features){
            if (f != 0.0){
                stringBuilder.append(Config.SVM_SEPARATOR);
                stringBuilder.append(i);
                stringBuilder.append(Config.KEY_VALUE_SEPARATOR);
                stringBuilder.append(f);
            }
            i++;
        }
        stringBuilder.append("\n");
        FileUtils.writeStringToFile(new File(Config.TEMP_FILE), stringBuilder.toString());

        String[] predict = {"-q", "-b", "1", Config.TEMP_FILE, Config.SVM_MODEL_FILE, Config.SVM_OUTPUT_FILE};
        svm_predict.main(predict);

        String result = FileUtils.readFileToString(new File(Config.SVM_OUTPUT_FILE));
        String clusterIdString = result.split("\n")[1].split(" ")[0];
        return (int) Float.parseFloat(clusterIdString);
    }
}
