package com.pachira.spider.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pachira.spider.util.ProxyLocConstant;

public class ProxyConfigLoad {
	private static final Logger logger = LoggerFactory.getLogger(ProxyConfigLoad.class);

	public static List<String[]> getProxyList(String proxy_location) {
		List<String[]> proxyList = new ArrayList<String[]>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(proxy_location)));
			String line = "";
			while ((line = br.readLine()) != null) { 
				proxyList.add(new String[] { line.split(":")[0],line.split(":")[1] });
			}
		} catch (IOException e) {
			logger.error("load proxy ip error!");
		}
		return proxyList;
	}

	public static void main(String[] args) throws IOException {
		ProxyPool proxyPool = new ProxyPool(getProxyList(ProxyLocConstant.PROXY_LOC_CURRENT), false);
		proxyPool.setReuseInterval(10000);
		// proxyPool.saveProxyList();

		List<HttpHost> httphostList = new ArrayList<HttpHost>();
		int i = 0;
		System.in.read();
		while (proxyPool.getIdleNum() > 2) {
			HttpHost httphost = proxyPool.getProxy();
			httphostList.add(httphost);
			logger.info("borrow object>>>>" + i + ">>>>" + httphostList.get(i).toString());
			i++;
		}
//		System.out.println(proxyPool.allProxyStatus());
		for (i = 0; i < httphostList.size(); i++) {
			proxyPool.returnProxy(httphostList.get(i), 200);
			logger.info("return object>>>>" + i + ">>>>" + httphostList.get(i).toString());
		}
//		System.out.println(proxyPool.allProxyStatus());
	}
}
