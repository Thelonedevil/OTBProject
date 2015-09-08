package com.github.otbproject.otbproject.web;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.config.Configs;
import com.github.otbproject.otbproject.config.WebConfig;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.version.Version;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class WebInterface {
    public static void start() {
        File path = new File(warPath());
        if (App.VERSION.type == Version.Type.SNAPSHOT) {
            App.logger.warn("You are running a dev build of OTB. Please grab the latest build of the web interface and place in \"" +
                    FSUtil.webDir() + File.separator + "\" as \"web-interface-" + WebVersion.latest() +
                    ".war\". Releases will automatically download the latest version of the web interface for you");
        } else if (!path.exists() || (Configs.getFromWebConfig(WebConfig::isAutoUpdating) && (WebVersion.current().compareTo(WebVersion.latest()) < 0))) {
            WarDownload.downloadLatest();
        }
        startInterface(Configs.getFromWebConfig(WebConfig::getPortNumber), Configs.getFromWebConfig(WebConfig::getIpBinding));
    }

    private static void startInterface(int port, String address) {
        App.logger.info("Starting web interface version " + WebVersion.current());
        Server server = new Server();
        ServerConnector http = new ServerConnector(server);
        http.setHost(address);
        http.setPort(port);
        server.addConnector(http);
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setWar(warPath());
        server.setHandler(webapp);
        try {
            server.start();
        } catch (Exception e) {
            App.logger.catching(e);
        }
    }

    static String warPath() {
        return FSUtil.webDir() + File.separator + "web-interface-" + WebVersion.current() + ".war";
    }

    public static void openInBrowser() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI.create("http://127.0.0.1:" + Configs.getFromWebConfig(WebConfig::getPortNumber)));
            } catch (IOException e) {
                App.logger.catching(e);
            }
        } else {
            App.logger.warn("Unable to open web interface in browser - desktop not supported");
        }
    }
}
