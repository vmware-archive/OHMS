/* ********************************************************************************
 * MemoryInfoHelper.java
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
package com.vmware.vrack.hms.boardservice.ib.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vim.binding.vim.host.HardwareInfo;
import com.vmware.vrack.hms.boardservice.ib.InbandServiceImpl;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;

/**
 * Helper Class for Memory Info
 * 
 * @author Yagnesh Chawda
 */
public class MemoryInfoHelper
{
    private static Logger logger = Logger.getLogger( MemoryInfoHelper.class );

    public static List<PhysicalMemory> getMemoryInfo( HardwareInfo hardwareInfo )
        throws Exception
    {
        if ( hardwareInfo != null )
        {
            List<PhysicalMemory> physicalMemoryList = new ArrayList<>();
            PhysicalMemory memory = new PhysicalMemory();
            memory.setId( "DIMM_AGG" );
            // memory.setBankLabel("Aggregated");
            memory.setCapacityInBytes( BigInteger.valueOf( hardwareInfo.getMemorySize() ) );
            physicalMemoryList.add( memory );
            return physicalMemoryList;
        }
        else
        {
            throw new Exception( "Can not get Memory Info because the Hardware info object is NULL" );
        }
    }

    /**
     * Returns Memory specific sensor data for the node
     * 
     * @param serviceNode
     * @param component
     * @param inbandServiceImpl
     * @return
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getMemorySensor( ServiceHmsNode serviceNode, ServerComponent component,
                                                              InbandServiceImpl inbandServiceImpl )
                                                                  throws HmsException
    {
        List<PhysicalMemory> memories = null;
        List<ServerComponentEvent> componentSensors = new ArrayList<ServerComponentEvent>();
        try
        {
            if ( inbandServiceImpl != null )
            {
                memories = inbandServiceImpl.getSystemMemoryInfo( serviceNode );
            }
            else
            {
                String err = "Error while getting System Memory info because InbandServiceImpl object was found NULL";
                logger.error( err );
                throw new HmsException( err );
            }
        }
        catch ( HmsException e )
        {
            logger.error( "Unable to get System Memory Info List while trying to get Memory Sensors for node ["
                + serviceNode != null ? serviceNode.getNodeID() : serviceNode + "]" );
            throw e;
        }
        // Currently this memory sensor is used to generate HOST_OS_NOT_RESPONSIVE events.
        // if this is returning null or empty List, it means Node Inband is properly operating, otherwise it would have
        // thrown HmsException.
        return componentSensors;
    }
}
