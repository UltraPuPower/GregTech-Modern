package com.gregtechceu.gtceu.api.ui.holder.connector.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the associated element as being linked with a specific UI element
 * <p/>
 * The annotated field must be {@code public} and NOT {@code static} to be able to be located.
 * The annotation finder can find annotations from parent classes, but annotated members with same names will
 * overwrite parent class names.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface UIFieldLink {

    /**
     * Element ID prefix. must be unique to the element type as to not conflict with others.
     * <p/>
     * for example: {@code "item_in"} would link the annotated field to components with IDs like
     * {@code "item-in.0"}, {@code "item-in.1"} and {@code "item-in.2"}
     * @return the prefix that all elements' IDs this field should be linked to have.
     */
    String value();

}
