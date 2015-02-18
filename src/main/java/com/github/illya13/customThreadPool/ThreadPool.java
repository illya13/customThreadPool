package com.github.illya13.customThreadPool;

public interface ThreadPool {
    public void submit(Runnable runnable);
    public void shutdown();
    public void awaitTermination() throws InterruptedException;
}
