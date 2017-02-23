/* ********************************************************************************
 * SwitchPortsConfigChangeMessage.java
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