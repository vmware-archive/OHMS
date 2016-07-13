/* ********************************************************************************
 * ErrorMessages.java
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
package com.vmware.vrack.hms.aggregator.util;

public class ErrorMessages
{
    public static final String NO_ADDITIONAL_NIC_DETAILS_FOUND =
        "No Additonal Nic Detail was found via NetworkTopologyElements";

    public static final String ETHERNET_CONTROLLER_LIST_EMPTY = "EthernetController List is null or empty.";

    public static final String ERROR_GETTING_NET_TOPOLOGY_LIST = "Error while getting Network Topology List: ";

    public static final String ERROR_GETTING_NIC_INFO = "Error While getting Nic Info via netTopologyElements";
}
