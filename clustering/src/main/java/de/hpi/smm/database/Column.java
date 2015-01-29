package de.hpi.smm.database;

public class Column {
    public static final int STRING = 0;
    public static final int DOUBLE = 1;
    public static final int INT = 2;

    private final int type;
    private final String name;

    public Column(String name, int type){
        this.name = name;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
