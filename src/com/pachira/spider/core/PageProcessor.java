package com.pachira.spider.core;

import com.pachira.spider.parser.Page;

public interface PageProcessor {
	public void proccess(Page page);
	public WebSite getSite();
}
