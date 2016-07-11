/* ********************************************************************************
 * SensorHelper.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vmware.vrack.hms.common.boardvendorservice.api.helper.parsers.IpmiSensorParser;
import com.vmware.vrack.hms.ipmiservice.IpmiService;
import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.EntityId;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

/**
 * Helper class to fetch CPU Sensor Information
 *
 * @author VMware Inc.
 */
public class SensorHelper
{
    private static Logger logger = Logger.getLogger( SensorHelper.class );

    /**
     * Method to get Sensor Events
     *
     * @param serviceNode
     * @param typeList
     * @param boardEntityList
     * @param ipmiServiceExecutor
     * @return
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getSensorEvents( ServiceHmsNode serviceNode, List<SensorType> typeList,
                                                              List<EntityId> boardEntityList,
                                                              IpmiService ipmiServiceExecutor )
                                                                  throws HmsException
    {
        if ( serviceNode != null && serviceNode instanceof ServiceServerNode )
        {
            ServiceServerNode node = (ServiceServerNode) serviceNode;
            List<ServerComponentEvent> serverComponentSensor = new ArrayList<>();
            List<Map<String, String>> sensorData = new ArrayList<>();
            IpmiSensorParser serverComponentSensorParsers = new IpmiSensorParser();
            try
            {
                // get Sensor Data for all processor entity
                sensorData = ipmiServiceExecutor.getSensorDataForSensorTypeAndEntity( node, typeList, boardEntityList );
                for ( Map<String, String> data : sensorData )
                    logger.debug( data.toString() );
                // covert sensorData to ServerComponentEvent
                serverComponentSensor = serverComponentSensorParsers.getServerComponentSensor( sensorData );
                return serverComponentSensor;
            }
            catch ( Exception e )
            {
                logger.error( "Cannot get  sensor Info", e );
                throw new HmsException( "Unable to get  Sensor information", e );
            }
        }
        else
        {
            logger.error( "Error in get Sensor Events - node is null or invalid." );
            throw new HmsException( "Node is Null or invalid" );
        }
    }
}
