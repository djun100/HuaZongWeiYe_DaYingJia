package com.hzwydyj.finace;

import android.app.Application;
import android.content.Context;

import com.hzwydyj.finace.utils.MyLogger;

public class MyApplication extends Application {

	public static Context CONTEXT = null;
	private static final String NAME = "MyApplication";

	private MyLogger log = MyLogger.yLog();

	private String LastId;
	private String firstId;

	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化全局变量
		// log.i("初始化全局变量");
		CONTEXT = this;
		setLastId(NAME); // 初始化全局变量
		setFirstId(NAME); // 初始化全局变量
	}

	public String getLastId() {
		return LastId;
	}

	public void setLastId(String lastId) {
		LastId = lastId;
	}

	public String getFirstId() {
		return firstId;
	}

	public void setFirstId(String firstId) {
		this.firstId = firstId;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

}
