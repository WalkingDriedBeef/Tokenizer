package com.pachira.util;

import java.io.FileInputStream;
import java.util.HashMap;

public class Constants {
	private static HashMap<String, String> getStopWords(){
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			java.util.Scanner in = new java.util.Scanner(new FileInputStream("data/stopword.txt"), Constants.ENCODE);
			while(in.hasNext()) {
				String line = in.nextLine().trim();
				map.put(line, line);
			}
			in.close();
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("ERROR: no cache found! -- [data/stopword.txt]");
		}
		return null;
	}
	//文本处理编码
	public static String ENCODE = "GBK";
	//最大词长
	public static int NGRAM = 2;
	//词频阈值
	public static int WORD_FREQ_THRESHOLD = 50;
	//内部凝固度信息阈值
	public static double DEGREE_OF_COAGULATION = 1000.0;
	//停用词
	public static HashMap<String, String> STOP_WORDS = getStopWords();
}
