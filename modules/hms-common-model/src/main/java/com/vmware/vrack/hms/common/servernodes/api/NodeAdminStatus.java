/* ********************************************************************************
 * NodeAdminStatus.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api;

/**
 * @author sgakhar Enum to define possible Admin state of a node example: maintenance, decomission
 */
public enum NodeAdminStatus
{
    OPERATIONAL( "Node ID: {node_id} is operational." ),
    MAINTENANCE( "Node ID: {node_id} is under maintenance." ),
    DECOMISSION( "Node ID: {node_id} is decomissioned." ),
    MAINTENANCE_ERROR( "Node ID: {node_id} is in error state after Maintenance." );
    String message;

    private NodeAdminStatus( String message )
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String getMessage( String node_id )
    {
        return this.message.replace( "{node_id}", node_id );
    }
}
