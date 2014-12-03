package de.hpi.smm.sets;


import de.hpi.smm.helper.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalSet implements TestSet {
    private List<File> posts = new ArrayList<File>();
    int i = -1;

    public LocalSet (String path) {
        List<File> authorFolders = Util.asSortedList(Arrays.asList(new File(path).listFiles()));
        for (File folder : authorFolders){
            if (folder.isDirectory()){
                posts.addAll(Util.asSortedList(Arrays.asList(folder.listFiles())));
            }
        }
    }

    @Override
    public boolean next() {
        i++;
        return i < posts.size();
    }

    @Override
    public String getText() {
        return fileToString(posts.get(i));
    }

    private String fileToString(File file) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
