package com.pachira.spider.core;

import java.util.UUID;

import com.pachira.spider.parser.Page;
import com.pachira.util.FileUtils;

public class Main implements PageProcessor{

	public WebSite getSite() {
		return new WebSite().addStartRequest(new Request("http://www.dytt8.net"));
	}

	public void proccess(Page page) {
//		System.out.println(page.getUrl());
		if(page == null) return;
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
		FileUtils.writefileByGBK("urls", "urls.list", accum.toString());
		FileUtils.writefileByGBK("html",  UUID.randomUUID().toString(), page.getText());
	}
	public static void main(String[] args) {
		Spider.create(new Main()).thread(4).run();
	}

}
