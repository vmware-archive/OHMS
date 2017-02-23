/* ********************************************************************************
 * SwitchPortStatistics.java
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

package com.vmware.vrack.hms.common.switches.api;

import java.util.Date;

/**
 * The SwitchPortStatistics class represents the historical statistics for a particular SwitchPort. This class may be
 * extended to capture additional statistics, if needed.
 * 
 * @author VMware, Inc.
 */
public class SwitchPortStatistics
{

    public SwitchPortStatistics()
    {
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public long getTxSentPackets()
    {
        return txSentPackets;
    }

    public void setTxSentPackets( long txSentPackets )
    {
        this.txSentPackets = txSentPackets;
    }

    public long getTxDroppedPackets()
    {
        return txDroppedPackets;
    }

    public void setTxDroppedPackets( long txDroppedPackets )
    {
        this.txDroppedPackets = txDroppedPackets;
    }

    public long getTxErrors()
    {
        return txErrors;
    }

    public void setTxErrors( long txErrors )
    {
        this.txErrors = txErrors;
    }

    public long getRxReceivedPackets()
    {
        return rxReceivedPackets;
    }

    public void setRxReceivedPackets( long rxReceivedPackets )
    {
        this.rxReceivedPackets = rxReceivedPackets;
    }

    public long getRxDroppedPackets()
    {
        return rxDroppedPackets;
    }

    public void setRxDroppedPackets( long rxDroppedPackets )
    {
        this.rxDroppedPackets = rxDroppedPackets;
    }

    public long getRxErrors()
    {
        return rxErrors;
    }

    public void setRxErrors( long rxErrors )
    {
        this.rxErrors = rxErrors;
    }

    protected Date timestamp;

    protected long txSentPackets;

    protected long txDroppedPackets;

    protected long txErrors;

    protected long rxReceivedPackets;

    protected long rxDroppedPackets;

    protected long rxErrors;
}
