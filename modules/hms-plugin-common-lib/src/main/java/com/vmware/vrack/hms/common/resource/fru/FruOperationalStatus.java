/* ********************************************************************************
 * FruOperationalStatus.java
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
package com.vmware.vrack.hms.common.resource.fru;

/**
 * ENUM for FRU operational status
 */
public enum FruOperationalStatus
{
    /*
     * If the FRU is operational set the Operational status as "Operational" otherwise it's "Non Operational"
     */
    Operational, NonOperational, UnKnown;
}
