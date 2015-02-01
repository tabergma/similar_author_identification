package de.hpi.smm.clustering;

import de.hpi.smm.database.AbstractTableDefinition;
import de.hpi.smm.database.DatabaseAdapter;
import de.hpi.smm.database.SchemaConfig;
import org.apache.mahout.clustering.classify.WeightedPropertyVectorWritable;
import org.apache.mahout.math.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseResultHandler implements ResultHandler {

    private int runId;
    private int dataSetId;
    private DatabaseAdapter databaseAdapter;
    private AbstractTableDefinition documentClusterTable;
    private AbstractTableDefinition clusterTable;
    private List<String> documentIds;
    private int index = 0;
    private Map<Integer, Integer> nrOfDocuments;

    public DatabaseResultHandler(int dataSetId, int runId) {
        this.dataSetId = dataSetId;
        this.runId = runId;

        databaseAdapter = DatabaseAdapter.getSmaHanaAdapter();
        databaseAdapter.setSchema(SchemaConfig.getCompleteSchema(this.dataSetId, this.runId));

        documentClusterTable = databaseAdapter.getWriteTable(SchemaConfig.getDocumentClusterMappingTableName());
        clusterTable = databaseAdapter.getWriteTable(SchemaConfig.getClusterTableName());
        documentIds = readDocumentIds();

        this.nrOfDocuments = new HashMap<>();
    }

    @Override
    public void handleBlogPost(WeightedPropertyVectorWritable value, int clusterId, int documentId) {
        BlogPost blogPost = BlogPost.createFromVector(clusterId, documentId, value);

        countDocument(clusterId);

        documentClusterTable.setRecordValuesToNull();
        documentClusterTable.setValue(SchemaConfig.RUN_ID, this.dataSetId);
        documentClusterTable.setValue(SchemaConfig.DOCUMENT_ID, documentIds.get(index));
        documentClusterTable.setValue(SchemaConfig.CLUSTER_ID, blogPost.getClusterNumber());

        documentClusterTable.writeRecord();

        this.index++;
    }

    @Override
    public void handleCluster(Vector centerVector, int id, String name) {
        ClusterCentroid cluster = ClusterCentroid.createFromVector(id, name, centerVector);

        clusterTable.setRecordValuesToNull(); // this sets all values to NULL, so no value is left unattended
        clusterTable.setValue(SchemaConfig.RUN_ID, dataSetId);
        clusterTable.setValue(SchemaConfig.CLUSTER_ID, cluster.getId());
        clusterTable.setValue(SchemaConfig.NUMBER_OF_DOCUMENTS, this.nrOfDocuments.get(id));
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
        List<String> documentIds = new ArrayList<>();
        AbstractTableDefinition table = databaseAdapter.getReadTable(SchemaConfig.getFeatureTableName());
        while(table.next()) {
            documentIds.add(table.getString(SchemaConfig.DOCUMENT_ID));
        }
        return documentIds;
    }

    private void countDocument(int clusterId) {
        if (this.nrOfDocuments.containsKey(clusterId)) {
            this.nrOfDocuments.put(clusterId, this.nrOfDocuments.get(clusterId) + 1);
        } else {
            this.nrOfDocuments.put(clusterId, 1);
        }
    }

}
