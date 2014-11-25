package com.pachira.spider.spider;

import java.io.Serializable;

public class Request implements Serializable {
	private static final long serialVersionUID = -8914652832041530769L;
	//the http request url
	private String url = "";
	//the http method, default get
	private String method = "";
	
	public Request() {
	}
	public Request(String url) {
		this.url = url;
	}
	public Request(String url, String method) {
		this.url = url;
		this.method = method;
	}
	
	public String getUrl() {
		return url;
	}
	public String getMethod() {
		return method;
	}

}
