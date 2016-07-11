/* ********************************************************************************
 * NodeDiscoveryUtil.java
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
package com.vmware.vrack.hms.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vmware.vrack.hms.common.notification.DiscoveryResult;
import com.vmware.vrack.hms.common.notification.NodeActionStatus;
import com.vmware.vrack.hms.common.notification.NodeDiscoveryResponse;
import com.vmware.vrack.hms.common.notification.NodeDiscoveryResult;
import com.vmware.vrack.hms.common.notification.NodeDiscoveryStatus;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;

/**
 * Utility class to facilitate Node Discovery
 * 
 * @author VMware Inc.
 */
public class NodeDiscoveryUtil
{
    /**
     * Node level discovery indicator. Just before starting discovery process for that particular node, it will be set
     * to "RUNNING". After the discovery process is completed, the status will be either "SUCCESS" or "ERROR".
     */
    public static Map<String, NodeActionStatus> hostDiscoveryMap = new HashMap<String, NodeActionStatus>();

    public static Map<String, NodeActionStatus> switchDiscoveryMap = new HashMap<String, NodeActionStatus>();

    /**
     * Returns current node discovery status to the caller.
     * 
     * @return
     */
    public static NodeDiscoveryResponse getNodeDiscoveryStatus()
    {
        NodeActionStatus overallStatus = NodeActionStatus.SUCCESS;
        NodeDiscoveryResponse discoveryStatus = new NodeDiscoveryResponse();
        // Result object that holds result for Host and switches
        DiscoveryResult overallResult = new DiscoveryResult();
        NodeDiscoveryResult hostResult = new NodeDiscoveryResult();
        NodeDiscoveryResult switchResult = new NodeDiscoveryResult();
        overallResult.setHosts( hostResult );
        overallResult.setSwitches( switchResult );
        for ( String nodeId : hostDiscoveryMap.keySet() )
        {
            if ( hostDiscoveryMap.get( nodeId ) == NodeActionStatus.SUCCESS
                || hostDiscoveryMap.get( nodeId ) == NodeActionStatus.FAILURE )
            {
                ServerNode node = (ServerNode) ServerNodeConnector.getInstance().nodeMap.get( nodeId );
                ServerNodePowerStatus powerStatus = new ServerNodePowerStatus();
                powerStatus.setPowered( node.isPowered() );
                powerStatus.setDiscoverable( node.isDiscoverable() );
                powerStatus.setOperationalStatus( node.getOperationalStatus() );
                NodeDiscoveryStatus nodeStatus = new NodeDiscoveryStatus();
                nodeStatus.setStatus( powerStatus );
                nodeStatus.setNodeId( nodeId );
                if ( hostResult.getCompleted() == null )
                {
                    hostResult.setCompleted( new ArrayList<NodeDiscoveryStatus>() );
                }
                hostResult.getCompleted().add( nodeStatus );
                // discoveredHosts.add(nodeId);
            }
            else if ( hostDiscoveryMap.get( nodeId ) == NodeActionStatus.RUNNING )
            {
                NodeDiscoveryStatus nodeStatus = new NodeDiscoveryStatus();
                nodeStatus.setStatus( new ServerNodePowerStatus() );
                nodeStatus.setNodeId( nodeId );
                // If the status is RUNNING, add it to pending list
                if ( hostResult.getInProgress() == null )
                {
                    hostResult.setInProgress( new ArrayList<NodeDiscoveryStatus>() );
                }
                hostResult.getInProgress().add( nodeStatus );
                overallStatus = NodeActionStatus.RUNNING;
            }
        }
        for ( String switchId : switchDiscoveryMap.keySet() )
        {
            if ( switchDiscoveryMap.get( switchId ) == NodeActionStatus.SUCCESS
                || switchDiscoveryMap.get( switchId ) == NodeActionStatus.FAILURE )
            {
                ServerNodePowerStatus powerStatus = new ServerNodePowerStatus();
                if ( switchDiscoveryMap.get( switchId ) == NodeActionStatus.SUCCESS )
                {
                    powerStatus.setPowered( true );
                    powerStatus.setDiscoverable( true );
                    powerStatus.setOperationalStatus( "true" );
                }
                NodeDiscoveryStatus nodeStatus = new NodeDiscoveryStatus();
                nodeStatus.setStatus( powerStatus );
                nodeStatus.setNodeId( switchId );
                if ( switchResult.getCompleted() == null )
                {
                    switchResult.setCompleted( new ArrayList<NodeDiscoveryStatus>() );
                }
                switchResult.getCompleted().add( nodeStatus );
            }
            else if ( switchDiscoveryMap.get( switchId ) == NodeActionStatus.RUNNING )
            {
                NodeDiscoveryStatus nodeStatus = new NodeDiscoveryStatus();
                nodeStatus.setStatus( new ServerNodePowerStatus() );
                nodeStatus.setNodeId( switchId );
                // If the Switch status is RUNNING, add it to pending list
                if ( switchResult.getInProgress() == null )
                {
                    switchResult.setInProgress( new ArrayList<NodeDiscoveryStatus>() );
                }
                switchResult.getInProgress().add( nodeStatus );
                overallStatus = NodeActionStatus.RUNNING;
            }
        }
        discoveryStatus.setDiscoveryStatus( overallStatus );
        discoveryStatus.setResult( overallResult );
        return discoveryStatus;
    }
}
