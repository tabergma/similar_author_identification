package de.hpi.smm.sets;

import java.util.Comparator;

public class Author implements Comparable<Author> {
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

    @Override
    public int compareTo(Author other) {
        if (this.isUnknown() && other.isUnknown()) {
            return 0;
        }
        if (this.isUnknown()) {
            return 1;
        }
        if (other.isUnknown()) {
            return -1;
        }
        return this.getName().compareTo(other.getName());
    }

    @Override
    public String toString() {
        if (this.isUnknown()){
            return "UNKNOWN";
        }
        return name.toString();
    }
}
