/* ********************************************************************************
 * SwitchOspfv2Config.java
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
package com.vmware.vrack.hms.common.core.model.switches;

import java.util.List;

public class SwitchOspfv2Config
{
    public enum Mode
    {
        ACTIVE, PASSIVE
    };

    private Mode defaultMode;

    private List<Network> networks;

    private List<Interface> interfaces;

    private String routerId;

    public static class Network
    {
        private String areaId;

        private SwitchNetworkPrefix network;

        /**
         * @return the areaId
         */
        public String getAreaId()
        {
            return areaId;
        }

        /**
         * @param areaId the areaId to set
         */
        public void setAreaId( String areaId )
        {
            this.areaId = areaId;
        }

        /**
         * @return the network
         */
        public SwitchNetworkPrefix getNetwork()
        {
            return network;
        }

        /**
         * @param network the network to set
         */
        public void setNetwork( SwitchNetworkPrefix network )
        {
            this.network = network;
        }
    };

    /**
     * @return the defaultMode
     */
    public Mode getDefaultMode()
    {
        return defaultMode;
    }

    /**
     * @param defaultMode the defaultMode to set
     */
    public void setDefaultMode( Mode defaultMode )
    {
        this.defaultMode = defaultMode;
    }

    /**
     * @return the networks
     */
    public List<Network> getNetworks()
    {
        return networks;
    }

    /**
     * @param networks the networks to set
     */
    public void setNetworks( List<Network> networks )
    {
        this.networks = networks;
    }

    /**
     * @return the interfaces
     */
    public List<Interface> getInterfaces()
    {
        return interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces( List<Interface> interfaces )
    {
        this.interfaces = interfaces;
    }

    public String getRouterId()
    {
        return routerId;
    }

    public void setRouterId( String routerId )
    {
        this.routerId = routerId;
    }

    public static class Interface
    {
        private Mode mode;

        private String name;

        /**
         * @return the mode
         */
        public Mode getMode()
        {
            return mode;
        }

        /**
         * @param mode the mode to set
         */
        public void setMode( Mode mode )
        {
            this.mode = mode;
        }

        /**
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName( String name )
        {
            this.name = name;
        }
    };
}
