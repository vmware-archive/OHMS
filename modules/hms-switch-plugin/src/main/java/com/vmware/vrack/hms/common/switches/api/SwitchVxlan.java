/* ********************************************************************************
 * SwitchVxlan.java
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
package com.vmware.vrack.hms.common.switches.api;

public class SwitchVxlan
{
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getVni()
    {
        return vni;
    }

    public void setVni( String vni )
    {
        this.vni = vni;
    }

    public String getVlanName()
    {
        return vlanName;
    }

    public void setVlanName( String vlanName )
    {
        this.vlanName = vlanName;
    }

    private String name;

    private String vni;

    private String vlanName;
}
