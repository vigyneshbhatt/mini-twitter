package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundTaskUtils {

    private static final String LOG_TAG = "BackgroundTaskUtils";

    public static void runTask(Runnable task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }

}
