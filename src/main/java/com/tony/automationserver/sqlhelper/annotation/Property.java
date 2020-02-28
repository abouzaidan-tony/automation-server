package com.tony.automationserver.sqlhelper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tony.automationserver.sqlhelper.SQLHelper.SQLTypes;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {
    String name();

    SQLTypes type() default SQLTypes.String;
}