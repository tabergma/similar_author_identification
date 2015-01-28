package de.hpi.smm.database;

import java.sql.PreparedStatement;

public class JoinedTableDefinition extends AbstractTableDefinition {
    private final AbstractTableDefinition firstTable;
    private final AbstractTableDefinition secondTable;
    private final String joinKey;

    public JoinedTableDefinition(AbstractTableDefinition table1, AbstractTableDefinition table2, String joinKey) {
        super(String.format("%s_JOIN_%s", table1.getName(), table2.getName()));
        this.firstTable = table1;
        this.secondTable = table2;
        this.joinKey = joinKey;

        Column joinKeyColumn = firstTable.getColumns().get(firstTable.getCachedIndex(joinKey));
        this.addColumn(new Column(String.format("%s.%s", firstTable.getName(), joinKey), joinKeyColumn.getType()));

        addAllColumnsExcept(firstTable, joinKey);
        addAllColumnsExcept(secondTable, joinKey);
    }

    private void addAllColumnsExcept(AbstractTableDefinition tableDefinition, String exception) {
        for (Column column : tableDefinition.getColumns()){
            if (!column.getName().equals(exception) && !column.getName().equals(SchemaConfig.DATA_SET)){
                Column newColumn = new Column(String.format("%s.%s", tableDefinition.getName(), column.getName()), column.getType());
                this.addColumn(newColumn);
            }
        }
    }

    @Override
    public PreparedStatement getPreparedStatement() {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public void setPreparedStatement(PreparedStatement preparedStatement) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public String formatInsertStatement(String schemaName) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public String formatReadStatement(String schemaName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ");
        appendColumnNames(stringBuilder);
        stringBuilder.append(" FROM ");
        firstTable.appendTableName(stringBuilder, schemaName);
        stringBuilder.append(" JOIN ");
        secondTable.appendTableName(stringBuilder, schemaName);
        stringBuilder.append(" ON ");
        stringBuilder.append(firstTable.getName());
        stringBuilder.append(".");
        stringBuilder.append(joinKey);
        stringBuilder.append(" = ");
        stringBuilder.append(secondTable.getName());
        stringBuilder.append(".");
        stringBuilder.append(joinKey);
        return stringBuilder.toString();
    }

    @Override
    public void setValue(String columnName, int value) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public void setValue(String columnName, String value) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public void setFeatureValue(int featureNumber, double value) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public void writeRecord() {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public void setRecordValuesToNull() {
        throw new RuntimeException("method not implemented");
    }
}
