package br.com.agi.qa.pages;

import br.com.agi.qa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Page Object responsável pela funcionalidade de busca do Blog do Agi.
 *
 * <p><b>Estrutura real da busca (tema Astra):</b>
 * <ul>
 *   <li>Um ícone/link no header ({@code .ast-search-menu-icon a}) abre um overlay full-screen.</li>
 *   <li>O overlay tem o ID {@code ast-seach-full-screen-form} (typo no código do tema — "seach").</li>
 *   <li>O campo de input tem {@code name="s"} e classe {@code search-field}.</li>
 *   <li>O botão de submit tem {@code id="search_submit"} e {@code aria-label="Pesquisar"}.</li>
 *   <li>O botão de fechar o overlay tem {@code id="close"}.</li>
 * </ul>
 *
 * <p><b>Bug conhecido:</b> na Home page o ícone de busca não é renderizado
 * devido a um problema de layout. Use {@link #openFromInternalPage()} para
 * navegar a uma página interna antes de interagir com a busca.
 */
public class SearchPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(SearchPage.class);

    // -------------------------------------------------------------------------
    // Locators — mantidos como constantes para facilitar manutenção
    // -------------------------------------------------------------------------

    /**
     * Candidatos para o toggle/ícone de busca no header (páginas internas).
     */
    private static final List<By> SEARCH_TOGGLE_LOCATORS = List.of(
            By.cssSelector(".ast-search-menu-icon a"),
            By.cssSelector("a.astra-search-icon"),
            By.cssSelector("[class*='ast-search'] a"),
            By.cssSelector("a[href='#'][aria-label*='esquis']"),
            By.partialLinkText("Pesquisar"),                                    // ← adicionar
            By.xpath("//a[@href='#'][not(ancestor::*[contains(@class,'ast-search-box')])]"), // ← adicionar
            By.linkText("Pesquisar")
    );

    /**
     * Overlay full-screen de busca.
     * Nota: o ID original do tema tem um typo — "seach" em vez de "search".
     */
    @FindBy(css = "#ast-seach-full-screen-form, .ast-search-box.full-screen")
    private WebElement searchOverlay;

    /**
     * Campo de texto da busca dentro do overlay.
     */
    @FindBy(css = "input.search-field, input[name='s']")
    private WebElement searchInput;

    /**
     * Botão de submit da busca.
     */
    @FindBy(css = "#search_submit, button.search-submit, button[aria-label='Pesquisar']")
    private WebElement searchSubmitButton;

    /**
     * Botão de fechar o overlay.
     */
    @FindBy(css = "#close, .ast-search-box .close")
    private WebElement closeOverlayButton;

    // Seletores para a página de resultados (WordPress + Astra)
    private static final By SEARCH_RESULTS_ARTICLES =
            By.cssSelector("article.post, article.hentry, .ast-article-post, .search .hentry");

    private static final By NO_RESULTS_CONTAINER =
            By.cssSelector(".no-results, .not-found, .search-no-results");

    private static final By ENTRY_TITLE =
            By.cssSelector(".entry-title, h2.entry-title, .ast-blog-single-element");

    // -------------------------------------------------------------------------
    // Navegação
    // -------------------------------------------------------------------------

    /**
     * Navega para a página interna configurada como entry point da busca.
     * Necessário porque a Home tem um bug de layout que oculta o ícone de busca.
     *
     * @return a própria página para encadeamento fluente
     */
    public SearchPage openFromInternalPage() {
        ConfigReader config = ConfigReader.getInstance();
        String url = config.getProperty("base.url") + config.getProperty("search.entry.page");
        log.info("Navegando para página de entrada da busca: {}", url);
        driver.get(url);
        return this;
    }

    // -------------------------------------------------------------------------
    // Ações
    // -------------------------------------------------------------------------

    /**
     * Clica no ícone de busca do header para abrir o overlay full-screen.
     * Usa estratégia multi-locator para resiliência a mudanças de tema.
     *
     * @return a própria página
     */
    public SearchPage clickSearchToggle() {
        WebElement toggle = findSearchToggle();
        log.info("Clicando no toggle de busca...");
        waitForClickability(toggle).click();
        return this;
    }

    /**
     * Digita um termo no campo de busca do overlay.
     *
     * @param term termo a ser buscado
     * @return a própria página
     */
    public SearchPage typeSearchTerm(final String term) {
        WebElement input = waitForVisibility(searchInput);
        input.clear();
        input.sendKeys(term);
        log.info("Termo digitado na busca: '{}'", term);
        return this;
    }

    /**
     * Submete a busca clicando no botão de pesquisar.
     *
     * @return a própria página (será a página de resultados após submit)
     */
    public SearchPage submitSearch() {
        waitForClickability(searchSubmitButton).click();
        log.info("Busca submetida.");
        return this;
    }

    /**
     * Fecha o overlay de busca clicando no botão de fechar (X).
     *
     * @return a própria página
     */
//    public SearchPage closeSearchOverlay() {
//        waitForClickability(closeOverlayButton).click();
//        return this;
//    }

    public SearchPage closeSearchOverlay() {
        try {
            // Aguarda o botão ficar clicável
            WebElement closeButton = waitForClickability(closeOverlayButton);

            // Tenta clique normal
            try {
                closeButton.click();
            } catch (Exception e) {
                // Fallback: JavaScript click
                log.warn("Clique normal falhou, usando JavaScript click");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
            }

            // Aguarda o overlay desaparecer (aumenta o timeout)
            wait.until(ExpectedConditions.invisibilityOf(searchOverlay));

            // Pequena pausa para garantir que a animação terminou
            Thread.sleep(500);

        } catch (Exception e) {
            log.error("Erro ao fechar overlay: {}", e.getMessage());
            // Tenta ESC como último recurso
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }

        return this;
    }

    /**
     * @return URL atual da página
     */
    public String getUrl() {
        return getCurrentUrl();
    }

    // -------------------------------------------------------------------------
    // Verificações
    // -------------------------------------------------------------------------

    /**
     * Verifica se o ícone de busca está visível no header.
     *
     * @return {@code true} se o toggle estiver visível
     */
    public boolean isSearchToggleVisible() {
        // Aguarda até que qualquer candidato apareça no DOM
        try {
            wait.until(ExpectedConditions.or(
                    SEARCH_TOGGLE_LOCATORS.stream()
                            .map(ExpectedConditions::presenceOfElementLocated)
                            .toArray(org.openqa.selenium.support.ui.ExpectedCondition[]::new)
            ));
        } catch (Exception ignored) { /* nenhum apareceu no tempo limite */ }

        for (By locator : SEARCH_TOGGLE_LOCATORS) {
            try {
                List<WebElement> found = driver.findElements(locator);
                if (!found.isEmpty() && found.get(0).isDisplayed()) {
                    log.info("Toggle de busca visível com seletor: {}", locator);
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        log.warn("Toggle de busca NÃO encontrado em nenhum seletor candidato.");
        return false;
    }

    /**
     * Verifica se o overlay de busca está visível/aberto.
     *
     * @return {@code true} se o overlay estiver visível
     */
    public boolean isSearchOverlayVisible() {
        try {
            return waitForVisibility(searchOverlay).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se o campo de busca dentro do overlay está visível.
     *
     * @return {@code true} se o input estiver visível
     */
    public boolean isSearchInputVisible() {
        try {
            return waitForVisibility(searchInput).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se a página de resultados exibe ao menos um artigo encontrado.
     *
     * @return {@code true} se ao menos um resultado estiver presente
     */
    public boolean hasSearchResults() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_RESULTS_ARTICLES));
            List<WebElement> results = driver.findElements(SEARCH_RESULTS_ARTICLES);
            log.info("Quantidade de resultados encontrados: {}", results.size());
            return !results.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retorna a quantidade de resultados exibidos na página de busca.
     *
     * @return número de artigos na página de resultados
     */
    public int getResultsCount() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(SEARCH_RESULTS_ARTICLES));
            return driver.findElements(SEARCH_RESULTS_ARTICLES).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Verifica se a página de resultados exibe mensagem de "nenhum resultado".
     *
     * @return {@code true} se o container de sem-resultado estiver presente
     */
    public boolean hasNoResultsMessage() {
        try {
            List<WebElement> noResults = driver.findElements(NO_RESULTS_CONTAINER);
            if (!noResults.isEmpty() && noResults.get(0).isDisplayed()) {
                log.info("Mensagem 'nenhum resultado' encontrada.");
                return true;
            }
            // Fallback: verifica se não há artigos na página de resultados
            List<WebElement> articles = driver.findElements(SEARCH_RESULTS_ARTICLES);
            return articles.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retorna o texto do título da página de resultados de busca.
     * Exemplo: "Resultados encontrados para: xyzqwerty123456789"
     *
     * @return texto do título ou string vazia se não encontrado
     */
    public String getSearchResultsTitle() {
        try {
            WebElement title = waitForVisibility(
                    By.cssSelector(".ast-archive-title, .page-title, h1.page-title")
            );
            return title.getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Retorna o texto da mensagem exibida quando nenhum resultado é encontrado.
     * Exemplo: "Lamentamos, mas nada foi encontrado para sua pesquisa..."
     *
     * @return texto da mensagem ou string vazia se não encontrado
     */
    public String getNoResultsMessageText() {
        try {
            WebElement message = waitForVisibility(
                    By.cssSelector(".no-results .page-content p, .not-found .page-content p")
            );
            return message.getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Verifica se a URL atual é a de uma página de resultados de busca.
     * O WordPress usa o parâmetro {@code ?s=} na URL de busca.
     *
     * @return {@code true} se a URL contiver o parâmetro de busca
     */
    public boolean isOnSearchResultsPage() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("?s="),
                    ExpectedConditions.urlContains("&s=")
            ));
            return true;
        } catch (Exception e) {
            log.warn("Timeout aguardando URL de resultados. URL atual: {}", getCurrentUrl());
            return false;
        }
    }

    /**
     * Verifica se os títulos dos resultados contêm o termo pesquisado.
     *
     * @param term termo que deve aparecer nos resultados
     * @return {@code true} se ao menos um título contiver o termo (case-insensitive)
     */
    public boolean resultsContainTerm(final String term) {
        List<WebElement> titles = driver.findElements(ENTRY_TITLE);
        return titles.stream()
                .anyMatch(t -> normalize(t.getText()).contains(normalize(term)));
    }

    /**
     * Remove acentos e converte para minúsculas para comparação resiliente.
     */
    private String normalize(final String text) {
        return java.text.Normalizer
                .normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    // -------------------------------------------------------------------------
    // Helper privado
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // Helper privado
    // -------------------------------------------------------------------------

    private WebElement findSearchToggle() {
        // Aguarda qualquer candidato aparecer no DOM
        try {
            wait.until(ExpectedConditions.or(
                    SEARCH_TOGGLE_LOCATORS.stream()
                            .map(ExpectedConditions::presenceOfElementLocated)
                            .toArray(org.openqa.selenium.support.ui.ExpectedCondition[]::new)
            ));
        } catch (Exception e) {
            throw new NoSuchElementException(
                    "Toggle de busca não encontrado no DOM. Verifique se está em uma página interna do Astra."
            );
        }

        // Procura o elemento visível entre os candidatos
        for (By locator : SEARCH_TOGGLE_LOCATORS) {
            try {
                List<WebElement> found = driver.findElements(locator);
                if (!found.isEmpty() && found.get(0).isDisplayed()) {
                    log.debug("Toggle encontrado com seletor: {}", locator);
                    return found.get(0);
                }
            } catch (Exception ignored) {
                // Continua tentando outros locators
            }
        }

        throw new NoSuchElementException(
                "Toggle de busca encontrado no DOM mas nenhum está visível. " +
                        "Locators testados: " + SEARCH_TOGGLE_LOCATORS
        );
    }

}