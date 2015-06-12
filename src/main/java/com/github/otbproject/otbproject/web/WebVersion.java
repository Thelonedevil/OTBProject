package com.github.otbproject.otbproject.web;

import com.github.otbproject.otbproject.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class WebVersion {

    public static String getWebVersion(){
        String xmlPath = "http://ts.tldcode.uk:8081/nexus/content/repositories/releases/com/github/otbproject/web-interface/maven-metadata.xml";
        URL xmlURL;
        BufferedReader in = null;
        try {
            xmlURL = new URL(xmlPath);
            in = new BufferedReader(new InputStreamReader(xmlURL.openStream()));

            //TODO get nth to turn this into a stream using in.lines()
            while (true){
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                if(line.trim().startsWith("<release>")){
                    return line.replace("<release>","").replace("</release>","").trim();
                }

            }
        } catch (IOException e) {
            App.logger.catching(e);
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    App.logger.catching(e);
                }
            }
        }
        return "0.0.1";
    }
}