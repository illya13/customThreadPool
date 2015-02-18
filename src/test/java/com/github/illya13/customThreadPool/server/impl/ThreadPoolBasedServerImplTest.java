package com.github.illya13.customThreadPool.server.impl;

import com.github.illya13.customThreadPool.request.Request;
import com.github.illya13.customThreadPool.server.Server;
import com.github.illya13.customThreadPool.server.ThreadPool;
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

        Request request1 = new Request(101, 1000 + rnd.nextInt(300));
        Request request2 = new Request(102, 1000 + rnd.nextInt(300));
        server.handle(request1);
        server.handle(request2);

        Thread.sleep(3 * 1000);

        threadPool.shutdown();
        threadPool.awaitTermination();
    }
}