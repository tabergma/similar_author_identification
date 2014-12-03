package de.hpi.smm.helper;

public class MutableInt {
    int value = 0;

    public void increment () {
        ++value;
    }

    public int get () {
        return value;
    }
}