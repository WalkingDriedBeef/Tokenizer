package com.pachira.spider.mapdb;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;

public class Example {
	public static void main(String[] args) throws IOException {

        //Configure and open database using builder pattern.
        //All options are available with code auto-completion.
//        File dbFile = File.createTempFile("mapdb","db");
        File dbFile = new File("data/mapdb.db");
        DB db = DBMaker.newFileDB(dbFile)
                .closeOnJvmShutdown()
                .encryptionEnable("password")
                .make();

        //open an collection, TreeMap has better performance then HashMap
        ConcurrentNavigableMap<Integer,String> map = db.getTreeMap("collectionName");

        map.put(1,"one");
        map.put(2,"two");
        //map.keySet() is now [1,2] even before commit

        db.commit();  //persist changes into disk

        map.put(3,"three");
        //map.keySet() is now [1,2,3]
        db.rollback(); //revert recent changes
        //map.keySet() is now [1,2]

//        db.close();
        for(Integer i: map.keySet()){
        	System.out.println(i + "\t" + map.get(i));
        }
	}

}
