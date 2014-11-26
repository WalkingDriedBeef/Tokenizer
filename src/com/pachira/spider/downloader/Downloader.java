package com.pachira.spider.downloader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Sets;
import com.pachira.spider.spider.Request;
import com.pachira.spider.spider.WebSite;
import com.pachira.spider.util.HttpInfoConstant;


public class Downloader implements DownloaderInter {
	public static void main(String[] args) {
		Request request = new Request("http://www.dytt.net");
		WebSite site = new WebSite();
		site.addStartRequest(request);
		Downloader downloader = new Downloader();
		downloader.download(request, site);
	}
	private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
	private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();
	public void download(Request request, WebSite site) {
        Set<Integer> acceptStatCode;
        String charset = null;
        Map<String, String> headers = null;
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
            charset = site.getCharset();
            headers = site.getHeaders();
        } else {
            acceptStatCode = Sets.newHashSet(200);
        }
        CloseableHttpResponse httpResponse = null;
        try {
            HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);
            httpResponse = getHttpClient(site).execute(httpUriRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            System.out.println(httpResponse.getStatusLine());
            System.out.println(EntityUtils.toString(httpResponse.getEntity()));
        } catch (IOException e) {
        	System.err.println("download page " + request.getUrl() + " error");
        } finally {
        }
    }
	private CloseableHttpClient getHttpClient(WebSite site) {
        if (site == null) {
            return httpClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }
	protected HttpUriRequest getHttpUriRequest(Request request, WebSite site, Map<String, String> headers) {
		//confirm the method of request
		RequestBuilder requestBuilder = selectRequestMethod(request);
		//confirm the uri of request
		requestBuilder.setUri(request.getUrl());
		//confirm the heads of request
		if (headers != null) {
			for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
				requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
			}
		}
		//confirm the config of request
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
				.setConnectionRequestTimeout(site.getTimeOut())
				.setSocketTimeout(site.getTimeOut())
				.setConnectTimeout(site.getTimeOut())
				.setCookieSpec(CookieSpecs.BEST_MATCH);
		requestBuilder.setConfig(requestConfigBuilder.build());
		//config the proxy info of request
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
