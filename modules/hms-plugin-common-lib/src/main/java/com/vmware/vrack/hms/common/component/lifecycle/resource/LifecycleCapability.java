/* ********************************************************************************
 * LifecycleCapability.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.component.lifecycle.resource;

/**
 * <code>UpgradeCapability</code> is ... <br>
 */
public enum LifecycleCapability
{
    /**
     * The <code>UPGRADE</code> field.<br>
     */
    UPGRADE( LifecycleOperation.UPGRADE )
    /*
     * TODO: Below other capabilities can be revisited later.
     */
    /**
     * The <code>DOWNRADE</code> field.<br>
     */
    // DOWNRADE( LifecycleOperation.DOWNRADE ),
    /**
     * The <code>ROLLBACK</code> field.<br>
     */
    // ROLLBACK( LifecycleOperation.ROLLBACK ),
    /**
     * The <code>ROLLINGUPGRADE</code> field.<br>
     */
    // ROLLINGUPGRADE( LifecycleOperation.ROLLINGUPGRADE ),
    /**
     * The <code>RECOVER</code> field.<br>
     */
    // RECOVER( LifecycleOperation.RECOVER )
    ;
    private final LifecycleOperation lifecycleOperation;

    private LifecycleCapability( LifecycleOperation lifecycleOperation )
    {
        this.lifecycleOperation = lifecycleOperation;
    }

    @Override
    public String toString()
    {
        return lifecycleOperation.toString();
    }
}
