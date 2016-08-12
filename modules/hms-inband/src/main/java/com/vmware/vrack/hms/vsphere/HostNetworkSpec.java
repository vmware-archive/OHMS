/* ********************************************************************************
 * HostNetworkSpec.java
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
package com.vmware.vrack.hms.vsphere;

import java.util.List;
import java.util.Map;

import com.vmware.vim.binding.vmodl.ManagedObjectReference;

/**
 * Created by Jeffrey Wang on 4/25/14.
 */
public class HostNetworkSpec
{
    private Long hostId;

    private HostCredential hostCredential;

    private IpAllocation managementNetwork;

    private IpAllocation vMotionNetwork;

    private IpAllocation vsanNetwork;

    private ManagedObjectReference hostMor;

    private String sslThumbprint;

    private String license;

    private String inUseVmnic;

    private String availableVmnic;

    public HostNetworkSpec()
    {
    }

    public Long getHostId()
    {
        return hostId;
    }

    public void setHostId( final Long hostId )
    {
        this.hostId = hostId;
    }

    public HostCredential getHostCredential()
    {
        return hostCredential;
    }

    public void setHostCredential( final HostCredential hostCredential )
    {
        this.hostCredential = hostCredential;
    }

    public IpAllocation getManagementNetwork()
    {
        return managementNetwork;
    }

    public void setManagementNetwork( final IpAllocation managementNetwork )
    {
        this.managementNetwork = managementNetwork;
    }

    public IpAllocation getVMotionNetwork()
    {
        return vMotionNetwork;
    }

    public void setVMotionNetwork( final IpAllocation vMotionNetwork )
    {
        this.vMotionNetwork = vMotionNetwork;
    }

    public IpAllocation getVsanNetwork()
    {
        return vsanNetwork;
    }

    public void setVsanNetwork( final IpAllocation vsanNetwork )
    {
        this.vsanNetwork = vsanNetwork;
    }

    public ManagedObjectReference getHostMor()
    {
        return hostMor;
    }

    public void setHostMor( final ManagedObjectReference hostMor )
    {
        this.hostMor = hostMor;
    }

    public String getSslThumbprint()
    {
        return sslThumbprint;
    }

    public void setSslThumbprint( final String sslThumbprint )
    {
        this.sslThumbprint = sslThumbprint;
    }

    public String getLicense()
    {
        return license;
    }

    public void setLicense( final String license )
    {
        this.license = license;
    }

    public String getInUseVmnic()
    {
        return inUseVmnic;
    }

    public void setInUseVmnic( final String inUseVmnic )
    {
        this.inUseVmnic = inUseVmnic;
    }

    public String getAvailableVmnic()
    {
        return availableVmnic;
    }

    public void loadAvailableVmnic( final Map<String, String> hostVmnicPair )
    {
        if ( hostVmnicPair != null )
        {
            this.availableVmnic = hostVmnicPair.get( hostCredential.getIpAddress() );
        }
    }

    public void setAvailableVmnic( final String availableVmnic )
    {
        this.availableVmnic = availableVmnic;
    }

    public boolean isAvailableVmnicValid( List<String> availableVmnics )
    {
        return ( this.availableVmnic != null ) && availableVmnics.contains( this.availableVmnic );
    }
}
