package br.com.agi.qa.steps;

import br.com.agi.qa.pages.HomePage;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step Definitions para os cenários da Home Page.
 */
public class HomePageSteps {

    private static final Logger log = LoggerFactory.getLogger(HomePageSteps.class);

    private final HomePage homePage = new HomePage();

    @Dado("que acesso a Home do Blog do Agi")
    public void queAcessoAHomeDoAgi() {
        log.info("Abrindo a Home do Blog do Agi...");
        homePage.open();
    }

    @Então("a página deve ser carregada com sucesso")
    public void aPaginaDeveSerCarregadaComSucesso() {
        assertThat(homePage.getTitle())
                .as("O título da página não deve estar vazio")
                .isNotBlank();
    }

    @Então("o título da página deve conter {string}")
    public void oTituloDaPaginaDeveConter(final String expectedTitle) {
        assertThat(homePage.getTitle())
                .as("O título deve conter '%s'", expectedTitle)
                .containsIgnoringCase(expectedTitle);
    }

    @Então("a URL deve corresponder ao endereço do blog")
    public void aUrlDeveCorresponderAoEnderecoDoBlog() {
        assertThat(homePage.isUrlCorrect())
                .as("A URL deve corresponder à URL base configurada")
                .isTrue();
    }

    @Então("o header da página deve estar visível")
    public void oHeaderDaPaginaDeveEstarVisivel() {
        assertThat(homePage.isHeaderVisible())
                .as("O header deve estar visível")
                .isTrue();
    }

    @Então("o menu de navegação deve estar visível")
    public void oMenuDeNavegacaoDeveEstarVisivel() {
        assertThat(homePage.isNavigationMenuVisible())
                .as("O menu de navegação deve estar visível")
                .isTrue();
    }

    @Então("o conteúdo principal deve estar visível")
    public void oConteudoPrincipalDeveEstarVisivel() {
        assertThat(homePage.isMainContentVisible())
                .as("O conteúdo principal deve estar visível")
                .isTrue();
    }

    /**
     * Documenta o bug do ícone de busca ausente na Home.
     * Este step CONFIRMA que o ícone NÃO está visível (comportamento bugado atual).
     */
    @Então("o ícone de busca NÃO deve estar visível na Home")
    public void oIconeDeBuscaNaoDeveEstarVisivelNaHome() {
        log.warn("BUG IDENTIFICADO: Verificando ausência do ícone de busca na Home page.");
        assertThat(homePage.isSearchIconVisible())
                .as("BUG: O ícone de busca deveria estar visível na Home, "
                        + "mas está ausente devido a um problema de layout. "
                        + "Funciona corretamente nas páginas internas.")
                .isFalse();
    }
}
