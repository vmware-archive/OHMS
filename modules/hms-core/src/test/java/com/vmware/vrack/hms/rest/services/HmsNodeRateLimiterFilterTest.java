/* ********************************************************************************
 * HmsNodeRateLimiterFilterTest.java
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
package com.vmware.vrack.hms.rest.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vmware.vrack.common.event.enums.EventComponent;

public class HmsNodeRateLimiterFilterTest
{

    // Checks if expected nodeId is retrieved from given event URI
    @Test
    public void extractNodeIdFromEventURITest()
    {
        HmsNodeRateLimiterFilter hmsNodeRateLimiterFilter = new HmsNodeRateLimiterFilter();
        String nodeId = hmsNodeRateLimiterFilter.extractNodeIdFromEventURI( "/hms/event/host/nme/N1/" );
        assertNotNull( nodeId );
        assertEquals( nodeId, "N1" );
    }

    // Checks if node id is properly extracted from given URI for Host
    @Test
    public void extractNodeIdFromURITestForNode()
    {
        HmsNodeRateLimiterFilter hmsNodeRateLimiterFilter = new HmsNodeRateLimiterFilter();
        String nodeId =
            hmsNodeRateLimiterFilter.extractNodeIdFromURI( "api/1.0/hms/host/N1/powerstatus/", EventComponent.SERVER );
        assertNotNull( nodeId );
        assertEquals( nodeId, "N1" );
    }

    // Checks if node id is properly extracted from given URI for Switch
    @Test
    public void extractNodeIdFromURITestForSwitch()
    {
        HmsNodeRateLimiterFilter hmsNodeRateLimiterFilter = new HmsNodeRateLimiterFilter();
        String nodeId =
            hmsNodeRateLimiterFilter.extractNodeIdFromURI( "api/1.0/hms/switches/S1/", EventComponent.SWITCH );
        assertNotNull( nodeId );
        assertEquals( nodeId, "S1" );
    }

}
