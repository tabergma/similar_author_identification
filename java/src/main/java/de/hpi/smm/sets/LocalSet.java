package de.hpi.smm.sets;


import de.hpi.smm.helper.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LocalSet extends AbstractDataSet implements TestSet {
    private List<File> posts = new ArrayList<File>();
    private int i = -1;

    public LocalSet (String path, String setName) {
        super(-1, setName);
        File filePath = new File(path);
        if (!filePath.isDirectory()){
            return;
        }
        List<File> authorFolders = Util.asSortedList(Arrays.asList(filePath.listFiles()));
        for (File folder : authorFolders){
            System.out.println(String.format("Scanning %s%s...", path, folder.getName()));
            if (folder.isDirectory()){
                List<File> postsInDirectory = Util.asSortedList(Arrays.asList(folder.listFiles()));
                for (int j = 0; j < postsInDirectory.size(); j++){
                    putAuthorName(folder.getName());
                }
                posts.addAll(postsInDirectory);
            }
        }
    }

    public boolean next() {
        i++;
        return i < posts.size();
    }

    public String getText() {
        return fileToString(posts.get(i));
    }

    public Author getAuthor() {
        return authors.get(i);
    }

    private String fileToString(File file) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\\n");
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

    public boolean hasSize(int size) {
        if (size == -1){
            return true;
        }
        return size <= posts.size();
    }
}
