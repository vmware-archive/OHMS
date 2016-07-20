/* ********************************************************************************
 * PowerCycleServerTask.java
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
package com.vmware.vrack.hms.task.ipmi;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * Power Cycle Server Task to trigger power cycle server via BMC
 * 
 * @author Vmware
 */
public class PowerCycleServerTask
{
    private static Logger logger = Logger.getLogger( PowerCycleServerTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Power Cycle Server Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector;

    public PowerCycleServerTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
    }

    public PowerCycleServerTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public boolean executeTask()
        throws Exception, IpmiServiceResponseException
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi PowerCycleServer task for Node " + node.getNodeID() );
            throw new Exception( "PowerCycle Operation isn't supported" );
            // try
            // {
            // ChassisControl chassisControl =
            // new ChassisControl( IpmiVersion.V20, connector.getCipherSuite(), AuthenticationType.RMCPPlus,
            // PowerCommand.PowerCycle );
            // ChassisControlResponseData data =
            // (ChassisControlResponseData) connector.getConnector().sendMessage( connector.getHandle(),
            // chassisControl );
            // logger.info( "Power Cycled Node [ " + node.getNodeID() + " ]" );
            // return true;
            // }
            // catch ( IPMIException e )
            // {
            // logger.error( "Exception while executing the task PowerCycleServer: " + e.getCompletionCode() + ":"
            // + e.getMessage() );
            // logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
            // throw new IpmiServiceResponseException( e.getCompletionCode() );
            // }
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
