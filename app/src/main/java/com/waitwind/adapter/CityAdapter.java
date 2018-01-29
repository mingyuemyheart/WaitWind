package com.waitwind.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waitwind.R;
import com.waitwind.common.CONST;
import com.waitwind.dto.CityDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 城市
 */

public class CityAdapter extends BaseAdapter implements OnClickListener{
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<CityDto> mArrayList = new ArrayList<>();
	private int index = -1;
	
	private final class ViewHolder{
		ImageView ivWindLevel;
		TextView tvCityName;
		TextView tvProvinceName;
		ImageView ivDelete;
	}
	
	private ViewHolder mHolder = null;
	
	public CityAdapter(Context context, List<CityDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.adapter_city, null);
			mHolder = new ViewHolder();
			mHolder.ivWindLevel = (ImageView) convertView.findViewById(R.id.ivWindLevel);
			mHolder.tvCityName = (TextView) convertView.findViewById(R.id.tvCityName);
			mHolder.tvProvinceName = (TextView) convertView.findViewById(R.id.tvProvinceName);
			mHolder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
			mHolder.ivDelete.setOnClickListener(this);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		mHolder.ivDelete.setTag(position);
		
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
	
	/**
	 * 删除对话跨
	 */
	private void dialDialog() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.delete_dialog, null);
		TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
		LinearLayout llNegative = (LinearLayout) view.findViewById(R.id.llNegative);
		LinearLayout llPositive = (LinearLayout) view.findViewById(R.id.llPositive);
		TextView tvPositive = (TextView) view.findViewById(R.id.tvPositive);
		
		final Dialog dialog = new Dialog(mContext, R.style.CustomProgressDialog);
		dialog.setContentView(view);
		dialog.show();
		
		tvMessage.setText(mContext.getString(R.string.confirm_delete));
		tvPositive.setText(mContext.getString(R.string.sure));
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
				Intent intent = new Intent();
				intent.setAction(CONST.BROAD_SEARCH);
				intent.putExtra(CONST.SEARCH_INDEX, index);
				mContext.sendBroadcast(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		index = (Integer) v.getTag();
		switch (v.getId()) {
		case R.id.ivDelete:
			dialDialog();
			break;

		default:
			break;
		}
	}

}
