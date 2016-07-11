/* ********************************************************************************
 * LifecycleCapability.java
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
