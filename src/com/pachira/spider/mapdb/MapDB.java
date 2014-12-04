package com.pachira.spider.mapdb;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pachira.util.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * MapDB相关工具类
 */
public class MapDB {
	private Logger logger = LoggerFactory.getLogger(getClass());
	protected static final String MAP_NAME = "@spider@";
	// 数据库活动时间，默认10分钟
	// 超过10分钟之后系统会关闭数据库以释放空间
	private int activeTimeout = 10;
	private String dbPath;
	private Map<String, DBVisitInfo> dbMap = new HashMap<String, DBVisitInfo>();
	// 最大个数
	private int maxCount = -1;
	
    public MapDB(String dbPath) {
        this.dbPath = dbPath;
        if(!new File(dbPath).exists() && new File(dbPath).isDirectory()){
        	new File(dbPath).mkdirs();
        }

        // 初始化时加载一下数据
        List<String> dbList = FileUtils.enumFile(dbPath, ".+\\.mdb");
        for(String db: dbList) {
            String fileName = new File(db).getName();
            GetDB(fileName.substring(0, fileName.indexOf(".mdb")));
        }
    }

    public synchronized int TotalCount() {
        int sum = 0;
        for(DBVisitInfo db: dbMap.values()) {
            sum += db.count;
        }
        return sum;
    }

	public boolean PersistKV(String dbName, String key, String value) {
        // 按个添加在大数据量下很慢，不推荐使用
        while(maxCount > 0 && TotalCount() > maxCount) {
//            ThreadUtils.sleep(10);
            try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
        }

        synchronized (this) {
            DBVisitInfo db = GetDB(dbName);
            if(db == null) {
                return false;
            }

            ConcurrentMap<String, String> map = db.map;
            map.put(key, value);
            db.count = map.size();
            db.db.commit();
            return true;
        }
    }

    public boolean PersistKV(String dbName, Map<String, String> valueMap) {
        while(maxCount > 0 && TotalCount() > maxCount) {
        	 try {
 				Thread.sleep(10);
 			} catch (InterruptedException e) {
 			}
        }
        synchronized (this) {
            DBVisitInfo db = GetDB(dbName);
            if(db == null) {
                return false;
            }
            ConcurrentMap<String, String> map = db.map;

            for(Map.Entry<String, String> pair: valueMap.entrySet()) {
                map.put(pair.getKey(), pair.getValue());
            }
            db.count = map.size();
            db.db.commit();
            return true;
        }
    }

    /**
     * 从Map中获取数据，并删除节点
     */
    public synchronized boolean PeekKV(String dbName, List<String> keyList, Map<String, String> values) {
        DBVisitInfo db = GetDB(dbName);
        if(db == null) {
            return false;
        }

        ConcurrentMap<String, String> map = db.map;
        for(String key: keyList) {
            String val = map.get(key);
            if(val != null) {
                values.put(key, val);
                map.remove(key);
            }
        }
        db.count = map.size();
        return true;
    }

    /**
     * 从Map中获取数据，并删除节点
     */
    public synchronized String PeekKV(String dbName, String key) {
        DBVisitInfo db = GetDB(dbName);
        if(db == null) {
            return null;
        }

        ConcurrentMap<String, String> map = db.map;
        String str = map.get(key);
        map.remove(key);

        db.count = map.size();
        return str;
    }

    /**
     * 判断指定节点是否都存在
     * @param dbName 数据库名称
     * @param keyList key列表
     * @return 全部存在返回true，否则返回false
     */
    public synchronized boolean IsAllContained(String dbName, List<String> keyList) {
        DBVisitInfo db = GetDB(dbName);
        if(db == null) {
            return false;
        }

        ConcurrentMap<String, String> map = db.map;
        for(String key: keyList) {
            if(!map.containsKey(key)) {
                return false;
            }
        }

        return true;
    }
    public synchronized boolean IsAllContained(String dbName, String key) {
        DBVisitInfo db = GetDB(dbName);
        if(db == null) {
            return false;
        }
        ConcurrentMap<String, String> map = db.map;
        return map.containsKey(key);
    }

