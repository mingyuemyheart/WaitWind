package com.waitwind.common;

import com.waitwind.dto.WeatherData;
import com.waitwind.dto.WindData;

public class CONST {

	public static String APPID = "31";
	public static String UID = "4105";

	public static WindData windData = new WindData();//存放数据对象
	public static WeatherData data = new WeatherData();//存放天气数据集合
	public static String AQI_ADDR = "/aqi.png";//拍一拍保存的图片路径
	public static String SHOWGUIDE = "show_guide";
	public static String SHARED_FOCUS = "shared_focus";//关注的城市
	public static String BROAD_SEARCH = "broad_search";//刷新搜索列表广播
	public static String SEARCH_INDEX = "search_index";
}
