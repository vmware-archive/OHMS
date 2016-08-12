/* ********************************************************************************
 * VcCredentail.java
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

/**
 * Created by Jeffrey Wang on 7/26/14.
 */
public class VcCredentail
{
    private String vcIp;

    private String vcPrivateIp;

    private String vcUsername;

    private String vcPassword;

    private String vcVmName;

    public VcCredentail()
    {
    }

    public String getVcIp()
    {
        return vcIp;
    }

    public void setVcIp( final String vcIp )
    {
        this.vcIp = vcIp;
    }

    public String getVcPrivateIp()
    {
        return vcPrivateIp;
    }

    public void setVcPrivateIp( final String vcPrivateIp )
    {
        this.vcPrivateIp = vcPrivateIp;
    }

    public String getVcUsername()
    {
        return vcUsername;
    }

    public void setVcUsername( final String vcUsername )
    {
        this.vcUsername = vcUsername;
    }

    public String getVcPassword()
    {
        return vcPassword;
    }

    public void setVcPassword( final String vcPassword )
    {
        this.vcPassword = vcPassword;
    }

    public String getVcVmName()
    {
        return vcVmName;
    }

    public void setVcVmName( final String vcVmName )
    {
        this.vcVmName = vcVmName;
    }
}
