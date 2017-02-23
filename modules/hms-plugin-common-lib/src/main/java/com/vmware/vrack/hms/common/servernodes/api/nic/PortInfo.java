/* ********************************************************************************
 * PortInfo.java
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

package com.vmware.vrack.hms.common.servernodes.api.nic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.servernodes.api.SpeedInfo;

/**
 * @author VMware, Inc. Class for Ethernet Controller Port
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class PortInfo
{
    private String macAddress;

    private SpeedInfo linkSpeedInMBps;

    private NicStatus linkStatus;

    private String deviceName;

    private String switchName;

    private String switchPort;

    private String switchPortMac;

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress( String macAddress )
    {
        this.macAddress = macAddress;
    }

    @Override
    public int hashCode()
    {
        if ( macAddress != null )
        {
            return macAddress.hashCode();
        }
        else
        {
            // If macAddress is NULL, then lets return HashCode of this object as 0, to align with the definition of the
            // equals method.
            return 0;
        }
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        PortInfo other = (PortInfo) obj;
        if ( macAddress == null )
        {
            if ( other.macAddress != null )
                return false;
        }
        else if ( !macAddress.equals( other.macAddress ) )
            return false;
        return true;
    }

    public NicStatus getLinkStatus()
    {
        return linkStatus;
    }

    public void setLinkStatus( NicStatus linkStatus )
    {
        this.linkStatus = linkStatus;
    }

    public SpeedInfo getLinkSpeedInMBps()
    {
        return linkSpeedInMBps;
    }

    public void setLinkSpeedInMBps( SpeedInfo linkSpeedInMBps )
    {
        this.linkSpeedInMBps = linkSpeedInMBps;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName( String deviceName )
    {
        this.deviceName = deviceName;
    }

    public String getSwitchName()
    {
        return switchName;
    }

    /* setSwitchName is an optional property */
    public void setSwitchName( String switchName )
    {
        this.switchName = switchName;
    }

    public String getSwitchPort()
    {
        return switchPort;
    }

    /* setSwitchPort is an optional property */
    public void setSwitchPort( String switchPort )
    {
        this.switchPort = switchPort;
    }

    public String getSwitchPortMac()
    {
        return switchPortMac;
    }

    /* setSwitchPortMac is an optional property */
    public void setSwitchPortMac( String switchPortMac )
    {
        this.switchPortMac = switchPortMac;
    }

}
