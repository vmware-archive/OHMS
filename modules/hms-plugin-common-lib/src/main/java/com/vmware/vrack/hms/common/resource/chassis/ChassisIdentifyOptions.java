/* ********************************************************************************
 * ChassisIdentifyOptions.java
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

package com.vmware.vrack.hms.common.resource.chassis;

/**
 * Class to Hold Options for Chassis Identify Command
 * 
 * @author VMware, Inc.
 */
public class ChassisIdentifyOptions
{
    private Boolean identify;

    private Integer interval;

    private Boolean forceIdentifyChassis;

    public Integer getInterval()
    {
        return interval;
    }

    public void setInterval( Integer interval )
    {
        this.interval = interval;
    }

    public Boolean getForceIdentifyChassis()
    {
        return forceIdentifyChassis;
    }

    public void setForceIdentifyChassis( Boolean forceIdentifyChassis )
    {
        this.forceIdentifyChassis = forceIdentifyChassis;
    }

    public Boolean getIdentify()
    {
        return identify;
    }

    public void setIdentify( Boolean identify )
    {
        this.identify = identify;
    }

}
