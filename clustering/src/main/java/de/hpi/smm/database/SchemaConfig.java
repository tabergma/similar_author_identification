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
    public static final String DOCUMENT_CONTENT = ""; // TODO

    public static final String SMM_CONTENT = "POSTCONTENT";
    public static final String SMM_ID = "ID";
//    public static final String SMM_AUTHOR = "POSTAUTHOR";

    public static final String SPINN3R_CONTENT = "'\"content_extract\"'";
    public static final String SPINN3R_ID = "\"guid\"";
//    public static final String SPINN3R_AUTHOR = "\"atomname\"";

    public static final String MAIN_SCHEMA = "SMA1415";

    public static Schema getWholeSchema(int dataSet){
        Schema completeSchema = new Schema();
        addFeatureTable(completeSchema, dataSet);
        addClusterTable(completeSchema, dataSet);
        addLabelTable(completeSchema, dataSet);
        addDocumentClusterMappingTable(completeSchema, dataSet);
        addSpinn3rTable(completeSchema);
        addSmmTable(completeSchema);
        return completeSchema;
    }

    private static void addSmmTable(Schema schema) {
        AbstractTableDefinition tableDef = new SingleTableDefinition(getSmmTableName());

        tableDef.addColumn(new Column(SMM_CONTENT, Column.STRING));
        tableDef.addColumn(new Column(SMM_ID, Column.STRING));
//        tableDef.addColumn(new Column(SMM_AUTHOR, Column.STRING));

        schema.addTable(tableDef);
    }

    private static void addSpinn3rTable(Schema schema) {
        AbstractTableDefinition tableDef = new SingleTableDefinition(getSpinn3rTableName());

        tableDef.addColumn(new Column(SPINN3R_CONTENT, Column.STRING));
        tableDef.addColumn(new Column(SPINN3R_ID, Column.STRING));
//        tableDef.addColumn(new Column(SPINN3R_AUTHOR, Column.STRING));

        schema.addTable(tableDef);
    }

    private static void addLabelTable(Schema schema, int dataSet) {
        AbstractTableDefinition tableDef = new SingleTableDefinition(getLabelTableName(), dataSet);
        
        addDatasetColumn(tableDef);
        addClusterIdColumn(tableDef);
        tableDef.addColumn(new Column(LABELS, Column.STRING));

        schema.addTable(tableDef);
    }

    private static void addDocumentClusterMappingTable(Schema schema, int dataSet) {
        AbstractTableDefinition tableDef = new SingleTableDefinition(getDocumentClusterMappingTableName(), dataSet);
        
        addDatasetColumn(tableDef);
        addDocumentIdColumn(tableDef);
        addClusterIdColumn(tableDef);

        schema.addTable(tableDef);
    }

    private static void addClusterTable(Schema schema, int dataSet) {
        AbstractTableDefinition tableDef = new SingleTableDefinition(getClusterTableName(), dataSet);
        
        addDatasetColumn(tableDef);
        addClusterIdColumn(tableDef);
        tableDef.addColumn(new Column(NUMBER_OF_DOCUMENTS, Column.INT));
        addFeatureColumns(tableDef);

        schema.addTable(tableDef);
    }

    private static void addFeatureTable(Schema schema, int dataSet) {
        AbstractTableDefinition tableDef = new SingleTableDefinition(getFeatureTableName(), dataSet);
        
        addDatasetColumn(tableDef);
        addDocumentIdColumn(tableDef);
        addFeatureColumns(tableDef);

        schema.addTable(tableDef);
        
    }

    public static String getSpinn3rTableName() {
        return "SPINN3R.ENTRY";
    }

    public static String getSmmTableName() {
        return "SYSTEM.WEBPAGE";
    }

    public static String getLabelTableName () {
        return String.format("%s.%s", MAIN_SCHEMA, "SAI_LABELS");
    }

    public static String getDocumentClusterMappingTableName (){
        return String.format("%s.%s", MAIN_SCHEMA, "SAI_CLUSTER_TO_DOCUMENT");
    }

    public static String getClusterTableName (){
        return String.format("%s.%s", MAIN_SCHEMA, "SAI_CLUSTERS");
    }

    public static String getFeatureTableName (){
        return String.format("%s.%s", MAIN_SCHEMA, "SAI_FEATURES");
    }

    public static String getDocumentTableName () {
        return String.format("%s.%s", MAIN_SCHEMA, "SAI_FEATURES");
    }

    private static String getSchemaName(){
        return MAIN_SCHEMA;
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

    private void exceptionCaught(SQLException e, String statement) {
        System.out.println(String.format("Exception caught while executing statement: %s", statement));
        e.printStackTrace();
    }

}
