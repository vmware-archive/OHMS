/* ********************************************************************************
 * NBSwitchVlanConfig.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches;

import java.util.Set;

public class NBSwitchVlanConfig
{
    private String vid;

    private Set<String> taggedPorts;

    private Set<String> untaggedPorts;

    private NBSwitchNetworkPrefix ipAddress;

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
    public NBSwitchNetworkPrefix getIpAddress()
    {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress( NBSwitchNetworkPrefix ipAddress )
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
