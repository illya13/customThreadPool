package com.github.illya13.customThreadPool.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private final Request request;

    public RequestHandler(Request request) {
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
