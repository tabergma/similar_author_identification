package de.hpi.smm.database;

import java.sql.SQLException;

public class SchemaConfig {
    private static final int NUMBER_OF_FEATURE_COLUMNS = 900;

    public static final String CLUSTER_ID = "CLUSTER_ID";
    public static final String DOCUMENT_ID = "DOCUMENT_ID";
    public static final String DATA_SET = "DATA_SET";
    public static final String FEATURE_X = "FEATURE_%d";
    public static final String NUMBER_OF_DOCUMENTS = "NUMBER_OF_DOCUMENTS";
    public static final String LABELS = "LABELS";

    public static Schema getSchema(){
        Schema smm_schema = new Schema("SMA1415");
        addFeatureTable(smm_schema);
        addClusterTable(smm_schema);
        addLabelTable(smm_schema);
        addDocumentClusterMappingTable(smm_schema);
        return smm_schema;
    }

    private void exceptionCaught(SQLException e, String statement) {
        System.out.println(String.format("Exception caught while executing statement: %s", statement));
        e.printStackTrace();
    }

    private static void addLabelTable(Schema schema) {
        AbstractTableDefinition tableDef = new SingleTableDefiniton(getLabelTableName());
        
        addDatasetColumn(tableDef);
        addClusterIdColumn(tableDef);
        tableDef.addColumn(new Column(LABELS, Column.STRING));

        schema.addTable(tableDef);
    }

    private static void addDocumentClusterMappingTable(Schema schema) {
        AbstractTableDefinition tableDef = new SingleTableDefiniton(getDocumentClusterMappingTableName());
        
        addDatasetColumn(tableDef);
        addDocumentIdColumn(tableDef);
        addClusterIdColumn(tableDef);

        schema.addTable(tableDef);
    }

    private static void addClusterTable(Schema schema) {
        AbstractTableDefinition tableDef = new SingleTableDefiniton(getClusterTableName());
        
        addDatasetColumn(tableDef);
        addClusterIdColumn(tableDef);
        tableDef.addColumn(new Column(NUMBER_OF_DOCUMENTS, Column.INT));
        addFeatureColumns(tableDef);

        schema.addTable(tableDef);
    }

    private static void addFeatureTable(Schema schema) {
        AbstractTableDefinition tableDef = new SingleTableDefiniton(getFeatureTableName());
        
        addDatasetColumn(tableDef);
        addDocumentIdColumn(tableDef);
        addFeatureColumns(tableDef);

        schema.addTable(tableDef);
        
    }

    public static String getLabelTableName () {
        return "SAI_LABELS";
    }

    public static String getDocumentClusterMappingTableName (){
        return "SAI_CLUSTER_TO_DOCUMENT";
    }

    public static String getClusterTableName (){
        return "SAI_CLUSTERS";
    }

    public static String getFeatureTableName (){
        return "SAI_FEATURES";
    }

    private static void addDatasetColumn(AbstractTableDefinition tableDef) {
        tableDef.addColumn(new Column(DATA_SET, Column.STRING));
    }

    private static void addFeatureColumns(AbstractTableDefinition tableDef) {
        for (int i = 0; i < SchemaConfig.NUMBER_OF_FEATURE_COLUMNS; i++){
            tableDef.addColumn(new Column(String.format(FEATURE_X, i), Column.DOUBLE));
        }
    }

    private static void addClusterIdColumn(AbstractTableDefinition tableDef) {
        tableDef.addColumn(new Column(CLUSTER_ID, Column.INT));
    }

    private static void addDocumentIdColumn(AbstractTableDefinition tableDef) {
        tableDef.addColumn(new Column(DOCUMENT_ID, Column.INT));
    }

}