    /**
     * 构建一个数据对象
     * @param dbName 数据库名称
     */
    public DBVisitInfo GetDB(String dbName) {
        if(!dbMap.containsKey(dbName)) {
            DBVisitInfo db = new DBVisitInfo();
            db.isActive = false;
            dbMap.put(dbName, db);
        }

        DBVisitInfo db = dbMap.get(dbName);
        if(!db.isActive) {
            db = new DBVisitInfo();
            db.db = DBMaker.newFileDB(new File(dbPath, DbNameToFileName(dbName)))
//                    .asyncWriteEnable() // 有Bug，大数据量下commit会报异常
                    .cacheDisable()
//                    .deleteFilesAfterClose()
                    .closeOnJvmShutdown()
                    .make();

            db.map = db.db.createHashMap(MAP_NAME)
                    .counterEnable()
                    .makeOrGet();

            db.isActive = true;
            db.count = db.map.size();
            dbMap.put(dbName, db);
        }

        db = dbMap.get(dbName);
        db.lastVisitTime = new Date();

        // 关闭一下长时间没有访问的DB
        CloseInactiveDB();
        return db;
    }

    /**
     * 关闭掉长时间没有访问的数据库，不清空数据
     */
    private void CloseInactiveDB() {
        @SuppressWarnings("rawtypes")
		Iterator it = dbMap.entrySet().iterator();
        while(it.hasNext()) {
            @SuppressWarnings("unchecked")
			Map.Entry<String, DBVisitInfo> item = (Map.Entry<String, DBVisitInfo>)it.next();
            DBVisitInfo db = item.getValue();

            if(!db.isActive) {
                continue;
            }

            Date now = new Date();
            if(now.getTime() - db.lastVisitTime.getTime() >= activeTimeout * 60 * 1000) {
                // 超时了，关闭掉该数据库
                logger.info(String.format("inactive db %s, close to free cache", item.getKey()));
                SaveDB(item.getKey(), item.getValue());
            }
        }
    }

    /**
     * 关闭数据库，但不清空数据
     */
    public synchronized void Close() {
        @SuppressWarnings("rawtypes")
		Iterator it = dbMap.entrySet().iterator();
        while(it.hasNext()) {
            @SuppressWarnings("unchecked")
			Map.Entry<String, DBVisitInfo> item = (Map.Entry<String, DBVisitInfo>)it.next();
            SaveDB(item.getKey(), item.getValue());
        }
    }

    /**
     * 清空所有的DB数据
     */
    public synchronized void Reset() {
        // 删除所有的数据库相关文件
        // 首先关闭所有相关数据库文件
        for(Map.Entry<String, DBVisitInfo> db: dbMap.entrySet()) {
            SaveDB(db.getKey(), db.getValue());
            DeleteDB(db.getKey());
        }

        dbMap.clear();

        File dbDir = new File(dbPath);
        dbDir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                if(s.matches(".+\\.mdb|.+\\.mdb\\.p|.+\\.mdb\\.t")) {
                    File dbFile = new File(file, s);
                    dbFile.delete();
                }
                return false;
            }
        });
    }

    private void SaveDB(String dbName, DBVisitInfo db) {
        // 已经关闭了就跳过
        if(!db.isActive)
            return;

        ConcurrentMap<String, String> map = db.map;
        db.db.commit();
        db.isActive = false;
        db.lastVisitTime = null;

//        totalCount -= map.size();
        if(map.size() > 0) {
            // 还有数据，就清理一下再存储
            db.db.compact();
            db.db.close();
        } else {
            // 没有数据了就关闭后删除
            db.db.close();
            DeleteDB(dbName);
        }

        db.map = null;
        db.db = null;
    }

    /**
     * 删除数据库文件
     * @param dbName 数据名称
     */
    private void DeleteDB(String dbName) {
        File dbDir = new File(dbPath);

        File mdb = new File(dbDir, DbNameToFileName(dbName));
        if(mdb.exists()) {
            mdb.delete();
        }

        mdb = new File(dbDir, DbNameToFileName(dbName) + ".p");
        if(mdb.exists()) {
            mdb.delete();
        }

        mdb = new File(dbDir, DbNameToFileName(dbName) + ".t");
        if(mdb.exists()) {
            mdb.delete();
        }
    }

    private String DbNameToFileName(String dbName) {
        return dbName + ".mdb";
    }

    public void setActiveTimeout(int activeTimeout) {
        this.activeTimeout = activeTimeout;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    // 描述DB访问信息的结构体
    public class DBVisitInfo {
        public DB db;
        public ConcurrentMap<String, String> map;
        public Date lastVisitTime;
        boolean isActive;
        public int count;
    }
   
}
