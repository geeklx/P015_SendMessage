package com.liangxiao.sendmessage.application;

import com.thinkland.sdk.util.CommonFun;

import android.app.Application;

public class SendMessageApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		CommonFun.initialize(getApplicationContext(), false);
	}
}
