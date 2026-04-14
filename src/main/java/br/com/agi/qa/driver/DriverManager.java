package br.com.agi.qa.driver;

import br.com.agi.qa.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Gerencia o ciclo de vida do {@link WebDriver} por thread.
 *
 * <p>Utiliza {@link ThreadLocal} para garantir que cada thread de teste
 * tenha sua própria instância de WebDriver, permitindo execução paralela
 * sem conflitos de estado.
 *
 * <p>Responsabilidades desta classe (SRP):
 * <ul>
 *   <li>Inicializar o driver a partir das configurações</li>
 *   <li>Disponibilizar o driver para a thread corrente</li>
 *   <li>Encerrar e limpar o driver ao final do teste</li>
 * </ul>
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {
        // Classe utilitária - não deve ser instanciada
    }

    /**
     * Inicializa o driver para a thread atual com base nas configurações.
     * Se já existir um driver ativo, ele é encerrado antes de criar um novo.
     */
    public static void initDriver() {
        if (driverThreadLocal.get() != null) {
            quitDriver();
        }

        ConfigReader config = ConfigReader.getInstance();
        String browserName = config.getProperty("browser", "chrome");
        BrowserType browserType = BrowserType.fromString(browserName);
        WebDriver driver = DriverFactory.createDriver(browserType);

        configureTimeouts(driver, config);

        if (config.getBooleanProperty("window.maximize")) {
            driver.manage().window().maximize();
        }

        driverThreadLocal.set(driver);
    }

    /**
     * Retorna o driver da thread atual.
     *
     * @return instância do {@link WebDriver}
     * @throws IllegalStateException se o driver não tiver sido inicializado
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException(
                "WebDriver não inicializado para esta thread. Chame DriverManager.initDriver() antes."
            );
        }
        return driver;
    }

    /**
     * Cria e retorna um {@link WebDriverWait} com o timeout explícito configurado.
     *
     * @return instância de WebDriverWait
     */
    public static WebDriverWait getWait() {
        int timeoutSeconds = ConfigReader.getInstance().getIntProperty("timeout.explicit");
        return new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * Encerra o driver da thread atual e remove a referência do ThreadLocal.
     * Deve ser chamado ao final de cada cenário de teste.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void configureTimeouts(final WebDriver driver, final ConfigReader config) {
        int implicitTimeout = config.getIntProperty("timeout.implicit");
        int pageLoadTimeout = config.getIntProperty("timeout.page.load");

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitTimeout));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
    }
}
