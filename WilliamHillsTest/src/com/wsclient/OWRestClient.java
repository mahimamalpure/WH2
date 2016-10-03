package com.wsclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OWRestClient
{
	public static void main(String[] args) 
	{
		TestData testData = new TestData();
		testData.setParameterNames();
		testData.setParameterValues();
		testData.setResponseTagList();

		OWRestClient testRestClient = new OWRestClient();
		ResponceVO responceVO = testRestClient.callWS(testData);
		Map<Integer, WeatherData> outputMap = testRestClient.processResponse(responceVO, testData.getResponseTagMap());
		testRestClient.printOutputMap(outputMap);
		testRestClient.findDesiredOutput(outputMap, "Thursday", 10.0f);
	}

	private void printOutputMap(Map<Integer, WeatherData> outputMap) 
	{
		Set<Integer> keyset = outputMap.keySet();
		Iterator<Integer> keysetItr = keyset.iterator();

		while(keysetItr.hasNext())
		{
			int instance = keysetItr.next();
			WeatherData weatherData = outputMap.get(instance);
			
			String date = weatherData.getDate();
			float minTemprature = weatherData.getMinTemp();
			float maxTemprature = weatherData.getMaxTemp();

			System.out.println("Date: " + date + " minTemprature: " + minTemprature + " maxTemprature: " + maxTemprature);
		}
	}
	
	public WeatherData findDesiredOutput(Map<Integer, WeatherData> outputMap, String day, float minTemp) 
	{
		Set<Integer> keyset = outputMap.keySet();
		Iterator<Integer> keysetItr = keyset.iterator();

		while(keysetItr.hasNext())
		{
			int instance = keysetItr.next();
			WeatherData weatherData = outputMap.get(instance);
			
			String date = weatherData.getDate();
			float minTemprature = weatherData.getMinTemp();
			float maxTemprature = weatherData.getMaxTemp();
			
			if(minTemp < minTemprature && weatherData.getDay().equalsIgnoreCase(day))
			{
				return weatherData;
			}
		}
		return null;
	}

	public ResponceVO callWS(TestData testData)
	{
		try 
		{
			URL url = testData.getURL(testData);

			//System.out.println("URL : " + url.toString());

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			ResponceVO responceVO = new ResponceVO();
			responceVO.setResponseCode(conn.getResponseCode());

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String tempResponseStr;
			String totalResponseStr = "";

			while ((tempResponseStr = br.readLine()) != null) 
			{
				totalResponseStr += tempResponseStr;
			}

			responceVO.setResponseMessage(totalResponseStr);

			conn.disconnect();

			return responceVO;

		} catch (MalformedURLException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		return new ResponceVO();
	}

	public Map<Integer, WeatherData> processResponse(ResponceVO responceVO, List<String> responseTagMap) 
	{
		if(responceVO.getResponseCode() != 200) 
		{
			return processFaults(responceVO);
		}
		else
		{
			return processValidResponse(responceVO, responseTagMap);
		}	
	}

	private Map<Integer, WeatherData> processValidResponse(ResponceVO responceVO, List<String> responseTagList) 
	{
		String responceMessage = responceVO.getResponseMessage();
		//System.out.println("Responce Message : " + responceMessage);

		Map<Integer, WeatherData> outputMap = new HashMap<>();

		String city = JSONUtil.readJSONFile(responceMessage, "name", new Instance(0));
		String country = JSONUtil.readJSONFile(responceMessage, "country", new Instance(0));

		for(int instance = 1; instance < 8; instance++)
		{
			WeatherData weatherData = new WeatherData();

			for(String tagName : responseTagList)
			{
				String tagValue = JSONUtil.readJSONFile(responceMessage, tagName, new Instance(instance));

				weatherData.setData(tagName, tagValue);
				
				weatherData.setCity(city);
				weatherData.setCountry(country);
			}

			outputMap.put(instance, weatherData);
		}

		return outputMap;
	}

	public Map<Integer, WeatherData> processFaults(ResponceVO responceVO) 
	{
		String responceMessage = responceVO.getResponseMessage();	
		System.out.println("Error while calling webservice: " + responceMessage);		

		return new HashMap<>();
	}
}