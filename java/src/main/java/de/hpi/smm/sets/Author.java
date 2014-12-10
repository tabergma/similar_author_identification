package de.hpi.smm.sets;

public class Author {
    private final String name;

    public Author(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isUnknown() {
        return name == null;
    }
}
