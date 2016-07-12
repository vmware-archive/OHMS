/* ********************************************************************************
 * GuestCredential.java
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
package com.vmware.vrack.hms.vsphere.guest;

import com.vmware.vim.binding.vim.vm.guest.GuestAuthentication;
import com.vmware.vim.binding.vim.vm.guest.NamePasswordAuthentication;

/**
 * Created by Jeffrey Wang on 4/23/14.
 */
public class GuestCredential
{
    private String vmName;

    private String username;

    private String password;

    private boolean interactiveSession;

    private NamePasswordAuthentication guestAuthentication;

    private GuestCredential()
    {
    }

    public GuestCredential( final String vmName, final String username, final String password )
    {
        this( vmName, username, password, false );
    }

    public GuestCredential( final String vmName, final String username, final String password,
                            final boolean interactiveSession )
    {
        this.vmName = vmName;
        this.username = username;
        this.password = password;
        this.guestAuthentication = new NamePasswordAuthentication();
        this.guestAuthentication.setUsername( username );
        this.guestAuthentication.setPassword( password );
        this.interactiveSession = interactiveSession;
    }

    public String getVmName()
    {
        return vmName;
    }

    public GuestAuthentication getGuestAuthentication()
    {
        return guestAuthentication;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getPassword()
    {
        return this.password;
    }

    public boolean getInteractiveSession()
    {
        return this.interactiveSession;
    }

    private void setVmName( final String vmName )
    {
        this.vmName = vmName;
    }

    private void setUsername( final String username )
    {
        this.username = username;
    }

    private void setPassword( final String password )
    {
        this.password = password;
    }

    private void setInteractiveSession( final boolean interactiveSession )
    {
        this.interactiveSession = interactiveSession;
    }

    public static GuestCredential from( SecondBootConfiguration.GuestVmCredential guestVmCredential )
    {
        return new GuestCredential( guestVmCredential.guestVmName, guestVmCredential.guestUsername,
                                    guestVmCredential.guestPassword );
    }
}
