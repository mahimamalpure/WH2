package com.stepDefinition;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.Map;

import com.utilities.*;
import com.wsclient.OWRestClient;
import com.wsclient.ResponceVO;
import com.wsclient.TestData;
import com.wsclient.WeatherData;

public class WilliamHillSteps 
{
	TestData testData;
	ResponceVO responceVO;
	OWRestClient testRestClient;
	Map<Integer, WeatherData> outputMap;
	
	@Before public void startOfTest()
	{
		System.out.println("*****************Execution started**************");
		testData = new TestData();
		testData.setParameterNames();
		testData.setParameterValues();
		testData.setResponseTagList();

	}
	
	@Given("^I like to holiday in \"([^\"]*)\"$")
	public void i_like_to_holiday_in(String cityName) throws Throwable 
	{
		testData.setCity(cityName);
	}

	@Given("^I only like to holiday on \"([^\"]*)\"$")
	public void i_only_like_to_holiday_on(String dayOfWeek) throws Throwable 
	{   
		testData.setDay(dayOfWeek);
	}

	@When("^I look up the weather forecast$")
	public void i_look_up_the_weather_forecast() throws Throwable 
	{
		testRestClient = new OWRestClient();
		responceVO = testRestClient.callWS(testData);

	}

	@Then("^I receive the weather forecast$")
	public void i_receive_the_weather_forecast() throws Throwable 
	{
		outputMap = testRestClient.processResponse(responceVO, testData.getResponseTagMap());
	}

	@Then("^the temperature is warmer than \"([^\"]*)\" degrees$")
	public void the_temperature_is_warmer_than_degrees(float temperature) throws Throwable 
	{
		WeatherData ofDesiredDay = testRestClient.findDesiredOutput(outputMap, testData.getDayVal(), temperature);
		Assertions.assertValidJSON(responceVO.getResponseMessage());
		Assertions.assertDayOfWeek(testData.getDayVal(), ofDesiredDay.getDay());
		Assertions.assertDestination(testData.getCity(), ofDesiredDay.getCity());
		Assertions.assertTemperature(temperature, ofDesiredDay.getMinTemp());
		System.out.println("You can go on Holiday on " +ofDesiredDay.getDate() +" to " +ofDesiredDay.getCity());;
	}

	@After public void endOfTest() {
		System.out.println("********Execution Finished**************");
	}

}
