/* ********************************************************************************
 * SwitchSnmpConfigDisassemblers.java
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

public class SwitchSnmpConfigDisassemblers
{
    public static SwitchSnmpConfig fromSwitchSnmpConfig( NBSwitchSnmpConfig config )
    {
        SwitchSnmpConfig lConfig = new SwitchSnmpConfig();

        if ( config == null )
            return null;

        lConfig.setEnabled( true );
        lConfig.setServerIp( config.getServerIp() );
        lConfig.setServerPort( config.getServerPort() );
        lConfig.setUsers( fromSwitchSnmpUsers( config.getUsers() ) );

        return lConfig;
    }

    private static SwitchSnmpUser fromSwitchSnmpUser( NBSwitchSnmpUser user )
    {
        SwitchSnmpUser lUser = new SwitchSnmpUser();

        lUser.setAuthPassword( user.getAuthPassword() );
        lUser.setPrivPassword( user.getPrivPassword() );
        lUser.setUsername( user.getUsername() );
        lUser.setAuthType( fromAuthType( user.getAuthType() ) );
        lUser.setPrivType( fromPrivType( user.getPrivType() ) );

        return lUser;
    }

    private static List<SwitchSnmpUser> fromSwitchSnmpUsers( List<NBSwitchSnmpUser> users )
    {
        List<SwitchSnmpUser> lUsers = new ArrayList<SwitchSnmpUser>();

        if ( users == null )
            return null;

        for ( NBSwitchSnmpUser user : users )
        {
            if ( user != null )
            {
                lUsers.add( fromSwitchSnmpUser( user ) );
            }
        }

        return lUsers;
    }

    private static SwitchSnmpUser.AuthType fromAuthType( NBSwitchSnmpUser.AuthType type )
    {
        SwitchSnmpUser.AuthType lType = null;

        switch ( type )
        {
            case MD5:
                lType = SwitchSnmpUser.AuthType.MD5;
                break;
            case SHA:
                lType = SwitchSnmpUser.AuthType.SHA;
                break;
        }

        return lType;
    }

    private static SwitchSnmpUser.PrivType fromPrivType( NBSwitchSnmpUser.PrivType type )
    {
        SwitchSnmpUser.PrivType lType = null;

        switch ( type )
        {
            case AES:
                lType = SwitchSnmpUser.PrivType.AES;
                break;
            case DES:
                lType = SwitchSnmpUser.PrivType.DES;
                break;
        }

        return lType;
    }
}
