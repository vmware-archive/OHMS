/* ********************************************************************************
 * EthernetController.java
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
package com.vmware.vrack.hms.common.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class for Ethernet Controller related Properties
 *
 * @author VMware Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class EthernetController
    extends FruComponent
{
    private String id;

    private String speedInMbps;

    private String firmwareVersion;

    private List<PortInfo> portInfos;

    private int numPorts;

    private String hostId;

    public int getNumPorts()
    {
        return numPorts;
    }

    public void setNumPorts( int numPorts )
    {
        this.numPorts = numPorts;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getSpeedInMbps()
    {
        return speedInMbps;
    }

    public void setSpeedInMbps( String speedInMbps )
    {
        this.speedInMbps = speedInMbps;
    }

    public String getFirmwareVersion()
    {
        return firmwareVersion;
    }

    public void setFirmwareVersion( String firmwareVersion )
    {
        this.firmwareVersion = firmwareVersion;
    }

    public List<PortInfo> getPortInfos()
    {
        return portInfos;
    }

    public void setPortInfos( List<PortInfo> portInfos )
    {
        this.portInfos = portInfos;
    }

    public String getHostId()
    {
        return hostId;
    }

    public void setHostId( String hostId )
    {
        this.hostId = hostId;
    }

    /**
     * Get the Physical Ethernet Controller FRU Information. Wrapper method to get the EthernetController object for the
     * node
     *
     * @param serverNodeEthernetController
     * @param nodeID
     * @return EthernetController
     */
    public EthernetController getEthernetController( com.vmware.vrack.hms.common.resource.fru.EthernetController serverNodeEthernetController,
                                                     String nodeID )
    {
        EthernetController ethernetController = new EthernetController();
        List<PortInfo> portInfoList = new ArrayList<PortInfo>();
        ethernetController.setId( serverNodeEthernetController.getId() );
        ethernetController.setFirmwareVersion( serverNodeEthernetController.getFirmwareVersion() );
        ethernetController.setSpeedInMbps( serverNodeEthernetController.getSpeedInMbps() );
        ethernetController.setLocation( serverNodeEthernetController.getLocation() );
        ethernetController.setNumPorts( serverNodeEthernetController.getPortInfos().size() );
        ethernetController.setHostId( nodeID );
        if ( serverNodeEthernetController.getComponentIdentifier() != null )
        {
            ethernetController.setComponentIdentifier( serverNodeEthernetController.getComponentIdentifier() );
        }
        int portSize = serverNodeEthernetController.getPortInfos().size();
        for ( int i = 0; i < portSize; i++ )
        {
            PortInfo portInfo = new PortInfo();
            portInfo = portInfo.getPortInfo( serverNodeEthernetController.getPortInfos().get( i ) );
            portInfoList.add( portInfo );
        }
        ethernetController.setPortInfos( portInfoList );
        return ethernetController;
    }
}
