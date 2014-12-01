package com.pachira.spider.core;

import com.pachira.spider.mapdb.Pipeline;
import com.pachira.spider.parser.Page;
import com.pachira.util.FileUtils;

public class Main implements PageProcessor{
	private Pipeline pip = new Pipeline();
	public WebSite getSite() {
		return new WebSite().setDomain("http://www.bjfreeport.com/").addStartRequest(new Request("http://www.bjfreeport.com/index.html"));
	}

	public void proccess(Page page) {
		pip.ps_urls(page.getTargetRequests());
//		System.out.println(page.getUrl());
		StringBuilder accum = new StringBuilder();
		int flag = 0;
		for (Request req : page.getTargetRequests()) {
			flag += 1;
			accum.append(req.getUrl()+System.getProperty("line.separator"));
			if(flag % 1000 == 0){
				FileUtils.writefileByGBK("data", "urls.list", accum.toString());
				accum.delete(0, accum.length());
			}
		}
		FileUtils.writefileByGBK("urls", "urls.list.1", accum.toString());
	}
	public static void main(String[] args) {
		Spider.create(new Main()).thread(4).run();
	}

}
