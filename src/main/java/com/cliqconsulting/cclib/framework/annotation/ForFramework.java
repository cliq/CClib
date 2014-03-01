package com.cliqconsulting.cclib.framework.annotation;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * ForFramework
 * <p/>
 * Created by Flavio Ramos on 2/20/14 14:35.
 * Copyright (c) 2013. All rights reserved.
 */
@Qualifier
@Retention(RUNTIME)
public @interface ForFramework {
}