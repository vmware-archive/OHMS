/* ********************************************************************************
 * LifecycleOperationConfiguration.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.component.lifecycle.resource;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;

/**
 * <code>LifecycleOperationConfiguration</code> is a class for holding the required information to be passed onto
 * plugins for carrying out lifecycle operation. It provides below information to plugins for carrying out lifecycle
 * operation.
 * <ol>
 * <li>Type of Lifecycle operation, such as UPGRADE/DOWNGRADE etc.</li>
 * <li>Low Level Component.</li>
 * <li>Universally unique id to track Lifecycle operation status.</li>
 * <li>Firmware/Binary file absolute path.</li>
 * <li>Connection details for connecting to Inband and OOB interfaces of the node.</li>
 * <li>SFTP Connection details.</li>
 * </ol>
 * <br>
 */
public class LifecycleOperationConfiguration
{
    /**
     * The low level component.
     */
    private LowLevelComponent lowLevelComponent;

    /**
     * The lifecycle operation.
     */
    private LifecycleOperation lifecycleOperation;

    /**
     * Absolute path of the binary file.
     */
    private String file;

    /**
     * Configuration for connecting to Management console of the node.
     */
    private ServiceHmsNode serviceHmsNode;

    /**
     * Unique id to monitor Lifecycle operation status.
     */
    private String operationId;

    /** The vendor. */
    private String vendor;

    /**
     * Gets the service hms node.
     *
     * @return the service hms node
     */
    public ServiceHmsNode getServiceHmsNode()
    {
        return serviceHmsNode;
    }

    /**
     * Sets the service hms node.
     *
     * @param serviceHmsNode the new service hms node
     */
    public void setServiceHmsNode( ServiceHmsNode serviceHmsNode )
    {
        this.serviceHmsNode = serviceHmsNode;
    }

    /**
     * Gets the file.
     *
     * @return the file
     */
    public String getFile()
    {
        return file;
    }

    /**
     * Sets the file.
     *
     * @param file the new file
     */
    public void setFile( String file )
    {
        this.file = file;
    }

    /**
     * Gets the low level component.
     *
     * @return the low level component
     */
    public LowLevelComponent getLowLevelComponent()
    {
        return lowLevelComponent;
    }

    /**
     * Sets the low level component.
     *
     * @param lowLevelComponent the new low level component
     */
    public void setLowLevelComponent( LowLevelComponent lowLevelComponent )
    {
        this.lowLevelComponent = lowLevelComponent;
    }

    /**
     * Gets the lifecycle operation.
     *
     * @return the lifecycle operation
     */
    public LifecycleOperation getLifecycleOperation()
    {
        return lifecycleOperation;
    }

    /**
     * Sets the lifecycle operation.
     *
     * @param lifecycleOperation the new lifecycle operation
     */
    public void setLifecycleOperation( LifecycleOperation lifecycleOperation )
    {
        this.lifecycleOperation = lifecycleOperation;
    }

    /**
     * Gets the operation id.
     *
     * @return the operation id.
     */
    public String getOperationId()
    {
        return operationId;
    }

    /**
     * Sets the operation handle.
     *
     * @param operationId the new operation id
     */
    public void setOperationId( String operationId )
    {
        this.operationId = operationId;
    }

    /**
     * Gets the vendor.
     *
     * @return the vendor
     */
    public String getVendor()
    {
        return vendor;
    }

    /**
     * Sets the vendor.
     *
     * @param vendor the new vendor
     */
    public void setVendor( String vendor )
    {
        this.vendor = vendor;
    }
}
