package com.waitwind.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.waitwind.R;
import com.waitwind.fragment.PolluteFragment;
import com.waitwind.fragment.AqiFragment;
import com.waitwind.fragment.SpeedForecastFragment;
import com.waitwind.utils.CommonUtil;
import com.waitwind.view.MainViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 预报
 */

public class WeatherActivity extends BaseActivity implements OnClickListener{
	
	private Context mContext = null;
	private ImageView ivBack = null;
	private TextView tvTitle = null;
	private MainViewPager viewPager = null;
	private List<Fragment> fragments = new ArrayList<>();
	private LinearLayout llContainer = null;//装载圆点容器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		mContext = this;
		initWidget();
		initViewPager();
	}
	
	private void initWidget() {
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ivBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.speed_forecast));
		llContainer = (LinearLayout) findViewById(R.id.llContainer);
	}
	
	/**
	 * 初始化viewPager
	 */
	private void initViewPager() {
		Fragment fragment = new SpeedForecastFragment();
		fragments.add(fragment);
		
		fragment = new PolluteFragment();
		fragments.add(fragment);
		
		fragment = new AqiFragment();
		fragments.add(fragment);
		
		viewPager = (MainViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new MyPagerAdapter());
		viewPager.setSlipping(true);//设置ViewPager是否可以滑动
		viewPager.setOffscreenPageLimit(fragments.size());
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		for (int i = 0; i < fragments.size(); i++) {
			ImageView imageView = new ImageView(mContext);
			if (i == 0) {
				imageView.setImageResource(R.drawable.point_blue);
			}else {
				imageView.setImageResource(R.drawable.point_gray);
			}
			LayoutParams params = new LinearLayout.LayoutParams((int)(CommonUtil.dip2px(mContext, 8)), (int)(CommonUtil.dip2px(mContext, 8)));
			params.setMargins((int)(CommonUtil.dip2px(mContext, 5)), 0, (int)(CommonUtil.dip2px(mContext, 5)), 0);
			imageView.setLayoutParams(params);
			llContainer.addView(imageView);
		}
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < llContainer.getChildCount(); i++) {
				ImageView image = (ImageView) llContainer.getChildAt(i);
				if (i == arg0) {
					image.setImageResource(R.drawable.point_blue);
				}else {
					image.setImageResource(R.drawable.point_gray);
				}
			}
			switch (arg0) {
			case 0:
				tvTitle.setText(getString(R.string.speed_forecast));
				break;
			case 1:
				tvTitle.setText(getString(R.string.pollute_condition));
				break;
			case 2:
				tvTitle.setText(getString(R.string.weather_quality));
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * @ClassName: MyOnClickListener
	 * @Description: TODO头标点击监听
	 * @author Panyy
	 * @date 2013 2013年11月6日 下午2:46:08
	 *
	 */
	private class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	};
	
	/**
	 * @ClassName: MyPagerAdapter
	 * @Description: TODO填充ViewPager的数据适配器
	 * @author Panyy
	 * @date 2013 2013年11月6日 下午2:37:47
	 *
	 */
	private class MyPagerAdapter extends PagerAdapter {
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(fragments.get(position).getView());
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = fragments.get(position);
			if (!fragment.isAdded()) { // 如果fragment还没有added
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.add(fragment, fragment.getClass().getSimpleName());
				ft.commit();
				/**
				 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
				 * 会在进程的主线程中,用异步的方式来执行。
				 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
				 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
				 */
				getFragmentManager().executePendingTransactions();
			}

			if (fragment.getView().getParent() == null) {
				container.addView(fragment.getView()); // 为viewpager增加布局
			}
			return fragment.getView();
		}
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
	};
}
