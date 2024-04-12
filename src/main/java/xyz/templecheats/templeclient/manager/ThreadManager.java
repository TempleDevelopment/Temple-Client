package xyz.templecheats.templeclient.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public void invokeThread(final Runnable command) {
        executorService.execute(command);
    }
}