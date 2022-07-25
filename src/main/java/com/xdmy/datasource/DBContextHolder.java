package com.xdmy.datasource;

/**
 * 多库切换
 */
public class DBContextHolder {
	private static ThreadLocal<String> contextHolder = new ThreadLocal<String>();

	public static String getDbType() {
		String db = contextHolder.get();
		return db;
	}

	public static void setDbType(String str) {
		contextHolder.set(str);
	}

	public static void clearDBType() {
		contextHolder.remove();
	}
}
