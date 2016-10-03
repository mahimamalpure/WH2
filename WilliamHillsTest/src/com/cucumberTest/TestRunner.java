package com.cucumberTest;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "Feature"
		,glue={"com.stepDefinition"}
		,dryRun = false
		,monochrome = true
		,format = {"pretty"}
		)

public class TestRunner 
{

}