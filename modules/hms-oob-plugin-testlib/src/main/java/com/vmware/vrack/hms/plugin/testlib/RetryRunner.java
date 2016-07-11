/* ********************************************************************************
 * RetryRunner.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.plugin.testlib;

import org.junit.AssumptionViolatedException;
import org.junit.Ignore;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.TestWithParameters;

/**
 * Extend BlockJUnit4ClassRunnerWithParameters to implement Retry for failed tests, with parameters
 *
 * @author VMware Inc.
 */
public class RetryRunner
    extends BlockJUnit4ClassRunnerWithParameters
{
    private int retryCount = 3;

    private long sleepTime = 3000;

    private int failedAttempts = 0;

    private final Object[] parameters;

    public RetryRunner( Class<?> klass, TestWithParameters test )
        throws InitializationError
    {
        super( test );
        this.parameters = test.getParameters().toArray( new Object[test.getParameters().size()] );
    }

    @Override
    public void run( final RunNotifier notifier )
    {
        EachTestNotifier testNotifier = new EachTestNotifier( notifier, getDescription() );
        Statement statement = classBlock( notifier );
        try
        {
            statement.evaluate();
        }
        catch ( AssumptionViolatedException e )
        {
            testNotifier.fireTestIgnored();
        }
        catch ( StoppedByUserException e )
        {
            throw e;
        }
        catch ( Throwable e )
        {
            retry( testNotifier, statement, e );
        }
    }

    @Override
    protected void runChild( final FrameworkMethod method, RunNotifier notifier )
    {
        Description description = describeChild( method );
        if ( method.getAnnotation( Ignore.class ) != null )
        {
            notifier.fireTestIgnored( description );
        }
        else
        {
            if ( method.getAnnotation( Repeat.class ) != null && method.getAnnotation( Ignore.class ) == null )
            {
                retryCount = method.getAnnotation( Repeat.class ).retryCount();
                sleepTime = method.getAnnotation( Repeat.class ).sleepTime();
            }
            runTestUnit( methodBlock( method ), description, notifier );
        }
    }

    /**
     * Runs a {@link Statement} that represents a leaf (aka atomic) test.
     */
    protected final void runTestUnit( Statement statement, Description description, RunNotifier notifier )
    {
        EachTestNotifier eachNotifier = new EachTestNotifier( notifier, description );
        eachNotifier.fireTestStarted();
        try
        {
            statement.evaluate();
        }
        catch ( AssumptionViolatedException e )
        {
            eachNotifier.addFailedAssumption( e );
        }
        catch ( Throwable e )
        {
            retry( eachNotifier, statement, e );
        }
        finally
        {
            eachNotifier.fireTestFinished();
        }
    }

    public void retry( EachTestNotifier notifier, Statement statement, Throwable currentThrowable )
    {
        Throwable caughtThrowable = currentThrowable;
        while ( retryCount > failedAttempts )
        {
            try
            {
                timeout( failedAttempts + 1 );
                statement.evaluate();
                return;
            }
            catch ( Throwable t )
            {
                failedAttempts++;
                caughtThrowable = t;
            }
        }
        notifier.addFailure( caughtThrowable );
    }

    public void timeout( int failedAttempt )
    {
        /* Increase timeout after every failure */
        sleepTime = failedAttempt * sleepTime;
        try
        {
            Thread.sleep( sleepTime );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
    }
}
