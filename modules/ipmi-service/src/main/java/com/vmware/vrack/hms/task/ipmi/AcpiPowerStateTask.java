/* ********************************************************************************
 * AcpiPowerStateTask.java
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
import com.vmware.vrack.coding.commands.application.GetAcpiPowerState;
import com.vmware.vrack.coding.commands.application.GetAcpiPowerStateResponseData;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * Returns the ACPI Power State of the System as well as Device Power State
 * 
 * @author Vmware
 */
public class AcpiPowerStateTask
{
    private static Logger logger = Logger.getLogger( AcpiPowerStateTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in ACPI Power State Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    public byte[] data = null;

    IpmiTaskConnector connector;

    public AcpiPowerStateTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
    }

    public AcpiPowerStateTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public AcpiPowerState executeTask()
        throws Exception, IpmiServiceResponseException
    {
        AcpiPowerState powerState = null;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request execute ipmi AcpipowerState task for Node " + node.getNodeID() );
            try
            {
                GetAcpiPowerStateResponseData rd =
                    (GetAcpiPowerStateResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                          new GetAcpiPowerState( IpmiVersion.V20,
                                                                                                                 connector.getCipherSuite(),
                                                                                                                 AuthenticationType.RMCPPlus,
                                                                                                                 IpmiCommandParameters.GET_ACPI_POWER_STATE_PARAM ) );
                powerState = new AcpiPowerState();
                powerState.setSystemAcpiPowerState( rd.getSystemAcpiPowerState().toString() );
                powerState.setDeviceAcpiPowerState( rd.getDeviceAcpiPowerState().toString() );
                rd = null;
                return powerState;
            }
            catch ( IPMIException e )
            {
                logger.error( "Exception while executing the task AcpiPowerState: " + e.getCompletionCode() + ":"
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
