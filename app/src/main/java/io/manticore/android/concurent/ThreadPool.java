package io.manticore.android.concurent;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.manticore.android.util.IOUtils;

public class ThreadPool extends ThreadPoolExecutor {
    private static ThreadPool instance;

    private boolean isPaused;
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();

    private ThreadPool() {
        super(IOUtils.getCores() * 3, IOUtils.getCores() * 4, 3L, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
        Log.i(Thread.currentThread().getName(), "Using " + String.valueOf(IOUtils.getCores() * 3) + " to " + String.valueOf(IOUtils.getCores() * 4) + " threads");
    }

    public static ThreadPool getInstance() {
        if (instance == null || instance.isShutdown()) {
            synchronized (ThreadPool.class) {
                instance = new ThreadPool();
            }
        }

        return instance;
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