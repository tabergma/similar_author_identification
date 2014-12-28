package de.hpi.smm.sets;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class SetDownloader {
    private final String downloadPath;
    private final HanaSet hanaSet;

    public SetDownloader(HanaSet hanaSet, String localSetPath) {
        this.downloadPath = localSetPath;
        this.hanaSet = hanaSet;
    }

    public void run() {
        int i = 0;
        while(hanaSet.next()){
            String content = hanaSet.getText();
            Author author = hanaSet.getAuthor(i);
            File filePath = new File(String.format("%s%s/%d", downloadPath, author.toString(), i));
            try {
                FileUtils.writeStringToFile(filePath, content);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            i++;
            if (i % 100 == 0){
                System.out.println(String.format("Downloaded %d documents...", i));
            }

        }

    }
}
