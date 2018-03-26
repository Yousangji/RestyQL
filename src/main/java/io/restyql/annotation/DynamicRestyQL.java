package io.restyql.annotation;

import java.lang.annotation.Annotation;

/*
   Created at: sangji 2018.03
   for creating instance of RestyQL interface to change interfaces's variable dynamically
 */
public class DynamicRestyQL implements RestyQL {

    private String[] value;
    private String[] ignore;

    public DynamicRestyQL(String[] value, String[] ignore) {
        this.value = value;
        this.ignore = ignore;
    }

    @Override
    public String[] value() {
        return value;
    }

    @Override
    public String[] ignore() {
        return ignore;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return DynamicRestyQL.class;
    }
}
