/* ********************************************************************************
 * FruDataChangeMessage.java
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

import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.vmware.vrack.hms.common.rest.model.FruComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;

/**
 * HMS FRU Data Change Massager
 */
public class FruDataChangeMessage
    extends ApplicationEvent
{

    private List<FruComponent> fruComponent;

    private ServerComponent component;

    private String nodeID;

    /**
     * @param source
     * @param component
     */
    public FruDataChangeMessage( List<FruComponent> source, ServerComponent component )
    {
        super( source );
        this.setFruComponent( source );
        this.setComponent( component );
    }

    /**
     * @param source
     * @param component
     * @param nodeID
     */
    public FruDataChangeMessage( List<FruComponent> source, String nodeID, ServerComponent component )
    {
        super( source );
        this.setFruComponent( source );
        this.setNodeID( nodeID );
        this.setComponent( component );
    }

    /**
     * Get FRU Information
     *
     * @return List<FruComponent>
     */
    public List<FruComponent> getFruComponent()
    {
        return fruComponent;
    }

    /**
     * Set FRU information
     *
     * @param fruComponent
     */
    public void setFruComponent( List<FruComponent> fruComponent )
    {
        this.fruComponent = fruComponent;
    }

    /**
     * Get Server component
     *
     * @return ServerComponent
     */
    public ServerComponent getComponent()
    {
        return component;
    }

    /**
     * Set Server component
     *
     * @param component
     */
    public void setComponent( ServerComponent component )
    {
        this.component = component;
    }

    /**
     * Get Node ID
     *
     * @return nodeID
     */
    public String getNodeID()
    {
        return nodeID;
    }

    /**
     * Set Node ID
     *
     * @param nodeID
     */
    public void setNodeID( String nodeID )
    {
        this.nodeID = nodeID;
    }

}
