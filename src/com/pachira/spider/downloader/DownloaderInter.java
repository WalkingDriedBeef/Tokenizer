package com.pachira.spider.downloader;

import com.pachira.spider.core.Request;
import com.pachira.spider.core.WebSite;
import com.pachira.spider.parser.Page;

public interface DownloaderInter {
	public Page download(Request request, WebSite site);
}
