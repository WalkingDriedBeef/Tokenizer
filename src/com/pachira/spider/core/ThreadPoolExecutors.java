package com.pachira.spider.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread pool for workers.<br></br>
 * Use {@link java.util.concurrent.ExecutorService} as inner implement. <br></br>
 * New feature: <br></br>
 * 1. Block when thread pool is full to avoid poll many urls without process. <br></br>
 * 2. Count of thread alive for monitor.
 * thanks for code4crafer@gmail.com
 * @author luneneSDK
 * @since 0.1.0
 */
public class ThreadPoolExecutors {
	
    private int threadNum;
    //thread safe integer class, flag of current pool's alive thread
    private AtomicInteger threadAlive = new AtomicInteger();
    /**
     * 1、ReentrantLock 类实现了 Lock ，它拥有与 synchronized 相同的并发性和内存语义，但是添加了类似锁投票、定时锁等候和可中断锁等候的一些特性。
     * 2、它还提供了在激烈争用情况下更佳的性能。（换句话说，当许多线程都想访问共享资源时，JVM 可以花更少的时候来调度线程，把更多时间用在执行线程上。）
     */
    private ReentrantLock reentrantLock = new ReentrantLock();
    
    private Condition condition = reentrantLock.newCondition();
    private ExecutorService executorService;
    
    public ThreadPoolExecutors(int threadNum) {
        this.threadNum = threadNum;
        this.executorService = Executors.newFixedThreadPool(threadNum);
    }
    
    public int getThreadAlive() {
        return threadAlive.get();
    }
    public int getThreadNum() {
        return threadNum;
    }

	public void execute(final Runnable runnable) {
		// 判断当前线程池活动线程数是否大于线程池最大线程数
		if (threadAlive.get() >= threadNum) {
			try {
				// 加锁
				reentrantLock.lock();
				while (threadAlive.get() >= threadNum) {
					try {
						// 如果当前线程池依然没有空闲线程，则等待，直到线程池有空闲线程，之后释放锁
						condition.await();
						// logger.warn("thread pool is full!");
					} catch (InterruptedException e) {
					}
				}
			} finally {
				// 释放锁
				reentrantLock.unlock();
			}
		}
		// 增加降低线程池中活动线程数标记
		threadAlive.incrementAndGet();
		// 执行任务
		executorService.execute(new Runnable() {
			public void run() {
				try {
					//执行任务
					runnable.run();
				} finally {
					try {
						reentrantLock.lock();// 加锁
						threadAlive.decrementAndGet();// 降低线程池中活动线程数标记
						condition.signal();// 唤醒线程
					} finally {
						// 释放锁
						reentrantLock.unlock();
					}
				}
			}
		});
	}
    public boolean isShutdown() {
        return executorService.isShutdown();
    }
    public void shutdown() {
        executorService.shutdown();
    }
}
