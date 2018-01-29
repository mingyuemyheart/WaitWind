package com.waitwind.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.waitwind.activity.MainActivity;
import com.waitwind.R;
import com.waitwind.common.CONST;
import com.waitwind.utils.CommonUtil;

/**
 * 引导页
 */

public class GuideFragment extends Fragment implements OnClickListener{
	
	private RelativeLayout reMain = null;
	private ImageView ivEnter = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_guide, null);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initWidget(view);
	}
	
	private void initWidget(View view) {
		reMain = (RelativeLayout) view.findViewById(R.id.reMain);
		ivEnter = (ImageView) view.findViewById(R.id.ivEnter);
		ivEnter.setOnClickListener(this);
		
		int index = getArguments().getInt("index");
		if (index == 0) {
			ivEnter.setVisibility(View.GONE);
			reMain.setBackgroundResource(R.drawable.guide1);
		}else if (index == 1) {
			ivEnter.setVisibility(View.GONE);
			reMain.setBackgroundResource(R.drawable.guide2);
		}else if (index == 2) {
			ivEnter.setVisibility(View.GONE);
			reMain.setBackgroundResource(R.drawable.guide3);
		}else if (index == 3) {
			ivEnter.setVisibility(View.VISIBLE);
			reMain.setBackgroundResource(R.drawable.guide4);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivEnter:
			SharedPreferences sharedPreferences = getActivity().getSharedPreferences(CONST.SHOWGUIDE, Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			editor.putString("VersionCode", CommonUtil.getVersion(getActivity()));
			editor.commit();
			
			startActivity(new Intent(getActivity(), MainActivity.class));
			getActivity().finish();
			break;

		default:
			break;
		}
	}
	
}
