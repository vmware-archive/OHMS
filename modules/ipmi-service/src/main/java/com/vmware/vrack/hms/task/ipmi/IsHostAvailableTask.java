/* ********************************************************************************
 * IsHostAvailableTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ipmi;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;

/**
 * Host Availablity Task class for discovering host availability
 * 
 * @author Vmware
 */
public class IsHostAvailableTask
{
    private static Logger logger = Logger.getLogger( IsHostAvailableTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Is Host Available Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector;

    public IsHostAvailableTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
    }

    public IsHostAvailableTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public boolean executeTask()
        throws Exception
    {
        boolean status = false;
        if ( node instanceof ServiceServerNode )
        {
            logger.debug( "Received request execute IsHostAvailable task for Node " + node.getNodeID() );
            try
            {
                if ( connector != null )
                {
                    status = true;
                    logger.info( "The Host [ " + node.getNodeID() + " ] is available: " + node.getManagementIp() );
                    return status;
                }
            }
            catch ( Exception e )
            {
                logger.error( "Exception while executing the task IsHostAvailable: ", e );
            }
        }
        else
        {
            String err = String.format( NULL_CONNECTOR_EXCEPTION_MSG, node,
                                        ( node instanceof ServiceServerNode ) ? "" : "NOT ", connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
        return status;
    }
}
