/* ********************************************************************************
 * ChassisIdentifyTask.java
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
import com.vmware.vrack.coding.commands.chassis.ChassisIdentifyCommand;
import com.vmware.vrack.coding.commands.chassis.ChassisIdentifyCommandResponseData;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;
import com.vmware.vrack.hms.utils.IpmiUtil;

/**
 * Task to Identify Chassis via some kind of mechanism(i.e flashing lights, sounds, front panel LEDs).
 * 
 * @author Vmware
 */
public class ChassisIdentifyTask
{
    private static Logger logger = Logger.getLogger( ChassisIdentifyTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Chassis Identify Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector;

    private ChassisIdentifyOptions chassisIdentifyOptions;

    public ChassisIdentifyTask( ServiceHmsNode node, IpmiTaskConnector connector,
                                ChassisIdentifyOptions chassisIdentifyOptions )
    {
        this( node, chassisIdentifyOptions );
        this.connector = connector;
    }

    public ChassisIdentifyTask( ServiceHmsNode node, ChassisIdentifyOptions chassisIdentifyOptions )
    {
        this.node = (ServiceServerNode) node;
        this.chassisIdentifyOptions = chassisIdentifyOptions;
    }

    public boolean executeTask()
        throws Exception, IpmiServiceResponseException
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request execute ipmi ChassisIdentify task for Node " + node.getNodeID() );
            try
            {
                connector.getConnector().sendMessage( connector.getHandle(),
                                                      new SetSessionPrivilegeLevel( IpmiVersion.V20,
                                                                                    connector.getCipherSuite(),
                                                                                    AuthenticationType.RMCPPlus,
                                                                                    PrivilegeLevel.Administrator ) );
                // Sample Byte parameter looks like following
                // byte[] byteParameter = {0x05, 0x00};
                byte[] byteParameter = IpmiUtil.getByteArrayFromChassisIdentifyOptions( chassisIdentifyOptions );
                ChassisIdentifyCommand chassisIdentifyCommand =
                    new ChassisIdentifyCommand( IpmiVersion.V20, connector.getCipherSuite(),
                                                AuthenticationType.RMCPPlus, byteParameter );
                ChassisIdentifyCommandResponseData data =
                    (ChassisIdentifyCommandResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                               chassisIdentifyCommand );
                data = null;
                return true;
            }
            catch ( IPMIException e )
            {
                logger.error( "Exception while executing the task ChassisIdentify: " + e.getCompletionCode() + ":"
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
