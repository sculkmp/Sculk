package org.sculk.lang;

import lombok.Getter;

@Getter
public enum Language {
    ENG("eng.ini"),
    FR("fr.ini");

    private final String fileName;

    Language(String fileName) {
        this.fileName = fileName;
    }

    public static Language fromCode(String code) {
        return switch (code.toLowerCase()) {
            case "eng", "en", "english" -> ENG;
            case "fr", "french" -> FR;
            default -> throw new IllegalArgumentException("Unsupported language code: " + code);
        };
    }

    public static String fromLang(Language lang) {
        return switch (lang) {
            case ENG -> "English";
            case FR -> "Français";
            default -> throw new IllegalArgumentException("Unsupported language: " + lang);
        };
    }

    public static String fromLang(String lang) {
        return switch (lang.toLowerCase()) {
            case "eng", "en", "english" -> "English";
            case "fr", "french" -> "Français";
            default -> throw new IllegalArgumentException("Unsupported language: " + lang);
        };
    }
}