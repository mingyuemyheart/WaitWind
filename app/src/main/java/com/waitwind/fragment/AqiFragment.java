package com.waitwind.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.waitwind.R;
import com.waitwind.common.CONST;
import com.waitwind.dto.WeatherData;
import com.waitwind.manager.XiangJiManager;
import com.waitwind.utils.CommonUtil;
import com.waitwind.utils.OkHttpUtil;
import com.waitwind.utils.WeatherUtil;
import com.waitwind.view.AqiQualityView;
import com.waitwind.view.AqiQualityWeekView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 空气质量
 */

public class AqiFragment extends Fragment implements OnClickListener{
	
	private LinearLayout llContainer = null;
	private int width = 0;
	private float density = 0;
	private TextView tvAqiText = null;
	private TextView tvAqi = null;
	private TextView tvAqiText2 = null;
	private RelativeLayout reAqi = null;
	private HorizontalScrollView hScrollView = null;
	private LinearLayout llContainer1 = null;
	private TextView tvHour = null;
	private TextView tvDay = null;
	private TextView tvPrompt = null;
	private String hourAqi = null;
	private String dayAqi = null;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHH");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
	private RelativeLayout reMain = null;
	private TextView tvNoData = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_aqi, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}
	
	private void initWidget(View view) {
		llContainer = (LinearLayout) view.findViewById(R.id.llContainer);
		tvAqiText = (TextView) view.findViewById(R.id.tvAqiText);
		tvAqi = (TextView) view.findViewById(R.id.tvAqi);
		tvAqiText2 = (TextView) view.findViewById(R.id.tvAqiText2);
		reAqi = (RelativeLayout) view.findViewById(R.id.reAqi);
		hScrollView = (HorizontalScrollView) view.findViewById(R.id.hScrollView);
		llContainer1 = (LinearLayout) view.findViewById(R.id.llContainer1);
		tvHour = (TextView) view.findViewById(R.id.tvHour);
		tvHour.setOnClickListener(this);
		tvDay = (TextView) view.findViewById(R.id.tvDay);
		tvDay.setOnClickListener(this);
		tvPrompt = (TextView) view.findViewById(R.id.tvPrompt);
		reMain = (RelativeLayout) view.findViewById(R.id.reMain);
		tvNoData = (TextView) view.findViewById(R.id.tvNoData);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		density = dm.density;
		
		Date date = new Date();
    	long timestamp = date.getTime();
    	String start1 = sdf1.format(timestamp);
    	String end1 = sdf1.format(timestamp+1000*60*60*24);
    	String start2 = sdf2.format(timestamp);
    	String end2 = sdf2.format(timestamp+1000*60*60*24*7);
    	
    	if (CONST.data.lat != 0 && CONST.data.lng != 0) {
    		reMain.setVisibility(View.VISIBLE);
			tvNoData.setVisibility(View.GONE);
			OkHttp1(XiangJiManager.getXJSecretUrl(CONST.data.lng, CONST.data.lat, start1, end1, timestamp));
			OkHttp2(XiangJiManager.getXJSecretUrl2(CONST.data.lng, CONST.data.lat, start2, end2, timestamp));
		}else {
			reMain.setVisibility(View.GONE);
			tvNoData.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 异步请求
	 */
	private void OkHttp1(String url) {
		OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					return;
				}
				final String result = response.body().string();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject obj = new JSONObject(result);
								List<WeatherData> hourList = new ArrayList<>();
								String aqiDate = null;
								if (!obj.isNull("reqTime")) {
									aqiDate = obj.getString("reqTime");
								}
								if (!obj.isNull("series")) {
									JSONArray array = obj.getJSONArray("series");
									for (int i = 0; i < array.length(); i++) {
										WeatherData data = new WeatherData();
										data.aqi = String.valueOf(array.get(i));
										hourList.add(data);
									}
								}

								if (hourList.size() == 0) {
									reMain.setVisibility(View.GONE);
									tvNoData.setVisibility(View.VISIBLE);
									return;
								}else {
									reMain.setVisibility(View.VISIBLE);
									tvNoData.setVisibility(View.GONE);

									hourAqi = hourList.get(0).aqi;
									setValue(hourAqi);

									AqiQualityView aqiView = new AqiQualityView(getActivity());
									aqiView.setData(hourList, aqiDate);
									llContainer.removeAllViews();
									llContainer.addView(aqiView, new LinearLayout.LayoutParams(width*3, LayoutParams.WRAP_CONTENT));
								}
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
			}
		});
	}
	
	/**
	 * 异步请求
	 */
	private void OkHttp2(String url) {
		OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					return;
				}
				final String result = response.body().string();
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject obj = new JSONObject(result);
								List<WeatherData> hourList = new ArrayList<>();
								String aqiDate = null;
								if (!obj.isNull("reqTime")) {
									aqiDate = obj.getString("reqTime");
								}
								if (!obj.isNull("series")) {
									JSONArray array = obj.getJSONArray("series");
									for (int i = 0; i < array.length(); i++) {
										WeatherData data = new WeatherData();
										data.aqi = String.valueOf(array.get(i));
										hourList.add(data);
									}
								}

								dayAqi = hourList.get(0).aqi;

								AqiQualityWeekView aqiView = new AqiQualityWeekView(getActivity());
								aqiView.setData(hourList, aqiDate);
								llContainer1.removeAllViews();
								llContainer1.addView(aqiView, new LinearLayout.LayoutParams((int)(CommonUtil.dip2px(getActivity(), width/density)), LayoutParams.WRAP_CONTENT));
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
			}
		});
	}
	
	private void setValue(String aqi) {
		if (!TextUtils.isEmpty(aqi)) {
			tvAqi.setText(aqi);
			tvAqiText2.setText(WeatherUtil.getAqi(getActivity(), Integer.valueOf(aqi)));
			tvAqiText.setText(WeatherUtil.getAqiText(getActivity(), Integer.valueOf(aqi)));
			if (Integer.valueOf(aqi) <= 50) {
				reAqi.setBackgroundResource(R.drawable.circle_zero);
			} else if (Integer.valueOf(aqi) >= 51 && Integer.valueOf(aqi) <= 100)  {
				reAqi.setBackgroundResource(R.drawable.circle_one);
			} else if (Integer.valueOf(aqi) >= 101 && Integer.valueOf(aqi) <= 150)  {
				reAqi.setBackgroundResource(R.drawable.circle_two);
			} else if (Integer.valueOf(aqi) >= 151 && Integer.valueOf(aqi) <= 200)  {
				reAqi.setBackgroundResource(R.drawable.circle_three);
			} else if (Integer.valueOf(aqi) >= 201 && Integer.valueOf(aqi) <= 300)  {
				reAqi.setBackgroundResource(R.drawable.circle_four);
			} else if (Integer.valueOf(aqi) >= 301)  {
				reAqi.setBackgroundResource(R.drawable.circle_five);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvHour:
			hScrollView.setVisibility(View.VISIBLE);
			llContainer1.setVisibility(View.GONE);
			tvHour.setBackgroundResource(R.drawable.corner_left_title_bg);
			tvDay.setBackgroundResource(R.drawable.corner_right_white);
			tvHour.setTextColor(getResources().getColor(R.color.white));
			tvDay.setTextColor(getResources().getColor(R.color.title_bg));
			tvPrompt.setText(getString(R.string.current_prompt));
			setValue(hourAqi);
			break;
		case R.id.tvDay:
			hScrollView.setVisibility(View.GONE);
			llContainer1.setVisibility(View.VISIBLE);
			tvHour.setBackgroundResource(R.drawable.corner_left_white);
			tvDay.setBackgroundResource(R.drawable.corner_right_title_bg);
			tvHour.setTextColor(getResources().getColor(R.color.title_bg));
			tvDay.setTextColor(getResources().getColor(R.color.white));
			tvPrompt.setText(getString(R.string.today_prompt));
			setValue(dayAqi);
			break;

		default:
			break;
		}
	}
	
}
