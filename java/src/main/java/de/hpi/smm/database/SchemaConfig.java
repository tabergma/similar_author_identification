package de.hpi.smm.database;

import de.hpi.smm.sets.DataSetSelector;

public class SchemaConfig {
    private static final int NUMBER_OF_FEATURE_COLUMNS = 900;

    public static Schema getSchema(){
        Schema smm_schema = new Schema("SMA1415");
        addFeatureTable(smm_schema);
        addClusterTable(smm_schema);
        addLabelTable(smm_schema);
        addDocumentClusterMappingTable(smm_schema);
        return smm_schema;
    }

    private static void addLabelTable(Schema schema) {
        TableDefinition tableDef = new TableDefinition(getLabelTableName());
        
        addDatasetColumn(tableDef);
        addClusterIdColumn(tableDef);
        tableDef.addColumn(new Column("LABELS", Column.STRING));

        schema.addTable(tableDef);
    }

    private static void addDocumentClusterMappingTable(Schema schema) {
        TableDefinition tableDef = new TableDefinition(getDocumentClusterMappingTableName());
        
        addDatasetColumn(tableDef);
        addDocumentIdColumn(tableDef);
        addClusterIdColumn(tableDef);

        schema.addTable(tableDef);
    }

    private static void addClusterTable(Schema schema) {
        TableDefinition tableDef = new TableDefinition(getClusterTableName());
        
        addDatasetColumn(tableDef);
        addClusterIdColumn(tableDef);
        addFeatureColumns(tableDef);
        tableDef.addColumn(new Column("NUMBER_OF_DOCUMENTS", Column.INT));

        schema.addTable(tableDef);
    }

    private static void addFeatureTable(Schema schema) {
        TableDefinition tableDef = new TableDefinition(getFeatureTableName());
        
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

    private static void addDatasetColumn(TableDefinition tableDef) {
        tableDef.addColumn(new Column("DATA_SET", Column.STRING));
    }

    private static void addFeatureColumns(TableDefinition tableDef) {
        for (int i = 0; i < SchemaConfig.NUMBER_OF_FEATURE_COLUMNS; i++){
            tableDef.addColumn(new Column(String.format("FEATURE_%d", i), Column.DOUBLE));
        }
    }

    private static void addClusterIdColumn(TableDefinition tableDef) {
        tableDef.addColumn(new Column("CLUSTER_ID", Column.INT));
    }

    private static void addDocumentIdColumn(TableDefinition tableDef) {
        tableDef.addColumn(new Column("DOCUMENT_ID", Column.INT));
    }

}
