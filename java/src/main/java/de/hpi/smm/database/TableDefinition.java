package de.hpi.smm.database;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class TableDefinition {
    private List<Column> columns = new ArrayList<Column>();
    private String name;
    private PreparedStatement preparedStatement;

    public TableDefinition(String name){
        this.name = name;
    }

    public void addColumn(Column column){
        this.columns.add(column);
    }

    public String getName(){
        return this.name;
    }

    public int getSize(){
        return columns.size();
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setPreparedStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }
}
