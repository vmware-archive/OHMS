/* ********************************************************************************
 * VmkPingFault.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere.vmkping;

import java.io.Serializable;

/**
 * Author: Tao Ma Date: 3/3/14
 */
public class VmkPingFault
    implements Serializable
{
    /*
     * Re-factor hard code string 2014-08-07
     */
    private static final String VMK_PING_FAULT_ERR_MSG = "VmkPingFault [errMsg=";

    private static final long serialVersionUID = 1L;

    private String errMsg;

    /**
     * @return the errMsg
     */
    public String getErrMsg()
    {
        return errMsg;
    }

    /**
     * @param errMsg the errMsg to set
     */
    public void setErrMsg( String errMsg )
    {
        this.errMsg = errMsg;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( VMK_PING_FAULT_ERR_MSG );
        builder.append( errMsg );
        builder.append( "]" );
        return builder.toString();
    }
}
