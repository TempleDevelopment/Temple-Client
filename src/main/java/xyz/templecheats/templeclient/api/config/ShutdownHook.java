package xyz.templecheats.templeclient.api.config;

import xyz.templecheats.templeclient.TempleClient;

/**
 * @author XeonLyfe
 */

public class ShutdownHook extends Thread {

    @Override
    public void run() {
        TempleClient.configManager.saveAll();
    }
}