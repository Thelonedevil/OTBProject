package com.github.otbproject.otbproject.web;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.config.Configs;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.version.Version;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class WebInterface {

    // Resource path pointing to where the WEBROOT is
    public static final String WAR_PATH = FSUtil.webDir()+ File.separator+"web-interface.war";
    public static void start() {
        File path = new File(WAR_PATH);
        if (App.VERSION.type == Version.Type.SNAPSHOT) {
            App.logger.warn("You are running a dev build of OTBProject, please also grab the latest build of the web interface and place in \"" +
                    FSUtil.webDir() + File.separator + "\" as \"web-interface-" + WebVersion.latest() +
                    ".war\". Releases will automatically download this for you");
        } else if (!path.exists() || (WebVersion.current().compareTo(WebVersion.latest()) < 0)) {
            WarDownload.downloadLatest();
        }
        startInterface(Configs.getWebConfig().getPortNumber(), Configs.getWebConfig().getIp_binding());
    }

    private static void startInterface(int port, String address) {
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
