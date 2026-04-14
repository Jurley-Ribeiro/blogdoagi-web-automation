package br.com.agi.qa.pages;

import br.com.agi.qa.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Classe base para todos os Page Objects.
 *
 * <p>Centraliza os utilitários de espera e interação com elementos,
 * evitando duplicação de código nos Page Objects filhos (DRY).
 *
 * <p>Todos os Page Objects devem estender esta classe.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = DriverManager.getWait();
        PageFactory.initElements(driver, this);
    }

    /**
     * Aguarda até que o elemento seja visível na tela.
     *
     * @param element elemento a aguardar
     * @return o próprio elemento após se tornar visível
     */
    protected WebElement waitForVisibility(final WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Aguarda até que o elemento identificado pelo locator seja visível.
     *
     * @param locator locator do elemento
     * @return o elemento após se tornar visível
     */
    protected WebElement waitForVisibility(final By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Aguarda até que o elemento seja clicável.
     *
     * @param element elemento a aguardar
     * @return o próprio elemento quando estiver clicável
     */
    protected WebElement waitForClickability(final WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Verifica se um elemento está visível na página.
     *
     * @param element elemento a verificar
     * @return {@code true} se o elemento estiver visível
     */
    protected boolean isElementVisible(final WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retorna o título atual da página.
     *
     * @return título da página
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Retorna a URL atual da página.
     *
     * @return URL atual
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
