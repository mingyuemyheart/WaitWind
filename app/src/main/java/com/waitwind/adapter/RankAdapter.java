package com.waitwind.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.waitwind.R;
import com.waitwind.dto.RankDto;
import com.waitwind.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.waitwind.utils.WeatherUtil;

/**
 * 排行榜
 */

public class RankAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {

	private Context mContext = null;
	private List<RankDto> mArrayList = null;
	private LayoutInflater mInflater = null;

	public RankAdapter(Context context, List<RankDto> mArrayList) {
		this.mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return mArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private HeaderViewHolder mHeaderHolder = null;
	private class HeaderViewHolder {
		TextView tvHeader;
	}

	@Override
	public long getHeaderId(int position) {
		return mArrayList.get(position).section;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mHeaderHolder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.sticky_grid_header, null);
			mHeaderHolder.tvHeader = (TextView) convertView.findViewById(R.id.tvHeader);
			convertView.setTag(mHeaderHolder);
		} else {
			mHeaderHolder = (HeaderViewHolder) convertView.getTag();
		}
		
		RankDto dto = mArrayList.get(position);
		mHeaderHolder.tvHeader.setText(dto.header);

		return convertView;
	}
	
	
	private ViewHolder mHolder = null;
	private class ViewHolder {
		TextView tvRank;
		TextView tvCity;
		TextView tvDistrict;
		TextView tvWindSpeed;
		TextView tvAqi;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.sticky_grid_item, null);
			mHolder.tvRank = (TextView) convertView.findViewById(R.id.tvRank);
			mHolder.tvCity = (TextView) convertView.findViewById(R.id.tvCity);
			mHolder.tvDistrict = (TextView) convertView.findViewById(R.id.tvDistrict);
			mHolder.tvWindSpeed = (TextView) convertView.findViewById(R.id.tvWindSpeed);
			mHolder.tvAqi = (TextView) convertView.findViewById(R.id.tvAqi);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		RankDto dto = mArrayList.get(position);
		mHolder.tvRank.setText(dto.rank+"");
		mHolder.tvCity.setText(dto.city);
		mHolder.tvDistrict.setText(dto.district);
		
		if (TextUtils.isEmpty(dto.windSpeed)) {
			mHolder.tvWindSpeed.setVisibility(View.GONE);
		}else {
			mHolder.tvWindSpeed.setVisibility(View.VISIBLE);
			mHolder.tvWindSpeed.setText(dto.windSpeed  + mContext.getString(R.string.unit_speed));
		}
		if (TextUtils.isEmpty(dto.aqi)) {
			mHolder.tvAqi.setVisibility(View.GONE);
		}else {
			mHolder.tvAqi.setVisibility(View.VISIBLE);
			mHolder.tvAqi.setText(dto.aqi);
			mHolder.tvAqi.setBackgroundResource(WeatherUtil.getAqiBg(mContext, Integer.valueOf(dto.aqi)));
		}
		
		return convertView;
	}
	
}
