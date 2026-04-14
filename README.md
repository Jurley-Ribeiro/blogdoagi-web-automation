# 🤖 Blog do Agi — Web Test Automation

Projeto de automação de testes Web para o [Blog do Agi](https://blog.agibank.com.br/), desenvolvido como parte do processo seletivo para a vaga de QA Automation.

---

## 🛠️ Stack Tecnológica

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal |
| Maven | 3.8+ | Gerenciamento de dependências e build |
| Selenium | 4.18.1 | Automação do browser |
| Cucumber | 7.15.0 | BDD / Gherkin (pt-BR) |
| JUnit | 4.13.2 | Runner dos testes |
| WebDriverManager | 5.9.2 | Download automático dos drivers |
| AssertJ | 3.25.1 | Assertions fluentes |

---

## 🏗️ Arquitetura do Projeto

```
src/
├── main/java/br/com/agi/qa/
│   ├── config/
│   │   └── ConfigReader.java       # Lê config.properties (Singleton)
│   ├── driver/
│   │   ├── BrowserType.java        # Enum: CHROME | FIREFOX | EDGE
│   │   ├── DriverFactory.java      # Factory: cria instância do WebDriver
│   │   └── DriverManager.java      # ThreadLocal: ciclo de vida do driver
│   └── pages/
│       ├── BasePage.java           # Classe base com utilitários de espera
│       ├── HomePage.java           # Page Object da Home do Blog do Agi
│       └── SearchPage.java         # Page Object da funcionalidade de busca
└── test/
    ├── java/br/com/agi/qa/
    │   ├── hooks/
    │   │   └── Hooks.java          # Before/After: driver + screenshot em falha
    │   ├── runner/
    │   │   └── TestRunner.java     # Runner do Cucumber
    │   └── steps/
    │       ├── HomePageSteps.java  # Steps da Home Page
    │       └── SearchSteps.java    # Steps da funcionalidade de busca
    └── resources/
        ├── config.properties       # ⚙️ Todas as configurações
        └── features/
            ├── home_page.feature   # Cenários da Home (incl. bug documentado)
            └── search.feature      # Cenários de busca (core do desafio)
```

---

## ✅ Pré-requisitos

- **Java 21+** → [Download Temurin](https://adoptium.net/)
- **Maven 3.8+** → [Download](https://maven.apache.org/download.cgi)
- **Um dos browsers instalados:** Google Chrome, Mozilla Firefox ou Microsoft Edge

> 💡 **Drivers não precisam ser baixados manualmente.** O WebDriverManager gerencia isso.

---

## 🚀 Como Executar

### 1. Clonar o repositório

```bash
git clone https://github.com/SEU_USUARIO/blogdoagi-web-automation.git
cd blogdoagi-web-automation
```

### 2. Executar todos os testes

```bash
mvn clean test
```

### 3. Executar por browser

```bash
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
mvn clean test -Dbrowser=chrome   # padrão
```

### 4. Modo headless (CI/CD)

```bash
mvn clean test -Dheadless=true
```

### 5. Filtrar por tag

```bash
# Apenas smoke tests
mvn clean test -Dcucumber.filter.tags="@smoke"

# Apenas testes de busca
mvn clean test -Dcucumber.filter.tags="@search"

# Excluir cenários de bug documentado
mvn clean test -Dcucumber.filter.tags="not @bug"

# Smoke sem bugs (recomendado para pipeline)
mvn clean test -Dcucumber.filter.tags="@smoke and not @bug"
```

---

## ⚙️ Configurações (`config.properties`)

| Propriedade | Padrão | Descrição |
|---|---|---|
| `base.url` | `https://blog.agibank.com.br/` | URL base do blog |
| `search.entry.page` | `emprestimos/` | Página interna para testar a busca |
| `search.term.valid` | `emprestimo` | Termo que deve retornar resultados |
| `search.term.no.results` | `xyzqwerty123456789` | Termo sem resultados |
| `browser` | `chrome` | Browser padrão |
| `headless` | `false` | Modo headless |
| `timeout.explicit` | `15` | Timeout explícito (segundos) |

Todas as propriedades podem ser sobrescritas com `-Dchave=valor`.

---

## 🧪 Cenários de Teste

### Feature: Home Page (`home_page.feature`)

| Tag | Cenário | Resultado Esperado |
|---|---|---|
| `@smoke` | Acessar a Home com sucesso | Título contém "Agi", URL correta |
| `@smoke` | Verificar elementos estruturais | Header, nav e conteúdo visíveis |
| `@bug` | Ícone de busca ausente na Home | **Bug documentado**: ícone ausente apenas na Home |

### Feature: Busca (`search.feature`)

| Tag | Cenário | Resultado Esperado |
|---|---|---|
| `@smoke` | Busca por termo válido | Resultados exibidos, URL com `?s=` |
| `@smoke` | Busca sem resultados | Mensagem de "nenhum resultado" |
| `@smoke` | Fechar overlay sem pesquisar | Overlay fecha, usuário permanece na página |

---

## 🐛 Bug Identificado

### 🔴 Ícone de busca ausente na Home Page

**Descrição:** A Home page (`https://blog.agibank.com.br/`) apresenta um problema de layout onde o ícone de busca (lupa) que deveria aparecer no canto superior direito não é renderizado. O input de busca está localizado no rodapé da página em vez do header.

**Impacto:** Usuários não conseguem encontrar e utilizar a busca a partir da página inicial.

**Comportamento esperado:** O ícone de busca deve estar visível no header da Home, como ocorre em todas as outras páginas do blog.

**Comportamento atual:** O ícone está ausente. O campo de busca aparece apenas no rodapé.

**Páginas afetadas:** Apenas a Home (`/`). Todas as páginas internas funcionam corretamente.

**Evidência:** Cenário `@bug` no arquivo `home_page.feature` documenta e valida o comportamento atual.

---

## 📊 Relatórios

```bash
# HTML (abrir no browser)
open target/cucumber-reports/cucumber.html

# Windows
start target/cucumber-reports/cucumber.html
```

---

## 🔄 Pipeline CI/CD (GitHub Actions)

O workflow `.github/workflows/ci.yml`:
- Executa a cada `push` em `main` e `develop`
- Executa a cada `pull_request` para `main`
- Permite execução manual com escolha de browser
- Roda sempre em modo headless
- Publica relatório Cucumber como artefato
- Captura screenshots de falhas automaticamente

---

## 🏛️ Decisões de Design Pattern

| Padrão | Aplicação |
|---|---|
| **Page Object Model** | `HomePage`, `SearchPage` isolam seletores da lógica de teste |
| **Factory Method** | `DriverFactory` cria o driver correto por browser |
| **Singleton** | `ConfigReader` carrega o properties uma única vez |
| **ThreadLocal** | `DriverManager` isola driver por thread (pronto para paralelismo) |
| **Composite Locator** | Multi-seletor com fallback para resistência a mudanças de tema |
| **SRP** | Cada classe tem uma responsabilidade única |
| **Zero Hardcode** | URLs, timeouts, browsers e termos de busca via `config.properties` |
| **Bug Documentation** | Cenários `@bug` documentam defeitos conhecidos como testes vivos |

---

## 👨‍💻 Desenvolvedor

**Jurley Colares Ribeiro**

- 🔗 LinkedIn: [https://www.linkedin.com/in/jurley-ribeiro/](https://www.linkedin.com/in/jurley-ribeiro/)
- 🐙 GitHub: [https://github.com/Jurley-Ribeiro](https://github.com/Jurley-Ribeiro)
- 📧 E-mail: [jurley76@hotmail.com](mailto:jurley76@hotmail.com)

---
