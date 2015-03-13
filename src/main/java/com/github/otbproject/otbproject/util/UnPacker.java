package com.github.otbproject.otbproject.util;

import com.github.otbproject.otbproject.App;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;

/**
 * Created by Justin on 13/03/2015.
 */
public class UnPacker {

    public UnPacker(String dir,String[] files, String destDir){
        for (String file : files) {
            InputStream input = getClass().getResourceAsStream(dir+"/"+file);
            try {
                File fileOut = new File(destDir+ File.separator+file);
                Files.copy(input,fileOut.toPath());
            } catch (IOException e) {
                App.logger.catching(e);
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    App.logger.catching(e);
                }
            }
        }
    }
}
