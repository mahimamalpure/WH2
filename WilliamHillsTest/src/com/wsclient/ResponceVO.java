package com.wsclient;

public class ResponceVO
{
	private int responseCode = -1; 
	private String responseMessage = "Error while calling webservice!";
	
	public String getResponseMessage() 
	{
		return responseMessage;
	}
	
	public void setResponseMessage(String responseMessage) 
	{
		this.responseMessage = responseMessage;
	}
	
	public int getResponseCode()
	{
		return responseCode;
	}
	
	public void setResponseCode(int responseCode) 
	{
		this.responseCode = responseCode;
	} 
}
