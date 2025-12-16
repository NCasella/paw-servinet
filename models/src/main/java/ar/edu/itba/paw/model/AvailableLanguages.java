package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.exceptions.InvalidOperationException;

public enum AvailableLanguages {
    ENGLISH("en"),
    SPANISH("es");

    private final String code;

    AvailableLanguages(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AvailableLanguages fromLanguageCode(String code) {
        for (AvailableLanguages lang : AvailableLanguages.values()) {
            if (lang.getCode().equalsIgnoreCase(code)) {
                return lang;
            }
        }
        throw new InvalidOperationException("Language code not supported: " + code);
    }
}
