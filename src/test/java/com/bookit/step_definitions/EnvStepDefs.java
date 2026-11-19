package com.bookit.step_definitions;

import com.bookit.utilities.BookitUtils;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.Given;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;


public class EnvStepDefs {

    public Logger LOG= LogManager.getLogger();

    @Given("I get related environment information")
    public void i_get_related_environment_information() {

        String selectEnv = ConfigurationReader.getProperty("environment");
        System.out.println("selectEnv = " + selectEnv);
        LOG.info("Environment is "+selectEnv);

        String browser = ConfigurationReader.getProperty("browser");
        System.out.println("browser = " + browser);
        LOG.error("Browser is "+browser);

        String path = System.getProperty("user.dir");
        System.out.println("path = " + path);

        System.out.println("Environment.URL = " + Environment.URL);
        LOG.info("Environment.URL = " + Environment.URL);

        System.out.println("Environment.BASE_URL = " + Environment.BASE_URL);
        LOG.info("Environment.BASE_URL = " + Environment.BASE_URL);

        Map<String, String> teacher = BookitUtils.returnCredentials("teacher");
        LOG.info("Role is teacher and credentials as followings "+teacher);

        // intentionally to make it fail
        // Map<String, String> student = BookitUtils.returnCredentials("student");



    }

}
