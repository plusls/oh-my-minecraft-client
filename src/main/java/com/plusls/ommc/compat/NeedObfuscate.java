package com.plusls.ommc.compat;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;


@Retention(CLASS)
@Target(TYPE)
public @interface NeedObfuscate {
}