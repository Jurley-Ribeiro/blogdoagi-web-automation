package br.com.agi.qa.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;

/**
 * Runner principal do Cucumber com JUnit 5.
 *
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.com.agi.qa.hooks,br.com.agi.qa.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value =
        "pretty, " +
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm, " +
        "html:target/cucumber-reports/cucumber.html, " +
        "json:target/cucumber-reports/cucumber.json, " +
        "junit:target/cucumber-reports/cucumber.xml")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "")
public class TestRunner {

}