package com.wsclient;

import java.util.Calendar;
import java.util.Date;

public class WeatherData
{
	private String date;
	private String city;
	private String country;
	private float minTemp;
	private float maxTemp;
	
	public String getDateInMillis() 
	{
		return date;
	}
	
	public String getDay() 
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.parseLong(date) * 1000);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		
		if(day == Calendar.SUNDAY)
		{
			return "Sunday";
		}
		else if(day == Calendar.MONDAY)
		{
			return "Monday";
		}
		else if(day == Calendar.TUESDAY)
		{
			return "Tuesday";
		}
		else if(day == Calendar.WEDNESDAY)
		{
			return "Wednesday";
		}
		else if(day == Calendar.THURSDAY)
		{
			return "Thursday";
		}
		else if(day == Calendar.FRIDAY)
		{
			return "Friday";
		}
		else if(day == Calendar.SATURDAY)
		{
			return "Saturday";
		}
		
		return "";
	}
	
	public String getDate() 
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.parseLong(date) * 1000);
		Date UTCDate = cal.getTime();
		return UTCDate.toString();
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public float getMinTemp() {
		return minTemp;
	}
	public void setMinTemp(String minTemp) {
		this.minTemp = Float.parseFloat(minTemp);
	}
	public float getMaxTemp() {
		return maxTemp;
	}
	public void setMaxTemp(String maxTemp) {
		this.maxTemp = Float.parseFloat(maxTemp);
	}
	
	public void setData(String tagName, String tagValue)
	{
		if(tagName.equalsIgnoreCase("name"))
		{
			setCity(tagValue);
		}
		else if(tagName.equalsIgnoreCase("country"))
		{
			setCountry(tagValue);
		}
		else if(tagName.equalsIgnoreCase("min"))
		{
			setMinTemp(tagValue);
		}
		else if(tagName.equalsIgnoreCase("max"))
		{
			setMaxTemp(tagValue);
		}
		else if(tagName.equalsIgnoreCase("dt"))
		{
			setDate(tagValue);
		}
	}
}
