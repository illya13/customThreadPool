package com.github.illya13.customThreadPool.impl;

import com.github.illya13.customThreadPool.Request;
import com.github.illya13.customThreadPool.Server;
import com.github.illya13.customThreadPool.ThreadPool;
import org.junit.Test;

import java.util.Random;

public class ThreadPoolBasedServerImplTest {
    @Test
    public void testHandle() throws Exception {
        ThreadPool threadPool = new CachedThreadPoolImpl(8);
        Server server = new ThreadPoolBasedServerImpl(threadPool);

        Random rnd = new Random();

        for(int i=0; i<100; i++) {
            Request request = new Request(i, 1000 + rnd.nextInt(300));
            server.handle(request);
        }

        Thread.sleep(30 * 1000);

        threadPool.shutdown();

        Thread.sleep(30 * 1000);
    }
}