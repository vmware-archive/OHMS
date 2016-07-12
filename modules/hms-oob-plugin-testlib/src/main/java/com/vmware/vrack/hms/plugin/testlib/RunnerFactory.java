/* ********************************************************************************
 * RunnerFactory.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
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
