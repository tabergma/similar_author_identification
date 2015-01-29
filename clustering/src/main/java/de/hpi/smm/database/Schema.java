package de.hpi.smm.database;

import java.util.HashMap;
import java.util.Map;

public class Schema {
    private Map<String, AbstractTableDefinition> tableDefinitions = new HashMap<String, AbstractTableDefinition>();

    public void addTable(AbstractTableDefinition tableDefinition){
        tableDefinitions.put(tableDefinition.getName(), tableDefinition);
    }

    public Map<String, AbstractTableDefinition> getTableDefinitions() {
        return tableDefinitions;
    }

    public AbstractTableDefinition getTableDefinition(String tableName) {
        return tableDefinitions.get(tableName);
    }
}
