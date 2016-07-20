/* ********************************************************************************
 * SensorUtil.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.EntityId;
import com.vmware.vrack.hms.common.resource.fru.SensorType;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * Utility class to retrieve specific Sensor Data
 * 
 * @author Yagnesh Chawda
 */
public class SensorUtil
{
    private static Logger logger = Logger.getLogger( SensorUtil.class );

    // public synchronized static List<Map<String, String>> getSpecificSensorData(List<Map<String,String>> sensorData ,
    // EntityId entityId, SensorType sensorType) throws Exception
    public synchronized static List<Map<String, String>> getSpecificSensorData( List<Map<String, String>> sensorData,
                                                                                EntityId entityId,
                                                                                SensorType sensorType )
                                                                                    throws Exception
    {
        List<Map<String, String>> filteredSensorDataList = new ArrayList<Map<String, String>>();
        try
        {
            if ( sensorData != null )
            {
                for ( int i = 0; i < sensorData.size(); i++ )
                {
                    try
                    {
                        Map<String, String> sensorInstance = sensorData.get( i );
                        if ( entityId == null
                            || ( entityId != null && entityId.toString().equals( sensorInstance.get( "entityId" ) ) ) )
                        {
                            if ( sensorType == null || ( sensorType != null
                                && sensorType.toString().equals( sensorInstance.get( "sensorType" ) ) ) )
                            {
                                filteredSensorDataList.add( sensorInstance );
                            }
                        }
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Exception while getting filtered Sensor data :", e );
                    }
                }
                return filteredSensorDataList;
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting Filterd Sensor List", e );
            throw new HmsException( "Error while getting Filterd Sensor List", e );
        }
        return null;
    }
}
