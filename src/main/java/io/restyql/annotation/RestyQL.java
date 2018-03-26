package io.restyql.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestyQL {

    String[] value() default {};

    String[] ignore() default {};
}
