package br.com.agi.qa.hooks;

import br.com.agi.qa.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hooks do Cucumber para gerenciar o ciclo de vida do WebDriver
 * e capturar evidências em caso de falha.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>{@code @Before} → inicializa o driver antes de cada cenário</li>
 *   <li>{@code @After}  → encerra o driver e captura screenshot em falhas</li>
 * </ul>
 */
public class Hooks {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    /**
     * Executado antes de cada cenário.
     * Inicializa o WebDriver com o browser configurado.
     *
     * @param scenario cenário Cucumber atual (para logging)
     */
    @Before
    public void beforeScenario(final Scenario scenario) {
        log.info("▶ Iniciando cenário: [{}]", scenario.getName());
        DriverManager.initDriver();
    }

    /**
     * Executado após cada cenário.
     * Captura screenshot se o cenário falhou, depois encerra o driver.
     *
     * @param scenario cenário Cucumber atual
     */
    @After
    public void afterScenario(final Scenario scenario) {
        if (scenario.isFailed()) {
            captureScreenshot(scenario);
        }

        log.info("■ Encerrando cenário: [{}] | Status: {}", scenario.getName(), scenario.getStatus());
        DriverManager.quitDriver();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void captureScreenshot(final Scenario scenario) {
        try {
            WebDriver driver = DriverManager.getDriver();
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot - Falha no cenário");
            log.warn("📸 Screenshot capturado para o cenário falho: [{}]", scenario.getName());
        } catch (Exception e) {
            log.error("Não foi possível capturar screenshot: {}", e.getMessage());
        }
    }
}
