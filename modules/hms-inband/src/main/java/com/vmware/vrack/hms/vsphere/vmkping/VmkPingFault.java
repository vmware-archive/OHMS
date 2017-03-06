/* ********************************************************************************
 * VmkPingFault.java
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