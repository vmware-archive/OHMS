/* ********************************************************************************
 * GetSystemBootOptionsTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ipmi;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.vmware.vrack.coding.commands.BootOptionCommandParameters;
import com.vmware.vrack.coding.commands.chassis.GetSystemBootOptionsCommand;
import com.vmware.vrack.coding.commands.chassis.GetSystemBootOptionsCommandResponseData;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;
import com.vmware.vrack.hms.utils.IpmiUtil;

/**
 * Task to Get System Boot Options
 * 
 * @author Vmware
 */
public class GetSystemBootOptionsTask
{
    private static Logger logger = Logger.getLogger( GetSystemBootOptionsTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Get System Boot Options Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector;

    public GetSystemBootOptionsTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
    }

    public GetSystemBootOptionsTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public SystemBootOptions executeTask()
        throws Exception, IpmiServiceResponseException
    {
        SystemBootOptions systemBootOptions = null;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi GetSystemBootOptions task for Node " + node.getNodeID() );
            try
            {
                byte[] preparedParameter = BootOptionCommandParameters.GET_BOOT_FLAGS;
                GetSystemBootOptionsCommand getSystemBootOptions =
                    new GetSystemBootOptionsCommand( IpmiVersion.V20, connector.getCipherSuite(),
                                                     AuthenticationType.RMCPPlus, preparedParameter );
                GetSystemBootOptionsCommandResponseData data =
                    (GetSystemBootOptionsCommandResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                                    getSystemBootOptions );
                try
                {
                    systemBootOptions =
                        IpmiUtil.getSystemBootOptionsFromGetSystemBootOptionsCommandResponseData( data );
                }
                catch ( Exception e )
                {
                    String err =
                        String.format( "Error extracting system Boot Options data from IPMI get Boot Options command response data, for node [ %s ]",
                                       node.getNodeID() );
                    logger.error( err, e );
                    throw e;
                }
                return systemBootOptions;
            }
            catch ( IPMIException e )
            {
                logger.error( "Exception while executing the task GetSystemBootOptions: " + e.getCompletionCode() + ":"
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
