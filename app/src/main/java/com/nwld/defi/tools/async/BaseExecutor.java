package com.nwld.defi.tools.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseExecutor {
    private ExecutorService fixedThreadPool;

    private BaseExecutor() {
        fixedThreadPool = Executors.newFixedThreadPool(6);
    }

    private static class BaseExecutorHolder {
        private static final BaseExecutor executor = new BaseExecutor();
    }

    public static BaseExecutor getInstance() {
        return BaseExecutorHolder.executor;
    }

    public void execute(Runnable runnable) {
        fixedThreadPool.execute(runnable);
    }
}
