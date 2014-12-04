package com.pachira.spider.mapdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pachira.spider.core.Request;
import com.pachira.spider.util.GenUtils;
import com.pachira.spider.util.MapDBConstant;

public class Pipeline {
	private MapDB dbs = null;
	private String dbPath;
	public Pipeline(String dbPath) {
		this.dbPath = dbPath;
		this.dbs = new MapDB(this.dbPath);
	}
	public void insert_urls(String dbName, List<Request> targets) {
		Map<String, String> map = new HashMap<String, String>();
		for (Request request : targets) {
			map.put(GenUtils.md5(request.getUrl()), request.getUrl());
		}
		dbs.PersistKV(dbName, map);
	}
	public void insert_url(String dbName, String url) {
		dbs.PersistKV(dbName, GenUtils.md5(url), url);
	}
	
	public void insert_htmls(String dbName, Map<String, String> htmls) {
		dbs.PersistKV(dbName, htmls);
	}
	public void insert_html(String dbName, String url, String html) {
		dbs.PersistKV(dbName, GenUtils.md5(url), html);
	}
	public long get_count(String dbName){
		return dbs.GetDB(dbName).count;
	}
	public static void main(String[] args) {
		Pipeline urlPip = new Pipeline("DB");
		System.out.println(urlPip.get_count(MapDBConstant.IASK_CONTENT_DB_NAME));
		System.out.println(urlPip.get_count(MapDBConstant.IASK_URL_DB_NAME));
	}
}
