package com.wsclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TestData 
{
	private String endPoint;
	private String resource;
	private String unitValue;
	private String cityValue;
	private String countryValue;
	private int cntValue;
	private String dayValue;
	private String appidValue;
	
	private String unit;
	private String city;
	private String cnt;
	private String day;
	private String appid;
	
	private List<String> responseTagList;
	
	public void setParameterValues()
	{
		endPoint = "http://api.openweathermap.org";
		resource = "/data/2.5/forecast/daily";
		unitValue = "metric";
		//cityValue = "";
		countryValue = "AU";
		cntValue = 16;
		//dayValue = "Thursday";
		appidValue = "96acdceb02b637c8b1d72f616fcbc867";
	}
	
	public void setCity(String cityVal){
		cityValue = cityVal;
	}
	
	public void setDay(String dayVal){
		dayValue = dayVal;
	}
	
	public void setParameterNames()
	{		
		unit = "units";
		city = "q";
		cnt = "CNT";
		day = "day";
		appid = "appid";
	}
	
	public void setResponseTagList()
	{
		responseTagList = new ArrayList<>();
		//responseTagList.add("name");
		responseTagList.add("min");
		//responseTagList.add("country");
		responseTagList.add("cnt");
		responseTagList.add("max");
		responseTagList.add("dt");
		
	}
	
	public String getEndPoint()
	{
		return endPoint + resource;
	}
	
	public String getCity()
	{
		return cityValue;
	}
	
	public String getDayVal()
	{
		return dayValue;
	}
	
	public String getDestination()
	{
		return city + "=" + cityValue + "," + countryValue;
	}

	public String getAppid() 
	{
		return appid + "=" + appidValue;
	}
	
	public String getDay() 
	{
		return day + "=" + dayValue;
	}
	
	public String getUnit() 
	{
		return unit + "=" + unitValue;
	}
	
	public String getCnt() 
	{
		return cnt + "=" + cntValue;
	}
	
	public URL getURL(TestData testData) throws MalformedURLException 
	{
		URL url = new URL(testData.getEndPoint() + "?" + testData.getDestination() + "&" + testData.getAppid() + "&" + testData.getCnt() + "&" + testData.getUnit() + "");
		
		return url;
	}

	public List<String> getResponseTagMap() 
	{
		return responseTagList;
	}
}
