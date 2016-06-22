/* ********************************************************************************
 * SwitchPortsConfigChangeMessage.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.inventory;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchPortInfo;

/**
 * Switch Config Change Message - Port Information
 */
public class SwitchPortsConfigChangeMessage
    extends ApplicationEvent
{
    private List<NBSwitchPortInfo> portsList;

    private String switchID;

    /**
     * @param source
     * @param switchID
     */
    public SwitchPortsConfigChangeMessage( List<NBSwitchPortInfo> source, String switchID )
    {
        super( source );
        this.setPortsList( source );
        this.setSwitchID( switchID );
    }

    public List<NBSwitchPortInfo> getPortsList()
    {
        return portsList;
    }

    public void setPortsList( List<NBSwitchPortInfo> portsList )
    {
        this.portsList = portsList;
    }

    public String getSwitchID()
    {
        return switchID;
    }

    public void setSwitchID( String switchID )
    {
        this.switchID = switchID;
    }
}
