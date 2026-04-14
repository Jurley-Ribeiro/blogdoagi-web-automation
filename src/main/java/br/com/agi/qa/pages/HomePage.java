package br.com.agi.qa.pages;

import br.com.agi.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Page Object que representa a Home do Blog do Agi.
 *
 * <p>Encapsula todos os elementos e ações disponíveis na página inicial,
 * mantendo os testes desacoplados dos seletores HTML (POM - Page Object Model).
 *
 * <p><b>Estratégia multi-locator para o ícone de busca:</b> o blog pode sofrer
 * atualizações de tema WordPress. Em vez de um único seletor frágil, tentamos
 * uma lista de candidatos em ordem de especificidade, retornando o primeiro
 * elemento visível encontrado (Composite Locator Pattern).
 */
public class HomePage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(HomePage.class);

    /**
     * Candidatos de seletor para o ícone de busca (lupa), em ordem de
     * especificidade. Inclui classes comuns dos temas Astra, GeneratePress,
     * Kadence, OceanWP e WordPress padrão.
     */
    private static final List<By> SEARCH_ICON_LOCATORS = List.of(
            By.cssSelector(".ast-search-menu-icon"),
            By.cssSelector(".ast-search-icon"),
            By.cssSelector(".ast-header-search-icon"),
            By.cssSelector("[class*='ast-search']"),
            By.cssSelector(".header-search"),
            By.cssSelector(".search-toggle"),
            By.cssSelector(".search-icon"),
            By.cssSelector(".nav-search"),
            By.cssSelector("[class*='search-icon']"),
            By.cssSelector("[class*='search-toggle']"),
            By.cssSelector("button[aria-label*='earch']"),
            By.cssSelector("a[aria-label*='earch']"),
            By.cssSelector("[title*='Buscar'], [title*='Pesquisa'], [title*='Search']"),
            By.xpath("//header//*[contains(@class,'search') and not(self::input)]"),
            By.xpath("//*[contains(@class,'search') and (self::button or self::a or self::span)]"
                    + "[not(ancestor::*[contains(@class,'content') or contains(@class,'article')])]")
    );

    // -------------------------------------------------------------------------
    // Elementos mapeados via @FindBy (elementos estáveis e previsíveis)
    // -------------------------------------------------------------------------

    @FindBy(css = "header, .site-header, #masthead, [class*='header']")
    private WebElement header;

    @FindBy(css = "nav, .main-navigation, #site-navigation, .navigation-menu, "
            + "[class*='main-nav'], [class*='primary-nav'], [role='navigation']")
    private WebElement navigationMenu;

    @FindBy(css = "main, .site-main, #main, #content, [role='main']")
    private WebElement mainContent;

    // -------------------------------------------------------------------------
    // Navegação
    // -------------------------------------------------------------------------

    /**
     * Abre a página Home do Blog do Agi e aguarda o carregamento completo.
     *
     * @return a própria página para encadeamento fluente
     */
    public HomePage open() {
        String baseUrl = ConfigReader.getInstance().getProperty("base.url");
        log.info("Navegando para: {}", baseUrl);
        driver.get(baseUrl);
        return this;
    }

    // -------------------------------------------------------------------------
    // Ações
    // -------------------------------------------------------------------------

    /**
     * Clica no ícone de busca (lupa) usando a estratégia multi-locator.
     *
     * @return a própria página para encadeamento fluente
     */
    public HomePage clickSearchIcon() {
        WebElement icon = findSearchIcon();
        waitForClickability(icon).click();
        return this;
    }

    // -------------------------------------------------------------------------
    // Verificações / Queries
    // -------------------------------------------------------------------------

    /** @return título da aba do browser */
    public String getTitle() {
        return getPageTitle();
    }

    /** @return URL atual da página */
    public String getUrl() {
        return getCurrentUrl();
    }

    /**
     * Verifica se o ícone de busca está visível usando estratégia multi-locator.
     * Tenta cada seletor candidato e retorna {@code true} se qualquer um
     * corresponder a um elemento visível.
     *
     * @return {@code true} se o ícone de busca estiver visível
     */
    public boolean isSearchIconVisible() {
        for (By locator : SEARCH_ICON_LOCATORS) {
            try {
                List<WebElement> elements = driver.findElements(locator);
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    log.info("Ícone de busca encontrado com seletor: {}", locator);
                    return true;
                }
            } catch (Exception e) {
                log.debug("Seletor {} não encontrou elemento: {}", locator, e.getMessage());
            }
        }
        log.warn("Ícone de busca não encontrado com nenhum dos {} seletores candidatos.",
                SEARCH_ICON_LOCATORS.size());
        return false;
    }

    /** @return {@code true} se o header estiver visível */
    public boolean isHeaderVisible() {
        try {
            return waitForVisibility(header).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** @return {@code true} se o menu de navegação estiver visível */
    public boolean isNavigationMenuVisible() {
        try {
            return waitForVisibility(navigationMenu).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** @return {@code true} se o conteúdo principal estiver visível */
    public boolean isMainContentVisible() {
        try {
            return waitForVisibility(mainContent).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se a URL atual pertence ao domínio base configurado.
     *
     * <p>Compara normalizando as URLs (remove trailing slash) para evitar
     * falsos negativos quando o browser adiciona ou remove a barra final.
     *
     * @return {@code true} se a URL atual iniciar com a URL base configurada
     */
    public boolean isUrlCorrect() {
        String baseUrl = normalize(ConfigReader.getInstance().getProperty("base.url"));
        String currentUrl = normalize(getCurrentUrl());
        boolean matches = currentUrl.startsWith(baseUrl);
        if (!matches) {
            log.warn("URL esperada: {} | URL atual: {}", baseUrl, currentUrl);
        }
        return matches;
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    /**
     * Percorre os locators candidatos e retorna o primeiro WebElement visível.
     *
     * @return WebElement do ícone de busca
     * @throws org.openqa.selenium.NoSuchElementException se nenhum seletor encontrar o elemento
     */
    private WebElement findSearchIcon() {
        for (By locator : SEARCH_ICON_LOCATORS) {
            try {
                List<WebElement> elements = driver.findElements(locator);
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    return elements.get(0);
                }
            } catch (Exception ignored) {
                // Tenta o próximo candidato
            }
        }
        throw new org.openqa.selenium.NoSuchElementException(
                "Ícone de busca não encontrado. Nenhum dos seletores candidatos correspondeu."
        );
    }

    /** Remove trailing slash e espaços para comparação de URL. */
    private String normalize(final String url) {
        return url.trim().replaceAll("/$", "").toLowerCase();
    }
}
