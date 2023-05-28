package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTask;

public abstract class BackgroundTaskHandler<T extends Service.ServiceObserver> extends Handler {
    private T observer;
    public BackgroundTaskHandler(T observer) {
        super(Looper.getMainLooper());
        this.observer=observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(BackgroundTask.SUCCESS_KEY);
        if (success) {
            handleSuccess(msg.getData(), observer);
        } else if (msg.getData().containsKey(BackgroundTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(BackgroundTask.MESSAGE_KEY);
            observer.handleFailure("Failed to " + getMessageFiller() + " : " + message);

        } else if (msg.getData().containsKey(BackgroundTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(BackgroundTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }

    protected abstract void handleSuccess(Bundle data, T observer);
    protected abstract String getMessageFiller();
}
