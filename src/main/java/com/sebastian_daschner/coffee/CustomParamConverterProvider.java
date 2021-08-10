package com.sebastian_daschner.coffee;

import com.sebastian_daschner.coffee.beans.entity.SortCriteria;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.stream.Stream;

@Provider
public class CustomParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        // declare a converter for this type
        if (rawType.equals(SortCriteria.class))
            return (ParamConverter<T>) new SortCriteriaParamConverter();
        return null;
    }

}

class SortCriteriaParamConverter implements ParamConverter<SortCriteria> {

    @Override
    public SortCriteria fromString(String value) {
        return Stream.of(SortCriteria.values())
                .filter(t -> t.name().equalsIgnoreCase(value))
                .findAny().orElse(null);
    }

    @Override
    public String toString(SortCriteria value) {
        return value.name().toLowerCase();
    }
}