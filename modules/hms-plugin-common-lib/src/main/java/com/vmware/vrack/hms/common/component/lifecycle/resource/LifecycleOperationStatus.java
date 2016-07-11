/* ********************************************************************************
 * LifecycleOperationStatus.java
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
package com.vmware.vrack.hms.common.component.lifecycle.resource;

/**
 * <code>LifecycleOperationStatus</code> is ... <br>
 */
public class LifecycleOperationStatus
{
    /** The operation status. */
    private final OperationStatus operationStatus;

    /** The message. */
    private final String message;

    /**
     * Instantiates a new lifecycle operation status.
     *
     * @param operationStatus the operation status
     * @param message the message
     */
    public LifecycleOperationStatus( OperationStatus operationStatus, String message )
    {
        this.operationStatus = operationStatus;
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
}
