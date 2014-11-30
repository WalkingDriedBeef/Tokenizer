package com.pachira.spider.mapdb;

import java.util.BitSet;

/**
 * ������ BloomFilterTest
 * ������ com.utilTest
 * ���ߣ� zhouyh
 * ʱ�䣺 2014-8-29 ����02:54:56
 * ������ ��¡����������ͳ�Ĳ�¡��������֧�ִӼ�����ɾ����Ա
 */
public class BloomFilter {
	//DEFAULT_SIZEΪ2��29�η������˴�������28λ
	private static final int DEFAULT_SIZE = 2<<28;
	/*
	 * ��ͬ��ϣ���������ӣ�һ��ȡ����
	 * seeds���鹲��8��ֵ����������8�ֲ�ͬ�Ĺ�ϣ����
	 */
	private int[] seeds = new int[]{3, 5, 7, 11, 13, 31, 37, 61};
	/*
	 * ��ʼ��һ��������С��λ��
	 * BitSetʵ�����ɡ�������λ�����ɵ�һ��Vector��
	 * ����ϣ����Ч�ʵر�������������ء���Ϣ����Ӧʹ��BitSet.
	 */
	private BitSet bitSets = new BitSet(DEFAULT_SIZE);
	//����hash��������
	private SimpleHash[] hashFuns = new SimpleHash[seeds.length];
	
	public BloomFilter(){
		/**
		 *  �������е�hashֵ������seeds.length��hashֵ����8λ��
		 *  ͨ������SimpleHash.hash(),���Եõ�����8��hash��������ó�hashֵ��	
		 *  ����DEFAULT_SIZE(�����ַ����ĳ��ȣ���seeds[i](һ��ָ��������)���ɵõ���Ҫ���Ǹ�hashֵ��λ�á�		
		 */
		for(int i=0; i<seeds.length; i++){
			hashFuns[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
		}
	}
	/**
	 * 
	 * ��������add
	 * ���ߣ�zhouyh
	 * ����ʱ�䣺2014-8-30 ����02:07:35
	 * ���������������ַ�����ǵ�bitSets�У��������ַ�����8������ֵ��λ��Ϊ1
	 * @param value
	 */
	public synchronized void add(String value){
		for(SimpleHash hashFun : hashFuns){
			bitSets.set(hashFun.hash(value), true);
		}
	}
	/**
	 * 
	 * ��������isExit
	 * ���ߣ�zhouyh
	 * ����ʱ�䣺2014-8-30 ����02:12:30
	 * �������жϸ������ַ����Ƿ��Ѿ�������bloofilter�У�������ڷ���true�������ڷ���false
	 * @param value
	 * @return
	 */
	public synchronized boolean isExit(String value){
		//�жϴ����ֵ�Ƿ�Ϊnull
		if(null == value){
			return false;
		}
		
		for(SimpleHash hashFun : hashFuns){
			if(!bitSets.get(hashFun.hash(value))){
				//����ж�8��hash����ֵ����һ��λ�ò����ڼ����ж�Ϊ������Bloofilter��
				return false;
			}
		}
		
		return true;
	}
	
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
		 * 
		 * ��������hash
		 * ���ߣ�zhouyh
		 * ����ʱ�䣺2014-8-30 ����01:47:10
		 * ����������hash�ĺ������û�����ѡ���������õ�hash����
		 * @param value
		 * @return
		 */
		public int hash(String value){
			int result = 0;
			int length = value.length();
			for(int i=0; i<length; i++){
				result = seed*result + value.charAt(i);
			}
			
			return (cap-1) & result;
		}
	}

}
