package io.manticore.android.concurent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool extends ThreadPoolExecutor {

    private boolean isPaused;
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();
    private static ThreadPool instance;
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    public static ThreadPool getInstance() {
        if(instance == null) {
            synchronized (ThreadPool.class) {
                instance = new ThreadPool();
            }
        }
        return instance;
    }

    private ThreadPool() {
        super(NUMBER_OF_CORES * 2, NUMBER_OF_CORES * 3, 3L, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
    }

    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        pauseLock.lock();
        try {
            while (isPaused) unpaused.await();
        } catch (InterruptedException ie) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }
}