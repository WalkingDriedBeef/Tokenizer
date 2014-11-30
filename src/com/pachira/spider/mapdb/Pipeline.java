package com.pachira.spider.mapdb;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pachira.spider.core.Request;
import com.pachira.spider.util.GenUtils;

public class Pipeline {
	private static final int BUFFER_SIZE = 10000;
	private int current_size = 0;
	private URLMapDB urlMapDB = URLMapDB.create();
	private Logger logger = LoggerFactory.getLogger(getClass());
	//Persistence
	public synchronized void ps_urls(List<Request> targets) {
		for (Request request : targets) {
			current_size += 1;
			urlMapDB.put(GenUtils.md5(request.getUrl()), request.getUrl());
		}
		if(current_size == BUFFER_SIZE){
			logger.info("url map db commit.");
			urlMapDB.commit();
			current_size = 0;
		}
	}
	public synchronized void ps_url(String line) {
		current_size += 1;
		urlMapDB.put(GenUtils.md5(line), line);
		if(current_size % BUFFER_SIZE == 0){
			logger.info("url map db commit.");
			urlMapDB.commit();
			current_size = 0;
		}
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
