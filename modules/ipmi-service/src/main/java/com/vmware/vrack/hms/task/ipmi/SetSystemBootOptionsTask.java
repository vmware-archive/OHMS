/* ********************************************************************************
 * SetSystemBootOptionsTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ipmi;

import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.commands.session.SetSessionPrivilegeLevel;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.vmware.vrack.coding.commands.chassis.SetSystemBootOptionsCommand;
import com.vmware.vrack.coding.commands.chassis.SetSystemBootOptionsCommandResponseData;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;
import com.vmware.vrack.hms.utils.IpmiUtil;

/**
 * Task to Set System Boot Options
 * 
 * @author Vmware
 */
public class SetSystemBootOptionsTask
{
    private static Logger logger = Logger.getLogger( SetSystemBootOptionsTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Set System Boot Options Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    private SystemBootOptions bootOptions;

    IpmiTaskConnector connector;

    public SetSystemBootOptionsTask( ServiceHmsNode node, IpmiTaskConnector connector, SystemBootOptions bootOptions )
    {
        this( node, bootOptions );
        this.connector = connector;
    }

    public SetSystemBootOptionsTask( ServiceHmsNode node, SystemBootOptions bootOptions )
    {
        this.node = (ServiceServerNode) node;
        this.bootOptions = bootOptions;
    }

    public boolean executeTask()
        throws Exception, IpmiServiceResponseException
    {
        if ( node instanceof ServiceServerNode && connector != null && bootOptions != null )
        {
            logger.debug( "Received request to execute ipmi SetSystemBootOptions task for Node" + node.getNodeID() );
            try
            {
                connector.getConnector().sendMessage( connector.getHandle(),
                                                      new SetSessionPrivilegeLevel( IpmiVersion.V20,
                                                                                    connector.getCipherSuite(),
                                                                                    AuthenticationType.RMCPPlus,
                                                                                    PrivilegeLevel.Administrator ) );
                // Sample Byte parameter looks like following
                // byte[] byteParameter = {0x05, 0x00, 0x04, 0x00, 0x00, 0x00};
                byte[] byteParameter = IpmiUtil.getByteArrayFromSystemBootOptions( bootOptions );
                SetSystemBootOptionsCommand setSystemBootOptions =
                    new SetSystemBootOptionsCommand( IpmiVersion.V20, connector.getCipherSuite(),
                                                     AuthenticationType.RMCPPlus, byteParameter );
                SetSystemBootOptionsCommandResponseData data =
                    (SetSystemBootOptionsCommandResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                                    setSystemBootOptions );
                data = null;
                return true;
            }
            catch ( IPMIException e )
            {
                logger.error( "Exception while executing the task SetSystemBootOptions: " + e.getCompletionCode() + ":"
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
