package com.pachira.detection;

public class Ngram {
	public String ID;
	public String data;
	public int number;
	public int gram;
	public boolean isIncludeWords;
	public Ngram() {
	}
	public Ngram(String ID, String data) {
		this.ID = ID;
		this.data = data;
	}
	public Ngram(String ID, String data, int number,int gram, boolean isIncludeWords) {
		this.ID = ID;
		this.data = data;
		this.number = number;
		this.gram = gram;
		this.isIncludeWords = isIncludeWords;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ID: " + this.ID);
		sb.append(" data: " + this.data);
		sb.append(" number: " + this.number);
		sb.append(" gram: " + this.gram);
		sb.append(" isIncludeWords: " + this.isIncludeWords);
		return sb.toString();
	}
}
