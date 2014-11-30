package com.pachira.spider.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pachira.spider.core.WebSite;

import java.util.HashSet;
import java.util.Set;

/**
 * Example program to list links from a URL.
 */
public class LinksExtractor {
	/**
	 * program to list links from URL. GET IT FROM JSOUP
	 * @param html     : the URL's html content
	 * @param base_url : URL
	 * @param site     : if site's domain is null, get all urls else get urls under website's demain
	 * @return
	 */
	public Set<String> links(String html, String base_url, WebSite site){
		Set<String> urls = new HashSet<String>();
		Document doc = Jsoup.parse(html, base_url);
		Elements links = doc.select("a[href]");
		String domain = site.getDomain();
		if(domain != null){
    		if(!domain.startsWith("http://")){
    			domain = "http://";
    		}
    		if(domain.endsWith("/")){
    			domain = domain.substring(0, domain.length() - 1);
    		}
    	}
        for (Element link : links) {
        	if(formatLink(link, domain)!= null){
        		urls.add(formatLink(link, domain));
        	}
        }
		return urls;
	}
	//format url string
	private String formatLink(Element link, String domain) {
		if (link.attr("abs:href") != null && link.attr("abs:href").trim().length() > 0) {
			String linkinfo = link.attr("abs:href");
			if (linkinfo.endsWith("#") || linkinfo.endsWith("/")) {
				linkinfo = linkinfo.substring(0, linkinfo.length() - 1);
			}
			if (linkinfo.endsWith("/#")) {
				linkinfo = linkinfo.substring(0, linkinfo.length() - 2);
			}
			if(domain != null){
				return linkinfo.startsWith(domain) ? linkinfo :null;
			}else{
				return linkinfo;
			}
		}
		return null;
	}
}
