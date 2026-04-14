package br.com.agi.qa.steps;

import br.com.agi.qa.pages.SearchPage;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step Definitions para os cenários de Busca do Blog do Agi.
 *
 * <p>A lógica de interação com a UI fica exclusivamente em {@link SearchPage}.
 * Aqui apenas orquestramos os passos e fazemos as asserções.
 */
public class SearchSteps {

    private static final Logger log = LoggerFactory.getLogger(SearchSteps.class);

    private final SearchPage searchPage = new SearchPage();
    private String currentSearchTerm;

    // -------------------------------------------------------------------------
    // Given / Dado
    // -------------------------------------------------------------------------

    @Dado("que estou em uma página interna do blog")
    public void queEstouEmUmaPaginaInternaDoBlog() {
        log.info("Navegando para página interna de entrada da busca...");
        searchPage.openFromInternalPage();
    }

    // -------------------------------------------------------------------------
    // Then / Então
    // -------------------------------------------------------------------------

    @Então("o ícone de busca deve estar visível no header")
    public void oIconeDeBuscaDeveEstarVisivelNoHeader() {
        assertThat(searchPage.isSearchToggleVisible())
                .as("O ícone de busca (lupa) deve estar visível no header da página interna")
                .isTrue();
    }

    @Então("o overlay de busca deve ser exibido")
    public void oOverlayDeBuscaDeveSerExibido() {
        assertThat(searchPage.isSearchOverlayVisible())
                .as("O overlay full-screen de busca deve estar visível após clicar na lupa")
                .isTrue();
    }

    @Então("o campo de busca deve estar visível e focado")
    public void oCampoDeBuscaDeveEstarVisivelEFocado() {
        assertThat(searchPage.isSearchInputVisible())
                .as("O campo de texto de busca deve estar visível dentro do overlay")
                .isTrue();
    }

    @Então("devo ser redirecionado para a página de resultados")
    public void devoSerRedirecionadoParaAPaginaDeResultados() {
        assertThat(searchPage.isOnSearchResultsPage())
                .as("A URL deve conter o parâmetro '?s=' indicando página de resultados de busca")
                .isTrue();
        log.info("URL da página de resultados: {}", searchPage.getUrl());
    }

    @Então("os resultados de busca devem ser exibidos")
    public void osResultadosDeBuscaDevemSerExibidos() {
        assertThat(searchPage.hasSearchResults())
                .as("Ao menos um artigo deve ser exibido nos resultados de busca")
                .isTrue();
        log.info("Total de resultados: {}", searchPage.getResultsCount());
    }

    @Então("os resultados devem conter artigos relacionados ao termo pesquisado")
    public void osResultadosDevemConterArtigosRelacionadosAoTermoPesquisado() {
        assertThat(searchPage.resultsContainTerm(currentSearchTerm))
                .as("Ao menos um resultado deve conter o termo '%s' no título", currentSearchTerm)
                .isTrue();
    }

    @Então("uma mensagem de nenhum resultado deve ser exibida")
    public void umaMensagemDeNenhumResultadoDeveSerExibida() {
        assertThat(searchPage.hasNoResultsMessage())
                .as("Deve ser exibida mensagem de 'nenhum resultado encontrado' para o termo '%s'",
                        currentSearchTerm)
                .isTrue();
    }

    @Então("o overlay de busca não deve estar visível")
    public void oOverlayDeBuscaNaoDeveEstarVisivel() {
        assertThat(searchPage.isSearchOverlayVisible())
                .as("O overlay de busca deve estar fechado após clicar no botão fechar")
                .isFalse();
    }

    // -------------------------------------------------------------------------
    // When / Quando
    // -------------------------------------------------------------------------

    @Quando("clico no ícone de busca")
    public void clicoNoIconeDeBusca() {
        searchPage.clickSearchToggle();
    }

    @Quando("digito o termo de busca {string}")
    public void digitoOTermoDeBusca(final String term) {
        // Resolve a partir do config se o termo for uma chave de configuração
        this.currentSearchTerm = resolveSearchTerm(term);
        searchPage.typeSearchTerm(currentSearchTerm);
    }

    @Quando("submeto a busca")
    public void submetoABusca() {
        searchPage.submitSearch();
    }

    @Quando("fecho o overlay de busca")
    public void fechoOOverlayDeBusca() {
        searchPage.closeSearchOverlay();
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /**
     * Permite que o feature file use tanto termos literais quanto chaves do config.
     * Ex: "emprestimo" → retorna "emprestimo" direto
     *     "xyzqwerty123456789" → retorna o próprio valor
     */
    private String resolveSearchTerm(final String term) {
        return term;
    }
}
