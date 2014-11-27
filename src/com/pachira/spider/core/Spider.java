package com.pachira.spider.core;

import com.pachira.spider.downloader.HttpClientDownloader;
import com.pachira.spider.downloader.HttpClientDownloaderInter;
import com.pachira.spider.parser.Page;

public class Spider {
	/**
	 * step1: get request and download the request page, then get all links on request page!
	 */
	public static Spider create(PageProcessor process) {
		return new Spider(process);
	}
	private PageProcessor proccess = null;
	
	public Spider(PageProcessor process) {
		this.proccess = process;
	}

	public void run(){
		HttpClientDownloaderInter downloader = new HttpClientDownloader();
		Page page = downloader.download(proccess.getSite().getStartRequests().get(0), proccess.getSite());
		proccess.proccess(page);
	}

}
