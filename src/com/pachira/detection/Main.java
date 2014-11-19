package com.pachira.detection;

import java.io.FileInputStream;

import com.pachira.tokenizer.Dict;
import com.pachira.tokenizer.Lat;
import com.pachira.util.Constants;

public class Main {
	public void coarse(String file, Lat lat, Dict dict, CoarseHandling chand, NgramCount ngramcount) {
		try {
			int index = 0;
			java.util.Scanner in = new java.util.Scanner(new FileInputStream(file), Constants.ENCODE);
			while(in.hasNext()) {
				index += 1;
				if (index % 10000 == 0){
					System.out.println("Handle lines: " + index);
				}
				String line = in.nextLine().trim();
				String coarseline = chand.change(line);
//				System.out.println(coarseline);
				if(coarseline == null || coarseline.trim().length() < 1) continue;
				for(String cl: coarseline.trim().split(" ")){
					if(cl == null || cl.trim().length() < 1)continue;
					String seg = lat.segmenter(dict, cl);
//					System.out.println(seg);
					ngramcount.count(seg, Constants.NGRAM);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("ERROR: no cache found! -- [" + file +"]");
		}
	}
	public static void main(String[] args) {
		String file = "data/XIYOUJI.txt";
		String dict_path = "data/words.dict.bak";
		Main main = new Main();
		CoarseHandling chand = new CoarseHandling();
		Lat lat = new Lat();
		Dict dict = new Dict(dict_path);
		NgramCount ngramcount = new NgramCount();
		main.coarse(file, lat, dict, chand, ngramcount);
		Detection det = new Detection();
		for(String key: ngramcount.getNgramHash().keySet()){
			for(String tkey: ngramcount.getNgramHash().get(key).keySet()){
				double thresold = det.degreeOfCoagulation(ngramcount.getNgramHash(), ngramcount.getNgramHash().get(key).get(tkey), ngramcount.getTotal_char_number());
				if(thresold >= Constants.DEGREE_OF_COAGULATION){
					Ngram ngramx = ngramcount.getNgramHash().get(key).get(tkey);
					System.out.println(thresold + "\t" + ngramx.data + "\t" + ngramx.number);
				}
//				System.out.println(ngramcount.getNgramHash().get(key).get(tkey));
			}
		}
		System.out.println(ngramcount.getTotal_char_number());
		
	}
}
