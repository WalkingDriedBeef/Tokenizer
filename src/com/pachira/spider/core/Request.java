package com.pachira.spider.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {
	private static final long serialVersionUID = -8914652832041530769L;
	//the http request url
	private String url = null;
	//the http method, default get
	private String method = null;
	//store the extras info
	private Map<String, Object> extras = null;
	//store the status code
	private String STATUS_CODE = "DEFAULT_STATUS";
	
	public String getSTATUS_CODE() {
		return STATUS_CODE;
	}
	public void setSTATUS_CODE(String sTATUS_CODE) {
		STATUS_CODE = sTATUS_CODE;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Map<String, Object> getExtras() {
		return extras;
	}
	public Object getExtra(String extra) {
		if(extras == null){
			return null;
		}
		return extras.get(extra);
	}
	public Request putExtra(String extra, Object val){
		if(extras == null){
			extras = new HashMap<String, Object>();
		}
		extras.put(extra, val);
		return this;
	}
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
