package de.hpi.smm.database;

import java.sql.PreparedStatement;

public class JoinedTableDefinition extends AbstractTableDefinition {
    private final AbstractTableDefinition firstTable;
    private final AbstractTableDefinition secondTable;
    private final String firstJoinKey;
    private final String secondJoinKey;

    private String joinType = "";

    public JoinedTableDefinition(AbstractTableDefinition table1, String joinKey1, AbstractTableDefinition table2, String joinKey2) {
        super(String.format("%s_JOIN_%s", table1.getName(), table2.getName()));
        this.firstTable = table1;
        this.secondTable = table2;
        this.firstJoinKey = joinKey1;
        this.secondJoinKey = joinKey2;

        int columnIndex = firstTable.getCachedIndex(this.firstJoinKey);
        Column joinKeyColumn = firstTable.getColumns().get(columnIndex);
        this.addColumn(new Column(String.format("%s.%s", firstTable.getName(), this.firstJoinKey), joinKeyColumn.getType()));

        addAllColumnsExcept(firstTable, this.firstJoinKey);
        addAllColumnsExcept(secondTable, this.secondJoinKey);
    }

    private void addAllColumnsExcept(AbstractTableDefinition tableDefinition, String exception) {
        for (Column column : tableDefinition.getColumns()){
            if (!column.getName().equals(exception) && !column.getName().equals(SchemaConfig.RUN_ID)){
                Column newColumn = new Column(String.format("%s.%s", tableDefinition.getName(), column.getName()), column.getType());
                this.addColumn(newColumn);
            }
        }
    }

    protected int getCachedIndex(String columnName) {
        String completeColumnName = "";
        if (firstTable.hasColumn(columnName)){
            completeColumnName = String.format("%s.%s", firstTable.getName(), columnName);
        } else {
            completeColumnName = String.format("%s.%s", secondTable.getName(), columnName);
        }
        return super.getCachedIndex(completeColumnName);
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
    public String formatInsertStatement() {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public String formatCompleteReadStatement() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ");
        appendColumnNames(stringBuilder);
        stringBuilder.append(" FROM ");
        firstTable.appendTableName(stringBuilder);
        stringBuilder.append(this.joinType);
        stringBuilder.append(" JOIN ");
        secondTable.appendTableName(stringBuilder);
        stringBuilder.append(" ON ");
        stringBuilder.append(firstTable.getName());
        stringBuilder.append(".");
        stringBuilder.append(firstJoinKey);
        stringBuilder.append(" = ");
        stringBuilder.append(secondTable.getName());
        stringBuilder.append(".");
        stringBuilder.append(secondJoinKey);
        if (firstTable.getWhereClause().equals("") && secondTable.getWhereClause().equals("")){
            //
        } else if (!firstTable.getWhereClause().equals("")){
            stringBuilder.append(" WHERE ");
            stringBuilder.append(firstTable.getWhereClause());
        } else if (!secondTable.getWhereClause().equals("")){
            stringBuilder.append(" WHERE ");
            stringBuilder.append(secondTable.getWhereClause());
        } else {
            stringBuilder.append(" WHERE ");
            stringBuilder.append(firstTable.getWhereClause());
            stringBuilder.append(" AND ");
            stringBuilder.append(secondTable.getWhereClause());
        }
        return stringBuilder.toString();
    }

    @Override
    public String formatReadStatement() {
        throw new RuntimeException("method not implemented");
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

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }
}
