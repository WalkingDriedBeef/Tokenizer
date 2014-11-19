package com.pachira.tokenizer;

import java.io.FileInputStream;
import java.util.HashMap;

import com.pachira.util.Constants;

public class Dict {
	private int word_max_len = 5;
	private HashMap<String, Double> dict_hash = new HashMap<String, Double>();
	

	public int getWord_max_len() {
		return word_max_len;
	}
	public HashMap<String, Double> getDict_hash() {
		return dict_hash;
	}
	public Dict(String dict_path) {
		try {
			java.util.Scanner in = new java.util.Scanner(new FileInputStream(dict_path), Constants.ENCODE);
			while(in.hasNext()) {
				String line = in.nextLine().trim();
				if(line.contains("\t")){
					String word = line.split("\t")[0];
					word_max_len = word.length() > word_max_len ? word.length() : word_max_len;
					double weight = 0.8;
					try {
						weight = Double.parseDouble(line.split("\t")[1]);
					} catch (Exception e) {
					}
					this.dict_hash.put(word, weight);
				}else{
					word_max_len = line.length() > word_max_len ? line.length() : word_max_len;
					this.dict_hash.put(line, 0.8);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("ERROR: no cache found! -- [" + dict_path +"]");
		}
	}
	public static void main(String[] args) {
		Dict dict = new Dict("data/words.dict.bak");
		for(String key :dict.getDict_hash().keySet()){
			System.out.println(key + "----" + dict.getDict_hash().get(key));
		}
		System.out.println(dict.getWord_max_len());
	}
}
