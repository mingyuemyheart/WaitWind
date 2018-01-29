package com.waitwind.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.waitwind.R;
import com.waitwind.dto.CityDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 城市搜索
 */

public class CitySearchAdapter extends BaseAdapter{
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<CityDto> mArrayList = new ArrayList<>();
	
	private final class ViewHolder{
		ImageView ivWindLevel;
		TextView tvCityName;
		TextView tvProvinceName;
	}
	
	private ViewHolder mHolder = null;
	
	public CitySearchAdapter(Context context, List<CityDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_city_search, null);
			mHolder = new ViewHolder();
			mHolder.ivWindLevel = (ImageView) convertView.findViewById(R.id.ivWindLevel);
			mHolder.tvCityName = (TextView) convertView.findViewById(R.id.tvCityName);
			mHolder.tvProvinceName = (TextView) convertView.findViewById(R.id.tvProvinceName);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		CityDto dto = mArrayList.get(position);
		mHolder.tvCityName.setText(dto.disName+dto.addr);
		if (!TextUtils.isEmpty(dto.cityName)) {
			if (dto.cityName.contains(dto.provinceName)) {
				mHolder.tvProvinceName.setText(dto.cityName+"-"+dto.disName+"-"+dto.addr);
			}else {
				mHolder.tvProvinceName.setText(dto.provinceName+"-"+dto.cityName+"-"+dto.disName+"-"+dto.addr);
			}
		}else {
			mHolder.tvProvinceName.setText(dto.provinceName+"-"+dto.disName+"-"+dto.addr);
		}

		if (dto.windSpeed <= 5.5) {
			mHolder.ivWindLevel.setImageResource(R.drawable.iv_micro_wind);
		}else if (dto.windSpeed > 5.5 && dto.windSpeed <= 10.8) {
			mHolder.ivWindLevel.setImageResource(R.drawable.iv_big_wind);
		}else if (dto.windSpeed > 10.8) {
			mHolder.ivWindLevel.setImageResource(R.drawable.iv_force_wind);
		}
		
		return convertView;
	}

}
