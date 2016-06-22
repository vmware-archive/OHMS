/* ********************************************************************************
 * NBSwitchNodeIPs.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches;

import java.util.List;

import com.vmware.vrack.hms.common.resource.SwitchList;

/**
 * @author ambi
 */
public class NBSwitchNodeIPs
{
    List<SwitchList> switchList;

    public List<SwitchList> getSwitchList()
    {
        return switchList;
    }

    public void setSwitchList( List<SwitchList> switchList )
    {
        this.switchList = switchList;
    }
}
