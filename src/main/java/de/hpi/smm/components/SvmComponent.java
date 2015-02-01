package de.hpi.smm.components;

import de.hpi.smm.cluster_determination.BlogPost;
import de.hpi.smm.database.*;
import de.hpi.smm.libsvm.svm_train;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SvmComponent {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -jar svm_component.jar <data-set-id> <run-id> <svm-model-file>");
            System.out.println("-----------------------------------------------");
            System.out.println("<data-set-id> and <run-id> have to identical to the ones used for the result component!");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
            System.out.println("run-id:         this id distinguish between different runs");
            System.out.println("svm-model-file: file location for model file");
            return;
        }

        int dataSetId = Integer.parseInt(args[0]);
        int runId = Integer.parseInt(args[1]);
        String modelFile = args[2];

        run(modelFile, runId, dataSetId);
    }

    /**
     * Gets the features from the database,
     * trains the SVM model and
     * write the resulting model to disk.
     *
     * @param modelFile the result model file
     * @param dataSetId
     */
    public static void run(String modelFile, int runId, int dataSetId) throws IOException {
        // read features and cluster id of all blog posts
        System.out.print("Reading blog posts with features and cluster id ... ");
        List<BlogPost> blogPosts = read(runId, dataSetId);
        System.out.println("Done.");

        // create model and write it to disk
        System.out.print("Training the SVM ... ");
        String[] args = {"-q", "-t", "2", "-s", "0", "-c", "100", "-b", "1", modelFile};
        svm_train svm_train = new svm_train();
        svm_train.train(args, blogPosts);
        System.out.println("Done.");

        System.out.println("Finished.");
    }

    private static List<BlogPost> read(int runId, int dataSetId) {
        DatabaseAdapter databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        Schema schema = SchemaConfig.getCompleteSchema(dataSetId, runId);
        databaseAdapter.setSchema(schema);

        AbstractTableDefinition featureTableDefinition = schema.getTableDefinition(SchemaConfig.getFeatureTableName());
        AbstractTableDefinition documentClusterMapping = schema.getTableDefinition(SchemaConfig.getDocumentClusterMappingTableName());

        AbstractTableDefinition table = new JoinedTableDefinition(featureTableDefinition, SchemaConfig.DOCUMENT_ID, documentClusterMapping, SchemaConfig.DOCUMENT_ID);

        table = databaseAdapter.getReadTable(table);

        List<BlogPost> blogPosts = new ArrayList<>();
        while(table.next()) {
            BlogPost blogPost = new BlogPost();
            blogPost.setNumber(table.getInt(SchemaConfig.CLUSTER_ID));
            blogPost.setDocumentId(table.getString(SchemaConfig.DOCUMENT_ID));
            List<Float> features = new ArrayList<>();
            int i = 0;
            while (table.getFeatureValue(i) != -1) {
                features.add((float) table.getFeatureValue(i));
                i++;
            }
            blogPost.setPoint(features.toArray(new Float[features.size()]));
            blogPosts.add(blogPost);
        }

        databaseAdapter.closeConnection();

        return blogPosts;
    }

}
