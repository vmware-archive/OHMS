/* ********************************************************************************
 * SwitchVlanConfig.java
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

import java.util.Set;

public class SwitchVlanConfig
{
    private String vid;

    private Set<String> taggedPorts;

    private Set<String> untaggedPorts;

    private SwitchNetworkPrefix ipAddress;

    private Igmp igmp;

    /**
     * @return the vid
     */
    public String getVid()
    {
        return vid;
    }

    /**
     * @param vid the vid to set
     */
    public void setVid( String vid )
    {
        this.vid = vid;
    }

    /**
     * @return the taggedPorts
     */
    public Set<String> getTaggedPorts()
    {
        return taggedPorts;
    }

    /**
     * @param taggedPorts the taggedPorts to set
     */
    public void setTaggedPorts( Set<String> taggedPorts )
    {
        this.taggedPorts = taggedPorts;
    }

    /**
     * @return the untaggedPorts
     */
    public Set<String> getUntaggedPorts()
    {
        return untaggedPorts;
    }

    /**
     * @param untaggedPorts the untaggedPorts to set
     */
    public void setUntaggedPorts( Set<String> untaggedPorts )
    {
        this.untaggedPorts = untaggedPorts;
    }

    /**
     * @return the ipAddress
     */
    public SwitchNetworkPrefix getIpAddress()
    {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress( SwitchNetworkPrefix ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    /**
     * @return the igmp
     */
    public Igmp getIgmp()
    {
        return igmp;
    }

    /**
     * @param igmp the igmp to set
     */
    public void setIgmp( Igmp igmp )
    {
        this.igmp = igmp;
    }

    public static class Igmp
    {
        private Boolean igmpQuerier;

        /**
         * @return the igmpQuerier
         */
        public Boolean getIgmpQuerier()
        {
            return igmpQuerier;
        }

        /**
         * @param igmpQuerier the igmpQuerier to set
         */
        public void setIgmpQuerier( Boolean igmpQuerier )
        {
            this.igmpQuerier = igmpQuerier;
        }
    };
}
