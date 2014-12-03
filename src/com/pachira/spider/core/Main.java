package com.pachira.spider.core;

import com.pachira.spider.mapdb.Pipeline;
import com.pachira.spider.parser.Page;
import com.pachira.spider.proxy.ProxyConfigLoad;
import com.pachira.spider.util.ProxyLocConstant;
import com.pachira.util.FileUtils;

public class Main implements PageProcessor{
	private Pipeline pip = new Pipeline();

	public WebSite getSite() {
//		return new WebSite().setDomain("http://www.bjfreeport.com/").addStartRequest(new Request("http://www.bjfreeport.com/index.html"));
//		return new WebSite().setDomain("http://www.dytt8.net").addStartRequest(new Request("http://www.dytt8.net"));
		return new WebSite()
//		        .setHttpProxyPool(ProxyConfigLoad.getProxyList(ProxyLocConstant.PROXY_LOC_CURRENT))
				.setDomain("http://baike.baidu.com")
				.addStartRequest(new Request("http://baike.baidu.com/shenghuo"));
	}

	public void proccess(Page page) {
		pip.ps_urls(page.getTargetRequests());
		pip.ps_content(page.getUrl(), page.getText());
		StringBuilder accum = new StringBuilder();
		int flag = 0;
		for (Request req : page.getTargetRequests()) {
			flag += 1;
			accum.append(req.getUrl()+System.getProperty("line.separator"));
			if(flag % 1000 == 0){
				FileUtils.writefileByGBK("urls", "urls.list.baidubaike", accum.toString());
				accum.delete(0, accum.length());
			}
		}
		FileUtils.writefileByGBK("urls", "urls.list.baidubaike", accum.toString());
//		String tmpurl = page.getUrl().replace("/", "_").replace(".", "_").replace(":", "_");
//		FileUtils.writefileByGBK("content", tmpurl, page.getText());
	}
	public static void main(String[] args) {
		Spider.create(new Main()).thread(10).run();
	}

}
