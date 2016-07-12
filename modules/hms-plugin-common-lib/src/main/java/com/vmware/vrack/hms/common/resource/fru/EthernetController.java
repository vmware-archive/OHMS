/* ********************************************************************************
 * EthernetController.java
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
package com.vmware.vrack.hms.common.resource.fru;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.servernodes.api.AbstractServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.nic.PortInfo;

/**
 * Class for EthernetController related properties EthernetController has the FRU component indentifiers which helps to
 * identify the Server component Ethernet Controller/NIC FRU
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class EthernetController
    extends AbstractServerComponent
{
    private String speedInMbps;

    private String firmwareVersion;

    private List<PortInfo> portInfos;

    private String pciDeviceId;

    public String getPciDeviceId()
    {
        return pciDeviceId;
    }

    public void setPciDeviceId( String pciDeviceId )
    {
        this.pciDeviceId = pciDeviceId;
    }

    public String getSpeedInMbps()
    {
        return speedInMbps;
    }

    public void setSpeedInMbps( String speedInMbps )
    {
        this.speedInMbps = speedInMbps;
    }

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public void setFirmwareVersion( String firmwareVersion )
    {
        this.firmwareVersion = firmwareVersion;
    }

    public List<PortInfo> getPortInfos()
    {
        return portInfos;
    }

    public void setPortInfos( List<PortInfo> portInfos )
    {
        this.portInfos = portInfos;
    }

    @Override
    public int hashCode()
    {
        int hash = 1;
        if ( componentIdentifier.getManufacturer() != null )
        {
            hash = 31 * hash + componentIdentifier.getManufacturer().hashCode();
        }
        if ( portInfos != null )
        {
            hash = 31 * hash + portInfos.hashCode();
        }
        if ( componentIdentifier.getProduct() != null )
        {
            hash = 31 * hash + componentIdentifier.getProduct().hashCode();
        }
        return hash;
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
        EthernetController other = (EthernetController) obj;
        if ( componentIdentifier.getManufacturer() == null )
        {
            if ( other.componentIdentifier.getManufacturer() != null )
                return false;
        }
        else if ( !componentIdentifier.getManufacturer().equals( other.componentIdentifier.getManufacturer() ) )
            return false;
        if ( portInfos == null )
        {
            if ( other.portInfos != null )
                return false;
        }
        else if ( !portInfos.equals( other.portInfos ) )
            return false;
        if ( componentIdentifier.getProduct() == null )
        {
            if ( other.componentIdentifier.getProduct() != null )
                return false;
        }
        else if ( !componentIdentifier.getProduct().equals( other.componentIdentifier.getProduct() ) )
            return false;
        return true;
    }
}
