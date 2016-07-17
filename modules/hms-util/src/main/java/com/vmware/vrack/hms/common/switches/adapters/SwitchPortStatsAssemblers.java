/* ********************************************************************************
 * SwitchPortStatsAssemblers.java
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
package com.vmware.vrack.hms.common.switches.adapters;

import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchPortStats;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;

import java.util.ArrayList;
import java.util.List;

public final class SwitchPortStatsAssemblers
{
    public static List<NBSwitchPortStats> toSwitchPortStatsList( List<SwitchPort> ports )
    {
        List<NBSwitchPortStats> lStatsList = new ArrayList<NBSwitchPortStats>();
        if ( ports == null )
            return null;
        for ( SwitchPort port : ports )
        {
            lStatsList.add( toSwitchPortStats( port ) );
        }
        return lStatsList;
    }

    public static NBSwitchPortStats toSwitchPortStats( SwitchPort port )
    {
        NBSwitchPortStats lStats = new NBSwitchPortStats();
        if ( port == null || port.getStatistics() == null )
            return null;
        lStats.setRxDroppedPackets( port.getStatistics().getRxDroppedPackets() );
        lStats.setRxErrors( port.getStatistics().getRxErrors() );
        lStats.setRxReceivedPackets( port.getStatistics().getRxReceivedPackets() );
        lStats.setTimestamp( port.getStatistics().getTimestamp() );
        lStats.setTxDroppedPackets( port.getStatistics().getTxDroppedPackets() );
        lStats.setTxErrors( port.getStatistics().getTxErrors() );
        lStats.setTxSentPackets( port.getStatistics().getTxSentPackets() );
        lStats.setPortName( port.getName() );
        return lStats;
    }
}
