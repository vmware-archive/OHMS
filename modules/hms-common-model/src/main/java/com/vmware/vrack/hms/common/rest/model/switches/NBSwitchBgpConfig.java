/* ********************************************************************************
 * NBSwitchBgpConfig.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches;

import java.util.List;

public class NBSwitchBgpConfig
{
    private int localAsn;

    private String localIpAddress;

    private int peerAsn;

    private String peerIpAddress;

    private List<NBSwitchNetworkPrefix> exportedNetworks;

    /**
     * @return the localAsn
     */
    public int getLocalAsn()
    {
        return localAsn;
    }

    /**
     * @param localAsn the localAsn to set
     */
    public void setLocalAsn( int localAsn )
    {
        this.localAsn = localAsn;
    }

    /**
     * @return the localIpAddress
     */
    public String getLocalIpAddress()
    {
        return localIpAddress;
    }

    /**
     * @param localIpAddress the localIpAddress to set
     */
    public void setLocalIpAddress( String localIpAddress )
    {
        this.localIpAddress = localIpAddress;
    }

    /**
     * @return the peerAsn
     */
    public int getPeerAsn()
    {
        return peerAsn;
    }

    /**
     * @param peerAsn the peerAsn to set
     */
    public void setPeerAsn( int peerAsn )
    {
        this.peerAsn = peerAsn;
    }

    /**
     * @return the peerIpAddress
     */
    public String getPeerIpAddress()
    {
        return peerIpAddress;
    }

    /**
     * @param peerIpAddress the peerIpAddress to set
     */
    public void setPeerIpAddress( String peerIpAddress )
    {
        this.peerIpAddress = peerIpAddress;
    }

    /**
     * @return the exportedNetworks
     */
    public List<NBSwitchNetworkPrefix> getExportedNetworks()
    {
        return exportedNetworks;
    }

    /**
     * @param exportedNetworks the exportedNetworks to set
     */
    public void setExportedNetworks( List<NBSwitchNetworkPrefix> exportedNetworks )
    {
        this.exportedNetworks = exportedNetworks;
    }
}
