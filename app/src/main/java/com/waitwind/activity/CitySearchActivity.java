package com.waitwind.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.amap.api.maps.model.LatLng;
import com.waitwind.R;
import com.waitwind.adapter.CityAdapter;
import com.waitwind.adapter.CitySearchAdapter;
import com.waitwind.common.CONST;
import com.waitwind.dto.CityDto;
import com.waitwind.utils.DBManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 城市搜索
 */

public class CitySearchActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private ImageView ivBack = null;
	private EditText etSearch = null;
	private ProgressBar progressBar = null;
	private MyBroadCastReceiver mReceiver = null;
	
	//存放搜索后列表
	private ListView mListView = null;
	private CityAdapter cityAdapter = null;
	private List<CityDto> cityList = new ArrayList<>();
	
	//搜索列表
	private ListView searchListView = null;
	private CitySearchAdapter searchAdapter = null;
	private List<CityDto> searchList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_search);
		mContext = this;
		initBroadCast();
		initWidget();
		initListView();
		initSearchListView();
	}
	
	private void initBroadCast() {
		mReceiver = new MyBroadCastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CONST.BROAD_SEARCH);
		registerReceiver(mReceiver, intentFilter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}
	
	private class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (TextUtils.equals(arg1.getAction(), CONST.BROAD_SEARCH)) {
				int index = arg1.getIntExtra(CONST.SEARCH_INDEX, -1);
				if (index != -1) {
					cityList.remove(index);
					if (cityAdapter != null) {
						cityAdapter.notifyDataSetChanged();
					}
					saveData(mContext, cityList);
				}
				
			}
		}
		
	}
	
	private void initWidget() {
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ivBack.setOnClickListener(this);
		etSearch = (EditText) findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(watcher);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
	}
	
	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void afterTextChanged(Editable arg0) {
			if (arg0.toString() == null) {
				return;
			}

			progressBar.setVisibility(View.VISIBLE);
			searchList.clear();
			if (arg0.toString().trim().equals("")) {
				progressBar.setVisibility(View.GONE);
				searchListView.setVisibility(View.GONE);
			}else {
				searchListView.setVisibility(View.VISIBLE);
				getCityInfo(arg0.toString().trim());
			}

		}
	};
	
	/**
	 * 获取城市信息
	 */
	private void getCityInfo(String keyword) {
		DBManager dbManager = new DBManager(mContext);
		dbManager.openDateBase();
		dbManager.closeDatabase();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
		Cursor cursor = database.rawQuery("select * from "+DBManager.TABLE_NAME1+" where PRO like "+"\"%"+keyword+"%\""+" or CITY like "+"\"%"+keyword+"%\""+" or DIST like "+"\"%"+keyword+"%\"",null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			CityDto dto = new CityDto();
			dto.provinceName = cursor.getString(cursor.getColumnIndex("PRO"));
			dto.cityName = cursor.getString(cursor.getColumnIndex("CITY"));
			if (TextUtils.equals(dto.cityName, "市辖区")) {
				dto.cityName = "";
			}
			dto.disName = cursor.getString(cursor.getColumnIndex("DIST"));
			dto.addr = cursor.getString(cursor.getColumnIndex("ADDR"));
			dto.lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("LAT")));
			dto.lng = Double.parseDouble(cursor.getString(cursor.getColumnIndex("LON")));
			LatLng latLng = new LatLng(dto.lat, dto.lng);
			float[] p = getVector(latLng);
			double speed = Math.sqrt(p[0]*p[0] + p[1]*p[1]);
			BigDecimal bd = new BigDecimal((float)speed);
			float value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			dto.windSpeed = value;
			searchList.add(dto);
		}
		if (searchAdapter != null) {
			searchAdapter.notifyDataSetChanged();
			progressBar.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 获取粒子的速度
	 * @param latLng
	 * @return
	 */
	private float[] getVector(LatLng latLng) {
		float a = (float)((CONST.windData.width - 1 - 1e-6)*(latLng.longitude - CONST.windData.x0)/(CONST.windData.x1 - CONST.windData.x0));
		float b = (float)((CONST.windData.height - 1 - 1e-6)*(latLng.latitude - CONST.windData.y0)/(CONST.windData.y1 - CONST.windData.y0));
		
		int na = (int) Math.min(Math.floor(a), CONST.windData.width - 1);
		int nb = (int) Math.min(Math.floor(b), CONST.windData.height - 1);
		int ma = (int) Math.min(Math.ceil(a), CONST.windData.width - 1);
		int mb = (int) Math.min(Math.ceil(b), CONST.windData.height - 1);
		
		float fa = a - na;
		float fb = b - nb;
		
		int index = CONST.windData.height;
		int count = CONST.windData.dataList.size();
		
		float[] array = new float[2];
		try {
			float vx = (float)(CONST.windData.dataList.get(Math.min(na*index+nb, count-1)).initX * (1-fa)*(1-fb)+ 
					CONST.windData.dataList.get(Math.min(ma*index+nb, count-1)).initX * fa*(1-fb)+
					CONST.windData.dataList.get(Math.min(na*index+mb, count-1)).initX * (1-fa)*fb+
					CONST.windData.dataList.get(Math.min(ma*index+mb, count-1)).initX * fa*fb) / 2;
			
			float vy = (float)(CONST.windData.dataList.get(Math.min(na*index+nb, count-1)).initY * (1-fa)*(1-fb)+ 
					CONST.windData.dataList.get(Math.min(ma*index+nb, count-1)).initY * fa*(1-fb)+
					CONST.windData.dataList.get(Math.min(na*index+mb, count-1)).initY * (1-fa)*fb+
					CONST.windData.dataList.get(Math.min(ma*index+mb, count-1)).initY * fa*fb) / 2;
			
			array[0] = vx;
			array[1] = vy;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return array;
	}
	
	/**
	 * 初始化listview
	 */
	private void initListView() {
		int size = readData(mContext, cityList);
		for (int i = 0; i < size; i++) {
			CityDto dto = cityList.get(i);
			LatLng latLng = new LatLng(dto.lat, dto.lng);
			float[] p = getVector(latLng);
			double speed = Math.sqrt(p[0]*p[0] + p[1]*p[1]);
			BigDecimal bd = new BigDecimal((float)speed);
			float value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			dto.windSpeed = value;
		}
		
		mListView = (ListView) findViewById(R.id.listView);
		cityAdapter = new CityAdapter(mContext, cityList);
		mListView.setAdapter(cityAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				CityDto dto = cityList.get(arg2);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", dto);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	/**
	 * 初始化listview
	 */
	private void initSearchListView() {
		searchListView = (ListView) findViewById(R.id.searchListView);
		searchAdapter = new CitySearchAdapter(mContext, searchList);
		searchListView.setAdapter(searchAdapter);
		searchListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				CityDto dto = searchList.get(arg2);
				searchListView.setVisibility(View.GONE);
				etSearch.setText("");
				
				boolean isAdded = false;//判断是否已经添加
				String name = dto.provinceName+dto.cityName+dto.disName+dto.addr;
				for (int i = 0; i < cityList.size(); i++) {
					String name2 = cityList.get(i).provinceName+cityList.get(i).cityName+cityList.get(i).disName+cityList.get(i).addr;
					if (TextUtils.equals(name, name2)) {
						isAdded = true;
						break;
					}
				}
				
				if (isAdded == false) {//判断重复添加
					if (cityList.size() == 5) {//最多添加5个关注城市
						cityList.remove(0);
					}
					cityList.add(dto);
					if (cityAdapter != null) {
						cityAdapter.notifyDataSetChanged();
					}
					
					saveData(mContext, cityList);
				}
			}
		});
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
			dto.lat = sp.getFloat("lat"+i, 0);
			dto.lng = sp.getFloat("lng"+i, 0);
			dto.provinceName = sp.getString("province"+i, null);
			dto.disName = sp.getString("dis"+i, null);
			dto.addr = sp.getString("addr"+i, null);
			list.add(dto);
		}
		return size;
	}
	
	/**
	 * 保存数据到手机本地
	 */
	private void saveData(Context context, List<CityDto> saveList) {
		if (saveList == null) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(CONST.SHARED_FOCUS, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		int size = saveList.size();
		editor.putInt("saveListSize", size);
		for (int i = 0; i < size; i++) {
			editor.remove("city"+i);
			editor.remove("province"+i);
			editor.remove("lat"+i);
			editor.remove("lng"+i);
			editor.remove("dis"+i);
			editor.remove("addr"+i);
			
			editor.putString("city"+i, saveList.get(i).cityName);
			editor.putString("province"+i, saveList.get(i).provinceName);
			editor.putFloat("lat"+i, (float)(saveList.get(i).lat));
			editor.putFloat("lng"+i, (float)(saveList.get(i).lng));
			editor.putString("dis"+i, saveList.get(i).disName);
			editor.putString("addr"+i, saveList.get(i).addr);
		}
		editor.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBack:
			finish();
			break;

		default:
			break;
		}
	}
	
}
