/* ********************************************************************************
 * PowerStatusServerTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ipmi;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.chassis.GetChassisStatus;
import com.veraxsystems.vxipmi.coding.commands.chassis.GetChassisStatusResponseData;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * Power Status Server Task to get current power status of the server via BMC
 * 
 * @author Vmware
 */
public class PowerStatusServerTask
{
    private static Logger logger = Logger.getLogger( PowerStatusServerTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Power Status Server Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector;

    public PowerStatusServerTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
    }

    public PowerStatusServerTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public boolean executeTask()
        throws Exception, IpmiServiceResponseException
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi PowerStatusServer task for Node " + node.getNodeID() );
            try
            {
                boolean status;
                GetChassisStatusResponseData rd =
                    (GetChassisStatusResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                         new GetChassisStatus( IpmiVersion.V20,
                                                                                                               connector.getCipherSuite(),
                                                                                                               AuthenticationType.RMCPPlus ) );
                // node.setPowered(rd.isPowerOn());
                status = rd.isPowerOn();
                rd = null;
                logger.info( String.format( "Power Status is [ %s ] for node [ %s ]", ( status ? "ON" : "OFF" ),
                                            node.getNodeID() ) );
                return status;
            }
            catch ( IPMIException e )
            {
                logger.error( "Exception while executing the task PowerStatusServer: " + e.getCompletionCode() + ":"
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
