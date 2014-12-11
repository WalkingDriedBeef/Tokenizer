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
	 * 1.�����������ӳأ�����ͬʱΪ�ܶ��߳��ṩhttp��������
	 * 2.ά������������ÿ��·�ɻ����������϶������ơ�Ĭ�ϣ�ÿ��·�ɻ����ϵ����Ӳ�����2����
	 * �����������ܳ���20����ʵ��Ӧ���У�������ƿ��ܻ�̫С�ˣ������ǵ�������Ҳʹ��HttpЭ��ʱ 
	 */
    private PoolingHttpClientConnectionManager connectionManager;

    public DownLoaderGenerator() {
    	//�Զ����socket��������Ժ�ָ����Э�飨Http��Https����ϵ���������������Զ�������ӹ�����
    	Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()  
    	        .register("http", PlainConnectionSocketFactory.INSTANCE)  
    	        .register("https", SSLConnectionSocketFactory.getSocketFactory())  
    	        .build();  
    	
        connectionManager = new PoolingHttpClientConnectionManager(reg);
        //����ÿ��·���ϵ����������
        connectionManager.setDefaultMaxPerRoute(20);
        connectionManager.setMaxTotal(200);
    }
    /**
     * ���һ������
     * @param site
     * @return
     */
    public CloseableHttpClient getClient(WebSite site) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
        /**
         * ����UserAgent
         */
        if (site != null && site.getUserAgent() != null) {
            httpClientBuilder.setUserAgent(site.getUserAgent());
        } else {
            httpClientBuilder.setUserAgent("");
        }
        if (site == null || site.isUseGzip()) {
        	/**
        	 * HTTPЭ����������һ��ʵ��һ���ض��ķ����HTTPЭ��Ĵ������ͨ������£�Э���������Ὣһ������ͷ��Ϣ���뵽���ܻ��߷��͵���Ϣ�С�
        	 * Э��������Ҳ���Բ�����Ϣ������ʵ�塪��Ϣ���ݵ�ѹ��/��ѹ�����Ǹ��ܺõ����ӡ�
        	 * ͨ��������ͨ��ʹ�á�װ�Ρ�����ģʽ��һ����װʵ��������װ��ԭ����ʵ����ʵ�֡�һ�����������Ժϲ����γ�һ���߼���Ԫ��
        	 * 
        	 * Э������������ͨ��������ϢЭ���������紦��״̬����ͨ��HTTPִ�������ġ�Э������������ʹ��Http�����Ĵ洢һ�����߶����������Ĵ���״̬��
        	 * ͨ����ֻҪ��������������һ���ض�״̬��http�����ģ���ô����ִ�е�˳�������ν�����Э�����������໥������ϵ���������ض���˳��ִ�У�
        	 * ��ô����Ӧ�ð����ض���˳����뵽Э�鴦�����С�Э�鴦�����������̰߳�ȫ�ġ�������servlets��Э����������Ӧ��ʹ�ñ���ʵ�壬
        	 * ���Ƿ�����Щ������ͬ���ģ��̰߳�ȫ�ģ���
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
         * ����socket config
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
