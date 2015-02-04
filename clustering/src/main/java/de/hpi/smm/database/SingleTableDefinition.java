package de.hpi.smm.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SingleTableDefinition extends AbstractTableDefinition {
    private static final int BATCH_SIZE = 100;

    private int currentBatchSize = 0;
    private PreparedStatement preparedStatement = null;

    public SingleTableDefinition(String combinedSchemaTableName) {
        super(combinedSchemaTableName);
    }

    public SingleTableDefinition(String combinedSchemaTableName, int runId) {
        super(combinedSchemaTableName);
        this.addWhereClause(String.format("%s.%s = '%s'", combinedSchemaTableName, SchemaConfig.RUN_ID, runId));
    }

    public SingleTableDefinition(String schema, String name) {
        super(String.format("%s.%s", schema, name));
    }

    @Override
    public String formatInsertStatement() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO ");
        appendTableName(stringBuilder);
        stringBuilder.append(" (");
        appendColumnNames(stringBuilder);
        stringBuilder.append(") VALUES (");
        for (int i = 0; i < this.getColumns().size(); i++){
            stringBuilder.append("?");
            if (i < this.getColumns().size() - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public String formatReadStatement() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ");
        appendColumnNames(stringBuilder);
        stringBuilder.append(" FROM ");
        appendTableName(stringBuilder);
        return stringBuilder.toString();
    }

    @Override
    public void setValue(String columnName, String value) {
        int index = getCachedIndex(columnName) + 1;
        try {
            this.preparedStatement.setString(index, value);
        } catch (SQLException e) {
            setValueExceptionCaught(e);
        }
    }

    @Override
    public void setValue(String columnName, int value) {
        int index = getCachedIndex(columnName) + 1;
        try {
            this.preparedStatement.setInt(index, value);
        } catch (SQLException e) {
            setValueExceptionCaught(e);
        }
    }

    @Override
    public void setFeatureValue(int featureNumber, double value) {
        try {
            this.preparedStatement.setDouble(firstFeatureColumn + featureNumber, value);
        } catch (SQLException e) {
            this.setValueExceptionCaught(e);
        }
    }

    @Override
    public void writeRecord() {
        try {
            this.preparedStatement.addBatch();
            if (++currentBatchSize == BATCH_SIZE){
                this.preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            executeBatchExceptionCaught(e);
        }
    }

    @Override
    public void setRecordValuesToNull() {
        for (int i = 0; i < this.getColumns().size(); i++) {
            try {
                this.preparedStatement.setString(i + 1, null);
            } catch (SQLException e) {
                this.setValueExceptionCaught(e);
            }
        }
    }

    @Override
    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    @Override
    public void setPreparedStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

}
