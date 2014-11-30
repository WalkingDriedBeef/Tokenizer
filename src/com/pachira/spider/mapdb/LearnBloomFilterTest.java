package com.pachira.spider.mapdb;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 类名： BloomFilterTest
 * 包名： com.utilTest
 * 作者： zhouyh
 * 时间： 2014-8-29 下午02:54:56
 * 描述： 布隆过滤器，传统的布隆过滤器不支持从集合中删除成员
 */
public class LearnBloomFilterTest {
	/**
	 * 某文件中包含了一些8位的电话号码，统计出现的电话号码的次数
	 * --8为最大是99 999 999，大约是99M的bit，12.5MB的内存，就可以统计出来出现的号码。
	 * --99 999 999 bit ~ 99 999 999 / 8 /1024 kb ~ 12207 kb ~ 12.3M
	 */
	//DEFAULT_SIZE为2的27次方，即此处的左移26位
	private static final int DEFAULT_SIZE = 2<<26;
	
	/*
	 * 不同哈希函数的种子，一般取质数
	 * seeds数组共有8个值，则代表采用8种不同的哈希函数
	 */
	private int[] seeds = new int[]{3, 5, 7, 11, 13, 31, 37, 61};
	
	public static class SimpleHash {
		/*
		 * cap为DEFAULT_SIZE，即用于结果的最大字符串的值
		 * seed为计算hash值的一个key值，具体对应上文中的seeds数组
		 */
		private int cap;
		private int seed;
		/**
		 * 
		 * 构造函数
		 * 作者：zhouyh
		 * @param cap
		 * @param seed
		 */
		public SimpleHash(int cap, int seed){
			this.cap = cap;
			this.seed = seed;
		}
		/**
		 * 加快Hash效率的另一个有效途径是编写良好的自定义对象的HashCode，String的实现采用了如下的计算方法：   
		 * for (int i = 0; i < len; i++) {      
		 *     h = 31*h + val[off++];      
		 * }      
		 * hash = h;        
		 * 这种方法HashCode的计算方法可能最早出现在Brian W. Kernighan和Dennis M. Ritchie的
		 * 《The C Programming Language》中，被认为是性价比最高的算法（又被称为times33算法，
		 * 因为C中乘数常量为33，JAVA中改为31），实际上，包括List在内的大多数的对象都是用这种方法计算Hash值。   
		 * 
		 * 另一种比较特殊的hash算法称为布隆过滤器，它以牺牲细微精度为代价，换来存储空间的大量节俭，
		 * 常用于诸如判断用户名重复、是否在黑名单上等等。  
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
			System.out.println(simHash.hash("中国"));
			System.out.println(new String("中国").hashCode());
			
		}
	}
}
