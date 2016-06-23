/* ********************************************************************************
 * SelfTestTask.java
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
import com.vmware.vrack.coding.commands.application.GetSelfTestResults;
import com.vmware.vrack.coding.commands.application.GetSelfTestResultsResponseData;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * Performs Self Test and returns the Status
 * 
 * @author Vmware
 */
public class SelfTestTask
{
    private static Logger logger = Logger.getLogger( SelfTestTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in Self Test Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector;

    public SelfTestTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
    }

    public SelfTestTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public SelfTestResults executeTask()
        throws Exception, IpmiServiceResponseException
    {
        SelfTestResults results = null;
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi SelfTest task for Node " + node.getNodeID() );
            try
            {
                GetSelfTestResultsResponseData rd =
                    (GetSelfTestResultsResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                           new GetSelfTestResults( IpmiVersion.V20,
                                                                                                                   connector.getCipherSuite(),
                                                                                                                   AuthenticationType.RMCPPlus,
                                                                                                                   IpmiCommandParameters.GET_SELF_TEST_RESULTS_PARAM ) );
                results = new SelfTestResults();
                results.setSelfTestResultCode( rd.getSelfTestResultCode() );
                if ( rd.isAllTestPassed() )
                {
                    results.setSelfTestResultFailureCode( rd.getSelfTestFailureCode() );
                    results.setErrors( null );
                }
                else
                {
                    results.setSelfTestResultFailureCode( rd.getSelfTestFailureCode() );
                    results.setErrors( rd.getErrors() );
                }
                return results;
            }
            catch ( IPMIException e )
            {
                logger.error( "Exception while executing the task SelfTest: " + e.getCompletionCode() + ":"
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
