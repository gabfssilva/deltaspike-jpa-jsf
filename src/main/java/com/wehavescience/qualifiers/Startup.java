package com.wehavescience.qualifiers;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Startup {
}
