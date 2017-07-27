package io.manticore.android.concurent;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import io.manticore.android.util.IOUtils;

public class ThreadPool extends ThreadPoolExecutor {
    private static final ThreadPool instance = new ThreadPool();

    private boolean isPaused;
    private ArrayList<Future> futures = new ArrayList<>();
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition condition = pauseLock.newCondition();

    private ThreadPool() {
        super(IOUtils.getCores() * 3, IOUtils.getCores() * 4, 3L, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
        Log.i(Thread.currentThread().getName(), "Using " + String.valueOf(IOUtils.getCores() * 3) + " to " + String.valueOf(IOUtils.getCores() * 4) + " threads");
    }

    public static ThreadPool getInstance() {
        return instance;
    }

    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        pauseLock.lock();
        try {
            while (isPaused) condition.await();
        } catch (InterruptedException ie) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    @NonNull
    @Override
    public Future<?> submit(Runnable task) {
        futures.add(super.submit(task));
        return super.submit(task);
    }

    public void clean() {
        synchronized (this) {
            getQueue().clear();

            for(Future task : futures) {
                if (!task.isDone()) {
                    task.cancel(true);
                }
            }

            futures.clear();
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
            condition.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }
}