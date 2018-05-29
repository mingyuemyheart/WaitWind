package com.waitwind.view;

/**
 * 绘制平滑曲线
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.waitwind.R;
import com.waitwind.dto.WeatherData;
import com.waitwind.utils.CommonUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WindForeView extends View{
	
	private Context mContext;
	private SimpleDateFormat sdf0 = new SimpleDateFormat("yyyyMMddHH");
	private SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
	private List<WeatherData> tempList = new ArrayList<>();
	private float maxTemp = 0;//最高温度
	private float minTemp = 0;//最低温度
	private Paint lineP = null;//画线画笔
	private Paint textP = null;//写字画笔

	private float clickX = 0;
	private float clickY = 0;
	private String speed = null;//选择点的风速
	private Bitmap leftBitmap = null;
	private Bitmap middleBitmap = null;
	private Bitmap rightBitmap = null;
	private boolean isFirst = true;//判断是否为第一次绘制

	public WindForeView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public WindForeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public WindForeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}
	
	private void init() {
		lineP = new Paint();
		lineP.setStyle(Paint.Style.STROKE);
		lineP.setStrokeCap(Paint.Cap.ROUND);
		lineP.setAntiAlias(true);
		
		textP = new Paint();
		textP.setAntiAlias(true);
		
		Bitmap lBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left_speed);
		leftBitmap = ThumbnailUtils.extractThumbnail(lBitmap, (int)(CommonUtil.dip2px(mContext, 60)), (int)(CommonUtil.dip2px(mContext, 40)));
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.middle_speed);
		middleBitmap = ThumbnailUtils.extractThumbnail(mBitmap, (int)(CommonUtil.dip2px(mContext, 60)), (int)(CommonUtil.dip2px(mContext, 40)));
		Bitmap rBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.right_speed);
		rightBitmap = ThumbnailUtils.extractThumbnail(rBitmap, (int)(CommonUtil.dip2px(mContext, 60)), (int)(CommonUtil.dip2px(mContext, 40)));
	}
	
	/**
	 * 对cubicView进行赋值
	 */
	public void setData(List<WeatherData> dataList) {
		if (!dataList.isEmpty()) {
			tempList.addAll(dataList);
			
			maxTemp = Float.valueOf(tempList.get(0).speed);
			minTemp = Float.valueOf(tempList.get(0).speed);
			for (int i = 0; i < tempList.size(); i++) {
				if (maxTemp <= Float.valueOf(tempList.get(i).speed)) {
					maxTemp = Float.valueOf(tempList.get(i).speed);
				}
				if (minTemp >= Float.valueOf(tempList.get(i).speed)) {
					minTemp = Float.valueOf(tempList.get(i).speed);
				}
			}
			
			maxTemp = 35;
			minTemp = 0;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (tempList.isEmpty()) {
			return;
		}
		
		canvas.drawColor(Color.TRANSPARENT);
		float w = canvas.getWidth();
		float h = canvas.getHeight();
		float chartW = w-CommonUtil.dip2px(mContext, 50);
		float chartH = h-CommonUtil.dip2px(mContext, 100);
		float leftMargin = CommonUtil.dip2px(mContext, 30);
		float rightMargin = CommonUtil.dip2px(mContext, 20);
		float topMargin = CommonUtil.dip2px(mContext, 40);
		float bottomMargin = CommonUtil.dip2px(mContext, 60);
		float chartMaxH = chartH * maxTemp / (Math.abs(maxTemp)+Math.abs(minTemp));//同时存在正负值时，正值高度
		float chartMinH = chartH * minTemp / (Math.abs(maxTemp)+Math.abs(minTemp));//同时存在正负值时，负值高度
		
		int size = tempList.size();
		
		//获取曲线上每个温度点的坐标
		for (int i = 0; i < size; i++) {
			WeatherData dto = tempList.get(i);
			dto.x = (chartW/(size-1))*i + leftMargin;
			
			float temp = Float.valueOf(tempList.get(i).speed);
			if (temp >= 0) {
				dto.y = chartMaxH - chartH*Math.abs(temp)/(Math.abs(maxTemp)+Math.abs(minTemp)) + topMargin;
				if (minTemp >= 0) {
					dto.y = chartH - chartH*Math.abs(temp)/(Math.abs(maxTemp)+Math.abs(minTemp)) + topMargin;
				}
			}else {
				dto.y = chartMaxH + chartH*Math.abs(temp)/(Math.abs(maxTemp)+Math.abs(minTemp)) + topMargin;
				if (maxTemp < 0) {
					dto.y = chartH*Math.abs(temp)/(Math.abs(maxTemp)+Math.abs(minTemp)) + topMargin;
				}
			}
			tempList.set(i, dto);
		}
		
		//绘制刻度线，每间隔为20
		float totalDivider = Math.abs(maxTemp)+Math.abs(minTemp);
		int itemDivider = 5;
		for (int i = 0; i <= totalDivider; i+=itemDivider) {
			float dividerY = 0;
			int value = i;
			if (value >= 0) {
				dividerY = chartMaxH - chartH*Math.abs(value)/(Math.abs(maxTemp)+Math.abs(minTemp)) + topMargin;
				if (minTemp >= 0) {
					dividerY = chartH - chartH*Math.abs(value)/(Math.abs(maxTemp)+Math.abs(minTemp)) + topMargin;
				}
			}else {
				dividerY = chartMaxH + chartH*Math.abs(value)/(Math.abs(maxTemp)+Math.abs(minTemp)) + topMargin;
				if (maxTemp < 0) {
					dividerY = chartH*Math.abs(value)/(Math.abs(maxTemp)+Math.abs(minTemp)) + topMargin;
				}
			}
			
			textP.setColor(Color.GRAY);
			textP.setTextSize(CommonUtil.dip2px(mContext, 12));
			canvas.drawText(String.valueOf(i), CommonUtil.dip2px(mContext, 5), dividerY-CommonUtil.dip2px(mContext, 5), textP);
			
			if (i == maxTemp) {
				canvas.drawText("("+getResources().getString(R.string.wind_speed)+getResources().getString(R.string.unit_speed)+")", CommonUtil.dip2px(mContext, 25), dividerY-CommonUtil.dip2px(mContext, 5), textP);
			}
			
			Paint dashP = new Paint();
			dashP.setStyle(Paint.Style.STROKE);
			dashP.setAntiAlias(true);
			dashP.setColor(0xffdb7497);
			dashP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
			Path dashPath = new Path();
			dashPath.moveTo(CommonUtil.dip2px(mContext, 5), dividerY);
			dashPath.lineTo(w-CommonUtil.dip2px(mContext, 5), dividerY);
			PathEffect pe = new DashPathEffect(new float[]{10,10,10,10}, 1);
			dashP.setPathEffect(pe);
			if (i == 5) {
				textP.setColor(0xffdb7497);
				canvas.drawText(getResources().getString(R.string.micor_wind), chartW+CommonUtil.dip2px(mContext, 20), dividerY+CommonUtil.dip2px(mContext, 15), textP);
				canvas.drawPath(dashPath, dashP);
			}else if (i == 10) {
				textP.setColor(0xffdb7497);
				canvas.drawText(getResources().getString(R.string.big_wind), chartW+CommonUtil.dip2px(mContext, 20), dividerY+CommonUtil.dip2px(mContext, 15), textP);
				canvas.drawPath(dashPath, dashP);
			}else if (i == 15) {
				textP.setColor(0xffdb7497);
				canvas.drawText(getResources().getString(R.string.force_wind), chartW+CommonUtil.dip2px(mContext, 20), dividerY+CommonUtil.dip2px(mContext, 15), textP);
				canvas.drawPath(dashPath, dashP);
			}else {
				lineP.setColor(0xfff2f2f2);
				lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 1));
				canvas.drawLine(CommonUtil.dip2px(mContext, 5), dividerY, w-CommonUtil.dip2px(mContext, 5), dividerY, lineP);
			}
		}
		
		//绘制贝塞尔曲线
		for (int i = 0; i < tempList.size()-1; i++) {
			float x1 = tempList.get(i).x;
			float y1 = tempList.get(i).y;
			float x2 = tempList.get(i+1).x;
			float y2 = tempList.get(i+1).y;
			
			float wt = (x1 + x2) / 2;
			
			float x3 = wt;
			float y3 = y1;
			float x4 = wt;
			float y4 = y2;
			
			lineP.setColor(0x302bade9);
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 5));
			Path pathLow = new Path();
			pathLow.moveTo(x1, y1);
			pathLow.cubicTo(x3, y3, x4, y4, x2, y2);
			canvas.drawPath(pathLow, lineP);
		}
		
		for (int i = 0; i < tempList.size(); i++) {
			WeatherData dto = tempList.get(i);
			
			//绘制曲线上每个时间点marker
			lineP.setColor(getResources().getColor(R.color.black));
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 5));
			canvas.drawPoint(dto.x, dto.y, lineP);
			
