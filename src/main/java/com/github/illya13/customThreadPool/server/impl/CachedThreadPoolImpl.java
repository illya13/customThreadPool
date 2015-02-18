package com.github.illya13.customThreadPool.server.impl;

import com.github.illya13.customThreadPool.server.ThreadPool;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CachedThreadPoolImpl implements ThreadPool{
    private int currThreads;
    private final int maxThreads;

    private final List<Worker> workers;
    private final List<Thread> threads;
    private final Queue<Runnable> queue;

    private final Object workersLock;

    public CachedThreadPoolImpl(int maxThreads) {
        this.currThreads = 0;
        this.maxThreads = maxThreads;

        workers = new LinkedList<Worker>();
        threads = new LinkedList<Thread>();
        queue = new ConcurrentLinkedQueue<Runnable>();

        workersLock = new Object();
    }

    @Override
    public void submit(Runnable runnable) {
        // find idle if any
        for(Worker worker: workers) {
            if (worker.checkAndSetJob(runnable))
                return;
        }

        // can we start a new one
        synchronized (workersLock) {
            if (currThreads < maxThreads) {
                addWorker(runnable);
                currThreads++;
                return;
            }
        }

        // put into queue
        queue.add(runnable);
    }

    @Override
    public void shutdown() {
        synchronized (workersLock) {
            for (Worker worker : workers) {
                worker.terminate();
            }
        }
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        for(Thread thread: threads) {
            thread.join();
        }
    }

    private void addWorker(Runnable runnable) {
        Worker worker = new Worker();
        workers.add(worker);
        worker.checkAndSetJob(runnable);

        Thread thread = new Thread(worker, "thread-" + Integer.toString(threads.size()));
        threads.add(thread);
        thread.start();
    }

    private enum State {
        IDLE, RUNNING, TERMINATED
    }

    private class Worker implements Runnable {
        private volatile State state;
        private volatile Runnable job;
        private Object lock;

        public Worker() {
            state = State.IDLE;
            lock = new Object();
        }

        @Override
        public void run() {
            try {
                while (state != State.TERMINATED) {
                    if (state == State.RUNNING) {
                        do {
                            job.run();
                            job = queue.poll();
                        } while (job != null);
                    }
                    synchronized (lock) {
                        if (state != State.TERMINATED) {
                            state = State.IDLE;
                            lock.wait();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public boolean checkAndSetJob(Runnable runnable) {
            synchronized (lock) {
                if (state != State.IDLE)
                    return false;

                job = runnable;
                state = State.RUNNING;

                lock.notify();

                return true;
            }
        }

        public void terminate() {
            synchronized (lock) {
                state = State.TERMINATED;
                lock.notify();
            }
        }
    }
}
