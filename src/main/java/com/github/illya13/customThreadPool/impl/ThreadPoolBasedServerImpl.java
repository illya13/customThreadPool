package com.github.illya13.customThreadPool.impl;

import com.github.illya13.customThreadPool.Request;
import com.github.illya13.customThreadPool.Server;
import com.github.illya13.customThreadPool.ThreadPool;

public class ThreadPoolBasedServerImpl implements Server {
    private final ThreadPool threadPool;

    public ThreadPoolBasedServerImpl(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public void handle(Request request) {
        threadPool.submit(new DefaultRequestHandler(request));
    }
}