//			//绘制曲线上每个时间点的温度值
//			textP.setColor(getResources().getColor(R.color.title_bg));
//			textP.setTextSize(CommonUtil.dip2px(mContext, 12));
//			BigDecimal bd = new BigDecimal(Float.valueOf(tempList.get(i).speed));
//			float value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
//			float tempWidth = textP.measureText(String.valueOf(value));
//			canvas.drawText(String.valueOf(value), dto.x-tempWidth/2, dto.y-CommonUtil.dip2px(mContext, 5f), textP);
			
			//绘制每个时间点上的时间值
			textP.setColor(getResources().getColor(R.color.gray));
			textP.setTextSize(CommonUtil.dip2px(mContext, 12));
			try {
				String hourlyTime = sdf1.format(sdf0.parse(tempList.get(i).date));
				float hourWidth = textP.measureText(hourlyTime+getResources().getString(R.string.hour));
				canvas.drawText(hourlyTime+getResources().getString(R.string.hour), dto.x-hourWidth/2, h-bottomMargin+CommonUtil.dip2px(mContext, 15), textP);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		
		//绘制时间点一行的直线
		lineP.setColor(getResources().getColor(R.color.title_bg));
		lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 3));
		canvas.drawLine(CommonUtil.dip2px(mContext, 5), h-bottomMargin, w-CommonUtil.dip2px(mContext, 5), h-bottomMargin, lineP);
		
		if (isFirst) {
			clickX = tempList.get(0).x;
			clickY = tempList.get(0).y;
			speed = tempList.get(0).speed;
			isFirst = false;
		}
		if (clickX != 0 && clickY != 0) {
			lineP.setColor(getResources().getColor(R.color.title_bg));
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 17));
			canvas.drawPoint(clickX, clickY, lineP);
			
			lineP.setColor(getResources().getColor(R.color.white));
			lineP.setStrokeWidth(CommonUtil.dip2px(mContext, 12));
			canvas.drawPoint(clickX, clickY, lineP);
			
			textP.setColor(getResources().getColor(R.color.white));
			textP.setTextSize(CommonUtil.dip2px(mContext, 12));
			BigDecimal bd = new BigDecimal(Float.valueOf(speed));
			float value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			float tempWidth = textP.measureText(String.valueOf(value)+getResources().getString(R.string.unit_speed));
			
			if (clickX == tempList.get(0).x && clickY == tempList.get(0).y) {
				canvas.drawBitmap(leftBitmap, clickX-CommonUtil.dip2px(mContext, 10), clickY-leftBitmap.getHeight()-CommonUtil.dip2px(mContext, 10), textP);
				canvas.drawText(String.valueOf(value)+getResources().getString(R.string.unit_speed), clickX, clickY-CommonUtil.dip2px(mContext, 30), textP);
			}else if (clickX == tempList.get(size-1).x && clickY == tempList.get(size-1).y) {
				canvas.drawBitmap(rightBitmap, clickX-CommonUtil.dip2px(mContext, 50), clickY-rightBitmap.getHeight()-CommonUtil.dip2px(mContext, 10), textP);
				canvas.drawText(String.valueOf(value)+getResources().getString(R.string.unit_speed), clickX-tempWidth, clickY-CommonUtil.dip2px(mContext, 30), textP);
			}else {
				canvas.drawBitmap(middleBitmap, clickX-middleBitmap.getWidth()/2, clickY-middleBitmap.getHeight()-CommonUtil.dip2px(mContext, 10), textP);
				canvas.drawText(String.valueOf(value)+getResources().getString(R.string.unit_speed), clickX-tempWidth/2, clickY-CommonUtil.dip2px(mContext, 30), textP);
			}
			
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float x = event.getX();
			float y = event.getY();
			for (int i = 0; i < tempList.size(); i++) {
				WeatherData dto = tempList.get(i);
				if (x > dto.x-50 && x < dto.x+50 && y > dto.y-50 && y < dto.y+50) {
					clickX = dto.x;
					clickY = dto.y;
					speed = dto.speed;
					postInvalidate();
					break;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

}
