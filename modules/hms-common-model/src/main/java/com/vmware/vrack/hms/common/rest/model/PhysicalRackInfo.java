/* ********************************************************************************
 * PhysicalRackInfo.java
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

package com.vmware.vrack.hms.common.rest.model;

import java.util.List;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;

/**
 * <code>PhysicalRackInfo</code> is ... <br>
 * the class containing information on a physical rack <br>
 * The PhysicalRackInfo class contains basic information regarding the <br>
 * physical rack. The list of servers and switches are also part of this <br>
 * class. This class is returned when the PRM northbound API getRackInfo() <br>
 * is invoked<br>
 * Usage:<br>
 * <code>PhysicalRackInfo rackInfo = PRMServerNodeService.getRackInfo();</code>
 *
 * @since <Product revision>
 * @version <Implementation version of this type>
 * @author sgakhar
 */
public class PhysicalRackInfo
{

    private String rackId;

    private String product;

    private String manufacturer;

    private List<ServerInfo> serverNodeList;

    @Deprecated
    private List<SwitchInfo> switchNodeList;

    private List<NBSwitchInfo> nbSwitchNodeList;

    /**
     * @return the rackId
     */
    public String getRackId()
    {
        return rackId;
    }

    /**
     * @param rackId the rackId to set
     */
    public void setRackId( String rackId )
    {
        this.rackId = rackId;
    }

    /**
     * @return the product
     */
    public String getProduct()
    {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct( String product )
    {
        this.product = product;
    }

    /**
     * @return the manufacturer
     */
    public String getManufacturer()
    {
        return manufacturer;
    }

    /**
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer( String manufacturer )
    {
        this.manufacturer = manufacturer;
    }

    /**
     * @return the serverList
     */
    public List<ServerInfo> getServerNodeList()
    {
        return serverNodeList;
    }

    /**
     * @param serverNodeList the serverList to set
     */
    public void setServerList( List<ServerInfo> serverNodeList )
    {
        this.serverNodeList = serverNodeList;
    }

    /**
     * @return the switchList
     */
    public List<SwitchInfo> getSwitchNodeList()
    {
        return switchNodeList;
    }

    /**
     * @param switchNodeList the switchList to set
     */
    public void setSwitchList( List<SwitchInfo> switchNodeList )
    {
        this.switchNodeList = switchNodeList;
    }

    /**
     * @return the switchList
     */
    public List<NBSwitchInfo> getNBSwitchNodeList()
    {
        return nbSwitchNodeList;
    }

    /**
     * @param switchNodeList the switchList to set
     */
    public void setNBSwitchNodeList( List<NBSwitchInfo> nbSwitchNodeList )
    {
        this.nbSwitchNodeList = nbSwitchNodeList;
    }
}
