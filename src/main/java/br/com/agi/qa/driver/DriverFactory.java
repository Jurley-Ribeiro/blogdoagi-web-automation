package br.com.agi.qa.driver;

import br.com.agi.qa.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Fábrica responsável por criar instâncias de {@link WebDriver}.
 *
 * <p>Utiliza o padrão <b>Factory Method</b> para isolar a lógica de
 * criação de cada browser. O {@link WebDriverManager} cuida do download
 * e configuração automática dos binários dos drivers.
 *
 * <p>Responsabilidades desta classe (SRP):
 * <ul>
 *   <li>Instanciar o driver correto para cada {@link BrowserType}</li>
 *   <li>Aplicar as opções de configuração (headless, window size, etc.)</li>
 * </ul>
 */
public final class DriverFactory {

    private DriverFactory() {
        // Classe utilitária - não deve ser instanciada
    }

    /**
     * Cria e retorna uma instância de {@link WebDriver} para o browser informado.
     *
     * @param browserType tipo do browser desejado
     * @return instância configurada do WebDriver
     */
    public static WebDriver createDriver(final BrowserType browserType) {
        return switch (browserType) {
            case CHROME  -> createChromeDriver();
            case FIREFOX -> createFirefoxDriver();
            case EDGE    -> createEdgeDriver();
        };
    }

    // -------------------------------------------------------------------------
    // Métodos privados de criação por browser
    // -------------------------------------------------------------------------

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = buildChromeOptions();
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = buildFirefoxOptions();
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = buildEdgeOptions();
        return new EdgeDriver(options);
    }

    // -------------------------------------------------------------------------
    // Builders de opções por browser
    // -------------------------------------------------------------------------

    private static ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (isHeadless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
        return options;
    }

    private static FirefoxOptions buildFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (isHeadless()) {
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        return options;
    }

    private static EdgeOptions buildEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (isHeadless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        return options;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static boolean isHeadless() {
        return ConfigReader.getInstance().getBooleanProperty("headless");
    }
}
