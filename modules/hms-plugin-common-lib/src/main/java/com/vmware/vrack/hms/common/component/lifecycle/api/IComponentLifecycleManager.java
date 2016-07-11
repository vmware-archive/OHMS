/* ********************************************************************************
 * IComponentLifecycleManager.java
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
package com.vmware.vrack.hms.common.component.lifecycle.api;

import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.api.IHmsComponentService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.component.lifecycle.resource.FileServerConfiguration;
import com.vmware.vrack.hms.common.component.lifecycle.resource.LifecycleOperationConfiguration;
import com.vmware.vrack.hms.common.component.lifecycle.resource.LifecycleOperationStatus;
import com.vmware.vrack.hms.common.component.lifecycle.resource.LowLevelComponent;
import com.vmware.vrack.hms.common.exception.HmsException;

/**
 * <code>IComponentLifecycleManager</code><br>
 * Every plugin service that uses IComponentLifecycleManager must annotate itself with HmsComponentUpgradePlugin
 * annotation.
 */
public interface IComponentLifecycleManager
    extends IHmsComponentService
{
    /**
     * Plugins MUST implement this method to initiate lifecycle operation.
     * <ol>
     * <li>Component to be upgraded.</li>
     * <li>Type of Lifecycle Operation (UPGRADE/DOWNGRADE etc.)</li>
     * <li>Binary file (absolute path).</li>
     * <li>SFTP Server configuration.</li>
     * <li>Connection details for connecting to Inband and OOB interfaces of the node.</li>
     * </ol>
     * <br>
     * Above configuration parameters will be provided to plugins for initiating lifecycle operation.
     *
     * @param lifecycleOperationConfiguration The lifecycle operation configuration
     * @return The lifecycle operation status
     */
    public LifecycleOperationStatus upgradeComponent( LifecycleOperationConfiguration lifecycleOperationConfiguration )
        throws HmsException;

    /**
     * Plugins MUST implement this method and return list of low level components that plugin can upgrade.
     *
     * @param serviceHmsNode the service hms node
     * @return The list of low level components that plugin can upgrade.
     */
    public List<LowLevelComponent> getUpgradeCapabilities( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Plugins MUST implement this method and return the status of an already initiated upgrade. HMS will pass on the
     * upgradeHandle that plugins returned while initiating an upgrade.
     *
     * @param handle The unique operation id that HMS passed to plugin at the time of initiating a low level component
     *            lifecycle operation.
     * @param serviceHmsNode the service hms node
     * @return The lifecycle operation status
     */
    public LifecycleOperationStatus getUpgradeStatus( String handle, ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Gets the current firmware version of the given low level component.
     *
     * @param lowLevelComponent the low level component
     * @param serviceHmsNode the service hms node
     * @return the current firmware version
     */
    public String getCurrentFirmwareVersion( LowLevelComponent lowLevelComponent, ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Sends File Server Configuration notification.
     * <p>
     * Implementation of this API must ensure that the sent file server information is cached and should be used while
     * initiating a component lifecycle operation.
     *
     * @param fileServerConfiguration the file server configuration
     */
    public void notifyFileServerConfiguration( FileServerConfiguration fileServerConfiguration )
        throws HmsException;
}
