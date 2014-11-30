package com.pachira.spider.core;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pachira.spider.downloader.Downloader;
import com.pachira.spider.downloader.DownloaderInter;
import com.pachira.spider.mapdb.BloomFilter;
import com.pachira.spider.parser.Page;
import com.pachira.spider.util.UrlUtils;

public class Spider {
	private Queue<Request> queue = null;
	private int threadNum = 1;
	private ThreadPoolExecutors threadpool = null;
	private PageProcessor process = null;
	private DownloaderInter downloader = null;
	private List<Request> startRequests = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	private HashSet<String> allLinks = null;
	private BloomFilter bloom = null;
	
	private Spider(PageProcessor process) {
		this.process = process;
	}
    protected void initComponent() {
        if (downloader == null) {
            this.downloader = new Downloader();
        }
        if(bloom == null){
        	bloom = new BloomFilter();
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
                if(!bloom.isExit(request.getUrl())){
                	bloom.add(request.getUrl());
                }
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
		while (true) {
			Request request = queue.poll();
			if (request == null) {
				if (threadpool.getThreadAlive() == 0) {
					break;
				}
				// wait until new url added
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				logger.info("wait unitil new url added!");
			} else {
				final Request requestFinal = request;
				threadpool.execute(new Task(requestFinal));
			}
		}
		threadpool.shutdown();
	}
	class Task implements Runnable{
		Request request = null;
		public Task(Request request) {
			this.request = request;
		}
		public void run() {
			processRequest(this.request);
		}
		/**
		 * process current request,then pageage the response to a page object!
		 * @param request
		 */
		private synchronized void processRequest(Request request) {
			Page page = downloader.download(request, process.getSite());
	        if (page == null) {
	        	request.setReqTimes(request.getReqTimes() + 1);
	        	page = new Page();
	        	page.setNeedCycleRetry(true);
	        	page.setRequest(request);
	        	//if download current html error, so cycle retry!
	            logger.info("html " + request.getUrl() +" is need cycle retry!");
	        }else{
	        	//if download current html success, so show this page
	        	process.proccess(page);
	        }
	        addTargetRequests(page, page.isNeedCycleRetry());
		}
		//add target requests(if request is need cycle retry, so just add current request or add response requets to the queue)
		private void addTargetRequests(Page page, boolean isNeddCycleRetry) {
			if (!isNeddCycleRetry && CollectionUtils.isNotEmpty(page.getTargetRequests())) {
				for (Request request : page.getTargetRequests()) {
					System.out.println(page);
					if (!bloom.isExit(request.getUrl())) {
						bloom.add(request.getUrl());
						queue.add(page.getRequest());
					}
				}
			} else {
				if (!bloom.isExit(request.getUrl()) && request.getReqTimes() <= UrlUtils.URL_REQUEST_TIMES_THRESHOLD) {
					bloom.add(request.getUrl());
					queue.add(page.getRequest());
				}
			}
			logger.info(String.format("LinkedBlockingQueue Size: [ %d ]",queue.size()));
		}
	}
	
}
