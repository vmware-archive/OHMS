/* ********************************************************************************
 * ServiceState.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.service;

/**
 * <code>ServiceState</code> is an enum that defines the various states of the service like RUNNING, MAINTENANCE etc.
 * <br>
 *
 * @author VMware Inc.
 */
public enum ServiceState
{
    /** Service State RUNNING. */
    RUNNING( "RUNNING" ), /** Service State NORMAL_MAINTENANCE. */
    NORMAL_MAINTENANCE( "NORMAL_MAINTENANCE" ), /** Service State FORCE_MAINTENANCE. */
    FORCE_MAINTENANCE( "FORCE_MAINTENANCE" ),;
    /** The state message. */
    private String stateMessage;

    /**
     * Instantiates a new service status.
     *
     * @param stateMessage the state message
     */
    private ServiceState( String stateMessage )
    {
        this.stateMessage = stateMessage;
    }

    /**
     * Gets the service state.
     *
     * @return the service state
     */
    public String getServiceState()
    {
        return this.stateMessage;
    }
}
