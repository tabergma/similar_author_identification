package de.hpi.smm.components;

import de.hpi.smm.Config;
import de.hpi.smm.cluster_determination.BlogPost;
import de.hpi.smm.database.*;
import de.hpi.smm.libsvm.svm_train;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SvmComponent {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments!");
            System.out.println("To start the program execute");
            System.out.println("  java -jar <jar-name> <modelFile> <data-set-id>");
            return;
        }

        String modelFile = args[0];
        int dataSetId = Integer.parseInt(args[1]);

        run(modelFile, dataSetId);
    }

    /**
     * Gets the features from the database,
     * trains the SVM model and
     * write the resulting model to disk.
     *
     * @param modelFile the result model file
     */
    public static void run(String modelFile, int dataSetId) throws IOException {
        // read features and cluster id of all blog posts
        List<BlogPost> blogPosts = new ArrayList<>();

        // create model and write it to disk
        String[] args = {"-q", "-t", "2", "-s", "0", "-c", "100", "-b", "1", Config.SVM_MODEL_FILE};
        svm_train svm_train = new svm_train();
        svm_train.train(args, blogPosts);
    }

    // TODO: data set id
    private static List<BlogPost> read(int dataSetId) {
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        Schema schema = SchemaConfig.getSchema();
        databaseAdapter.setSchema(schema);

        AbstractTableDefinition featureTableDefinition = schema.getTableDefinition(SchemaConfig.getFeatureTableName());
        AbstractTableDefinition documentClusterMapping = schema.getTableDefinition(SchemaConfig.getDocumentClusterMappingTableName());

        AbstractTableDefinition table = new JoinedTableDefinition(featureTableDefinition, documentClusterMapping, SchemaConfig.DOCUMENT_ID);

        table = databaseAdapter.getReadTable(table);

        List<BlogPost> blogPosts = new ArrayList<>();
        while(table.next()) {
            BlogPost blogPost = new BlogPost();
            blogPost.setNumber(table.getInt(SchemaConfig.CLUSTER_ID));
            blogPost.setDocumentId(table.getInt(SchemaConfig.DOCUMENT_ID));
            List<Float> features = new ArrayList<>();
            int i = 0;
            while (table.getFeatureValue(i) != -1) {
                features.add((float) table.getFeatureValue(i));
            }
            blogPost.setPoint(features.toArray(new Float[features.size()]));
            blogPosts.add(blogPost);
        }

        databaseAdapter.closeConnection();

        return blogPosts;
    }

}
