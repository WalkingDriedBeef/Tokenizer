package com.pachira.spider.core;

import com.pachira.spider.parser.Page;

public class Main implements PageProcessor{

	public WebSite getSite() {
		return new WebSite().addStartRequest(new Request("http://www.dytt8.net"));
	}

	public void proccess(Page page) {
//		System.out.println(page.getUrl());
	}
	public static void main(String[] args) {
		Spider.create(new Main()).thread(4).run();
	}

}
