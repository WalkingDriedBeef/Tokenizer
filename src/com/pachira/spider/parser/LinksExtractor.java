package com.pachira.spider.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * Example program to list links from a URL.
 */
public class LinksExtractor {
	public Set<String> links(String html, String base_url){
		Set<String> urls = new HashSet<String>();
		Document doc = Jsoup.parse(html, base_url);
		Elements links = doc.select("a[href]");
        for (Element link : links) {
        	urls.add(link.attr("abs:href"));
//            System.out.println(String.format(" * a: <%s>  [%s]", link.attr("abs:href"), link.text()));
        }
		return urls;
	}
}
