package fi.luukuton.jirin.domain;

/**
 * An enumeration which holds all dictionary site domains.
 */

public enum Dictionaries {
    DICT_GOO("https://dictionary.goo.ne.jp");

    private final String text;

    Dictionaries(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}