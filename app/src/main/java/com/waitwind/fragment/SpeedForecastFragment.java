package com.waitwind.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.waitwind.R;
import com.waitwind.common.CONST;
import com.waitwind.utils.CommonUtil;
import com.waitwind.view.WindForeView;

/**
 * 风速预报
 * @author shawn_sun
 *
 */

public class SpeedForecastFragment extends Fragment{
	
	private TextView tvDis2 = null;
	private TextView tvDes = null;
	private TextView tvPosition = null;
	private LinearLayout llContainer1 = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wind_forecast, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}
	
	private void initWidget(View view) {
		tvDis2 = (TextView) view.findViewById(R.id.tvDis2);
		tvDes = (TextView) view.findViewById(R.id.tvDes);
		tvPosition = (TextView) view.findViewById(R.id.tvPosition);
		llContainer1 = (LinearLayout) view.findViewById(R.id.llContainer1);
		
		if (CONST.data.dis2 != null) {
			tvDis2.setText(CONST.data.dis2);
		}
		if (CONST.data.dis1 != null && CONST.data.dis3 != null) {
			tvDes.setText(CONST.data.dis1+":"+CONST.data.dis3);
		}
		tvPosition.setText(CONST.data.position);
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		WindForeView cubicView = new WindForeView(getActivity());
		cubicView.setData(CONST.data.windList);
		llContainer1.addView(cubicView, new LinearLayout.LayoutParams(dm.widthPixels, LayoutParams.WRAP_CONTENT));
	}
}
