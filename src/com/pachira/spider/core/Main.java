package com.pachira.spider.core;

import com.pachira.spider.mapdb.Pipeline;
import com.pachira.spider.parser.Page;
import com.pachira.spider.proxy.ProxyConfigLoad;
import com.pachira.spider.util.GenUtils;
import com.pachira.spider.util.MapDBConstant;
import com.pachira.spider.util.ProxyLocConstant;
import com.pachira.util.FileUtils;

public class Main implements PageProcessor{
	private final static String dbPath = "DB";
	private Pipeline pip = new Pipeline(dbPath);
	

	public WebSite getSite() {
//		return new WebSite().setDomain("http://www.bjfreeport.com/").addStartRequest(new Request("http://www.bjfreeport.com/index.html"));
//		return new WebSite().setDomain("http://www.dytt8.net").addStartRequest(new Request("http://www.dytt8.net"));
		return new WebSite()
		        .setHttpProxyPool(ProxyConfigLoad.getProxyList(ProxyLocConstant.PROXY_LOC_CURRENT))
				.setDomain("http://www.iteye.com")
				.addStartRequest(new Request("http://www.iteye.com/ask"));
	}

	public void proccess(Page page) {
		pip.insert_urls(MapDBConstant.IASK_URL_DB_NAME, page.getTargetRequests());
		pip.insert_html(MapDBConstant.IASK_CONTENT_DB_NAME, GenUtils.md5(page.getUrl()), page.getUrl()+System.getProperty("line.separator") + page.getText());
		
		StringBuilder accum = new StringBuilder();
		int flag = 0;
		for (Request req : page.getTargetRequests()) {
			flag += 1;
			accum.append(req.getUrl()+System.getProperty("line.separator"));
			if(flag % 1000 == 0){
				FileUtils.writefileByGBK("urls", "urls.list.sinaAsk", accum.toString());
				accum.delete(0, accum.length());
			}
		}
		FileUtils.writefileByGBK("urls", "urls.list.sinaAsk", accum.toString());
	}
	public static void main(String[] args) {
		Spider.create(new Main()).thread(5).run();
	}

}
