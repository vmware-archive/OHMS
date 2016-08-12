/* ********************************************************************************
 * Ipv4DefaultRoute.java
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
package com.vmware.vrack.hms.switches.cumulus.model;

import com.vmware.vrack.hms.common.exception.HmsOobNetworkErrorCode;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.switches.cumulus.util.IpUtils;;

public class Ipv4DefaultRoute {
    public final static String command = "up ip route add default via";
    //Default route configured on the management interface eth0 looks like "gateway <address>"
    public final static String mgmtGateway = "gateway";
    private String gateway;

    /**
     * @return the gateway
     */
    public String getGateway() {
        return gateway;
    }

    /**
     * @param gateway
     *            the gateway to set
     */
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getString(boolean isMgmtIf) {
        String retVal = "";

        if (gateway != null) {
            if (isMgmtIf == false)
                retVal = String.format("%s %s", command, gateway);
            else
                retVal = String.format("%s %s", mgmtGateway, gateway);
        }

        return retVal;
    }

    /**
     * This is a factory method
     *
     * @param args
     * @return
     */
    public static Ipv4DefaultRoute getIpv4DefaultRoute(String args) throws HmsOobNetworkException {
        Ipv4DefaultRoute ipv4Route = new Ipv4DefaultRoute();
        String gateway = args.trim();

        if (!IpUtils.isValidIpv4Address(gateway))
            throw new HmsOobNetworkException("Invalid gateway IP Address " + gateway, null,
                    HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);

        ipv4Route.setGateway(gateway);

        return ipv4Route;
    }
}
