package com.github.illya13.customThreadPool.server.impl;

import com.github.illya13.customThreadPool.request.RequestHandler;
import com.github.illya13.customThreadPool.request.Request;
import com.github.illya13.customThreadPool.server.Server;
import com.github.illya13.customThreadPool.server.ThreadPool;

public class ThreadPoolBasedServerImpl implements Server {
    private final ThreadPool threadPool;

    public ThreadPoolBasedServerImpl(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public void handle(Request request) {
        threadPool.submit(new RequestHandler(request));
    }
}
