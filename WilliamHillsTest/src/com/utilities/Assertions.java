package com.utilities;
import static org.junit.Assert.*;

import java.io.FileReader;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Assertions 
{
	public static void assertDestination(String expectedCityName , String actualCityName)
	{
		assertTrue(" Something went wrong !! Weather forcast is NOT queried for : "+"'"+actualCityName+"'" , expectedCityName.equals(actualCityName));
	
		System.out.println("City Name is : " +actualCityName);
	}
	
	public static void assertDayOfWeek(String expectedDay , String actualDay )
	{
		assertTrue(" Something went wrong !! Weather forcast is NOT queried for : "+"'"+actualDay+"'" , expectedDay.equals(actualDay));
	
		System.out.println("Day of the week is " +actualDay);
	}
	
	public static void assertTemperature(float expectedTemp , float actualTemp )
	{
		assertTrue(" Something went wrong !! Temperature is less than "+"'"+expectedTemp+"'" , (actualTemp > expectedTemp));
		System.out.println("Minimum temperature is : " + actualTemp);
	}

	public static void assertValidJSON(String responseMessage)
	{
		boolean isValidJSON = true;

		JSONParser parser = new JSONParser();
		
		try
		{
			parser.parse(responseMessage);
		
		} catch (ParseException e) 
		{
			isValidJSON = false;
		}
		
		assertTrue("Response is not a valid JSON: " , isValidJSON);
		
		System.out.println("Response is a valid JSON.");

	}

}
