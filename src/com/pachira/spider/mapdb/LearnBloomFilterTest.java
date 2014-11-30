package com.pachira.spider.mapdb;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

/**
 * ������ BloomFilterTest
 * ������ com.utilTest
 * ���ߣ� zhouyh
 * ʱ�䣺 2014-8-29 ����02:54:56
 * ������ ��¡����������ͳ�Ĳ�¡��������֧�ִӼ�����ɾ����Ա
 */
public class LearnBloomFilterTest {
	/**
	 * ĳ�ļ��а�����һЩ8λ�ĵ绰���룬ͳ�Ƴ��ֵĵ绰����Ĵ���
	 * --8Ϊ�����99 999 999����Լ��99M��bit��12.5MB���ڴ棬�Ϳ���ͳ�Ƴ������ֵĺ��롣
	 * --99 999 999 bit ~ 99 999 999 / 8 /1024 kb ~ 12207 kb ~ 12.3M
	 */
	//DEFAULT_SIZEΪ2��27�η������˴�������26λ
	private static final int DEFAULT_SIZE = 2<<26;
	
	/*
	 * ��ͬ��ϣ���������ӣ�һ��ȡ����
	 * seeds���鹲��8��ֵ����������8�ֲ�ͬ�Ĺ�ϣ����
	 */
	private int[] seeds = new int[]{3, 5, 7, 11, 13, 31, 37, 61};
	
	public static class SimpleHash {
		/*
		 * capΪDEFAULT_SIZE�������ڽ��������ַ�����ֵ
		 * seedΪ����hashֵ��һ��keyֵ�������Ӧ�����е�seeds����
		 */
		private int cap;
		private int seed;
		/**
		 * 
		 * ���캯��
		 * ���ߣ�zhouyh
		 * @param cap
		 * @param seed
		 */
		public SimpleHash(int cap, int seed){
			this.cap = cap;
			this.seed = seed;
		}
		/**
		 * �ӿ�HashЧ�ʵ���һ����Ч;���Ǳ�д���õ��Զ�������HashCode��String��ʵ�ֲ��������µļ��㷽����   
		 * for (int i = 0; i < len; i++) {      
		 *     h = 31*h + val[off++];      
		 * }      
		 * hash = h;        
		 * ���ַ���HashCode�ļ��㷽���������������Brian W. Kernighan��Dennis M. Ritchie��
		 * ��The C Programming Language���У�����Ϊ���Լ۱���ߵ��㷨���ֱ���Ϊtimes33�㷨��
		 * ��ΪC�г�������Ϊ33��JAVA�и�Ϊ31����ʵ���ϣ�����List���ڵĴ�����Ķ����������ַ�������Hashֵ��   
		 * 
		 * ��һ�ֱȽ������hash�㷨��Ϊ��¡����������������ϸ΢����Ϊ���ۣ������洢�ռ�Ĵ����ڼ�
		 * �����������ж��û����ظ����Ƿ��ں������ϵȵȡ�  
		 * @param str
		 * @return
		 */
		public int hash(String value){
			int result = 0;
			int length = value.length();
			for(int i=0; i<length; i++){
				result = 31 * result + value.charAt(i);
			}
			
			return (cap-1) & result;
		}
		public static void main(String[] args) {
			SimpleHash simHash = new LearnBloomFilterTest.SimpleHash(DEFAULT_SIZE, 3);
			System.out.println(simHash.hash("�й�"));
			System.out.println(new String("�й�").hashCode());
			
		}
	}
}
