package com.zhouchao.db;

public class Prime {
	/**
	 * ���������� ѡ������ģ
	 * @param n
	 * @return
	 */
	public static int getRefinedModel(int n) {
		for (int i =n * 4 / 3; i >= n; i--) {
			if (isPrime(i)) {
				return i;
			}
		}
		return n;
	}
	/**
	 * �ж�ĳ�����Ƿ�Ϊ����  
	 * @param m
	 * @return
	 */
	public static boolean isPrime(int m) {
		if (m < 2) {
			return false;
		}
		for (int i = 2; i * i <= m; i++) {
			if (m % i == 0) {
				return false;
			}
		}
		return true;
	}
}
