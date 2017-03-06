/* ********************************************************************************
 * SwitchSnmpConfigAssemblers.java
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
package com.vmware.vrack.hms.common.switches.adapters;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchSnmpConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchSnmpUser;
import com.vmware.vrack.hms.common.switches.api.SwitchSnmpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchSnmpUser;

public class SwitchSnmpConfigAssemblers
{
    public static NBSwitchSnmpConfig toSwitchSnmpConfig( SwitchSnmpConfig config )
    {
        NBSwitchSnmpConfig lConfig = new NBSwitchSnmpConfig();

        if ( config == null || config.getEnabled() == false )
            return null;

        lConfig.setServerIp( config.getServerIp() );
        lConfig.setServerPort( config.getServerPort() );
        lConfig.setUsers( toSwitchSnmpUsers( config.getUsers() ) );

        return lConfig;
    }

    private static NBSwitchSnmpUser toSwitchSnmpUser( SwitchSnmpUser user )
    {
        NBSwitchSnmpUser lUser = new NBSwitchSnmpUser();

        lUser.setAuthPassword( user.getAuthPassword() );
        lUser.setPrivPassword( user.getPrivPassword() );
        lUser.setUsername( user.getUsername() );
        lUser.setAuthType( toAuthType( user.getAuthType() ) );
        lUser.setPrivType( toPrivType( user.getPrivType() ) );

        return lUser;
    }

    private static List<NBSwitchSnmpUser> toSwitchSnmpUsers( List<SwitchSnmpUser> users )
    {
        List<NBSwitchSnmpUser> lUsers = new ArrayList<NBSwitchSnmpUser>();

        if ( users == null )
            return null;

        for ( SwitchSnmpUser user : users )
        {
            if ( user != null )
            {
                lUsers.add( toSwitchSnmpUser( user ) );
            }
        }

        return lUsers;
    }

    private static NBSwitchSnmpUser.AuthType toAuthType( SwitchSnmpUser.AuthType type )
    {
        NBSwitchSnmpUser.AuthType lType = null;

        switch ( type )
        {
            case MD5:
                lType = NBSwitchSnmpUser.AuthType.MD5;
                break;
            case SHA:
                lType = NBSwitchSnmpUser.AuthType.SHA;
                break;
        }

        return lType;
    }

    private static NBSwitchSnmpUser.PrivType toPrivType( SwitchSnmpUser.PrivType type )
    {
        NBSwitchSnmpUser.PrivType lType = null;

        switch ( type )
        {
            case AES:
                lType = NBSwitchSnmpUser.PrivType.AES;
                break;
            case DES:
                lType = NBSwitchSnmpUser.PrivType.DES;
                break;
        }

        return lType;
    }
}
