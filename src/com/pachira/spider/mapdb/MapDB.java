package com.pachira.spider.mapdb;

import java.io.File;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapDB{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private DB DB_OBJECT = null;
	private ConcurrentNavigableMap<String, String> DB_TREEMAP = null;
	
	private MapDB(DB DB_OBJECT, ConcurrentNavigableMap<String, String> DB_TREEMAP) {
		this.DB_OBJECT = DB_OBJECT;
		this.DB_TREEMAP = DB_TREEMAP;
	}
	/**
	 * @param DB_NAME DB的名称
	 * @param DB_TYPE 目前默认为Map类型的DB
	 * @return
	 */
	public static MapDB create(String DB_NAME, int DB_TYPE) {
		DB db = DBMaker.newFileDB(new File(DB_NAME))
				//当jvm退出是关闭数据库
				.closeOnJvmShutdown()
				//使用加密算法存储数据
				.encryptionEnable("password")
				.make();
		ConcurrentNavigableMap<String,String> map = db.getTreeMap(DB_NAME + "_TREEMAP_NAME");
		return new MapDB(db, map);
	}
	public ConcurrentNavigableMap<String, String> getDB_TREEMAP() {
		return DB_TREEMAP;
	}
	public void setDB_TREEMAP(ConcurrentNavigableMap<String, String> dB_TREEMAP) {
		DB_TREEMAP = dB_TREEMAP;
	}
	public void put(String key, String value){
		if(DB_TREEMAP == null){
			logger.error("MapDB's tree map is null");
			return;
		}
		DB_TREEMAP.put(key, value);
	}
	public String get(String key){
		if(DB_TREEMAP == null){
			logger.error("MapDB's tree map is null");
			return null;
		}
		return DB_TREEMAP.get(key);
	}
	public void remove(String key){
		if(DB_TREEMAP == null){
			logger.error("MapDB's tree map is null");
			return;
		}
		if(DB_TREEMAP.containsKey(key)){
			DB_TREEMAP.remove(key);
		}
	}
	public void commit(){
		if(DB_OBJECT == null){
			logger.error("MapDB is null");
			return;
		}
		DB_OBJECT.commit();
	}
	public void rollback(){
		if(DB_OBJECT == null){
			logger.error("MapDB is null");
			return;
		}
		DB_OBJECT.rollback();
	}
	public void close(){
		if(DB_OBJECT == null){
			logger.error("MapDB is null");
			return;
		}
		DB_OBJECT.close();
	}
}
