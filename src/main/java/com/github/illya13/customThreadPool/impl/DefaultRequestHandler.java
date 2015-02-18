package com.github.illya13.customThreadPool.impl;

import com.github.illya13.customThreadPool.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRequestHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestHandler.class);

    private final Request request;

    public DefaultRequestHandler(Request request) {
        this.request = request;
    }

    @Override
    public void run() {
        LOGGER.info(Thread.currentThread().getName() + ": starting " + request);
        try {
            Thread.sleep(request.getExecutionTime());
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.info(Thread.currentThread().getName() + ": done " + request);
    }
}
