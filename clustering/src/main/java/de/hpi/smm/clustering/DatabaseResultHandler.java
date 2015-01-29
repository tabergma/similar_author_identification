package de.hpi.smm.clustering;

import de.hpi.smm.database.AbstractTableDefinition;
import de.hpi.smm.database.DatabaseAdapter;
import de.hpi.smm.database.SchemaConfig;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.math.Vector;

import java.util.ArrayList;
import java.util.List;


public class DatabaseResultHandler implements ResultHandler {

    private int dataSetId;
    private DatabaseAdapter databaseAdapter;
    private AbstractTableDefinition documentClusterTable;
    private AbstractTableDefinition clusterTable;
    private List<String> documentIds;
    private int index = 0;

    public DatabaseResultHandler(int dataSetId) {
        this.dataSetId = dataSetId;

        databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        databaseAdapter.setSchema(SchemaConfig.getSchema());

        documentClusterTable = databaseAdapter.getWriteTable(SchemaConfig.getDocumentClusterMappingTableName());
        clusterTable = databaseAdapter.getWriteTable(SchemaConfig.getClusterTableName());
        documentIds = readDocumentIds();
    }

    @Override
    public void handleBlogPost(WeightedPropertyVectorWritable value, int clusterId, int documentId) {
        BlogPost blogPost = BlogPost.createFromVector(clusterId, documentId, value);

        documentClusterTable.setRecordValuesToNull();
        documentClusterTable.setValue(SchemaConfig.DATA_SET, this.dataSetId);
        documentClusterTable.setValue(SchemaConfig.DOCUMENT_ID, documentIds.get(index));
        documentClusterTable.setValue(SchemaConfig.CLUSTER_ID, blogPost.getClusterNumber());

        documentClusterTable.writeRecord();

        this.index++;
    }

    @Override
    public void handleCluster(Vector centerVector, int id, String name) {
        ClusterCentroid cluster = ClusterCentroid.createFromVector(id, name, centerVector);

        clusterTable.setRecordValuesToNull(); // this sets all values to NULL, so no value is left unattended
        clusterTable.setValue(SchemaConfig.DATA_SET, dataSetId);
        clusterTable.setValue(SchemaConfig.CLUSTER_ID, cluster.getId());
        List<Double> features = cluster.getValues();
        for (int i = 0; i < features.size(); i++) {
            clusterTable.setFeatureValue(i, features.get(i));
        }
        clusterTable.writeRecord();
    }

    public void closeConnection() {
        databaseAdapter.closeConnection();
    }

    private List<String> readDocumentIds() {
        // TODO data set id
        List<String> documentIds = new ArrayList<>();
        AbstractTableDefinition table = databaseAdapter.getReadTable(SchemaConfig.getFeatureTableName());
        while(table.next()) {
            documentIds.add(table.getString(SchemaConfig.DOCUMENT_ID));
        }
        return documentIds;
    }

}
