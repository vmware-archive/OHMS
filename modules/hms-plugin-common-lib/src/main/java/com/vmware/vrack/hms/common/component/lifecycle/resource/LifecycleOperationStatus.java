/* ********************************************************************************
 * LifecycleOperationStatus.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
