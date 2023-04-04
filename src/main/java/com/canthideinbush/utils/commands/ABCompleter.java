package com.canthideinbush.utils.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ABCompleter {




    int index();

    String arg();

    String permission() default "";
}
