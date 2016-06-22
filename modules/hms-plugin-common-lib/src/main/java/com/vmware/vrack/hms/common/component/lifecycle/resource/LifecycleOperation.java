/* ********************************************************************************
 * LifecycleOperation.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.component.lifecycle.resource;

/**
 * <code>LifecyleOperation</code><br>
 */
public enum LifecycleOperation
{
    /**
     * The <code>UPGRADE</code> field.<br>
     */
    UPGRADE,
    /*
     * TODO: Below other capabilities can be revisited later.
     */
    /**
     * The <code>DOWNRADE</code> field.<br>
     */
    // DOWNRADE,
    /**
     * The <code>ROLLBACK</code> field.<br>
     */
    // ROLLBACK,
    /**
     * The <code>ROLLINGUPGRADE</code> field.<br>
     */
    // ROLLINGUPGRADE,
    /**
     * The <code>RECOVER</code> field.<br>
     */
    // RECOVER
    ;
}
