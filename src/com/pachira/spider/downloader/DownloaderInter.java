package com.pachira.spider.downloader;

import com.pachira.spider.core.Request;
import com.pachira.spider.core.WebSite;

public interface DownloaderInter {
	public void download(Request request, WebSite site);
}
