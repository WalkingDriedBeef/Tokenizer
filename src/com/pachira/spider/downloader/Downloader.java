package com.pachira.spider.downloader;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.pachira.spider.core.Request;
import com.pachira.spider.core.WebSite;
import com.pachira.spider.parser.LinksExtractor;
import com.pachira.spider.parser.Page;
import com.pachira.spider.util.HttpInfoConstant;
import com.pachira.spider.util.UrlUtils;


public class Downloader implements DownloaderInter {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private DownLoaderGenerator httpClientGenerator = new DownLoaderGenerator();
	private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();
	private LinksExtractor extractor = new LinksExtractor();
	
	public Page download(Request request, WebSite site) {
		String charset = null;
		Map<String, String> headers = null;
		Set<Integer> acceptStatusCodes = null;
		if (site != null) {
			acceptStatusCodes = site.getAcceptStatCode();
			charset = site.getCharset();
			headers = site.getHeaders();
		}else{
			acceptStatusCodes = Sets.newHashSet(200);
		}
		CloseableHttpResponse httpResponse = null;
		try {
			HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);
			httpResponse = getHttpClient(site).execute(httpUriRequest);
			request.setMethod(httpUriRequest.getMethod());
			request.putExtra(Request.STATUS_CODE, httpResponse.getStatusLine().getStatusCode());
			if(acceptStatusCodes.contains(httpResponse.getStatusLine().getStatusCode())){
				return handleResponse(request, httpResponse, charset, site);
			}else{
				logger.warn("download page " + request.getUrl() + " error, status code is: {}", httpResponse.getStatusLine().getStatusCode());
			}
		} catch (IOException e) {
			logger.error("download page error:" + request.getUrl() , e);
		} finally {
			if(httpResponse != null) {
				try {
					//释放连接
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
				} 
			}
		}
		return null;
	}
	private String getContent(CloseableHttpResponse httpResponse, String charset) throws IOException{
		if (charset == null) {
			byte[] contentBytes = EntityUtils.toByteArray(httpResponse.getEntity());
			charset = getHtmlCharset(httpResponse, contentBytes);
			if(charset == null || StringUtils.isBlank(charset)){
				return EntityUtils.toString(httpResponse.getEntity());
			}else{
				return new String(contentBytes, charset);
			}
		} else {
			return EntityUtils.toString(httpResponse.getEntity());
		}
	}
	private Page handleResponse(Request request, CloseableHttpResponse httpResponse, String charset, WebSite site) throws IOException{
		String content = getContent(httpResponse, charset);
		Page page = new Page();
		page.setUrl(request.getUrl());
		page.setRequest(request);
		page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
		page.setText(content);
		page.addTargetRequests(extractor.links(content, request.getUrl(), site));
		return page;
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
		RequestConfig.Builder requestConfigBuilder= RequestConfig.custom()
					.setConnectionRequestTimeout(site.getTimeOut())
					.setSocketTimeout(site.getTimeOut())
					.setConnectTimeout(site.getTimeOut())
					.setCookieSpec(CookieSpecs.BEST_MATCH);
			requestBuilder.setConfig(requestConfigBuilder.build());
			
		//config the proxy info of request
		if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
			HttpHost host = site.getHttpProxyFromPool();
			logger.info("proxy httphost: " + host.getAddress() + ":" + host.getPort());
			requestConfigBuilder.setProxy(host);
			request.putExtra("proxy", host);
		}
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
	protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws UnsupportedEncodingException  {
        String charset = null;
        // 1. encoding in http header Content-Type
        String value = httpResponse.getEntity().getContentType().getValue();
        charset = UrlUtils.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
        	logger.info("Auto get charset: {}",  charset);
            return charset;
        }
        // use default charset to decode first time
        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset.name());
        // 2.charset in meta
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");
            for (Element link : links) {
                // 2.1 html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");
                if (metaContent.indexOf("charset") != -1) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                }
                // 2.2 html5 <meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        logger.info("Auto get charset: {}",  charset);
        // 3 use tools as cpdetector for content decode
        return charset;
    }

}
