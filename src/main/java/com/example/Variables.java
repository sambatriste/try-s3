package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * パラメータを提供するクラス。
 */
public class Variables {

    /** パラメータ取得元 */
    interface VariableSource {
        /**
         * 指定されたキーに対応する値を取得する。
         * @param key キー
         * @return 値（キーに対応する値が存在しない場合、{@code null}）
         */
        String get(String key);
    }

    private final List<VariableSource> variableSources;

    public Variables() {
        List<VariableSource> variableSources = new ArrayList<>();
        variableSources.add(new EnvVariableSource());
        variableSources.add(System::getProperty);
        this.variableSources = variableSources;
    }

    public Variables(String propFilePath) {
        this();
        variableSources.add(new PropFileVariableSource(propFilePath));
    }

    public Variables(List<VariableSource> variableSources) {
        this.variableSources = variableSources;
    }

    /**
     * パラメータを取得する。
     * @param key キー
     * @return 値
     * @throws IllegalArgumentException キーに対応する値が存在しない場合
     */
    public String get(String key) {
        String val = get(key, null);
        if (val == null) {
            throw new IllegalArgumentException(key);
        }
        return val;
    }

    /**
     * パラメータを取得する。
     * @param key キー
     * @param defaultValue キーに対応する値が存在しない場合に使用するデフォルト値
     * @return 値
     */
    public String get(String key, String defaultValue) {
        String value = doGet(key);
        return (value == null) ? defaultValue : value;
    }

    /**
     * パラメータを数値として取得する。
     * @param key キー
     * @return 値
     * @throws IllegalArgumentException キーに対応する値が存在しない場合
     */
    public Number getNumber(String key) {
        String value = get(key);
        return new BigDecimal(value);
    }

    /**
     * パラメータを数値として取得する。
     * @param key キー
     * @param defaultValue キーに対応する値が存在しない場合に使用するデフォルト値
     * @return 値
     */
    public <T extends Number> Number getNumber(String key, T defaultValue) {
        String value = doGet(key);
        return (value == null) ? defaultValue : new BigDecimal(value);
    }

    private String doGet(String key) {
        for (VariableSource s : variableSources) {
            String value = s.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /** 値を環境変数から取得する{@link VariableSource}実装クラス。 */
    private static class EnvVariableSource implements VariableSource {
        @Override
        public String get(String key) {
            String envKey = key.replace(".", "_").toUpperCase();
            return System.getenv(envKey);
        }
    }

    /** 値をプロパティファイルから取得する{@link VariableSource}実装クラス。 */
    private static class PropFileVariableSource implements VariableSource {

        private final Properties properties;

        private PropFileVariableSource(String resourcePath) {
            this(load(resourcePath));
        }

        private PropFileVariableSource(Properties properties) {
            this.properties = properties;
        }

        @Override
        public String get(String key) {
            return properties.getProperty(key);
        }

        private static Properties load(String resourceName) {
            InputStream in = Variables.class.getResourceAsStream(resourceName);
            if (in == null) {
                throw new IllegalArgumentException(resourceName);
            }
            Properties properties = new Properties();
            try {
                properties.load(in);
            } catch (IOException e) {
                throw new UncheckedIOException(resourceName, e);
            }
            return properties;
        }
    }
}
