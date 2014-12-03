package com.pachira.spider.proxy;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pachira.util.FileUtils;

public class GetProxyIP {
	public static void main(String[] args) throws IOException {
		String url = "http://www.xici.net.co/nn/1";
		int pages = 180;
		//http://www.xici.net.co/nn/1 -- 186
		for (int i = 0; i < pages; i++) {
			Document doc = Jsoup.connect(url).get();
			String reg = "table#ip_list>tbody>tr.odd";
			Elements eles = doc.select(reg);
			StringBuffer accom = new StringBuffer();
			for(Element ele: eles){
				Elements tdeles = ele.select("td");
				if(tdeles.size() >= 4){
					accom.append(tdeles.get(2).text() + ":" + tdeles.get(3).text() + System.getProperty("line.separator"));
				}
			}
			FileUtils.writefileByGBK("data", "proxy.txt", accom.toString());
		}
	}

}
