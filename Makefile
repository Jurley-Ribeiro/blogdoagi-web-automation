# ================================================================
# Blog do Agi - Web Automation
# ================================================================
# Comandos disponíveis:
#   make help            - Mostrar esta ajuda
#   make test            - Executar todos os testes
#   make smoke           - Executar apenas smoke tests
#   make search          - Executar apenas testes de busca
#   make chrome          - Executar no Chrome
#   make firefox         - Executar no Firefox
#   make headless        - Executar em modo headless
#   make report          - Abrir relatório Allure interativo
#   make report-generate - Gerar relatório sem abrir browser
#   make clean           - Limpar relatórios
#   make ci              - Executar como CI (headless + relatório)
#   make tags TAGS="..." - Executar com tag customizada
# ================================================================

.PHONY: help test smoke search chrome firefox headless report report-generate clean ci tags

# Cores para output
GREEN  := $(shell tput -Txterm setaf 2 2>/dev/null || echo '')
YELLOW := $(shell tput -Txterm setaf 3 2>/dev/null || echo '')
BLUE   := $(shell tput -Txterm setaf 4 2>/dev/null || echo '')
RED    := $(shell tput -Txterm setaf 1 2>/dev/null || echo '')
RESET  := $(shell tput -Txterm sgr0 2>/dev/null || echo '')

# Configurações padrão
BROWSER  ?= chrome
HEADLESS ?= false
TAGS     ?=

# ================================================================
# Comandos principais
# ================================================================

help:
	@echo ""
	@echo "$(GREEN)╔══════════════════════════════════════════╗$(RESET)"
	@echo "$(GREEN)║     Blog do Agi - Web Automation         ║$(RESET)"
	@echo "$(GREEN)╚══════════════════════════════════════════╝$(RESET)"
	@echo ""
	@echo "$(YELLOW)Comandos disponíveis:$(RESET)"
	@echo "  $(BLUE)make test$(RESET)                      - Executar todos os testes"
	@echo "  $(BLUE)make smoke$(RESET)                     - Executar apenas smoke tests"
	@echo "  $(BLUE)make search$(RESET)                    - Executar apenas testes de busca"
	@echo "  $(BLUE)make chrome$(RESET)                    - Executar no Chrome"
	@echo "  $(BLUE)make firefox$(RESET)                   - Executar no Firefox"
	@echo "  $(BLUE)make headless$(RESET)                  - Executar em modo headless"
	@echo "  $(BLUE)make report$(RESET)                    - Abrir relatório Allure interativo"
	@echo "  $(BLUE)make report-generate$(RESET)           - Gerar relatório sem abrir browser"
	@echo "  $(BLUE)make clean$(RESET)                     - Limpar relatórios"
	@echo "  $(BLUE)make ci$(RESET)                        - Executar como CI (headless + relatório)"
	@echo "  $(BLUE)make tags TAGS=\"@smoke\"$(RESET)        - Executar com tag customizada"
	@echo ""
	@echo "$(YELLOW)Exemplos:$(RESET)"
	@echo "  make tags TAGS=\"@smoke and not @bug\""
	@echo "  make headless BROWSER=firefox"
	@echo ""

test:
	@echo "$(BLUE)🚀 Executando todos os testes...$(RESET)"
	mvn clean test -Dbrowser=$(BROWSER) -Dheadless=$(HEADLESS)
	@$(MAKE) report-generate

smoke:
	@echo "$(BLUE)🚀 Executando smoke tests...$(RESET)"
	mvn clean test -Dcucumber.filter.tags="@smoke" -Dbrowser=$(BROWSER) -Dheadless=$(HEADLESS)
	@$(MAKE) report-generate

search:
	@echo "$(BLUE)🚀 Executando testes de busca...$(RESET)"
	mvn clean test -Dcucumber.filter.tags="@search" -Dbrowser=$(BROWSER) -Dheadless=$(HEADLESS)
	@$(MAKE) report-generate

tags:
	@echo "$(BLUE)🚀 Executando testes com tag: $(TAGS)$(RESET)"
	mvn clean test -Dcucumber.filter.tags="$(TAGS)" -Dbrowser=$(BROWSER) -Dheadless=$(HEADLESS)
	@$(MAKE) report-generate

chrome:
	@echo "$(BLUE)🚀 Executando testes no Chrome...$(RESET)"
	mvn clean test -Dbrowser=chrome -Dheadless=$(HEADLESS)
	@$(MAKE) report-generate

firefox:
	@echo "$(BLUE)🚀 Executando testes no Firefox...$(RESET)"
	mvn clean test -Dbrowser=firefox -Dheadless=$(HEADLESS)
	@$(MAKE) report-generate

headless:
	@echo "$(BLUE)🚀 Executando testes em modo headless...$(RESET)"
	mvn clean test -Dbrowser=$(BROWSER) -Dheadless=true
	@$(MAKE) report-generate

report:
	@echo "$(BLUE)📊 Abrindo relatório Allure interativo...$(RESET)"
	@if command -v allure >/dev/null 2>&1; then \
		allure serve target/allure-results; \
	else \
		echo "$(RED)❌ Allure não instalado. Execute: npm install -g allure-commandline$(RESET)"; \
	fi

report-generate:
	@echo "$(BLUE)📊 Gerando relatório Allure...$(RESET)"
	@if command -v allure >/dev/null 2>&1; then \
		allure generate target/allure-results --clean -o target/allure-report; \
		echo "$(GREEN)✅ Relatório gerado em: target/allure-report/index.html$(RESET)"; \
	else \
		echo "$(YELLOW)⚠️  Allure não instalado — pulando geração de relatório.$(RESET)"; \
		echo "$(YELLOW)   Para instalar: npm install -g allure-commandline$(RESET)"; \
	fi

clean:
	@echo "$(YELLOW)🧹 Limpando relatórios e artefatos...$(RESET)"
	rm -rf target/allure-results target/allure-report target/cucumber-reports
	@echo "$(GREEN)✅ Limpeza concluída$(RESET)"

ci:
	@echo "$(BLUE)🤖 Executando em modo CI (headless + Chrome)...$(RESET)"
	mvn clean test -Dbrowser=chrome -Dheadless=true
	@$(MAKE) report-generate
	@echo "$(GREEN)✅ CI executado com sucesso$(RESET)"
