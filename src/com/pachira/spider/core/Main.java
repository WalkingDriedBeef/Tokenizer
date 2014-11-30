package com.pachira.spider.core;

import com.pachira.spider.mapdb.Pipeline;
import com.pachira.spider.parser.Page;

public class Main implements PageProcessor{
	private Pipeline pip = new Pipeline();
	public WebSite getSite() {
		return new WebSite().setDomain("http://www.dytt.net").addStartRequest(new Request("http://www.dytt.net"));
	}

	public void proccess(Page page) {
		pip.ps_urls(page.getTargetRequests());
//		System.out.println(page.getUrl());
//		if(page == null) return;
//		StringBuilder accum = new StringBuilder();
//		int flag = 0;
//		for (Request req : page.getTargetRequests()) {
//			flag += 1;
//			accum.append(req.getUrl()+System.getProperty("line.separator"));
//			if(flag % 1000 == 0){
//				FileUtils.writefileByGBK("data", "urls.list", accum.toString());
//				accum.delete(0, accum.length());
//			}
//		}
//		FileUtils.writefileByGBK("urls", "urls.list", accum.toString());
//		FileUtils.writefileByGBK("html",  UUID.randomUUID().toString(), page.getText());
	}
	public static void main(String[] args) {
		Spider.create(new Main()).thread(4).run();
	}

}
