/* ********************************************************************************
 * NodeAdminStatus.java
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
