package com.yanhai.core.resource.filter;

public enum FilterType {

    AND("and"),

    OR("or"),

    EQUALITY("eq"),

    CONTAINS("co"),

    STARTS_WITH("sw"),

    ENDS_WITH("ew"),

    PRESENCE("pr"),

    GREATER_THAN("gt"),

    GREATER_OR_EQUAL("ge"),

    LESS_THAN("lt"),

    LESS_OR_EQUAL("le");

    private String value;

    FilterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
