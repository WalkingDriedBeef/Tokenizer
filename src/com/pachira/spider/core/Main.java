package com.pachira.spider.core;

import com.pachira.spider.parser.Page;

public class Main implements PageProcessor{

	String starturl = "http://www.dytt8.net";
	public WebSite getSite() {
		return new WebSite().addStartRequest(new Request(starturl));
	}
	public static void main(String[] args) {
		Spider.create(new Main()).run();
	}

	public void proccess(Page page) {
		System.out.println(page);
	}

}