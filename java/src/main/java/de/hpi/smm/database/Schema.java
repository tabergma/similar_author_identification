package de.hpi.smm.database;

import java.util.HashMap;
import java.util.Map;

public class Schema {
    private final String name;
    private Map<String, TableDefinition> tableDefinitions = new HashMap<String, TableDefinition>();

    public Schema(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addTable(TableDefinition tableDefinition){
        tableDefinitions.put(tableDefinition.getName(), tableDefinition);
    }

    public Map<String, TableDefinition> getTableDefinitions() {
        return tableDefinitions;
    }

    public TableDefinition getTableDefinition(String tableName) {
        return tableDefinitions.get(tableName);
    }
}
