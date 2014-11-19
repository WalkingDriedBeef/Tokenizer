package com.pachira.detection;

import java.util.HashMap;

import com.pachira.util.Constants;

public class Detection {
	/**
	 * @param ngramHash
	 * @param IDS
	 */
	public double degreeOfCoagulation(HashMap<String, HashMap<String, Ngram>> ngramHash, Ngram ngram, int total_words_number) {
		if(ngram == null || ngramHash == null || ngram.number < Constants.WORD_FREQ_THRESHOLD || total_words_number <= 0 || ngram.gram == 1) return 0.0;
		double degreeOfCoagulation = 0.0;
		double each_prob = 1.0;
		double self_prob = 1.0;
		for(String key: ngram.data.split(" ")){
			if(ngramHash.containsKey(key)){
				each_prob *= ngramHash.get(key).get(key).number * 1.0 / total_words_number;
			}else{
				each_prob = 0.0;
				return 0.0;
			}
		}
		self_prob = ngram.number * 1.0 / total_words_number;
		degreeOfCoagulation = self_prob / each_prob;
		//ÐÅÏ¢ìØ
//		double info_prob = -Math.log(self_prob);
//		List<Double> list = new ArrayList<Double>();
//		list.add(degreeOfCoagulation);
//		list.add(info_prob);
//		System.out.println(info_prob + "\t" + degreeOfCoagulation);
		return degreeOfCoagulation;
	}
}
