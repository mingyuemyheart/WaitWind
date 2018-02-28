package com.waitwind.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.waitwind.R;
import com.waitwind.common.CONST;
import com.waitwind.dto.CityDto;
import com.waitwind.dto.WeatherData;
import com.waitwind.dto.WindDto;
import com.waitwind.manager.WindManager;
import com.waitwind.utils.AutoUpdateUtil;
import com.waitwind.utils.CommonUtil;
import com.waitwind.utils.OkHttpUtil;
import com.waitwind.utils.WeatherUtil;
import com.waitwind.view.WindView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements OnClickListener, AMapLocationListener, OnCameraChangeListener, 
InfoWindowAdapter, OnMapClickListener, OnInfoWindowClickListener, OnGeocodeSearchListener, OnMapScreenShotListener{
	
	private Context mContext = null;
	private RelativeLayout reTitle = null;
	private TextView tvTitle = null;
	private ImageView ivSetting = null;//设置按钮
	private ImageView ivLocation = null;
//	private ImageView ivExpand = null;//全屏按钮
	private Configuration configuration = null;//方向监听器
	private MapView mMapView = null;
	private AMap aMap = null;
	private float zoom = 4f;
	private RelativeLayout container = null;
	public RelativeLayout container2 = null;
	private int width = 0, height = 0;
	private WindView windView = null;
	private AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
	private AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	private Marker marker = null;//定位的marker
	private GeocodeSearch geocoderSearch = null;
	private boolean isCanClick = false;//判断点击infowindow是否生效
	private ImageView ivSearch = null;//搜索按钮
	private ImageView ivShare = null;
	private long mExitTime;//记录点击完返回按钮后的long型时间
	private RelativeLayout reContent = null;
	
	//marker infowindow
	private TextView tvDes = null;
	private TextView tvWind = null;
	private TextView tvAqi = null;
	private ProgressBar progressBar = null;
	private LinearLayout llMarkView = null;
	
	private DrawerLayout drawerlayout = null;
	private RelativeLayout reLeft = null;
    private ImageView ivPhe = null;
    private TextView tvTemp = null;
    private TextView tvFactWind = null;
    private TextView tvFactAqi = null;
    private LinearLayout llFact = null;
    private ProgressBar leftBar = null;
    private LinearLayout llCamera = null;
    private LinearLayout llRank = null;
    private LinearLayout llAbout = null;
    private LinearLayout llFeedback = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		showDialog();
		getWidthHeight();
		initWidget();
		initMap(savedInstanceState);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		configuration = newConfig;
		getWidthHeight();
		reloadWind();
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE); 
//			ivExpand.setImageResource(R.drawable.iv_expand);
			reTitle.setVisibility(View.VISIBLE);
		}else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE); 
//			ivExpand.setImageResource(R.drawable.iv_collose);
			reTitle.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 获取屏幕宽高
	 */
	private void getWidthHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		reTitle = (RelativeLayout) findViewById(R.id.reTitle);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		ivSetting = (ImageView) findViewById(R.id.ivSetting);
		ivSetting.setOnClickListener(this);
		ivLocation = (ImageView) findViewById(R.id.ivLocation);
		ivLocation.setOnClickListener(this);
