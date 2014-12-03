package com.pachira.spider.mapdb;

import java.util.List;
import java.util.Map;

import com.pachira.spider.core.Request;
import com.pachira.spider.util.GenUtils;
import com.pachira.spider.util.MapDBConstant;

public class Pipeline {
	private MapDB urlMapDB = MapDB.create(MapDBConstant.URL_DB_NAME, MapDBConstant.DB_MAP);
	private MapDB contMapDB = MapDB.create(MapDBConstant.CON_DB_NAME, MapDBConstant.DB_MAP);
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
	public synchronized void ps_content(String url, String content) {
		contMapDB.put(url, content);
		contMapDB.commit();
	}
	public void close() {
		urlMapDB.close();
		contMapDB.close();
	}
	public static void main(String[] args) {
		Pipeline pip = new Pipeline();
		Map<String, String> map = pip.urlMapDB.getDB_TREEMAP();
//		for(String key: map.keySet()){
//			System.out.println(key + "\t" + map.get(key));
//		}
		System.out.println(map.size());
		Map<String, String> mapC = pip.contMapDB.getDB_TREEMAP();
//		for(String key: mapC.keySet()){
//			System.out.println(key + "\t" + mapC.get(key));
//		}
		System.out.println(mapC.size());
	}
}
