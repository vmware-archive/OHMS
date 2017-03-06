/* ********************************************************************************
 * ServerDataChangeMessage.java
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

import org.springframework.context.ApplicationEvent;

import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;

/**
 * HMS Server Data Change Massager
 */
public class ServerDataChangeMessage
    extends ApplicationEvent
{

    private ServerInfo serverInfo;

    private ServerComponent component;

    /**
     * @param source
     */
    public ServerDataChangeMessage( ServerInfo source )
    {
        super( source );
        this.setServerInfo( source );
    }

    /**
     * @param source
     * @param component
     */
    public ServerDataChangeMessage( ServerInfo source, ServerComponent component )
    {
        super( source );
        this.setServerInfo( source );
        this.component = component;
    }

    /**
     * Get Server data Info
     * 
     * @return serverInfo
     */
    public ServerInfo getServerInfo()
    {
        return serverInfo;
    }

    /**
     * Set the Server Info
     * 
     * @param serverInfo
     */
    public void setServerInfo( ServerInfo serverInfo )
    {
        this.serverInfo = serverInfo;
    }

    /**
     * Get the server component
     *
     * @return ServerComponent
     */
    public ServerComponent getComponent()
    {
        return component;
    }

    /**
     * Set the server component
     * 
     * @param component
     */
    public void setComponent( ServerComponent component )
    {
        this.component = component;
    }

}
