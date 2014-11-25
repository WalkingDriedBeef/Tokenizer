package com.pachira.spider.downloader;

import java.util.Map;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import com.pachira.spider.spider.Request;
import com.pachira.spider.spider.WebSite;
import com.pachira.spider.util.HttpInfoConstant;


public class Downloader implements DownloaderInter {
	public void download(Request request) {
		
	}

	protected HttpUriRequest getHttpUriRequest(Request request, WebSite site, Map<String, String> headers) {
		RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
		if (headers != null) {
			for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
				requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
			}
		}
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
				.setConnectionRequestTimeout(site.getTimeOut())
				.setSocketTimeout(site.getTimeOut())
				.setConnectTimeout(site.getTimeOut())
				.setCookieSpec(CookieSpecs.BEST_MATCH);
		requestBuilder.setConfig(requestConfigBuilder.build());
		return requestBuilder.build();
	}

	/**
	 * 确定http请求的方式，默认get
	 * @param request
	 * @return
	 */
	protected RequestBuilder selectRequestMethod(Request request) {
		String method = request.getMethod();
		if (method == null || method.equalsIgnoreCase(HttpInfoConstant.Method.GET)) {
			return RequestBuilder.get();
		} else if (method.equalsIgnoreCase(HttpInfoConstant.Method.POST)) {
			return RequestBuilder.post();
		} else if (method.equalsIgnoreCase(HttpInfoConstant.Method.HEAD)) {
			return RequestBuilder.head();
		} else if (method.equalsIgnoreCase(HttpInfoConstant.Method.PUT)) {
			return RequestBuilder.put();
		} else if (method.equalsIgnoreCase(HttpInfoConstant.Method.DELETE)) {
			return RequestBuilder.delete();
		} else if (method.equalsIgnoreCase(HttpInfoConstant.Method.TRACE)) {
			return RequestBuilder.trace();
		}
		throw new IllegalArgumentException("Illegal HTTP Method " + method);
	}
}
