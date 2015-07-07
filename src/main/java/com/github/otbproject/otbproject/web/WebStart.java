package com.github.otbproject.otbproject.web;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.config.Configs;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.VersionClass;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class WebStart {

    // Resource path pointing to where the WEBROOT is
    public static final String WAR_PATH= FSUtil.webDir()+ File.separator+"web-interface-"+WebVersion.get()+".war";
    public static void main(String[] args) throws Exception {
        int port = Configs.getWebConfig().getPortNumber();
        String address = Configs.getWebConfig().getIp_binding();
        WebStart main = new WebStart(port,address);
    }


    public WebStart(int port,String address) {
        Server server = new Server();
        ServerConnector http = new ServerConnector(server);
        http.setHost(address);
        http.setPort(port);
        server.addConnector(http);
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(WAR_PATH);
        server.setHandler(webapp);
        try {
            server.start();
        } catch (Exception e) {
            App.logger.catching(e);
        }
    }

}
