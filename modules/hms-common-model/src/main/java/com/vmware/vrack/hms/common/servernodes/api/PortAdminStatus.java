/* ********************************************************************************
 * PortAdminStatus.java
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
package com.vmware.vrack.hms.common.servernodes.api;

/**
 * Enum to define possible Admin state of a PORT (for NIC or Switch)
 *
 * @author VMware Inc.
 */
public enum PortAdminStatus
{

    USED( "Port ID: {port_id} is USED." ),
    UNUSED( "Port ID: {port_id} is UNUSED." ),
    FAULTY( "Port ID: {port_id} is FAULTY." ),
    DOWN( "Port ID: {port_id} is DOWN." ),
    UNKNOWN( "Port ID: {port_id} is UNKNOWN." );

    String message;

    private PortAdminStatus( String message )
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String getMessage( String portId )
    {
        return this.message.replace( "{port_id}", portId );
    }
}