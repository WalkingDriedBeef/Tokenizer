package com.pachira.tokenizer;

public class Node {
	String data = "";
	int index_from = 0;
	int index_to = 0;
	int depth = 0;
	double weight = 0.0;
	boolean isLeaf = false;
	int isSingle = 0;
			
	public Node(String data) {
		this.data = data;
	}
	public Node(String data, int index_from, int index_to, int depth, double weight, boolean isLeaf, int isSingle) {
		this.data = data;
		this.index_from = index_from;
		this.index_to = index_to;
		this.depth = depth;
		this.weight = weight;
		this.isLeaf = isLeaf;
		this.isSingle = isSingle;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("data: " + this.data + " index_from: " + this.index_from);
		sb.append(" index_to: " + this.index_to + " weight: " + this.weight);
		sb.append(" isLeaf: " + this.isLeaf + " isSingle: " + this.isSingle);
		sb.append(" depth: " + this.depth);
		return sb.toString();
	}
	
}
