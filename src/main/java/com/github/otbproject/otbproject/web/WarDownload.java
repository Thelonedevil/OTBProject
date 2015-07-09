package com.github.otbproject.otbproject.web;

import com.github.otbproject.otbproject.App;
import com.github.otbproject.otbproject.util.Util;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

class WarDownload implements Runnable {

    public static void downloadLatest() {
        ExecutorService executor = Util.getSingleThreadExecutor("War Download");
        App.logger.info("Downloading web interface");
        try {
            executor.submit(new WarDownload());
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
            if (!executor.isTerminated()) {
                executor.shutdownNow();
                throw new WarDownloadException("Download took too long");
            }
        } catch (InterruptedException | WarDownloadException e) {
            App.logger.error("Error downloading web interface");
            App.logger.catching(e);
            App.logger.error("Please download the web interface manually");
            // TODO clean up failed download
        }
    }

    @Override
    public void run() {
        String war = "http://ts.tldcode.uk:8081/nexus/content/repositories/releases/com/github/otbproject/web-interface/"+WebVersion.latest()+"/"+"web-interface-"+WebVersion.latest()+".war";
        URL website = null;
        try {
            website = new URL(war);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(WebInterface.WAR_PATH);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            URL md5Original = new URL(war+".md5");
            BufferedReader in = new BufferedReader(new InputStreamReader(md5Original.openStream()));
            String inputLine = in.readLine();
            in.close();
            FileInputStream fis = new FileInputStream(new File(WebInterface.WAR_PATH));
            String md5 = DigestUtils.md5Hex(fis);
            fis.close();
            if(!slowEquals(fromHex(md5), fromHex(inputLine))){
                App.logger.error("Download of War file either corrupted or some 3rd party has changed the file");
            }
        } catch (IOException e) {
           App.logger.catching(e);
        }
    }

    public static boolean slowEquals(byte[] a, byte[] b)
    {
        int diff = a.length ^ b.length;
        for(int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }
    public static byte[] fromHex(String hex)
    {
        byte[] binary = new byte[hex.length() / 2];
        for(int i = 0; i < binary.length; i++)
        {
            binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return binary;
    }
}
