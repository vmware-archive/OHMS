/* ********************************************************************************
 * InventoryLoader.java
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
package com.vmware.vrack.hms.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vmware.vrack.hms.aggregator.util.InventoryUtil;
import com.vmware.vrack.hms.common.configuration.ServerItem;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.Constants;

@Component
public class InventoryLoader
{
    private static volatile InventoryLoader instance;

    private String inventoryFilePath;

    private Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();

    private Map<String, SwitchNode> switchNodeMap = new HashMap<String, SwitchNode>();

    private Map<String, List<HmsApi>> nodeComponentSupportedOob = new HashMap<String, List<HmsApi>>();

    private ServerNode applicationNode = new ServerNode( "HMS_IB_AGENT", null, null, null );

    @Value( "${hms.switch.host}" )
    private String hmsIpAddr;

    @Value( "${hms.switch.port}" )
    private int hmsPort;

    public Map<String, List<HmsApi>> getNodeComponentSupportedOob()
    {
        return nodeComponentSupportedOob;
    }

    public void setNodeComponentSupportedOob( Map<String, List<HmsApi>> nodeComponentSupportedOob )
    {
        this.nodeComponentSupportedOob = nodeComponentSupportedOob;
    }

    private InventoryLoader()
    {
    };

    public static InventoryLoader getInstance()
    {
        if ( instance == null )
            instance = new InventoryLoader();
        return instance;
    }

    public String getInventoryFilePath()
    {
        return inventoryFilePath;
    }

    public void setInventoryFilePath( String inventoryFilePath )
    {
        this.inventoryFilePath = inventoryFilePath;
    }

    public Map<String, ServerNode> getNodeMap()
    {
        return nodeMap;
    }

    public Map<String, ServerItem> getServerItemNodeMap()
    {
        Map<String, ServerItem> serverItemNodeMap = new HashMap<String, ServerItem>();
        if ( nodeMap == null )
            return null;
        for ( ServerNode node : nodeMap.values() )
        {
            serverItemNodeMap.put( node.getNodeID(), node.getServerItemObject() );
        }
        return serverItemNodeMap;
    }

    public void setNodeMap( Map<String, ServerNode> nodeMap )
    {
        this.nodeMap = nodeMap;
    }

    public ServerNode getNode( String node_id )
    {
        return nodeMap.get( node_id );
    }

    public ServerNode getApplicationNode()
    {
        return applicationNode;
    }

    public void setApplicationNode( ServerNode applicationNode )
    {
        this.applicationNode = applicationNode;
    }

    public List<HmsApi> getOOBSupportedServerComponents( String node_id )
    {
        validateSupportedOperations( node_id );
        return nodeComponentSupportedOob.get( node_id );
    }

    public boolean isServerComponentAvailableOOB( String node_id, HmsApi componnet )
    {
        validateSupportedOperations( node_id );
        if ( nodeComponentSupportedOob.containsKey( node_id ) )
            return nodeComponentSupportedOob.get( node_id ).contains( componnet );
        else
            return false;
    }

    public void addOOBSupportedServerComponents( String node_id, List<HmsApi> componnet )
    {
        nodeComponentSupportedOob.put( node_id, componnet );
    }

    private void validateSupportedOperations( String node_id )
    {
        try
        {
            if ( !nodeComponentSupportedOob.containsKey( node_id ) && nodeMap.containsKey( node_id ) )
                nodeComponentSupportedOob = InventoryUtil.getOOBSupportedOperations( hmsIpAddr, hmsPort,
                                                                                     Constants.HMS_OOB_SUPPORTED_OPEARIONS_ENDPOINT,
                                                                                     "application/json" );
        }
        catch ( HmsException e )
        {
        }
    }

    public Map<String, SwitchNode> getSwitchNodeMap()
    {
        return switchNodeMap;
    }

    public void setSwitchNodeMap( Map<String, SwitchNode> switchNodeMap )
    {
        this.switchNodeMap = switchNodeMap;
    }

    /**
     * Gets the Management Switch IP Address.
     *
     * @return the management switch IP address.
     */
    public String getHmsIpAddr()
    {
        return this.hmsIpAddr;
    }
}
