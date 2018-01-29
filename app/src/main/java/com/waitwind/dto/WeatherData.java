package com.waitwind.dto;

import java.util.ArrayList;
import java.util.List;

public class WeatherData {

	//风速预报
	public String position = null;
	public double lat = 0;
	public double lng = 0;
	public String dis1 = null;
	public String dis2 = null;
	public String dis3 = null;
	public String speed = null;//风速
	public String force = null;//风力
	public String dir = null;//风向
	public String aqi = null;//空气质量
	public String date = null;//时间
	public float x = 0;//x轴坐标点
	public float y = 0;//y轴坐标点
    public List<WeatherData> windList = new ArrayList<WeatherData>();
    
    //空气污染
    public int today = -1;//今天
    public int tommorow = -1;//明天
    public int afterTomm = -1;//后天
    
    /**
     * 清空污染条件数据
     */
    public void clearPollution() {
    	today = -1;
    	tommorow = -1;
    	afterTomm = -1;
    }
    
    /**
     * 清空风速预报数据
     */
    public void clearSpeedFore() {
    	dis1 = null;
    	dis2 = null;
    	dis3 = null;
    	windList.clear();
    }
    
}
