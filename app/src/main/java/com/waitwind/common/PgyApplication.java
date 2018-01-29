package com.waitwind.common;

import android.app.Application;

import com.umeng.socialize.PlatformConfig;

public class PgyApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	{
		//umeng分享的平台注册
		PlatformConfig.setWeixin("wxa5434c7f6b5011e7", "02d0337bcfdf0c102030be35f605d3d5");
		PlatformConfig.setQQZone("1104960218", "3AkAfhWUMx0dqo69");
	}
	
}
