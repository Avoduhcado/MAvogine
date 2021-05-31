package com.avogine.experimental.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

/**
 * Types annotated with {@code MemoryManaged} should explicitly handle implementing some method
 * to clean up any allocated memory they were assigned.
 */
@Retention(CLASS)
@Target(TYPE)
@Inherited
public @interface MemoryManaged {

}
