package com.pachira.spider.core;

import java.util.LinkedList;
import java.util.Queue;

import com.pachira.spider.downloader.HttpClientDownloader;
import com.pachira.spider.downloader.HttpClientDownloaderInter;
import com.pachira.spider.parser.Page;

public class Spider {
	private Queue<Request> queue = new LinkedList<Request>();
	/**
	 * step1: get request and download the request page, then get all links on request page!
	 */
	public static Spider create(PageProcessor process) {
		return new Spider(process);
	}
	private PageProcessor proccess = null;
	
	public Spider(PageProcessor process) {
		this.proccess = process;
		intoQueue();
	}
	private void intoQueue(){
		for(Request req: proccess.getSite().getStartRequests()){
			queue.add(req);
		}
	}

	public void run(){
		HttpClientDownloaderInter downloader = new HttpClientDownloader();
		Page page = downloader.download(queue.poll(), proccess.getSite());
		proccess.proccess(page);
	}

}
