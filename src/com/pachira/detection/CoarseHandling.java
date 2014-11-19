package com.pachira.detection;

public class CoarseHandling {
	/**
	 * 判断一个字符是否为中文字符
	 * @param ch 需要判断的字符
	 * @return true or false
	 */
	private static boolean isCh(char ch){
		return ch>='\u4e00' && ch <= '\u9fa5';
	}
	/**
	 * 判断一个字符是否为数字
	 * @param ch 需要判断的字符
	 * @return true or false
	 */
	private static boolean isNum(char ch){
		return ch >= '\u0030' && ch <= '\u0039';
	}
	/**
	 * 判断一个字符是否为英文
	 * @param ch 需要判断的字符
	 * @return true or false
	 */
	private static boolean isEng(char ch){
		return (ch >= '\u0041' && ch <= '\u005a') || (ch >= '\u0061' && ch <= '\u007a');
	}
	/**
	 * 全角转半角、繁体转简体、大写转小写，将非中文、数字、英文替换成空格
	 * @param input 需要转换的字符串
	 * @return 返回的字符串
	 */
	public String change(String sent) {
		char c[] = sent.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
			if (!isCh(c[i]) && !isNum(c[i]) && !isEng(c[i])){
				c[i] = '\u0020';
			}
		}
		return new String(c).toLowerCase().trim();
	}
	
}
