package com.waitwind.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.waitwind.R;
import com.waitwind.common.CONST;
import com.waitwind.utils.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 意见反馈
 */

public class FeedBackActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private ImageView ivBack = null;
	private TextView tvTitle = null;
	private EditText etContent = null;
	private TextView tvSubmit = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		mContext = this;
		initWidget();
	}
	
	private void initWidget() {
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ivBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("意见反馈");
		etContent = (EditText) findViewById(R.id.etContent);
		tvSubmit = (TextView) findViewById(R.id.tvSubmit);
		tvSubmit.setOnClickListener(this);
	}
	
	private boolean checkInfo() {
		if (TextUtils.isEmpty(etContent.getText().toString())) {
			Toast.makeText(mContext, getString(R.string.input_content), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 意见反馈
	 */
	private void OkHttpFeedback(String url) {
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("uid", CONST.UID);
		builder.add("content", etContent.getText().toString());
		builder.add("appid", CONST.APPID);
		RequestBody body = builder.build();
		OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					return;
				}
				final String result = response.body().string();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject object = new JSONObject(result);
								if (object != null) {
									if (!object.isNull("status")) {
										int status  = object.getInt("status");
										if (status == 1) {//成功
											Toast.makeText(mContext, getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
											finish();
										}else {
											//失败
											if (!object.isNull("msg")) {
												String msg = object.getString("msg");
												if (msg != null) {
													Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
												}
											}
										}
									}
								}
								cancelDialog();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			finish();
			break;
		case R.id.tvSubmit:
			if (checkInfo()) {
				showDialog();
				OkHttpFeedback("http://decision-admin.tianqi.cn/home/Work/request");
			}
			break;

		default:
			break;
		}
	}
	
}
