/* ********************************************************************************
 * SupportsUpgrade.java
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
package com.vmware.vrack.hms.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Every HmsComponentUpgradePlugin annotation must use this SupportsUpgrade annotation to tell which boards and
 * hypervisor combination it supports.
 *
 * @author VMware, Inc.
 */
@Documented
@Target( ElementType.TYPE )
@Inherited
@Retention( RetentionPolicy.RUNTIME )
public @interface SupportsUpgrade
{
    public String boardManufacturer();

    public String boardModel();

    public String hypervisorName();

    public String hypervisorProvider();
}
