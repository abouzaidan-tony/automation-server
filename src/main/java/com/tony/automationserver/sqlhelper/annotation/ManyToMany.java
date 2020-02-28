package com.tony.automationserver.sqlhelper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tony.automationserver.sqlhelper.SQLObject;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {
    Class<? extends SQLObject> targetEntity();

    String mappedBy();

    String inversedBy();

    String joinTable();
}