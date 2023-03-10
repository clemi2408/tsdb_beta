package de.cleem.bm.tsdb.model.config.datagenerator;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ValueTypes {


    STRING(java.lang.String.class, "string"),
    INTEGER(java.lang.Integer.class, "integer"),
    DOUBLE(java.lang.Double.class, "double"),
    NUMBER(java.lang.Number.class, "number");

    private final Class clazz;
    private final String string;

    private ValueTypes(final Class clazz, final String string){
        this.clazz=clazz;
        this.string=string;
    }
    private static final Map<Class, ValueTypes> LOOKUP1 =
            Arrays.stream(ValueTypes.values()).collect(Collectors.toMap(ValueTypes::getClazz, Function.identity()));

    private static final Map<String, ValueTypes> LOOKUP2 =
            Arrays.stream(ValueTypes.values()).collect(Collectors.toMap(ValueTypes::getString, Function.identity()));

    public static ValueTypes get(final Class clazz) {
        return LOOKUP1.get(clazz);
    }

    public static ValueTypes get(final String string) {
        return LOOKUP2.get(string.toLowerCase());
    }
}
