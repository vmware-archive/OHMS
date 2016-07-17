/* ********************************************************************************
 * HmsServiceState.java
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
package com.vmware.vrack.hms.common.rest.model;

import com.vmware.vrack.hms.common.service.ServiceState;

/**
 * Class for HMS Service State
 *
 * @author VMware Inc.
 */
public class HmsServiceState
{
    ServiceState hmsServiceState;

    public ServiceState getHmsServiceState()
    {
        return hmsServiceState;
    }

    public void setHmsServiceState( ServiceState hmsServiceState )
    {
        this.hmsServiceState = hmsServiceState;
    }

    @Override
    public String toString()
    {
        return "HmsServiceState [hmsServiceState=" + hmsServiceState + "]";
    }
}
