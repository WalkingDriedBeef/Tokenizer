package com.pachira.spider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * @author code4crafter@gmail.com <br>
 */
public class SamplerProcessor implements PageProcessor {
	public void process(Page page) {
//		List<String> links = page.getHtml().links().regex("http://my\\.oschina\\.net/flashsword/blog/\\d+").all();
		List<String> links = page.getHtml().links().all();
		page.addTargetRequests(links);
//		System.out.println(page.getHtml());
		page.putField("content", page.getHtml().$("div#Zoom").toString());
	}
	
	//http://www.dytt8.net/
	@SuppressWarnings("deprecation")
	public Site getSite() {
		return Site.me().setDomain("www.dytt8.net").addStartUrl("http://www.dytt8.net/");
	}

	public static void main(String[] args) {
		Spider.create(new SamplerProcessor()).addPipeline(new ConsolePipeline()).run();
	}
}
