/* ********************************************************************************
 * ServiceState.java
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
