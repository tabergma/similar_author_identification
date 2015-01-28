package de.hpi.smm.io;

import de.hpi.smm.Config;
import de.hpi.smm.clustering.BlogPost;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class SvmFeatureWriter {

    private PrintWriter svmWriter;

    public SvmFeatureWriter(String featureFile) throws FileNotFoundException, UnsupportedEncodingException {
        this.svmWriter = new PrintWriter(featureFile, "UTF-8");
    }

    public void writeFeaturesForDocument(Double[] features, int clusterId) {
        this.svmWriter.print(clusterId);
        for (int i = 0; i < features.length; i++){
            if (features[i] != 0.0){
                this.svmWriter.print(Config.SVM_SEPARATOR);
                this.svmWriter.print(i + 1);
                this.svmWriter.print(Config.KEY_VALUE_SEPARATOR);
                this.svmWriter.print(features[i]);
            }
        }
        this.svmWriter.println();
    }

    public void close() {
        this.svmWriter.close();
    }

    public void writeFeaturesForAllBlogPosts(List<BlogPost> blogPosts) {
        for (BlogPost blogPost : blogPosts){
            writeFeaturesForDocument(blogPost.getPoint(), blogPost.getClusterNumber());
        }
    }
}

