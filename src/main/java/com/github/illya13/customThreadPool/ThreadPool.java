package com.github.illya13.customThreadPool;

public interface ThreadPool {
    public void submit(Runnable runnable);
    public void shutdown();
}
