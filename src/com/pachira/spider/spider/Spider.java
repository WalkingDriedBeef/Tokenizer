package com.pachira.spider.spider;

public class Spider {
	/**
	 * step1: get request and download the request page, then get all links on request page!
	 */
	private Request request = null;
	public Request getRequest() {
		return request;
	}
	public Spider() {
	}
	public Spider(Request request) {
		this.request = request;
	}
	public Spider create(Request request) {
		return new Spider(request);
	}
	public void run(){
	}

}
