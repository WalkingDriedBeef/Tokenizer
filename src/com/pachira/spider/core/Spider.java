package com.pachira.spider.core;

import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pachira.spider.downloader.HttpClientDownloader;
import com.pachira.spider.downloader.HttpClientDownloaderInter;
import com.pachira.spider.parser.Page;

public class Spider {
	private Queue<Request> queue = null;
	private int threadNum = 1;
	private ThreadPoolExecutors threadpool = null;
	private PageProcessor process = null;
	private HttpClientDownloaderInter downloader = null;
	private List<Request> startRequests = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Spider(PageProcessor process) {
		this.process = process;
	}
    protected void initComponent() {
        if (downloader == null) {
            this.downloader = new HttpClientDownloader();
        }
        if (threadpool == null || threadpool.isShutdown()) {
                threadpool = new ThreadPoolExecutors(threadNum);
        }
        if(queue == null){
        	//thread safe
        	queue = new LinkedBlockingQueue<Request>();
        }
        if(startRequests == null){
        	startRequests = this.process.getSite().getStartRequests();
        }
        if (startRequests != null) {
            for (Request request : startRequests) {
                queue.add(request);
            }
        }
    }
	public int getThreadNum() {
		return threadNum;
	}
	public Spider thread(int threadNum) {
		this.threadNum = threadNum;
		return this;
	}
	/**
	 * step1: get request and download the request page, then get all links on request page!
	 */
	public static Spider create(PageProcessor process) {
		return new Spider(process);
	}

	public void run() {
		initComponent();
		logger.info("Spider - [" + UUID.randomUUID().toString() + "] started!");

		while (!Thread.currentThread().isInterrupted()) {
			Request request = queue.poll();
			System.out.println(request);
			if (request == null) {
				if (threadpool.getThreadAlive() == 0) {
					break;
				}
				// wait until new url added
				// waitNewUrl();
				logger.info("wait unitil new url added!");
			} else {
				final Request requestFinal = request;
				threadpool.execute(new Runnable() {
					public void run() {
						try {
							processRequest(requestFinal);
						} catch (Exception e) {
							logger.error("process request " + requestFinal + " error", e);
						} finally {
							// signalNewUrl();
						}
					}
				});
			}
		}
	}
	private void processRequest(Request request) {
		Page page = downloader.download(request, this.process.getSite());
        if (page == null) {
//            onError(request);
            logger.error("download html " + request.getUrl() +" error!");
            return;
        }
        // for cycle retry
        if (page.isNeedCycleRetry()) {
        	logger.error("html " + request.getUrl() +" is need cycle retry!");
        }
        this.process.proccess(page);;
        addTargetRequests(page);
	}
	//queue is thread safe
	private void addTargetRequests(Page page){
		if (CollectionUtils.isNotEmpty(page.getTargetRequests())) {
            for (Request request : page.getTargetRequests()) {
                queue.add(request);
            }
        }
	}
}
