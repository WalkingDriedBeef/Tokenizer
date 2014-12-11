package com.pachira.spider.downloader;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;

import com.pachira.spider.core.WebSite;

import java.io.IOException;
import java.util.Map;

public class DownLoaderGenerator {
	/**
	 * PoolingHttpClientConnectionManager
	 * 1.它管理着连接池，可以同时为很多线程提供http连接请求
	 * 2.维护的连接数在每个路由基础和总数上都有限制。默认，每个路由基础上的连接不超过2个，
	 * 总连接数不能超过20。在实际应用中，这个限制可能会太小了，尤其是当服务器也使用Http协议时 
	 */
    private PoolingHttpClientConnectionManager connectionManager;

    public DownLoaderGenerator() {
    	//自定义的socket工厂类可以和指定的协议（Http、Https）联系起来，用来创建自定义的连接管理器
    	Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()  
    	        .register("http", PlainConnectionSocketFactory.INSTANCE)  
    	        .register("https", SSLConnectionSocketFactory.getSocketFactory())  
    	        .build();  
    	
        connectionManager = new PoolingHttpClientConnectionManager(reg);
        //设置每个路由上的最大连接数
        connectionManager.setDefaultMaxPerRoute(20);
        connectionManager.setMaxTotal(200);
    }
    /**
     * 获得一个连接
     * @param site
     * @return
     */
    public CloseableHttpClient getClient(WebSite site) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
        /**
         * 设置UserAgent
         */
        if (site != null && site.getUserAgent() != null) {
            httpClientBuilder.setUserAgent(site.getUserAgent());
        } else {
            httpClientBuilder.setUserAgent("");
        }
        if (site == null || site.isUseGzip()) {
        	/**
        	 * HTTP协议拦截器是一种实现一个特定的方面的HTTP协议的代码程序。通常情况下，协议拦截器会将一个或多个头消息加入到接受或者发送的消息中。
        	 * 协议拦截器也可以操作消息的内容实体—消息内容的压缩/解压缩就是个很好的例子。
        	 * 通常，这是通过使用“装饰”开发模式，一个包装实体类用于装饰原来的实体来实现。一个拦截器可以合并，形成一个逻辑单元。
        	 * 
        	 * 协议拦截器可以通过共享信息协作——比如处理状态——通过HTTP执行上下文。协议拦截器可以使用Http上下文存储一个或者多个连续请求的处理状态。
        	 * 通常，只要拦截器不依赖于一个特定状态的http上下文，那么拦截执行的顺序就无所谓。如果协议拦截器有相互依赖关系，必须以特定的顺序执行，
        	 * 那么它们应该按照特定的顺序加入到协议处理器中。协议处理器必须是线程安全的。类似于servlets，协议拦截器不应该使用变量实体，
        	 * 除非访问这些变量是同步的（线程安全的）。
        	 */
            httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {
                public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
                    if (!request.containsHeader("Accept-Encoding")) {
                        request.addHeader("Accept-Encoding", "gzip");
                    }
                }
            });
        }
        /**
         * 设置socket config
         */
        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        if (site != null) {
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetryTimes(), true));
        }
        generateCookie(httpClientBuilder, site);
        return httpClientBuilder.build();
    }

    private void generateCookie(HttpClientBuilder httpClientBuilder, WebSite site) {
        CookieStore cookieStore = new BasicCookieStore();
        for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
            BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
            cookie.setDomain(site.getDomain());
            cookieStore.addCookie(cookie);
        }
        for (Map.Entry<String, Map<String, String>> domainEntry : site.getAllCookies().entrySet()) {
            for (Map.Entry<String, String> cookieEntry : domainEntry.getValue().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(domainEntry.getKey());
                cookieStore.addCookie(cookie);
            }
        }
        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }

}
