package com.pachira.spider.downloader;

import com.pachira.spider.spider.Request;
import com.pachira.spider.spider.WebSite;

public interface DownloaderInter {
	public void download(Request request, WebSite site);
}
