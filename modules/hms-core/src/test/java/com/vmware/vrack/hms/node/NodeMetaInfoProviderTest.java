/* ********************************************************************************
 * NodeMetaInfoProviderTest.java
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
package com.vmware.vrack.hms.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.common.util.CommonProperties;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnectorTest;

public class NodeMetaInfoProviderTest
{

    private static Logger logger = Logger.getLogger( SwitchNodeConnectorTest.class );

    public NodeMetaInfoProviderTest()
    {
    }

    @Test
    public void increaseConcurrentOperationCountTest()
    {
        int noOfConcurrentOperations = CommonProperties.getMaxConcurrentTasksPerNode();

        for ( int currentCount = 0; currentCount < noOfConcurrentOperations - 1; ++currentCount )
        {

            NodeMetaInfoProvider.increaseConcurrentOperationCount( "N1" );
        }

        Boolean lockAcquired = NodeMetaInfoProvider.increaseConcurrentOperationCount( "N1" );
        assertTrue( lockAcquired );

        for ( int currentCount = 0; currentCount <= noOfConcurrentOperations; ++currentCount )
        {

            NodeMetaInfoProvider.decreaseConcurrentOperationCount( "N1" );
        }

    }

    @Ignore
    @Test
    public void increaseConcurrentOperationCountFailTest()
    {
        int noOfConcurrentOperations = CommonProperties.getMaxConcurrentTasksPerNode();

        for ( int currentCount = 1; currentCount < noOfConcurrentOperations + 1; ++currentCount )
        {

            NodeMetaInfoProvider.increaseConcurrentOperationCount( "N1" );
        }

        Boolean lockAcquired = NodeMetaInfoProvider.increaseConcurrentOperationCount( "N1" );
        assertFalse( lockAcquired );

    }

    @Test
    public void decreaseConcurrentOperationCountTest()
    {

        NodeMetaInfoProvider.increaseConcurrentOperationCount( "N1" );

        Boolean lockAcquired = NodeMetaInfoProvider.decreaseConcurrentOperationCount( "N1" );

        assertTrue( lockAcquired );

    }

    @Test
    public void decreaseConcurrentOperationCountNegativeTest()
    {

        NodeMetaInfoProvider.increaseConcurrentOperationCount( "N1" );

        NodeMetaInfoProvider.decreaseConcurrentOperationCount( "N1" );
        Boolean lockAcquired = NodeMetaInfoProvider.decreaseConcurrentOperationCount( "N1" );

        assertFalse( lockAcquired );

    }

}
