package de.cleem.tub.tsdbb.commons.spring.banner;

// Inspired by: https://github.com/springbootcamp/bannerama/blob/master/src/main/java/org/springbootcamp/bannerama/Bannerama.java

public enum PlaceholderEnum {
    ;

    public static final String APPLICATION_NAME = "${spring.application.name}";
    public static final String APPLICATION_TITLE = "${application.banner.title}";
    public static final String APPLICATION_COPYRIGHT = "${application.banner.copyright}";
    public static final String APPLICATION_YEAR = "${application.banner.year}";

}