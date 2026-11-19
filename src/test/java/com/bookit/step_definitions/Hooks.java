package com.bookit.step_definitions;

import com.bookit.utilities.DB_Util;
import com.bookit.utilities.Driver;
import com.bookit.utilities.Environment;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.util.concurrent.TimeUnit;


public class Hooks {
	public Logger LOG= LogManager.getLogger();

	@Before("@db")
	public void dbHook() {
		DB_Util.createConnection();
		LOG.info("DB Connection is created");
	}

	@After("@db")
	public void afterDbHook() {
		DB_Util.destroy();
		LOG.info("DB Connection is destroyed");
	}
	
	@Before("@ui")
	public void setUp() {
		Driver.get().get(Environment.URL);
		Driver.get().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		LOG.info("UI Page is opened");

	}
	
	@After("@ui")
	public void tearDown(Scenario scenario) {
		// only takes a screenshot if the scenario fails
		if (scenario.isFailed()) {
			// taking a screenshot
			final byte[] screenshot = ((TakesScreenshot) Driver.get()).getScreenshotAs(OutputType.BYTES);
			scenario.attach(screenshot, "image/png","screenshot");
		}
		Driver.closeDriver();
		LOG.info("UI Page is closed");

	}
	
	
	
	
	
}
