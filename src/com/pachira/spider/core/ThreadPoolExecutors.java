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
     * 1��ReentrantLock ��ʵ���� Lock ����ӵ���� synchronized ��ͬ�Ĳ����Ժ��ڴ����壬���������������ͶƱ����ʱ���Ⱥ�Ϳ��ж����Ⱥ��һЩ���ԡ�
     * 2�������ṩ���ڼ�����������¸��ѵ����ܡ������仰˵��������̶߳�����ʹ�����Դʱ��JVM ���Ի����ٵ�ʱ���������̣߳��Ѹ���ʱ������ִ���߳��ϡ���
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
		// �жϵ�ǰ�̳߳ػ�߳����Ƿ�����̳߳�����߳���
		if (threadAlive.get() >= threadNum) {
			try {
				// ����
				reentrantLock.lock();
				while (threadAlive.get() >= threadNum) {
					try {
						// �����ǰ�̳߳���Ȼû�п����̣߳���ȴ���ֱ���̳߳��п����̣߳�֮���ͷ���
						condition.await();
						// logger.warn("thread pool is full!");
					} catch (InterruptedException e) {
					}
				}
			} finally {
				// �ͷ���
				reentrantLock.unlock();
			}
		}
		// ���ӽ����̳߳��л�߳������
		threadAlive.incrementAndGet();
		// ִ������
		executorService.execute(new Runnable() {
			public void run() {
				try {
					//ִ������
					runnable.run();
				} finally {
					try {
						reentrantLock.lock();// ����
						threadAlive.decrementAndGet();// �����̳߳��л�߳������
						condition.signal();// �����߳�
					} finally {
						// �ͷ���
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
