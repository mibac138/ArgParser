package com.github.mibac138.argparser.binder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mibac138 on 13-04-2017.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arg {
	String NO_NAME = "NO_NAME";
	
	String name() default NO_NAME;
}
