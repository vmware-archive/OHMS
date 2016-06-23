/* ********************************************************************************
 * RunnerFactory.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.plugin.testlib;

import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * Factory class to call RetryRunner with the required parameters
 *
 * @author VMware Inc.
 */
public class RunnerFactory
    implements ParametersRunnerFactory
{
    @Override
    public org.junit.runner.Runner createRunnerForTestWithParameters( TestWithParameters test )
        throws InitializationError
    {
        Class<?> klass = test.getTestClass().getJavaClass();
        return new RetryRunner( klass, test );
    }
}
