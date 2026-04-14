package br.com.agi.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lê as configurações do arquivo config.properties.
 *
 * <p>Prioridade de resolução de valores:
 * <ol>
 *   <li>System property (-Dchave=valor na linha de comando)</li>
 *   <li>Valor definido no config.properties</li>
 * </ol>
 *
 * <p>Segue o padrão Singleton para carregar o arquivo apenas uma vez.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static ConfigReader instance;
    private final Properties properties;

    private ConfigReader() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Retorna a instância única do ConfigReader (Singleton thread-safe).
     *
     * @return instância do ConfigReader
     */
    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    /**
     * Retorna o valor de uma propriedade.
     * System properties têm prioridade sobre o arquivo de configuração.
     *
     * @param key chave da propriedade
     * @return valor da propriedade
     * @throws IllegalArgumentException se a chave não for encontrada
     */
    public String getProperty(final String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        String fileValue = properties.getProperty(key);
        if (fileValue == null) {
            throw new IllegalArgumentException(
                String.format("Propriedade '%s' não encontrada em '%s'.", key, CONFIG_FILE)
            );
        }
        return fileValue.trim();
    }

    /**
     * Retorna o valor de uma propriedade ou um valor padrão caso não exista.
     *
     * @param key          chave da propriedade
     * @param defaultValue valor padrão
     * @return valor da propriedade ou defaultValue
     */
    public String getProperty(final String key, final String defaultValue) {
        try {
            return getProperty(key);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    /**
     * Retorna uma propriedade como boolean.
     *
     * @param key chave da propriedade
     * @return valor booleano da propriedade
     */
    public boolean getBooleanProperty(final String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    /**
     * Retorna uma propriedade como inteiro.
     *
     * @param key chave da propriedade
     * @return valor inteiro da propriedade
     */
    public int getIntProperty(final String key) {
        return Integer.parseInt(getProperty(key));
    }

    private void loadProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException(
                    String.format("Arquivo de configuração '%s' não encontrado no classpath.", CONFIG_FILE)
                );
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar o arquivo de configuração: " + e.getMessage(), e);
        }
    }
}
