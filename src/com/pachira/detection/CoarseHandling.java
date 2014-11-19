package com.pachira.detection;

public class CoarseHandling {
	/**
	 * �ж�һ���ַ��Ƿ�Ϊ�����ַ�
	 * @param ch ��Ҫ�жϵ��ַ�
	 * @return true or false
	 */
	private static boolean isCh(char ch){
		return ch>='\u4e00' && ch <= '\u9fa5';
	}
	/**
	 * �ж�һ���ַ��Ƿ�Ϊ����
	 * @param ch ��Ҫ�жϵ��ַ�
	 * @return true or false
	 */
	private static boolean isNum(char ch){
		return ch >= '\u0030' && ch <= '\u0039';
	}
	/**
	 * �ж�һ���ַ��Ƿ�ΪӢ��
	 * @param ch ��Ҫ�жϵ��ַ�
	 * @return true or false
	 */
	private static boolean isEng(char ch){
		return (ch >= '\u0041' && ch <= '\u005a') || (ch >= '\u0061' && ch <= '\u007a');
	}
	/**
	 * ȫ��ת��ǡ�����ת���塢��дתСд���������ġ����֡�Ӣ���滻�ɿո�
	 * @param input ��Ҫת�����ַ���
	 * @return ���ص��ַ���
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
