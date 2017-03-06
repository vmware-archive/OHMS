/* ********************************************************************************
 * CumulusEtcNetworkInterfacesTest.java
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
package com.vmware.vrack.hms;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration;
import com.vmware.vrack.hms.node.switches.SwitchNetworkConfigurationManager;
import com.vmware.vrack.hms.switches.cumulus.CumulusEtcNetworkInterfaces;

/**
 * Created by itrendafilov on 7/18/14.
 */
public class CumulusEtcNetworkInterfacesTest
{
    private ObjectMapper objectMapper = new ObjectMapper();

    public CumulusEtcNetworkInterfacesTest()
    {
    }

    public void runTest( String FilePath )
    {
        // Initialize the various sections like switch, server, and the hmsApp itself
        // via provided config.properties files.
        HmsConfigHolder.initializeHmsAppProperties();
        try
        {
            SwitchNetworkConfigurationManager sncm = new SwitchNetworkConfigurationManager();
            SwitchNetworkConfiguration tsnm = sncm.load( FilePath );

            CumulusEtcNetworkInterfaces cifaces = CumulusEtcNetworkInterfaces.parseNetworkConfiguration( tsnm );

            System.out.println( cifaces.toString() );
        }
        catch ( HmsException e )
        {
            System.out.println( "Error loading network configuration file " + FilePath + "\n" + e.getCause() );
        }
    }

    @Test
    public void testCumulusEtcNetworkInterfacesConfigurationFromFile()
    {
        this.runTest( getClass().getResource( "/network-config-A.json" ).getFile() );
    }

    @Test
    public void testOneClouldConfigTor1()
    {
        this.runTest( getClass().getResource( "/OneClould-Network-Config-Rack1.json" ).getFile() );

    }

    @Test
    public void testOneClouldConfigSpine1()
    {
        this.runTest( getClass().getResource( "/OneClould-Network-Config-Spine.json" ).getFile() );

    }
}
