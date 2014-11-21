package com.pachira.tokenizer;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Timer;

import com.pachira.util.Constants;

public class Lat {
	public String segmenter(Dict dict, String sent) {
		HashMap<Integer, List<Node>> lats = new HashMap<Integer, List<Node>>();
		for(int i = 0; i < sent.length(); i++){
			int j = 0;
			while(j < dict.getWord_max_len() && i + j < sent.length()){
				j ++;
				String tmp = sent.substring(i, i + j);
				if(dict.getDict_hash().containsKey(tmp) || j == 1){
					boolean isLeaf = (i + j == sent.length())? true:false;
					int isSingle = (j == 1) ? 1 : 0;
					double weight = dict.getDict_hash().containsKey(tmp)? dict.getDict_hash().get(tmp):0.8;
					Node node = new Node(tmp, i, i + j, 0, weight, isLeaf, isSingle);
					if(lats.containsKey(i)){
						List<Node> val = lats.get(i);
						val.add(node);
					}else{
						List<Node> val = new ArrayList<Node>();
						val.add(node);
						lats.put(i, val);
					}
				}
			}
		}
		//pruning
		for(Integer i: lats.keySet()){
			if(lats.get(i).size() > 1){
				lats.get(i).remove(0);
			}
		}
//		for(Integer i: lats.keySet()){
//			for(Node node: lats.get(i)){
//				System.out.println(i+"\t" + node);
//			}
//		}
		return getBestPath(lats);
	}
	/**
	 *  get best path [calculate the path's score]:
     *   1. 统计路径的所有单词的权重得分 [WEIGHTs / DEPTH]  * 50
     *   2. 统计路径的所有单字词数量得分 [DEPTH - SIGWN / DEPTH] * 15
     *   3. 统计路径的深度得分           [sentLen - DEPTH / sentLen] * 35
	 */
	private String getBestPath(HashMap<Integer, List<Node>> lats){
		if(lats == null){
			System.out.println("Waining: get best path exception!");
			return null;
		}
		Stack<Node> paths = new Stack<Node>();
		Node curNode = new Node("");
		paths.add(curNode);
		Node best = new Node("");
		while(! paths.isEmpty()){
			Node node = paths.pop();
			if(node.isLeaf){
				double path_score = 0.0;
				if(node.depth > 0){
					double s_weight = node.weight * 0.5000000 / node.depth;
					double s_single = (node.depth - node.isSingle) * 0.1500000 / node.depth;
					double s_depth = (node.index_to - node.depth) * 0.3500000 / node.index_to;
					path_score = s_weight + s_single + s_depth;
				}
//				System.out.println(path_score + "\t" + node);
				if(best.weight < path_score){
					best = node;
					node.weight = path_score;
				}
				continue;
			}
			List<Node> children = lats.get(node.index_to);
			if(children == null || children.size() == 0)continue;
			for (Node tnode: children){
				String data = node.data + " " + tnode.data;
				int index_to = tnode.index_to;
				int depth = node.depth + 1;
				double weight = node.weight + tnode.weight;
				int isSingle = node.isSingle + tnode.isSingle;
				
				Node tmp = new Node(data, 0, index_to, depth, weight, tnode.isLeaf, isSingle);
				paths.push(tmp);
			}
		}
//		System.out.println(best);
		return best.data.trim();
	}
	public void tokenizer(String input, Lat lat, Dict dict){
		try {
			java.util.Scanner in = new java.util.Scanner(new FileInputStream(input), Constants.ENCODE);
			while(in.hasNext()) {
				String line = in.nextLine().trim();
				String seg = lat.segmenter(dict, line);
				System.out.println(seg);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("ERROR: no cache found! -- [" + input +"]");
		}
	}
	public static void main(String[] args) {
//		System.out.println(new Date());
//		Dict dict = new Dict("data/words.dict.bak");
//		String sent = "笔记本电脑笔记本电脑笔记本电脑笔记本电脑笔记本电脑笔记本电脑笔记本电脑笔记本电脑笔记本电脑笔记本电脑";
//		Lat lat = new Lat();
//		System.out.println(lat.segmenter(dict, sent));
//		System.out.println(new Date());
		Dict dict = new Dict("data/words.dict.bak");
		String input = "data/XIYOUJI.txt";
		Lat lat = new Lat();
		lat.tokenizer(input, lat, dict);
	}
}
