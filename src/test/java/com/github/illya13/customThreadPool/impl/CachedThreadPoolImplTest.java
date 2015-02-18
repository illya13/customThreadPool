package com.github.illya13.customThreadPool.impl;

import com.github.illya13.customThreadPool.Request;
import com.github.illya13.customThreadPool.Server;
import com.github.illya13.customThreadPool.ThreadPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.Assert.*;

public class CachedThreadPoolImplTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedThreadPoolImplTest.class);

    private ThreadPool threadPool;
    private Server server;

    @Before
    public void before() throws Exception {
        threadPool = new CachedThreadPoolImpl(8);
        server = new ThreadPoolBasedServerImpl(threadPool);
    }

    @After
    public void after() throws Exception {
        threadPool.shutdown();
        threadPool.awaitTermination();
    }

    @Test
    public void testThreadReUse() throws Exception {
        Request request1 = new Request(101, 1001);
        server.handle(request1);
        Thread.sleep(3000);

        Request request2 = new Request(102, 1002);
        server.handle(request2);

        Thread.sleep(3000);

        assertTrue(((ThreadPoolBasedServerImpl) server).isRequestProcessedByTheSameThreadAsAnotherRequest(101, 102));
    }

    @Test
    public void testUseMaxThreadsAndQueue() throws Exception {
        Random rnd = new Random();

        for(int i=0; i<100; i++) {
            Request request = new Request(i, 1000 + rnd.nextInt(300));
            server.handle(request);
        }

        Thread.sleep(30 * 1000);

        for(int i=0; i<100; i++) {
            assertTrue(((ThreadPoolBasedServerImpl) server).isRequestProcessed(i));
        }
    }

    @Test
    public void testThreadsAfterBeenIdle() throws Exception {
        Random rnd = new Random();

        for(int i=0; i<100; i++) {
            Request request = new Request(i, 1000 + rnd.nextInt(300));
            server.handle(request);
        }

        Thread.sleep(30 * 1000);

        for(int i=0; i<100; i++) {
            assertTrue(((ThreadPoolBasedServerImpl) server).isRequestProcessed(i));
        }

        Request request1 = new Request(101, 1001);
        Request request2 = new Request(102, 1002);

        server.handle(request1);
        server.handle(request2);

        Thread.sleep(3000);

        assertFalse(((ThreadPoolBasedServerImpl) server).isRequestProcessedByTheSameThreadAsAnotherRequest(101, 102));
    }

    private static class ThreadPoolBasedServerImpl implements Server {
        private final ThreadPool threadPool;
        private final Map<Long, Thread> processed;

        public ThreadPoolBasedServerImpl(ThreadPool threadPool) {
            this.threadPool = threadPool;

            processed = new HashMap<Long, Thread>();
        }

        @Override
        public void handle(final Request request) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    LOGGER.info(Thread.currentThread().getName() + ": starting " + request);
                    try {
                        Thread.sleep(request.getExecutionTime());
                        processed.put(request.getId(), Thread.currentThread());
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                    LOGGER.info(Thread.currentThread().getName() + ": done " + request);
                }
            });
        }

        public boolean isRequestProcessed(long id) {
            return processed.containsKey(id);
        }

        public boolean isRequestProcessedByTheSameThreadAsAnotherRequest(long idOfRequest, long idOfAnotherRequest) {
            return processed.get(idOfRequest).equals(processed.get(idOfAnotherRequest));
        }

    }
}