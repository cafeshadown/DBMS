package com.zhouchao.db;

public class Prime {
	/**
	 * 除留余数法 选择合理的模
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
	 * 判断某整数是否为素数  
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
