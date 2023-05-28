package edu.byu.cs.tweeter.client.model.service.backgroundTask.Handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;

public class GetPagedItemsHandler<T> extends BackgroundTaskHandler<Service.GetPagedItemsObserver> {
    private String errorMessageFiller;
    public GetPagedItemsHandler(Service.GetPagedItemsObserver observer, String errorMessageFiller) {
        super(observer);
        this.errorMessageFiller=errorMessageFiller;
    }

    @Override
    protected void handleSuccess(Bundle data, Service.GetPagedItemsObserver observer) {
        List<T> items = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);

        observer.handleSuccess(items, hasMorePages);
    }

    @Override
    protected String getMessageFiller() {
        return errorMessageFiller;
    }
}
