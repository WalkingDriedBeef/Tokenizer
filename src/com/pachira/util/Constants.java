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
	//�ı��������
	public static String ENCODE = "GBK";
	//���ʳ�
	public static int NGRAM = 2;
	//��Ƶ��ֵ
	public static int WORD_FREQ_THRESHOLD = 50;
	//�ڲ����̶���Ϣ��ֵ
	public static double DEGREE_OF_COAGULATION = 1000.0;
	//ͣ�ô�
	public static HashMap<String, String> STOP_WORDS = getStopWords();
}
