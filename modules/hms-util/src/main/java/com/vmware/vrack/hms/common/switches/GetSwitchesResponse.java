/* ********************************************************************************
 * GetSwitchesResponse.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vrack.hms.common.notification.BaseResponse;

public class GetSwitchesResponse
    extends BaseResponse
{
    public GetSwitchesResponse()
    {
        super();
        switchList = new ArrayList<GetSwitchesResponseItem>();
    }

    List<GetSwitchesResponseItem> switchList;

    public List<GetSwitchesResponseItem> getSwitchList()
    {
        return switchList;
    }

    public void setSwitchList( List<GetSwitchesResponseItem> switchList )
    {
        this.switchList = switchList;
    }

    public void add( GetSwitchesResponseItem item )
    {
        switchList.add( item );
    }

    public static class GetSwitchesResponseItem
    {
        String name;

        String managementIpAddress;

        String type;

        public GetSwitchesResponseItem( String name, String managementIpAddress )
        {
            super();
            this.name = name;
            this.managementIpAddress = managementIpAddress;
        }

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public String getManagementIpAddress()
        {
            return managementIpAddress;
        }

        public void setManagementIpAddress( String managementIpAddress )
        {
            this.managementIpAddress = managementIpAddress;
        }

        public String getType()
        {
            return type;
        }

        public void setType( String type )
        {
            this.type = type;
        }
    }
}
