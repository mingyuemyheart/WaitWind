package com.waitwind.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.waitwind.R;
import com.waitwind.adapter.RankAdapter;
import com.waitwind.common.CONST;
import com.waitwind.dto.CityDto;
import com.waitwind.dto.RankDto;
import com.waitwind.manager.RankManager;
import com.waitwind.stickygridheaders.StickyGridHeadersGridView;
import com.waitwind.utils.OkHttpUtil;
import com.waitwind.utils.WeatherUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.weather.api.WeatherAPI;
import cn.com.weather.listener.AsyncResponseHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 排行榜
 */

public class RankActivity extends BaseActivity implements OnClickListener, AMapLocationListener{
	
	private Context mContext = null;
	private ImageView ivBack = null;
	private TextView tvTitle = null;
	private TextView tvWind = null;
	private TextView tvAqi = null;
	private AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
	private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	private String cityId = null;
	private LinearLayout llWind = null;
	private LinearLayout llAqi = null;
	private TextView tvWindDate = null;
	private TextView tvWindRank = null;
	private TextView tvWindSpeed = null;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmm");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH");
	private StickyGridHeadersGridView stickyGridView1 = null;
	private RankAdapter mAdapter1 = null;
	private List<RankDto> windList = new ArrayList<>();
	private int section1 = 1;
	private HashMap<String, Integer> sectionMap1 = new HashMap<>();
	private TextView tvAqiDate = null;
	private TextView tvAqiRank = null;
	private TextView tvAqiIndex = null;
	private StickyGridHeadersGridView stickyGridView2 = null;
	private RankAdapter mAdapter2 = null;
	private List<RankDto> aqiList = new ArrayList<>();
	private int section2 = 1;
	private HashMap<String, Integer> sectionMap2 = new HashMap<>();
	private LinearLayout llMain = null;
	private List<CityDto> focusList = new ArrayList<>();//关注城市列表
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank);
		mContext = this;
		showDialog();
		initWidget();
		initGridView1();
		initGridView2();
	}
	
	/**
	 * 读取保存在本地数据
	 */
	private int readData(Context context, List<CityDto> list) {
		list.clear();
		SharedPreferences sp = context.getSharedPreferences(CONST.SHARED_FOCUS, Context.MODE_PRIVATE);
		int size = sp.getInt("saveListSize", 0);
		for (int i = 0; i < size; i++) {
			CityDto dto = new CityDto();
			dto.cityName = sp.getString("city"+i, null);
			dto.provinceName = sp.getString("province"+i, null);
			dto.cityId = sp.getString("cityId"+i, null);
			dto.lat = sp.getFloat("lat"+i, 0);
			dto.lng = sp.getFloat("lng"+i, 0);
			list.add(dto);
		}
		return size;
	}
	
	private void initWidget() {
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ivBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("排行榜");
		tvWind = (TextView) findViewById(R.id.tvWind);
		tvWind.setOnClickListener(this);
		tvAqi = (TextView) findViewById(R.id.tvAqi);
		tvAqi.setOnClickListener(this);
		llWind = (LinearLayout) findViewById(R.id.llWind);
		llAqi = (LinearLayout) findViewById(R.id.llAqi);
		tvWindDate = (TextView) findViewById(R.id.tvWindDate);
		tvWindRank = (TextView) findViewById(R.id.tvWindRank);
		tvWindSpeed = (TextView) findViewById(R.id.tvWindSpeed);
		tvAqiDate = (TextView) findViewById(R.id.tvAqiDate);
		tvAqiRank = (TextView) findViewById(R.id.tvAqiRank);
		tvAqiIndex = (TextView) findViewById(R.id.tvAqiIndex);
		llMain = (LinearLayout) findViewById(R.id.llMain);
		
		readData(mContext, focusList);
		startLocation();
	}
	
	private void initGridView1() {
		stickyGridView1 = (StickyGridHeadersGridView) findViewById(R.id.stickyGridView1);
		mAdapter1 = new RankAdapter(mContext, windList);
		stickyGridView1.setAdapter(mAdapter1);
	}
	
	private void notifyGridView1() {
		for (int i = 0; i < windList.size(); i++) {
			RankDto sectionDto = windList.get(i);
			if (!sectionMap1.containsKey(sectionDto.header)) {
				sectionDto.section = section1;
				sectionMap1.put(sectionDto.header, section1);
				section1++;
			}else {
				sectionDto.section = sectionMap1.get(sectionDto.header);
			}
			windList.set(i, sectionDto);
		}
		
		if (mAdapter1 != null) {
			mAdapter1.notifyDataSetChanged();
		}
	}
	
	private void initGridView2() {
		stickyGridView2 = (StickyGridHeadersGridView) findViewById(R.id.stickyGridView2);
		mAdapter2 = new RankAdapter(mContext, aqiList);
		stickyGridView2.setAdapter(mAdapter2);
	}
	
	private void notifyGridView2() {
		for (int i = 0; i < aqiList.size(); i++) {
			RankDto sectionDto = aqiList.get(i);
			if (!sectionMap2.containsKey(sectionDto.header)) {
				sectionDto.section = section2;
				sectionMap2.put(sectionDto.header, section2);
				section2++;
			}else {
				sectionDto.section = sectionMap2.get(sectionDto.header);
			}
			aqiList.set(i, sectionDto);
		}
		
		if (mAdapter2 != null) {
			mAdapter2.notifyDataSetChanged();
		}
	}
	
	/**
	 * 开始定位
	 */
	private void startLocation() {
		mLocationOption = new AMapLocationClientOption();//初始化定位参数
        mLocationClient = new AMapLocationClient(mContext);//初始化定位
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setOnceLocation(true);//设置是否只定位一次,默认为false
        mLocationOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
        mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();//启动定位
	}
	
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
        	getCityId(amapLocation.getLongitude(), amapLocation.getLatitude());
        }
	}
	
	/**
	 * 获取天气数据
	 */
	private void getCityId(double lng, double lat) {
		WeatherAPI.getGeo(mContext,String.valueOf(lng), String.valueOf(lat), new AsyncResponseHandler(){
			@Override
			public void onComplete(JSONObject content) {
				super.onComplete(content);
				if (!content.isNull("geo")) {
					try {
						JSONObject geoObj = content.getJSONObject("geo");
						if (!geoObj.isNull("id")) {
							cityId = geoObj.getString("id").substring(0, 9) + ",";
							for (int i = 0; i < focusList.size(); i++) {
								String id = focusList.get(i).cityId + ",";
								cityId += id;
							}
							if (cityId != null) {
								OkHttpWind1(RankManager.getSecretUrl("wind", cityId));
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onError(Throwable error, String content) {
				super.onError(error, content);
			}
		});
	}
	
	/**
	 * 异步请求
	 */
	private void OkHttpWind1(String url) {
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
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject obj = new JSONObject(result.toString());
								if (!obj.isNull("time")) {
									String windDate = obj.getString("time");
									if (!TextUtils.isEmpty(windDate)) {
										try {
											tvWindDate.setText(sdf2.format(sdf1.parse(windDate)) + getString(R.string.hour) + getString(R.string.publish));
										} catch (ParseException e) {
											e.printStackTrace();
										}
									}
								}

								windList.clear();
								if (!obj.isNull("cityRank")) {
									JSONArray array = new JSONArray(obj.getString("cityRank"));

									for (int i = 0; i < array.length(); i++) {
										JSONObject itemObj = array.getJSONObject(i);
										if (i == 0) {
											if (!itemObj.isNull("rank")) {
												tvWindRank.setText(String.valueOf(itemObj.getInt("rank")));
											}
											if (!itemObj.isNull("wind")) {
												tvWindSpeed.setText(itemObj.getString("wind"));
											}
										}else {
											RankDto dto = new RankDto();
											dto.header = getString(R.string.your_focus_city);
											if (!itemObj.isNull("rank")) {
												dto.rank = itemObj.getInt("rank");
											}
											if (!itemObj.isNull("name")) {
												dto.city = itemObj.getString("name");
											}
											if (!itemObj.isNull("districtcn")) {
												dto.district = itemObj.getString("districtcn");
											}
											if (!itemObj.isNull("wind")) {
												dto.windSpeed = itemObj.getString("wind");
												windList.add(dto);
											}
										}
									}
								}

								if (!obj.isNull("rank")) {
									JSONArray array = new JSONArray(obj.getString("rank"));
									for (int i = 0; i < array.length(); i++) {
										JSONObject itemObj = array.getJSONObject(i);
										RankDto dto = new RankDto();
										dto.header = getString(R.string.nation_top_ten);
										if (!itemObj.isNull("rank")) {
											dto.rank = itemObj.getInt("rank");
										}
										if (!itemObj.isNull("name")) {
											dto.city = itemObj.getString("name");
										}
										if (!itemObj.isNull("districtcn")) {
											dto.district = itemObj.getString("districtcn");
										}
										if (!itemObj.isNull("wind")) {
											dto.windSpeed = itemObj.getString("wind");
											windList.add(dto);
										}
									}
								}

								notifyGridView1();
								llMain.setVisibility(View.VISIBLE);
								cancelDialog();
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
	private void OkHttpWind2(String url) {
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
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!TextUtils.isEmpty(result)) {
							try {
								JSONObject obj = new JSONObject(result.toString());
								if (!obj.isNull("time")) {
									String windDate = obj.getString("time");
									if (!TextUtils.isEmpty(windDate)) {
										try {
											tvAqiDate.setText(sdf2.format(sdf1.parse(windDate)) + getString(R.string.hour) + getString(R.string.publish));
										} catch (ParseException e) {
											e.printStackTrace();
										}
									}
								}

								aqiList.clear();
								if (!obj.isNull("cityRank")) {
									JSONArray array = new JSONArray(obj.getString("cityRank"));
									for (int i = 0; i < array.length(); i++) {
										JSONObject itemObj = array.getJSONObject(i);
										if (i == 0) {
											if (!itemObj.isNull("rank")) {
												tvAqiRank.setText(String.valueOf(itemObj.getInt("rank")));
											}
											if (!itemObj.isNull("aqi")) {
												String aqi = itemObj.getString("aqi");
												if (!TextUtils.isEmpty(aqi)) {
													tvAqiIndex.setText(aqi + " " + WeatherUtil.getAqi(mContext, Integer.valueOf(aqi)));
												}
											}
										}else {
											RankDto dto = new RankDto();
											dto.header = getString(R.string.your_focus_city);
											if (!itemObj.isNull("rank")) {
												dto.rank = itemObj.getInt("rank");
											}
											if (!itemObj.isNull("name")) {
												dto.city = itemObj.getString("name");
											}
											if (!itemObj.isNull("districtcn")) {
												dto.district = itemObj.getString("districtcn");
											}
											if (!itemObj.isNull("aqi")) {
												dto.aqi = itemObj.getString("aqi");
												aqiList.add(dto);
											}
										}
									}
								}

								if (!obj.isNull("rank")) {
									JSONArray array = new JSONArray(obj.getString("rank"));
									for (int i = 0; i < array.length(); i++) {
										JSONObject itemObj = array.getJSONObject(i);
										RankDto dto = new RankDto();
										dto.header = getString(R.string.nation_top_ten);
										if (!itemObj.isNull("rank")) {
											dto.rank = itemObj.getInt("rank");
										}
										if (!itemObj.isNull("name")) {
											dto.city = itemObj.getString("name");
										}
										if (!itemObj.isNull("districtcn")) {
											dto.district = itemObj.getString("districtcn");
										}
										if (!itemObj.isNull("aqi")) {
											dto.aqi = itemObj.getString("aqi");
											aqiList.add(dto);
										}
									}
								}

								cancelDialog();
								llAqi.setVisibility(View.VISIBLE);
								notifyGridView2();
							} catch (JSONException e1) {
								e1.printStackTrace();
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
		case R.id.tvWind:
			tvWind.setBackgroundResource(R.drawable.corner_left_title_bg);
			tvAqi.setBackgroundResource(R.drawable.corner_right_white);
			tvWind.setTextColor(getResources().getColor(R.color.white));
			tvAqi.setTextColor(getResources().getColor(R.color.title_bg));
			llWind.setVisibility(View.VISIBLE);
			llAqi.setVisibility(View.GONE);
			stickyGridView1.setVisibility(View.VISIBLE);
			stickyGridView2.setVisibility(View.GONE);
			break;
		case R.id.tvAqi:
			tvWind.setBackgroundResource(R.drawable.corner_left_white);
			tvAqi.setBackgroundResource(R.drawable.corner_right_title_bg);
			tvWind.setTextColor(getResources().getColor(R.color.title_bg));
			tvAqi.setTextColor(getResources().getColor(R.color.white));
			llWind.setVisibility(View.GONE);
			stickyGridView1.setVisibility(View.GONE);
			stickyGridView2.setVisibility(View.VISIBLE);
			
			if (aqiList.size() == 0) {
				showDialog();
				OkHttpWind2(RankManager.getSecretUrl("aqi", cityId));
			}else {
				llAqi.setVisibility(View.VISIBLE);
			}
			break;

		default:
			break;
		}
	}
}
