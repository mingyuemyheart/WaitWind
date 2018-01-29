package com.waitwind.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.waitwind.R;
import com.waitwind.common.CONST;
import com.waitwind.utils.CommonUtil;
import com.waitwind.utils.WeatherUtil;

/**
 * 条件污染
 */

public class PolluteFragment extends Fragment implements OnClickListener{
	
	private LinearLayout llToday = null, llTommorow, llAfter = null;
	private TextView tvToday = null, tvTommorow = null, tvAfter = null;
	private TextView tvDate1 = null, tvDate2 = null, tvDate3 = null;
	private ImageView ivAqi = null;
	private TextView tvAqiText = null;
	private TextView tvAqi = null;
	private int polution = 0;
	private ImageView ivScrollBar = null;
	private int width = 0;//屏幕宽
	private int scrollX = 0;
	private int barWidth = 0;//游标图片的宽度
	private int leftMargin = 0;//距离左边距
	private int rightMargin = 0;//距离又边距
	private int totalX = 0;//总宽度
	private int itemX = 0;//每一个item宽度
	private RelativeLayout reMain = null;
	private TextView tvNoData = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pollute, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}
	
	private void initWidget(View view) {
		llToday = (LinearLayout) view.findViewById(R.id.llToday);
		llToday.setOnClickListener(this);
		llTommorow = (LinearLayout) view.findViewById(R.id.llTommorow);
		llTommorow.setOnClickListener(this);
		llAfter = (LinearLayout) view.findViewById(R.id.llAfter);
		llAfter.setOnClickListener(this);
		tvToday = (TextView) view.findViewById(R.id.tvToday);
		tvTommorow = (TextView) view.findViewById(R.id.tvTommorow);
		tvAfter = (TextView) view.findViewById(R.id.tvAfter);
		tvDate1 = (TextView) view.findViewById(R.id.tvDate1);
		tvDate2 = (TextView) view.findViewById(R.id.tvDate2);
		tvDate3 = (TextView) view.findViewById(R.id.tvDate3);
		ivAqi = (ImageView) view.findViewById(R.id.ivAqi);
		tvAqiText = (TextView) view.findViewById(R.id.tvAqiText);
		ivScrollBar = (ImageView) view.findViewById(R.id.ivScrollBar);
		tvAqi = (TextView) view.findViewById(R.id.tvAqi);
		reMain = (RelativeLayout) view.findViewById(R.id.reMain);
		tvNoData = (TextView) view.findViewById(R.id.tvNoData);
		
		tvDate1.setText(CommonUtil.getDate(0));
		tvDate2.setText(CommonUtil.getDate(1));
		tvDate3.setText(CommonUtil.getDate(2));
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.iv_scroll_bar);
		Bitmap bitmap = ThumbnailUtils.extractThumbnail(b, (int)(CommonUtil.dip2px(getActivity(), 20)), (int)(CommonUtil.dip2px(getActivity(), 20)));
		barWidth = bitmap.getWidth();
		leftMargin = (int)(CommonUtil.dip2px(getActivity(), 20));
		rightMargin = (int)(CommonUtil.dip2px(getActivity(), 20));
		totalX = width-leftMargin-rightMargin;
		itemX = totalX/6;
		
		polution = CONST.data.today;
		if (polution != -1) {
			reMain.setVisibility(View.VISIBLE);
			tvNoData.setVisibility(View.GONE);
			setValue();
		}else {
			reMain.setVisibility(View.GONE);
			tvNoData.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 设置示意图片和温馨提示语
	 */
	private void setValue() {
		ivAqi.setImageResource(WeatherUtil.getPollutionSignal(getActivity(), polution));
		tvAqiText.setText(WeatherUtil.getPollutionText(getActivity(), polution));
		tvAqi.setText(WeatherUtil.getPollution(getActivity(), polution));
		
		if (polution == 0) {
			setScrollBarPosition(scrollX, leftMargin+itemX*0+itemX/2-barWidth/2);
			scrollX = leftMargin+itemX*0+itemX/2-barWidth/2;
		} else if (polution == 1)  {
			setScrollBarPosition(scrollX, leftMargin+itemX*1+itemX/2-barWidth/2);
			scrollX = leftMargin+itemX*1+itemX/2-barWidth/2;
		} else if (polution == 2)  {
			setScrollBarPosition(scrollX, leftMargin+itemX*2+itemX/2-barWidth/2);
			scrollX = leftMargin+itemX*2+itemX/2-barWidth/2;
		} else if (polution == 3)  {
			setScrollBarPosition(scrollX, leftMargin+itemX*3+itemX/2-barWidth/2);
			scrollX = leftMargin+itemX*3+itemX/2-barWidth/2;
		} else if (polution == 4)  {
			setScrollBarPosition(scrollX, leftMargin+itemX*4+itemX/2-barWidth/2);
			scrollX = leftMargin+itemX*4+itemX/2-barWidth/2;
		} else if (polution == 5)  {
			setScrollBarPosition(scrollX, leftMargin+itemX*5+itemX/2-barWidth/2);
			scrollX = leftMargin+itemX*5+itemX/2-barWidth/2;
		}
	}
	
	/**
	 * 设置游标位置动画
	 */
	private void setScrollBarPosition(float fromXDelta, float toXDelta) {
		TranslateAnimation anim = new TranslateAnimation(fromXDelta,toXDelta,0,0);
		anim.setDuration(500);
		anim.setFillAfter(true);
		ivScrollBar.setAnimation(anim);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llToday:
			llToday.setBackgroundResource(R.drawable.corner_left_title_bg);
			llTommorow.setBackgroundColor(getResources().getColor(R.color.white));
			llAfter.setBackgroundResource(R.drawable.corner_right_white);
			tvToday.setTextColor(getResources().getColor(R.color.white));
			tvTommorow.setTextColor(getResources().getColor(R.color.black));
			tvAfter.setTextColor(getResources().getColor(R.color.black));
			tvDate1.setTextColor(getResources().getColor(R.color.white));
			tvDate2.setTextColor(getResources().getColor(R.color.black));
			tvDate3.setTextColor(getResources().getColor(R.color.black));
			
			polution = CONST.data.today;
			setValue();
			break;
		case R.id.llTommorow:
			llToday.setBackgroundResource(R.drawable.corner_left_white);
			llTommorow.setBackgroundColor(getResources().getColor(R.color.title_bg));
			
			llAfter.setBackgroundResource(R.drawable.corner_right_white);
			tvToday.setTextColor(getResources().getColor(R.color.black));
			tvTommorow.setTextColor(getResources().getColor(R.color.white));
			tvAfter.setTextColor(getResources().getColor(R.color.black));
			tvDate1.setTextColor(getResources().getColor(R.color.black));
			tvDate2.setTextColor(getResources().getColor(R.color.white));
			tvDate3.setTextColor(getResources().getColor(R.color.black));
			
			polution = CONST.data.tommorow;
			setValue();
			break;
		case R.id.llAfter:
			llToday.setBackgroundResource(R.drawable.corner_left_white);
			llTommorow.setBackgroundColor(getResources().getColor(R.color.white));
			llAfter.setBackgroundResource(R.drawable.corner_right_title_bg);
			tvToday.setTextColor(getResources().getColor(R.color.black));
			tvTommorow.setTextColor(getResources().getColor(R.color.black));
			tvAfter.setTextColor(getResources().getColor(R.color.white));
			tvDate1.setTextColor(getResources().getColor(R.color.black));
			tvDate2.setTextColor(getResources().getColor(R.color.black));
			tvDate3.setTextColor(getResources().getColor(R.color.white));
			
			polution = CONST.data.afterTomm;
			setValue();
			break;

		default:
			break;
		}
	}
}
