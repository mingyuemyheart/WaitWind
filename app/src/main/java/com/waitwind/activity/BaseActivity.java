package com.waitwind.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.waitwind.view.MyDialog;

public class BaseActivity extends Activity{

	private Context mContext;
	public MyDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	public void showDialog() {
		if (mDialog == null) {
			mDialog = new MyDialog(mContext);
		}
		mDialog.show();
	}

	public void cancelDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}
	
}
