/* ********************************************************************************
 * NBSwitchNetworkPrefix.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches;

public class NBSwitchNetworkPrefix
{
    private String prefix;

    private Integer prefixLen;

    /**
     * @return the prefix
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }

    /**
     * @return the prefixLen
     */
    public Integer getPrefixLen()
    {
        return prefixLen;
    }

    /**
     * @param prefixLen the prefixLen to set
     */
    public void setPrefixLen( Integer prefixLen )
    {
        this.prefixLen = prefixLen;
    }
}
