package de.hpi.smm.helper;

import org.apache.hadoop.fs.FileStatus;

public class FileInfo {

    public static void print (FileStatus fs) {
        System.out.println(String.format("%s %s", fs.getPath(), fs.getLen()));
    }
}
