package de.hpi.smm.helper;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Util {
    public static
    <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }

    private static PrintStream errStream = null;

    public static void switchErrorPrint(boolean showErrorPrint){
        if (errStream == null){
            errStream = System.err;
        }
        if (showErrorPrint) {
            System.setErr(errStream);
        } else {
            System.setErr(new PrintStream(new OutputStream() {
                public void write(int b) {
                }
            }));
        }
    }
}
