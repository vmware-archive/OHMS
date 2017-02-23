/* ********************************************************************************
 * SpeedInfo.java
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

package com.vmware.vrack.hms.common.servernodes.api;

/**
 * Speed related Information
 * 
 * @author VMware, Inc.
 */
public class SpeedInfo
{
    private Long speed;

    private SpeedUnit unit;

    public SpeedInfo()
    {

    }

    public SpeedInfo( Long speed, SpeedUnit unit )
    {
        this.speed = speed;
        this.unit = unit;
    }

    public Long getSpeed()
    {
        return speed;
    }

    public void setSpeed( Long speed )
    {
        this.speed = speed;
    }

    public SpeedUnit getUnit()
    {
        return unit;
    }

    public void setUnit( SpeedUnit unit )
    {
        this.unit = unit;
    }

}
