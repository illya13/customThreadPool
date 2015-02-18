package com.github.illya13.customThreadPool.server;

import com.github.illya13.customThreadPool.request.Request;

public interface Server {
    public void handle(Request request);
}
