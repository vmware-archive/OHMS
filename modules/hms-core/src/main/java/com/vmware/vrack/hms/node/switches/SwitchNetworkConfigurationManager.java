/* ********************************************************************************
 * SwitchNetworkConfigurationManager.java
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
package com.vmware.vrack.hms.node.switches;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;

@Deprecated
public class SwitchNetworkConfigurationManager
{
    public SwitchNetworkConfigurationManager()
    {
    }

    public SwitchNetworkConfiguration load( String file )
        throws HmsException
    {
        SwitchNetworkConfiguration networkConfiguration = null;
        logger.debug( "Loading network configuration file " + file );

        try
        {
            networkConfiguration = objectMapper.readValue( new File( file ), SwitchNetworkConfiguration.class );
        }
        catch ( IOException e )
        {
            logger.error( "Error loading network configuration file " + file, e );
            throw new HmsException( "Error loading network configuration file " + file, e );
        }

        return networkConfiguration;
    }

    public void apply( SwitchNetworkConfiguration networkConfig, ISwitchService switchService, SwitchNode switchNode )
    {
        logger.info( "Applying network configuration to switch " + switchNode.getSwitchId() );

        try
        {
            switchService.applyNetworkConfiguration( switchNode, networkConfig );
        }
        catch ( HmsException e )
        {
            logger.error( "Error applying network configuration to switch " + switchNode.getSwitchId(), e );
        }
    }

    public void apply( SwitchNetworkConfiguration networkConfig, String switchId )
    {
        SwitchNodeConnector switchNodeConnector = SwitchNodeConnector.getInstance();
        SwitchNode switchNode = switchNodeConnector.getSwitchNode( switchId );
        ISwitchService switchService = switchNodeConnector.getSwitchService( switchId );
        apply( networkConfig, switchService, switchNode );
    }

    public boolean isValidConfigName( String configName )
    {
        String configsDir =
            HmsConfigHolder.getHMSConfigProperty( HmsConfigHolder.HMS_NETWORK_CONFIGURATIONS_DIRECTORY );
        String fileName = configName + ".json";
        File configFile = new File( configsDir, fileName );
        return ( configFile.exists() && configFile.canRead() );
    }

    public static void main( String[] args )
        throws IOException, HmsException
    {
        String switchId = "S0";
        SwitchNetworkConfigurationManager sncm = new SwitchNetworkConfigurationManager();

        // Initialize the various sections like switch, server, and the hmsApp itself
        // via provided config.properties files.
        HmsConfigHolder.initializeHmsAppProperties();

        SwitchNetworkConfiguration tsnm =
            sncm.load( "/Users/sunil/git/sddc-lanier/hms-core/src/main/resources/nwconfig-1.0.0.json" );
        sncm.apply( tsnm, switchId );
        logger.debug( sncm.objectMapper.writeValueAsString( tsnm ) );
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    private static Logger logger = Logger.getLogger( SwitchNetworkConfigurationManager.class );
}
