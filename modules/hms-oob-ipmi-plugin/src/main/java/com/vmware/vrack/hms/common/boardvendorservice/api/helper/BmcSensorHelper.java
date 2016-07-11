/* ********************************************************************************
 * BmcSensorHelper.java
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
package com.vmware.vrack.hms.common.boardvendorservice.api.helper;

import com.veraxsystems.vxipmi.api.async.ConnectionHandle;
import com.veraxsystems.vxipmi.api.sync.IpmiConnector;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.ipmiservice.IpmiServiceExecutor;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper Class for Dell board BMC event generation
 * 
 * @author VMware Inc.
 */
public class BmcSensorHelper
{
    private static Logger logger = Logger.getLogger( BmcSensorHelper.class );

    /**
     * Method to generate the BMC events
     * 
     * @param serviceNode
     * @return List<ServerComponentEvent>
     * @throws com.vmware.vrack.hms.common.exception.HmsException
     */
    public static List<ServerComponentEvent> getBmcSensor( ServiceHmsNode serviceNode )
        throws HmsException
    {
        if ( serviceNode != null && serviceNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceNode;
            List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
            ServerComponentEvent serverComponentSensorTemp = null;
            IpmiServiceExecutor ipmiServiceExecutor = new IpmiServiceExecutor();
            try
            {
                if ( checkRMCPPing( node.getManagementIp() ) )
                {
                    serverComponentSensorTemp = new ServerComponentEvent();
                    serverComponentSensorTemp.setEventId( "BMC STATUS" );
                    serverComponentSensorTemp.setEventName( NodeEvent.BMC_STATUS );
                    serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
                    serverComponentSensorTemp.setComponentId( node.getManagementIp() );
                    serverComponentSensorTemp.setDiscreteValue( "BMC reachable" );
                    serverComponentSensor.add( serverComponentSensorTemp );
                    boolean status = ipmiServiceExecutor.isHostAvailable( node );
                    if ( status != true )
                    {
                        serverComponentSensorTemp = new ServerComponentEvent();
                        serverComponentSensorTemp.setEventId( "BMC AUTHENTICATION" );
                        serverComponentSensorTemp.setEventName( NodeEvent.BMC_AUTHENTICATION_FAILURE );
                        serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
                        serverComponentSensorTemp.setComponentId( node.getManagementIp() );
                        serverComponentSensorTemp.setDiscreteValue( "BMC Authentication Failed" );
                        serverComponentSensor.add( serverComponentSensorTemp );
                    }
                    else
                    {
                        serverComponentSensorTemp = new ServerComponentEvent();
                        serverComponentSensorTemp.setEventId( "BMC AUTHENTICATION" );
                        serverComponentSensorTemp.setEventName( NodeEvent.BMC_STATUS );
                        serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
                        serverComponentSensorTemp.setComponentId( node.getManagementIp() );
                        serverComponentSensorTemp.setDiscreteValue( "BMC Authentication Success" );
                        serverComponentSensor.add( serverComponentSensorTemp );
                        ChassisIdentifyOptions data = new ChassisIdentifyOptions();
                        if ( ipmiServiceExecutor.performChassisIdentification( node, data ) != true )
                        {
                            serverComponentSensorTemp = new ServerComponentEvent();
                            serverComponentSensorTemp.setEventId( "BMC FAILURE" );
                            serverComponentSensorTemp.setEventName( NodeEvent.BMC_FAILURE );
                            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
                            serverComponentSensorTemp.setComponentId( node.getManagementIp() );
                            serverComponentSensorTemp.setDiscreteValue( "BMC Management Failure" );
                            serverComponentSensor.add( serverComponentSensorTemp );
                        }
                        else
                        {
                            serverComponentSensorTemp = new ServerComponentEvent();
                            serverComponentSensorTemp.setEventId( "BMC MANAGEMENT STATUS" );
                            serverComponentSensorTemp.setEventName( NodeEvent.BMC_STATUS );
                            serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
                            serverComponentSensorTemp.setComponentId( node.getManagementIp() );
                            serverComponentSensorTemp.setDiscreteValue( "BMC Management Success" );
                            serverComponentSensor.add( serverComponentSensorTemp );
                        }
                    }
                }
                else
                {
                    serverComponentSensorTemp = new ServerComponentEvent();
                    serverComponentSensorTemp.setEventId( "BMC STATUS" );
                    serverComponentSensorTemp.setEventName( NodeEvent.BMC_NOT_REACHABLE );
                    serverComponentSensorTemp.setUnit( EventUnitType.DISCRETE );
                    serverComponentSensorTemp.setComponentId( node.getManagementIp() );
                    serverComponentSensorTemp.setDiscreteValue( "BMC not reachable" );
                    serverComponentSensor.add( serverComponentSensorTemp );
                }
                return serverComponentSensor;
            }
            catch ( Exception e )
            {
                logger.error( "Cannot get BMC sensor Info", e );
                throw new HmsException( "Unable to BMC CPU Sensor information", e );
            }
        }
        else
        {
            throw new HmsException( "Node is Null or invalid" );
        }
    }

    public static boolean checkRMCPPing( String ipAddress )
        throws Exception
    {
        IpmiConnector connector;
        ConnectionHandle handle;
        final int PORT = 0;
        try
        {
            connector = new IpmiConnector( PORT );
            handle = connector.createConnection( InetAddress.getByName( ipAddress ) );
            if ( handle != null )
            {
                return true;
            }
        }
        catch ( Exception e )
        {
            logger.error( "BMC not reachable", e );
            throw e;
        }
        return false;
    }
}
