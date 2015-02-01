package de.hpi.smm.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTableDefinition {
    private List<Column> columns = new ArrayList<Column>();
    private Map<String, Integer> nameToIndex = new HashMap<String, Integer>();
    private String name;
    protected int firstFeatureColumn = -1;
    private ResultSet resultSet = null;
    String whereClause = "";

    public AbstractTableDefinition(String name){
        this.name = name;
    }

    public void addColumn(Column column){
        int index = this.columns.size();
        this.columns.add(column);
        nameToIndex.put(column.getName(), index);
        if (column.getName().contains("FEATURE_0")){
            firstFeatureColumn = index + 1;
        }
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    protected void appendColumnNames(StringBuilder stringBuilder) {
        for (int i = 0; i < columns.size(); i++){
            stringBuilder.append(columns.get(i).getName());
            if (i < columns.size() - 1) {
                stringBuilder.append(", ");
            }
        }
    }

    public void addWhereClause(String expression){
        if (whereClause.equals("")){
            whereClause = expression;
        } else {
            whereClause = String.format("%s AND %s", whereClause, expression);
        }
    }

    public String getWhereClause(){
        return whereClause;
    }

    protected void appendTableName(StringBuilder stringBuilder) {
        stringBuilder.append(this.name);
    }

    public String getName(){
        return this.name;
    }

    public int getSize(){
        return columns.size();
    }

    public abstract PreparedStatement getPreparedStatement();

    public abstract void setPreparedStatement(PreparedStatement preparedStatement);

    public abstract String formatInsertStatement();

    public String formatCompleteReadStatement(){
        if (getWhereClause().equals("")){
            return formatReadStatement();
        } else {
            return String.format("%s WHERE %s", formatReadStatement(), getWhereClause());
        }
    }

    public abstract String formatReadStatement();

    protected int getCachedIndex(String columnName) {
        return nameToIndex.get(columnName);
    }

    public abstract void setValue(String columnName, int value);

    public abstract void setValue(String columnName, String value);

    public abstract void setFeatureValue(int featureNumber, double value);

    public abstract void writeRecord();

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public boolean next() {
        try {
            return this.resultSet.next();
        } catch (SQLException e) {
            nextValueExceptionCaught(e);
            return false;
        }
    }

    public String getString(String columnName) {
        try {
            return resultSet.getString(getCachedIndex(columnName) + 1);
        } catch (SQLException e) {
            readValueExceptionCaught(e);
            return null;
        }
    }

    public int getInt(String columnName) {
        try {
            return resultSet.getInt(getCachedIndex(columnName) + 1);
        } catch (SQLException e) {
            readValueExceptionCaught(e);
            return -1;
        }
    }

    public double getFeatureValue(int index) {
        try {
            double value = resultSet.getDouble(firstFeatureColumn + index);
            if (resultSet.wasNull()){
                return -1.0;
            } else {
                return value;
            }
        } catch (SQLException e) {
            readValueExceptionCaught(e);
            return -1.0;
        }
    }

    private void nextValueExceptionCaught(SQLException e) {
        e.printStackTrace();
    }

    protected void setValueExceptionCaught(SQLException e) {
        e.printStackTrace();
    }

    protected void executeBatchExceptionCaught(SQLException e) {
        e.printStackTrace();
    }

    private void readValueExceptionCaught(SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Exception caught while reading a value!");
    }

    public abstract void setRecordValuesToNull();
}
