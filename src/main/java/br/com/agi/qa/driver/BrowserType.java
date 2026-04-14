package br.com.agi.qa.driver;

/**
 * Enum que representa os browsers suportados pela automação.
 */
public enum BrowserType {

    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("edge");

    private final String value;

    BrowserType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Converte uma String para o enum correspondente (case-insensitive).
     *
     * @param browserName nome do browser
     * @return BrowserType correspondente
     * @throws IllegalArgumentException se o browser não for suportado
     */
    public static BrowserType fromString(final String browserName) {
        for (BrowserType browser : values()) {
            if (browser.value.equalsIgnoreCase(browserName)) {
                return browser;
            }
        }
        throw new IllegalArgumentException(
            String.format("Browser '%s' não suportado. Opções válidas: chrome, firefox, edge.", browserName)
        );
    }
}
