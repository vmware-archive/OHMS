/* ********************************************************************************
 * SwitchServiceImplementation.java
 *
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/

package com.vmware.vrack.hms.common.switches.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * Every Switch implementation must annotate its class with the SwitchImplementation annotation.
 * The name is the name of the implementation, e.g. "cumulus", "arista", etc.
 * @author VMware, Inc.
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
public @interface SwitchServiceImplementation {
	String name();
}
