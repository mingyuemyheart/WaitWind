package com.waitwind.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.waitwind.R;
import com.waitwind.common.CONST;
import com.waitwind.utils.CommonUtil;

/**
 * 欢迎界面
 */

public class WelcomeActivity extends BaseActivity{

	private Context mContext = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		mContext = this;

		SharedPreferences sharedPreferences = getSharedPreferences(CONST.SHOWGUIDE, Context.MODE_PRIVATE);
		final String version = sharedPreferences.getString("VersionCode", null);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!TextUtils.equals(version, CommonUtil.getVersion(mContext))) {
					startActivity(new Intent(getApplication(), GuideActivity.class));
				}else {
					startActivity(new Intent(getApplication(), MainActivity.class));
				}
				finish();
			}
		}, 2000);
	}
	
	@Override
	public boolean onKeyDown(int KeyCode, KeyEvent event){
		if (KeyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}
	
}
