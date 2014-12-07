package com.pachira.spider.proxy;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetProxyIP {
	public static void main(String[] args) throws IOException {
		String url = "http://www.youdaili.net/Daili/http/2799.html";
		int pages = 1;
		//http://www.xici.net.co/nn/1 -- 186
		for (int i = 0; i < pages; i++) {
			Document doc = Jsoup.connect(url).get();
			String reg = "div.cont_font p";
			Elements eles = doc.select(reg);
			for(Element ele: eles){
				for(String key : ele.toString().split("<br />")){
					System.out.println(key.trim().split("@")[0]);
				}
			}
//			FileUtils.writefileByGBK("data", "proxy.txt", accom.toString());
		}
	}

}
