package de.hpi.smm.components;

import de.hpi.smm.cluster_determination.BlogPost;
import de.hpi.smm.database.*;
import de.hpi.smm.libsvm.svm_train;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SvmComponent {

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Wrong number of arguments!");
            System.out.println("-----------------------------------------------");
            System.out.println("To start the program execute");
            System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.SvmComponent <data-set-id> <run-id> <language> <svm-model-file>");
            System.out.println("-----------------------------------------------");
            System.out.println("<data-set-id> and <run-id> have to identical to the ones used for the result component!");
            System.out.println("data-set-id:");
            System.out.println("  1 -> smm data");
            System.out.println("  2 -> springer data");
            System.out.println("run-id:         this id distinguish between different runs");
            System.out.println("svm-model-file: file location for model file");
            System.out.println("language: language of the blog posts, can be 'de' or 'en'");
            return;
        }

        int dataSetId = Integer.parseInt(args[0]);
        int runId = Integer.parseInt(args[1]);
        String language = args[2];
        String modelFile = args[3];


        run(modelFile, runId, dataSetId, language);
    }

    /**
     * Gets the features from the database,
     * trains the SVM model and
     * write the resulting model to disk.
     *
     * @param runId        distinguish between different runs
     * @param dataSetId    identifies the original data set, 1 for smm data and 2 for springer data
     * @param svmModelFile the file location of the svm model file
     */
    public static void run(String svmModelFile, int runId, int dataSetId, String language) throws IOException {
        // read features and cluster id of all blog posts
        System.out.print("Reading blog posts with features and cluster id ... ");
        List<BlogPost> blogPosts = readBlogPosts(runId, dataSetId);
        System.out.println("Done.");

        // create model and write it to disk
        System.out.print("Training the SVM ... ");
        String[] args = {"-q", "-t", "2", "-s", "0", "-c", "100", "-b", "1", svmModelFile};
        svm_train svm_train = new svm_train();
        svm_train.train(args, blogPosts);
        System.out.println("Done.");

        System.out.println("Finished.");
    }

    public static List<BlogPost> readBlogPosts(int runId, int dataSetId) {
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
