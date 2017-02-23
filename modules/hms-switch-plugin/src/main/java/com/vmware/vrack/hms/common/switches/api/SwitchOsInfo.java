/* ********************************************************************************
 * SwitchOsInfo.java
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

import java.util.Date;

public class SwitchOsInfo
{
    private String osName;

    private String osVersion;

    private String firmwareName;

    private String firmwareVersion;

    private Date lastBoot;

    public String getOsName()
    {
        return osName;
    }

    public void setOsName( String osName )
    {
        this.osName = osName;
    }

    public String getOsVersion()
    {
        return osVersion;
    }

    public void setOsVersion( String osVersion )
    {
        this.osVersion = osVersion;
    }

    public String getFirmwareName()
    {
        return firmwareName;
    }

    public void setFirmwareName( String firmwareName )
    {
        this.firmwareName = firmwareName;
    }

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public void setFirmwareVersion( String firmwareVersion )
    {
        this.firmwareVersion = firmwareVersion;
    }

    public Date getLastBoot()
    {
        return lastBoot;
    }

    public void setLastBoot( Date lastBoot )
    {
        this.lastBoot = lastBoot;
    }
}