//		ivExpand = (ImageView) findViewById(R.id.ivExpand);
//		ivExpand.setOnClickListener(this);
		container = (RelativeLayout) findViewById(R.id.container);
		container2 = (RelativeLayout) findViewById(R.id.container2);
		ivSearch = (ImageView) findViewById(R.id.ivSearch);
		ivSearch.setOnClickListener(this);
		ivShare = (ImageView) findViewById(R.id.ivShare);
		ivShare.setOnClickListener(this);
		reContent = (RelativeLayout) findViewById(R.id.reContent);
		
		//侧拉页面
		drawerlayout = (DrawerLayout) findViewById(R.id.drawerlayout);
		drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		ivPhe = (ImageView) findViewById(R.id.ivPhe);
		tvTemp = (TextView) findViewById(R.id.tvTemp);
		tvFactWind = (TextView) findViewById(R.id.tvFactWind);
		tvFactAqi = (TextView) findViewById(R.id.tvFactAqi);
		llFact = (LinearLayout) findViewById(R.id.llFact);
		leftBar = (ProgressBar) findViewById(R.id.leftBar);
		llCamera = (LinearLayout) findViewById(R.id.llCamera);
		llCamera.setOnClickListener(this);
		llRank = (LinearLayout) findViewById(R.id.llRank);
		llRank.setOnClickListener(this);
		llAbout = (LinearLayout) findViewById(R.id.llAbout);
		llAbout.setOnClickListener(this);
		llFeedback = (LinearLayout) findViewById(R.id.llFeedback);
		llFeedback.setOnClickListener(this);
		reLeft = (RelativeLayout) findViewById(R.id.reLeft);
		ViewGroup.LayoutParams params = reLeft.getLayoutParams();
		params.width = width-150;
		reLeft.setLayoutParams(params);

		AutoUpdateUtil.checkUpdate(MainActivity.this, mContext, "87", getString(R.string.app_name), true);
		
		geocoderSearch = new GeocodeSearch(mContext);
		geocoderSearch.setOnGeocodeSearchListener(this);
	}
	
	/**
	 * 初始化地图
	 */
	private void initMap(Bundle bundle) {
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(bundle);
		if (aMap == null) {
			aMap = mMapView.getMap();
		}
		aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
		aMap.setOnCameraChangeListener(this);
		aMap.setInfoWindowAdapter(this);
		aMap.setOnMapClickListener(this);
		aMap.setOnInfoWindowClickListener(this);

		OkHttpWind(WindManager.getSecretUrl("1000", "0"));
	}
	
	/**
	 * 异步请求
	 */
	private void OkHttpWind(String url) {
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
								JSONObject obj = new JSONObject(result);
								if (obj != null) {
									if (!obj.isNull("gridHeight")) {
										CONST.windData.height = obj.getInt("gridHeight");
									}
									if (!obj.isNull("gridWidth")) {
										CONST.windData.width = obj.getInt("gridWidth");
									}
									if (!obj.isNull("x0")) {
										CONST.windData.x0 = obj.getDouble("x0");
									}
									if (!obj.isNull("y0")) {
										CONST.windData.y0 = obj.getDouble("y0");
									}
									if (!obj.isNull("x1")) {
										CONST.windData.x1 = obj.getDouble("x1");
									}
									if (!obj.isNull("y1")) {
										CONST.windData.y1 = obj.getDouble("y1");
									}

									LatLng latLngStart = aMap.getProjection().fromScreenLocation(new Point(0, 0));
									LatLng latLngEnd = aMap.getProjection().fromScreenLocation(new Point(width, height));
									CONST.windData.latLngStart = latLngStart;
									CONST.windData.latLngEnd = latLngEnd;

									if (!obj.isNull("field")) {
										JSONArray array = new JSONArray(obj.getString("field"));
										for (int i = 0; i < array.length(); i+=2) {
											WindDto dto2 = new WindDto();
											dto2.initX = (float)(array.optDouble(i));
											dto2.initY = (float)(array.optDouble(i+1));
											CONST.windData.dataList.add(dto2);
										}
									}

									reloadWind();

									ivSearch.setVisibility(View.VISIBLE);
									ivShare.setVisibility(View.VISIBLE);
									ivLocation.setVisibility(View.VISIBLE);
//						ivExpand.setVisibility(View.VISIBLE);
									startLocation();
									cancelDialog();
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
	public void onLocationChanged(AMapLocation al) {
        if (al != null && al.getErrorCode() == 0) {
        	tvTitle.setText(al.getCity()+al.getDistrict()+"\n"+al.getStreet());
			CONST.data.position = al.getCity()+al.getDistrict()+al.getStreet();
			addMarker(al.getLatitude(), al.getLongitude());
        }
	}
	
	private void addMarker(final double lat, final double lng) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.mark, null);
    	MarkerOptions options = new MarkerOptions();
    	options.anchor(0.5f, 1.0f);
    	if (view != null) {
	    	options.icon(BitmapDescriptorFactory.fromView(view));
		}else {
			options.icon(BitmapDescriptorFactory.fromResource(R.drawable.iv_location));
		}
    	options.position(new LatLng(lat, lng));
    	options.title(getString(R.string.app_name));
    	aMap.clear();
    	marker = aMap.addMarker(options);
    	marker.showInfoWindow();
    	
    	new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				CONST.data.lat = lat;
				CONST.data.lng = lng;
				OkHttpDetail(WindManager.getSecretUrl2(lng, lat));
			}
		}, 1000);
	}
	
	@Override
	public void onMapClick(LatLng arg0) {
		//latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系   
    	RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(arg0.latitude, arg0.longitude), 200, GeocodeSearch.AMAP); 
    	geocoderSearch.getFromLocationAsyn(query); 
		addMarker(arg0.latitude, arg0.longitude);
	}
	
	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
				String province = result.getRegeocodeAddress().getProvince();
				String city = result.getRegeocodeAddress().getCity();
				String district = result.getRegeocodeAddress().getDistrict();
				
				if (!TextUtils.isEmpty(province) || !TextUtils.isEmpty(district)) {
					if (TextUtils.isEmpty(city)) {
						tvTitle.setText(province+"\n"+district);
						CONST.data.position = province+district;
					}else {
						tvTitle.setText(city+"\n"+district);
						CONST.data.position = city+district;
					}
				}else {
					tvTitle.setText(getString(R.string.unkown_position));
					CONST.data.position = getString(R.string.unkown_position);
				}
			} 
		} 
	}
	
	@Override
	public View getInfoContents(Marker arg0) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.marker_info, null);
		tvDes = (TextView) view.findViewById(R.id.tvDes);
		tvWind = (TextView) view.findViewById(R.id.tvWind);
		tvAqi = (TextView) view.findViewById(R.id.tvAqi);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		llMarkView = (LinearLayout) view.findViewById(R.id.llMarkView);
		return view;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		return null;
	}
	
	@Override
	public void onInfoWindowClick(Marker arg0) {
		if (isCanClick) {
			startActivity(new Intent(mContext, WeatherActivity.class));
		}
	}
	
	@Override
	public void onCameraChange(CameraPosition arg0) {
		if (marker != null) {
			marker.hideInfoWindow();
		}
		container.removeAllViews();
		container2.removeAllViews();
	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		reloadWind();
	}

	long t = new Date().getTime();

	/**
	 * 重新加载风场
	 */
	private void reloadWind() {
		t = new Date().getTime() - t;
		if (t < 1000) {
			return;
		}

		LatLng latLngStart = aMap.getProjection().fromScreenLocation(new Point(0, 0));
		LatLng latLngEnd = aMap.getProjection().fromScreenLocation(new Point(width, height));
		CONST.windData.latLngStart = latLngStart;
		CONST.windData.latLngEnd = latLngEnd;
		if (windView == null) {
			windView = new WindView(mContext);
			windView.init(MainActivity.this);
			windView.setData(CONST.windData);
			windView.start();
			windView.invalidate();
		}

		container.removeAllViews();
		container.addView(windView);
	}
	
	/**
	 * 异步请求
	 */
	private void OkHttpDetail(String url) {
		progressBar.setVisibility(View.VISIBLE);
		llMarkView.setVisibility(View.INVISIBLE);
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
								JSONObject obj = new JSONObject(result);
								if (obj != null) {
									if (!obj.isNull("air")) {
										JSONObject airObj = new JSONObject(obj.getString("air"));
										if (!airObj.isNull("k")) {
											JSONObject kObj = new JSONObject(airObj.getString("k"));
											if (!kObj.isNull("k3")) {
//									String[] k3Array = kObj.getString("k3").split("\\|");
//									CONST.data.aqiList.clear();
//									for (int i = 0; i < k3Array.length; i++) {
//										WeatherData kDto = new WeatherData();
//										kDto.aqi = k3Array[i];
//										CONST.data.aqiList.add(kDto);
//									}
												if (tvAqi != null) {
													String aqi = WeatherUtil.lastValue(kObj.getString("k3"));
													if (!TextUtils.isEmpty(aqi)) {
														tvAqi.setText(aqi + " " + WeatherUtil.getAqi(mContext, Integer.valueOf(aqi)));
														tvAqi.setBackgroundResource(WeatherUtil.getAqiBg(mContext, Integer.valueOf(aqi)));
														CONST.data.aqi = aqi;
													}
												}

												if (tvFactAqi != null) {
													String aqi = WeatherUtil.lastValue(kObj.getString("k3"));
													if (!TextUtils.isEmpty(aqi)) {
														tvFactAqi.setText(aqi);
														tvFactAqi.setBackgroundResource(WeatherUtil.getAqiBg(mContext, Integer.valueOf(aqi)));
														CONST.data.aqi = aqi;
													}
												}
											}
										}
									}

									if (!obj.isNull("observe")) {
										JSONObject obObj = new JSONObject(obj.getString("observe"));
										if (!obObj.isNull("l")) {
											JSONObject lObj = new JSONObject(obObj.getString("l"));
											if (!lObj.isNull("l5")) {
												String weatherCode = WeatherUtil.lastValue(lObj.getString("l5"));
												Drawable drawable = getResources().getDrawable(R.drawable.phenomenon_drawable);
												drawable.setLevel(Integer.valueOf(weatherCode));
												ivPhe.setBackground(drawable);
											}
											if (!lObj.isNull("l1")) {
												String factTemp = WeatherUtil.lastValue(lObj.getString("l1"));
												tvTemp.setText(factTemp);
											}
											String windDir = WeatherUtil.lastValue(lObj.getString("l4"));
											String windSpeed = WeatherUtil.lastValue(lObj.getString("l11"));
											tvFactWind.setText(getString(WeatherUtil.getWindDirection(Integer.valueOf(windDir))) + " " + windSpeed+getString(R.string.unit_speed));
										}

									}

									if (!obj.isNull("discript")) {
										JSONObject disObj = new JSONObject(obj.getString("discript"));
										String dis1 = disObj.getString("1");
										String dis2 = disObj.getString("2");
										String dis3 = disObj.getString("3");
										if (dis1 != null && dis3 != null) {
											tvDes.setText(dis1+":"+dis3);
										}
										CONST.data.dis1 = dis1;
										CONST.data.dis2 = dis2;
										CONST.data.dis3 = dis3;
									}

									if (!obj.isNull("forecast")) {
										CONST.data.windList.clear();
										JSONArray foreArray = obj.getJSONArray("forecast");
										for (int i = 0; i < foreArray.length(); i+=3) {
											JSONObject foreObj = foreArray.getJSONObject(i);
											if (i == 0) {
												if (!foreObj.isNull("speed")) {
													String speed = foreObj.getString("speed");
													if (!TextUtils.isEmpty(speed)) {
														BigDecimal bd = new BigDecimal(Float.valueOf(speed));
														float value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
														CONST.data.speed = String.valueOf(value);
													}
												}
												if (!foreObj.isNull("level")) {
													CONST.data.force = foreObj.getString("level");
												}
												if (!foreObj.isNull("dirdes")) {
													CONST.data.dir = foreObj.getString("dirdes");
												}

												if (CONST.data.speed != null && CONST.data.force != null && CONST.data.dir != null) {
													tvWind.setText(CONST.data.speed+getString(R.string.unit_speed)+" "+CONST.data.force+getString(R.string.level)
															+"\n"+CONST.data.dir);
												}
											}

											WeatherData dto = new WeatherData();
											if (!foreObj.isNull("speed")) {
												dto.speed = foreObj.getString("speed");
											}
											if (!foreObj.isNull("date")) {
												dto.date = foreObj.getString("date");
											}
											CONST.data.windList.add(dto);
										}
									}

									if (!obj.isNull("kqwr")) {
										JSONObject kqObj = new JSONObject(obj.getString("kqwr"));
										CONST.data.clearPollution();
										if (!kqObj.isNull("024")) {
											String today = kqObj.getString("024");
											if (!TextUtils.isEmpty(today)) {
												CONST.data.today = Integer.valueOf(today)-61;
											}
										}
										if (!kqObj.isNull("048")) {
											String tommorow = kqObj.getString("048");
											if (!TextUtils.isEmpty(tommorow)) {
												CONST.data.tommorow = Integer.valueOf(tommorow)-61;
											}
										}
										if (!kqObj.isNull("072")) {
											String after = kqObj.getString("072");
											if (!TextUtils.isEmpty(after)) {
												CONST.data.afterTomm = Integer.valueOf(after)-61;
											}
										}
									}

									llMarkView.setVisibility(View.VISIBLE);
									isCanClick = true;
									llFact.setVisibility(View.VISIBLE);
									leftBar.setVisibility(View.GONE);
									progressBar.setVisibility(View.GONE);
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

	@Override
	public void onMapScreenShot(final Bitmap bitmap1) {//bitmap1为地图截屏
		Bitmap bitmap2 = CommonUtil.captureView(reContent);
		Bitmap bitmap3 = CommonUtil.mergeBitmap(MainActivity.this, bitmap1, bitmap2, true);
		CommonUtil.clearBitmap(bitmap1);
		CommonUtil.clearBitmap(bitmap2);
		Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.iv_bottom_share);
		Bitmap bitmap = CommonUtil.mergeBitmap(mContext, bitmap3, bitmap4, false);
		CommonUtil.clearBitmap(bitmap3);
		CommonUtil.clearBitmap(bitmap4);
		CommonUtil.share(MainActivity.this, bitmap);
	}

	@Override
	public void onMapScreenShot(Bitmap arg0, int arg1) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (drawerlayout.isDrawerOpen(reLeft)) {
				drawerlayout.closeDrawer(reLeft);
			}else {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					Toast.makeText(mContext, getString(R.string.confirm_exit)+getString(R.string.app_name), Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				} else {
					finish();
				}
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivSetting:
			if (drawerlayout.isDrawerOpen(reLeft)) {
				drawerlayout.closeDrawer(reLeft);
			}else {
				drawerlayout.openDrawer(reLeft);
			}
			break;
		case R.id.ivShare:
			aMap.getMapScreenShot(MainActivity.this);
			break;
		case R.id.ivSearch:
			startActivityForResult(new Intent(mContext, CitySearchActivity.class), 0);
			break;
		case R.id.ivLocation:
			startLocation();
			break;
		case R.id.ivExpand:
			if (configuration == null) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}else {
				if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
			}
			break;
		case R.id.llCamera:
			startActivity(new Intent(mContext, CameraAqiActivity.class));
			break;
		case R.id.llRank:
			startActivity(new Intent(mContext, RankActivity.class));
			break;
		case R.id.llAbout:
			startActivity(new Intent(mContext, AboutActivity.class));
			break;
		case R.id.llFeedback:
			startActivity(new Intent(mContext, FeedBackActivity.class));
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				if (data != null) {
					CityDto dto = data.getExtras().getParcelable("data");
					RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(dto.lat, dto.lng), 200, GeocodeSearch.AMAP); 
			    	geocoderSearch.getFromLocationAsyn(query); 
					addMarker(dto.lat, dto.lng);
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (mMapView != null) {
			mMapView.onResume();
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if (mMapView != null) {
			mMapView.onPause();
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mMapView != null) {
			mMapView.onSaveInstanceState(outState);
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMapView != null) {
			mMapView.onDestroy();
		}
	}

}
