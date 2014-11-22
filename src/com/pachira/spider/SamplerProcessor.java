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
		List<String> links = page.getHtml().links().regex("http://my\\.oschina\\.net/flashsword/blog/\\d+").all();
		page.addTargetRequests(links);
		for (String link: links) {
			System.out.println(">>>" + link);
		}
//		page.putField("title", page.getHtml().xpath("//div[@class='BlogEntity']/div[@class='BlogTitle']/h1").toString());
//		page.putField("content", page.getHtml().$("div.content").toString());
//		page.putField("tags", page.getHtml().xpath("//div[@class='BlogTags']/a/text()").all());
	}

	@SuppressWarnings("deprecation")
	public Site getSite() {
		return Site.me().setDomain("my.oschina.net").addStartUrl("http://my.oschina.net/flashsword/blog");
	}

	public static void main(String[] args) {
		Spider.create(new SamplerProcessor()).addPipeline(new ConsolePipeline()).run();
	}
}
