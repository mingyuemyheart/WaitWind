package com.waitwind.manager;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

public class RankManager {

	private final static String APPID = "f63d329270a44900";//机密需要用到的AppId
	private final static String CHINAWEATHER_DATA = "sanx_data_99";//加密秘钥名称
	private final static String URL = "http://scapi.weather.com.cn/weather/getRank";//请求距离下雨时间的URL
	
	public static String getDate(Calendar calendar, String format) {
		String date = null;
		SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
		dateFormat.applyPattern(format);
		date = dateFormat.format(calendar.getTime());
		return date;
	}
	
	/**
	 * 加密请求字符串
	 * @param url 基本串
	 * @param lng 经度
	 * @param lat 维度
	 * @return
	 */
	public static final String getSecretUrl(String type, String areaids) {
		String sysdate = getDate(Calendar.getInstance(), "yyyyMMddHHmm");//系统时间
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL);
		buffer.append("?");
		buffer.append("type=").append(type);
		buffer.append("&");
		buffer.append("areaids=").append(areaids);
		buffer.append("&");
		buffer.append("date=").append(sysdate);
		buffer.append("&");
		buffer.append("appid=").append(APPID);
		
		String key = getKey(CHINAWEATHER_DATA, buffer.toString());
		buffer.delete(buffer.lastIndexOf("&"), buffer.length());
		
		buffer.append("&");
		buffer.append("appid=").append(APPID.substring(0, 6));
		buffer.append("&");
		buffer.append("key=").append(key.substring(0, key.length() - 3));
		String result = buffer.toString();
		return result;
	}
	
	/**
	 * 获取秘钥
	 * @param key
	 * @param src
	 * @return
	 */
	public static final String getKey(String key, String src) {
		try{
			byte[] rawHmac = null;
			byte[] keyBytes = key.getBytes("UTF-8");
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			rawHmac = mac.doFinal(src.getBytes("UTF-8"));
			String encodeStr = Base64.encodeToString(rawHmac, Base64.DEFAULT);
			String keySrc = URLEncoder.encode(encodeStr, "UTF-8");
			return keySrc;
		}catch(Exception e){
			Log.e("SceneException", e.getMessage(), e);
		}
		return null;
	}

}
