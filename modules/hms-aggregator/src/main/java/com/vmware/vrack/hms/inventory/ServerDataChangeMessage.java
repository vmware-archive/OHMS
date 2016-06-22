/* ********************************************************************************
 * ServerDataChangeMessage.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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

    ServerComponent component;

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
