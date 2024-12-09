package com.gregtechceu.gtceu.api.ui.holder.connector.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate all fields that should have links made to them with this annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface UILinkSetter {

    /**
     * @return The type this field is allowed to link to
     */
    Class<?> value();
}
