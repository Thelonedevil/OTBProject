package com.github.otbproject.otbproject.web;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.VersionClass;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import java.io.File;

public class WebStart {

    // Resource path pointing to where the WEBROOT is
    public static final String WAR_PATH= FSUtil.webDir()+ File.separator+"WebInterface-"+new VersionClass().getVersion()+".war";
    public static void main(String[] args) throws Exception {
        int port = 8081;
        WebStart main = new WebStart(port);
    }


    public WebStart(int port) {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        try {
            tomcat.addWebapp("/", new File(WAR_PATH).getAbsolutePath());
        } catch (ServletException e) {
            App.logger.catching(e);
        }
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            App.logger.catching(e);
        }
        tomcat.getServer().await();
    }

}
