package com.github.otbproject.otbproject.web;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.fs.FSUtil;
import com.github.otbproject.otbproject.util.Util;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.*;

class WarDownload implements Callable<Boolean> {
    private static final int ATTEMPTS = 2;

    public static void downloadLatest() {
        ExecutorService executor = Util.getSingleThreadExecutor("War Download");
        App.logger.info("Downloading web interface");

        boolean success = false;

        for (int i = 1; i <= ATTEMPTS; i++) {
            App.logger.info("Attempting to download web interface (" + i + "/" + ATTEMPTS + ")");
            try {
                Future<Boolean> future = executor.submit(new WarDownload());
                future.get(1, TimeUnit.MINUTES);
                moveTempDownload();
                success = true;
                break;
            } catch (Exception e) {
                App.logger.error("Error downloading web interface");
                App.logger.catching(e);
                App.logger.error("Failed attempt to download web interface (" + i + "/" + ATTEMPTS + ")");
                cleanupTempDownload();
            }
        }

        if (success) {
            App.logger.info("Successfully downloaded web interface");
        } else {
            App.logger.error("Failed to download web interface.");
            App.logger.warn("Please download the web interface yourself from 'https://github.com/OTBProject/OTBWebInterface/releases/latest'," +
                    " and put it in: " + FSUtil.webDir() + File.separator);
        }

    }

    private static void cleanupTempDownload() {
        // TODO write
    }

    private static void moveTempDownload() {
        // TODO write
    }

    @Override
    public Boolean call() throws WarDownloadException {
        String warURL = "https://github.com/OTBProject/OTBWebInterface/releases/download/" + WebVersion.latest() + "/web-interface.war";
        URL website = null;
        try {
            website = new URL(warURL);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(WebInterface.WAR_PATH + ".download");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            URL md5Original = new URL(warURL+".md5");
            BufferedReader in = new BufferedReader(new InputStreamReader(md5Original.openStream()));
            String inputLine = in.readLine();
            in.close();
            FileInputStream fis = new FileInputStream(new File(WebInterface.WAR_PATH));
            String md5 = DigestUtils.md5Hex(fis);
            fis.close();
            if(!md5.equals(inputLine)){
                throw new WarDownloadException("Download of War file either corrupted or some 3rd party has changed the file");
            }
        } catch (IOException e) {
           throw new WarDownloadException(e);
        }
        return Boolean.TRUE;
    }
}
