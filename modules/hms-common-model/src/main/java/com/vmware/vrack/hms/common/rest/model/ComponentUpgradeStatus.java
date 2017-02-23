/* ********************************************************************************
 * ComponentUpgradeStatus.java
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

package com.vmware.vrack.hms.common.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.component.lifecycle.resource.OperationStatus;

/**
 * <code>ComponentUpgradeStatus</code> <br>
 * Purpose of this class is to send an upgrade status to HMS Aggregator by OOB Agent.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ComponentUpgradeStatus
{
    /** The operation status. */
    private OperationStatus operationStatus;

    /** The message. */
    private String message;

    /** The upgrade handle. */
    private String upgradeHandle;

    /**
     * Instantiates a new component upgrade status.
     */
    public ComponentUpgradeStatus()
    {
    }

    /**
     * Instantiates a new component upgrade status.
     *
     * @param upgradeHandle the upgrade handle
     * @param operationStatus the operation status
     * @param message the message
     */
    public ComponentUpgradeStatus( String upgradeHandle, OperationStatus operationStatus, String message )
    {
        this.operationStatus = operationStatus;
        this.upgradeHandle = upgradeHandle;
        this.message = message;
    }

    /**
     * Gets the operation status.
     *
     * @return the operation status
     */
    public OperationStatus getOperationStatus()
    {
        return operationStatus;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Gets the upgrade handle.
     *
     * @return the upgrade handle
     */
    public String getUpgradeHandle()
    {
        return upgradeHandle;
    }

    /**
     * Sets the operation status.
     *
     * @param operationStatus the new operation status
     */
    public void setOperationStatus( OperationStatus operationStatus )
    {
        this.operationStatus = operationStatus;
    }

    /**
     * Sets the message.
     *
     * @param message the new message
     */
    public void setMessage( String message )
    {
        this.message = message;
    }

    /**
     * Sets the upgrade handle.
     *
     * @param upgradeHandle the new upgrade handle
     */
    public void setUpgradeHandle( String upgradeHandle )
    {
        this.upgradeHandle = upgradeHandle;
    }
}
