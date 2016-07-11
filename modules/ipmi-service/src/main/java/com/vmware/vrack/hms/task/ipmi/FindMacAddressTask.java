/* ********************************************************************************
 * FindMacAddressTask.java
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
package com.vmware.vrack.hms.task.ipmi;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.common.TypeConverter;
import com.vmware.vrack.coding.commands.IpmiCommandParameters;
import com.vmware.vrack.coding.commands.transport.GetMacAddressCommand;
import com.vmware.vrack.coding.commands.transport.GetMacAddressCommandResponseData;
import com.vmware.vrack.coding.commands.transport.GetChannelInfoCommand;
import com.vmware.vrack.coding.commands.transport.GetChannelInfoCommandResponseData;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * Finds Mac Address of Bmc Node
 * 
 * @author Vmware
 */
public class FindMacAddressTask
{
    private static Logger logger = Logger.getLogger( FindMacAddressTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Find MAC Address Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector;

    public FindMacAddressTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
    }

    public FindMacAddressTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public String executeTask()
        throws Exception, IpmiServiceResponseException
    {
        String macAddress = null;
        int channelNumber = -1;
        byte[] getMacAddressCommandParam = IpmiCommandParameters.GET_MAC_ADDRESS_COMMAND_PARAM;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi FindMacAddress task for Node " + node.getNodeID() );
            try
            {
                // First send the Get Channel Info command to get the number of the channel we are using for IPMI over
                // LAN
                GetChannelInfoCommandResponseData cn =
                    (GetChannelInfoCommandResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                              new GetChannelInfoCommand( IpmiVersion.V20,
                                                                                                                         connector.getCipherSuite(),
                                                                                                                         AuthenticationType.RMCPPlus,
                                                                                                                         IpmiCommandParameters.GET_CHANNEL_INFO_CURRENT_CHANNEL_PARAMETER ) );
                channelNumber = cn.getChannelNumber();
                // Specify the channel number as aparameter to the Get MAC Address command
                getMacAddressCommandParam[0] = TypeConverter.intToByte( channelNumber );
                GetMacAddressCommandResponseData rd =
                    (GetMacAddressCommandResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                             new GetMacAddressCommand( IpmiVersion.V20,
                                                                                                                       connector.getCipherSuite(),
                                                                                                                       AuthenticationType.RMCPPlus,
                                                                                                                       getMacAddressCommandParam ) );
                macAddress = rd.getMacAddressAsString();
                return macAddress;
            }
            catch ( IPMIException e )
            {
                logger.error( "Exception while executing the task FindMacAddress: " + e.getCompletionCode() + ":"
                    + e.getMessage() );
                logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
                throw new IpmiServiceResponseException( e.getCompletionCode() );
            }
        }
        else
        {
            String err = String.format( NULL_CONNECTOR_EXCEPTION_MSG, node,
                                        ( node instanceof ServiceServerNode ) ? "" : "NOT ", connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }
}
