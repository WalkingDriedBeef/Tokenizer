package com.pachira.detection;


import java.util.HashMap;

import com.pachira.util.Constants;


public class NgramCount {
	private HashMap<String, HashMap<String, Ngram>> ngramHash = new HashMap<String, HashMap<String,Ngram>>();
	private int total_char_number = 0;
	
	public int getTotal_char_number() {
		return total_char_number;
	}
	public HashMap<String, HashMap<String, Ngram>> getNgramHash() {
		return ngramHash;
	}
	public void count(String sent, int N){
		total_char_number += sent.trim().length();
		String[] list = sent.trim().split(" ");
		for(int i = 0; i < list.length; i++){
			int j = 0;
			while(j < N && i + j < list.length){
				j++;
				String data = sublist(i, i + j, list);
				String ID = data.substring(0,1);
				boolean isIncludeWords = data.replace(" ", "").length() == j;
				if(!isIncludeWords)continue;
				if(Constants.STOP_WORDS.containsKey(ID))continue;
//				System.out.println(data);
//				System.out.println(ID);
				if(ngramHash.containsKey(ID)){
					HashMap<String, Ngram> val = ngramHash.get(ID);
					if(val.containsKey(data)){
						Ngram ngram = val.get(data);
						ngram.number += 1;
					}else{
						Ngram ngram = new Ngram(ID, data, 1, j, isIncludeWords);
						val.put(data, ngram);
					}
				}else{
					HashMap<String, Ngram> val = new HashMap<String, Ngram>();
					Ngram ngram = new Ngram(ID, data, 1, j, isIncludeWords);
					val.put(data, ngram);
					ngramHash.put(ID, val);
				}
 			}
		}
	}
	private String sublist(int start, int end, String[] list){
		if(start<0 || end > list.length){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for(int i = start; i < end; i++){
			sb.append(list[i] + " ");
		}
		return sb.toString().trim();
	}
	public static void main(String[] args) {
		String sent = "要 认真 贯 彻 胡 锦 涛 总 书记 在 中央 纪委 七次 全会 上的 重要 讲话";
		NgramCount ngramcount = new NgramCount();
		ngramcount.count(sent, Constants.NGRAM);
		System.out.println(ngramcount.getNgramHash());
	}
}
