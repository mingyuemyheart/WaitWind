package com.waitwind.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.waitwind.R;
import com.waitwind.utils.CommonUtil;

public class AboutActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private ImageView ivBack = null;
	private TextView tvTitle = null;
	private TextView tvVersion = null;
	private TextView tvAddr = null;
	private TextView tvNumber = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutus);
		mContext = this;
		initWidget();
	}
	
	private void initWidget() {
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ivBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("关于我们");
		tvVersion = (TextView) findViewById(R.id.tvVersion);
		tvVersion.setText(getString(R.string.version) + CommonUtil.getVersion(mContext));
		tvAddr = (TextView) findViewById(R.id.tvAddr);
		tvAddr.setText("http://www.tianqi.cn/");
		tvAddr.setOnClickListener(this);
		tvNumber = (TextView) findViewById(R.id.tvNumber);
		tvNumber.setText("010-68408068");
		tvNumber.setOnClickListener(this);
	}
	
	/**
	 * 拨打电话对话框
	 * @param message
	 */
	private void dialDialog(final String message) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.delete_dialog, null);
		TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
		LinearLayout llNegative = (LinearLayout) view.findViewById(R.id.llNegative);
		LinearLayout llPositive = (LinearLayout) view.findViewById(R.id.llPositive);

		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();

		tvMessage.setText(message);
		llNegative.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		llPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + message));
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			finish();
			break;
		case R.id.tvAddr:
			Intent intent = new Intent(mContext, WebViewActivity.class);
			intent.putExtra("URL", tvAddr.getText().toString());
			startActivity(intent);
			break;
		case R.id.tvNumber:
			dialDialog(tvNumber.getText().toString());
			break;

		default:
			break;
		}
	}
	
}
