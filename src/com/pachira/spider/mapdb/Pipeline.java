package com.pachira.spider.mapdb;

import java.util.List;
import java.util.Map;

import com.pachira.spider.core.Request;
import com.pachira.spider.util.GenUtils;

public class Pipeline {
	private URLMapDB urlMapDB = URLMapDB.create();
//	private Logger logger = LoggerFactory.getLogger(getClass());
	//Persistence
	public synchronized void ps_urls(List<Request> targets) {
		for (Request request : targets) {
			urlMapDB.put(GenUtils.md5(request.getUrl()), request.getUrl());
		}
		urlMapDB.commit();
	}
	public synchronized void ps_url(String line) {
		urlMapDB.put(GenUtils.md5(line), line);
		urlMapDB.commit();
	}
	public void close() {
		urlMapDB.close();
	}
	public static void main(String[] args) {
		Pipeline pip = new Pipeline();
		Map<String, String> map = pip.urlMapDB.getURL_DB_TREEMAP();
		for(String key: map.keySet()){
			System.out.println(key + "\t" + map.get(key));
		}
		System.out.println(map.size());
	}
}
