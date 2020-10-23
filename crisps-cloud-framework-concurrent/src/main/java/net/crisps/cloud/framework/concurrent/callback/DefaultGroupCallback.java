package net.crisps.cloud.framework.concurrent.callback;

import net.crisps.cloud.framework.concurrent.wrapper.WorkerWrapper;

import java.util.List;

public class DefaultGroupCallback implements IGroupCallback {
    @Override
    public void success(List<WorkerWrapper> workerWrappers) {

    }

    @Override
    public void failure(List<WorkerWrapper> workerWrappers, Exception e) {

    }
}
