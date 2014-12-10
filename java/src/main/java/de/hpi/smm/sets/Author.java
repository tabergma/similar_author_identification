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
//        if (other == null){
//            return this.getName().compareTo(null);
//        }
        return this.getName().compareTo(other.getName());
    }
}
