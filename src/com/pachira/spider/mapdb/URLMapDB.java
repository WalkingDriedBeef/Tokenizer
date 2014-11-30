package com.pachira.spider.mapdb;

import java.io.File;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pachira.spider.util.MapDBConstant;

public class URLMapDB{
	private DB DB_OBJECT = null;
	private ConcurrentNavigableMap<String, String> URL_DB_TREEMAP = null;
	
	public ConcurrentNavigableMap<String, String> getURL_DB_TREEMAP() {
		return URL_DB_TREEMAP;
	}
	public void setURL_DB_TREEMAP(ConcurrentNavigableMap<String, String> uRL_DB_TREEMAP) {
		URL_DB_TREEMAP = uRL_DB_TREEMAP;
	}
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private URLMapDB(DB DB_OBJECT, ConcurrentNavigableMap<String, String> URL_DB_TREEMAP) {
		this.DB_OBJECT = DB_OBJECT;
		this.URL_DB_TREEMAP = URL_DB_TREEMAP;
	}
	public static URLMapDB create() {
		DB db = DBMaker.newFileDB(new File(MapDBConstant.URL_DB_NAME))
				//当jvm退出是关闭数据库
				.closeOnJvmShutdown()
				//使用加密算法存储数据
				.encryptionEnable("password")
				.make();
		ConcurrentNavigableMap<String,String> map = db.getTreeMap("URL_DB_TREEMAP_NAME");
		return new URLMapDB(db, map);
	}
	public void put(String key, String value){
		if(URL_DB_TREEMAP == null){
			logger.error("URLMapDB's tree map is null");
			return;
		}
		URL_DB_TREEMAP.put(key, value);
	}
	public String get(String key){
		if(URL_DB_TREEMAP == null){
			logger.error("URLMapDB's tree map is null");
			return null;
		}
		return URL_DB_TREEMAP.get(key);
	}
	public void remove(String key){
		if(URL_DB_TREEMAP == null){
			logger.error("URLMapDB's tree map is null");
			return;
		}
		if(URL_DB_TREEMAP.containsKey(key)){
			URL_DB_TREEMAP.remove(key);
		}
	}
	public void commit(){
		if(DB_OBJECT == null){
			logger.error("URLMapDB is null");
			return;
		}
		DB_OBJECT.commit();
	}
	public void rollback(){
		if(DB_OBJECT == null){
			logger.error("URLMapDB is null");
			return;
		}
		DB_OBJECT.rollback();
	}
	public void close(){
		if(DB_OBJECT == null){
			logger.error("URLMapDB is null");
			return;
		}
		DB_OBJECT.close();
	}
	
//	public static void main(String[] args) {
//		String xline = "ftp://ygdy8:ygdy8@y006.dygod.org:1081/[阳光电影www.ygdy8.com].西野的恋爱与冒险.BD.720p.中文字幕.rmvb";
//		xline = "http://www.dytt8.net/html/gndy/dyzz/20141127/46733.html";
//		Map<String, String> map = new TreeMap<String, String>();
//		URLMapDB urlsDB = URLMapDB.create();
//		String filename = "urls/urls.list";
//		String charset = "gbk";
//		try {
//			java.util.Scanner in = new java.util.Scanner(new FileInputStream(filename),charset);
//			int index = 0;
//			while(in.hasNext()) {
//				index += 1;
//				if(index % 100000 == 0){
//					System.out.println(index);
//				}
//				String line = in.nextLine().trim();
//				String md5str = md5(line);
//				map.put(md5str, line.trim());
//				urlsDB.put(md5str, line.trim());
//			}
//			urlsDB.commit();
//			in.close();
//			String md5xline = urlsDB.md5(xline);
//			System.out.println(new Date().getTime());
//			System.out.println(map.get(md5xline));
//			System.out.println(System.currentTimeMillis());
//			System.out.println(urlsDB.get(md5xline));
//			System.out.println(System.currentTimeMillis());
//			urlsDB.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
