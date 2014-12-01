package com.pachira.spider.mapdb;

import java.util.BitSet;
import java.util.List;

import com.pachira.util.FileUtils;

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
				result = seed*result + value.charAt(i);
			}
			
			return (cap-1) & result;
		}
	}
	public static void main(String[] args) {
		BloomFilter bloom = new BloomFilter();
		List<String> list = FileUtils.readFile("urls/urls.list.1", "gbk");
		for (String line : list) {
			if(!bloom.isExit(line)){
				System.out.println("---" + line);
				bloom.add(line);
			}else{
//				System.out.println("exists: " + line);
			}
		}
	}

}
