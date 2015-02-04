package de.hpi.smm.cluster_determination;


import de.hpi.smm.libsvm.svm_predict;

import java.util.List;

public class Svm {

    public static Integer getNearestCluster(List<Float> features, String modelFile) throws Exception {
        BlogPost blogPost = new BlogPost();
        blogPost.setPoint(features.toArray(new Float[features.size()]));

        String[] args = {"-q", "-b", "1", modelFile};
        svm_predict svm_predict = new svm_predict();
        return (int) svm_predict.predict(args, blogPost);
    }
}
