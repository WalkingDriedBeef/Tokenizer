package com.pachira.spider.downloader;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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

import com.pachira.spider.core.Request;
import com.pachira.spider.core.WebSite;
import com.pachira.spider.parser.LinksExtractor;
import com.pachira.spider.util.HttpInfoConstant;
import com.pachira.spider.util.UrlUtils;


public class Downloader implements DownloaderInter {
	public static void main(String[] args) {
		Request request = new Request("http://www.dytt8.net/");
		WebSite site = new WebSite();
		site.addStartRequest(request);
		Downloader downloader = new Downloader();
		downloader.download(request, site);
	}
	private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
	private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();
	private LinksExtractor extractor = new LinksExtractor();
	public void download(Request request, WebSite site) {
        String charset = null;
        Map<String, String> headers = null;
        if (site != null) {
            charset = site.getCharset();
            headers = site.getHeaders();
        }
        CloseableHttpResponse httpResponse = null;
        try {
            HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);
            httpResponse = getHttpClient(site).execute(httpUriRequest);
            if(charset == null){
            	byte [] contentBytes = EntityUtils.toByteArray(httpResponse.getEntity());
            	charset = getHtmlCharset(httpResponse, contentBytes);
            	String content = new String(contentBytes, charset);
//            	System.out.println(content);
            	Set<String> links = extractor.links(content, request.getUrl());
            	for (String string : links) {
					System.out.println(string);
				}
            }else{
            	System.out.println(EntityUtils.toString(httpResponse.getEntity(),charset));
            }
        } catch (IOException e) {
        	e.printStackTrace();
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
	protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        String charset;
        // 1. encoding in http header Content-Type
        String value = httpResponse.getEntity().getContentType().getValue();
        charset = UrlUtils.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
        	System.err.println("Auto get charset: {}");
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
        System.err.println("Auto get charset: {}");
        // 3 use tools as cpdetector for content decode
        return charset;
    }

}
