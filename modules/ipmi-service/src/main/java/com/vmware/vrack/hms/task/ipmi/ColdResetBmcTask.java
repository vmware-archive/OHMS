/* ********************************************************************************
 * ColdResetBmcTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ipmi;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.vmware.vrack.coding.commands.IpmiCommandParameters;
import com.vmware.vrack.coding.commands.application.ColdResetCommand;
import com.vmware.vrack.coding.commands.application.ColdResetCommandResponseData;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * Cold Resets the BMC node.
 * 
 * @author Vmware
 */
public class ColdResetBmcTask
{
    private static Logger logger = Logger.getLogger( ColdResetBmcTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Cold Reset BMC Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector;

    public ColdResetBmcTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
    }

    public ColdResetBmcTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public boolean executeTask()
        throws Exception, IpmiServiceResponseException
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi ColdResetBmc task for Node " + node.getNodeID() );
            try
            {
                ColdResetCommandResponseData rd =
                    (ColdResetCommandResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                         new ColdResetCommand( IpmiVersion.V20,
                                                                                                               connector.getCipherSuite(),
                                                                                                               AuthenticationType.RMCPPlus,
                                                                                                               IpmiCommandParameters.COLD_RESET_PARAM ) );
                rd = null;
                return true;
            }
            catch ( IPMIException e )
            {
                logger.error( "Exception while executing the task ColdResetBmc: " + e.getCompletionCode() + ":"
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
