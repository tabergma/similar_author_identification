package de.hpi.smm.sets;

public class Author implements Comparable<Author> {

    public static final String UNKNOWN = "UNKNOWN";
    private final String name;

    public Author(String name){
        if (name == null || name == UNKNOWN){
            this.name = null;
        } else {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

    public boolean isUnknown() {
        return name == null;
    }

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
            return UNKNOWN;
        }
        return name.toString();
    }
}
