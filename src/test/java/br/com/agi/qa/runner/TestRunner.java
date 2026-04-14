package br.com.agi.qa.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Runner principal do Cucumber.
 *
 * <p>Para rodar por tag:
 * <pre>
 *   mvn test -Dcucumber.filter.tags="@smoke"          # apenas smoke tests
 *   mvn test -Dcucumber.filter.tags="@search"         # apenas testes de busca
 *   mvn test -Dcucumber.filter.tags="@home"           # apenas testes da home
 *   mvn test -Dcucumber.filter.tags="not @bug"        # exclui cenários de bug
 *   mvn test -Dcucumber.filter.tags="@smoke and not @bug"
 * </pre>
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "br.com.agi.qa.hooks",
                "br.com.agi.qa.steps"
        },
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "junit:target/cucumber-reports/cucumber.xml"
        },
        monochrome = true,
        publish = false
)
public class TestRunner {
    // Classe de runner — não precisa de implementação
}
