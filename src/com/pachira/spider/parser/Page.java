package com.pachira.spider.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.pachira.spider.core.Request;
import com.pachira.spider.util.UrlUtils;

public class Page {
	private String text = null;
	private String url = null;
	private int statusCode = -1;
	private Request request = null;
	private boolean needCycleRetry;
	private List<Request> targetRequests = new ArrayList<Request>();

	public boolean isNeedCycleRetry() {
		return needCycleRetry;
	}

	public void setNeedCycleRetry(boolean needCycleRetry) {
		this.needCycleRetry = needCycleRetry;
	}

	public List<Request> getTargetRequests() {
		return targetRequests;
	}

	public void addTargetRequests(Set<String> links) {
		for (String s : links) {
            if (StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:")) continue;
            s = UrlUtils.canonicalizeUrl(s, url.toString());
//            System.out.println(">>>>"+url + "\t\t\t" + s + "\t\t\t" + links.size());
            targetRequests.add(new Request(s, this.getRequest().getMethod()));
        }
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
	@Override
	public String toString() {
		StringBuilder accum = new StringBuilder();
		accum.append("url            : " + this.getUrl() + System.getProperty("line.separator"));
		accum.append("status code    : " + this.statusCode + System.getProperty("line.separator"));
		accum.append("needCycleRetry : " + this.needCycleRetry + System.getProperty("line.separator"));
		if(targetRequests!= null && targetRequests.size() > 0){
			for(Request request: targetRequests){
				accum.append("-target request: " + request + System.getProperty("line.separator"));
			}
		}
		return accum.toString();
	}
}
