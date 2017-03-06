/* ********************************************************************************
 * SwitchPortStats.java
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

import java.util.Date;

public class SwitchPortStats
{
    private String portName;

    private Date timestamp;

    private long txSentPackets;

    private long txDroppedPackets;

    private long txErrors;

    private long rxReceivedPackets;

    private long rxDroppedPackets;

    private long rxErrors;

    /**
     * @return the portName
     */
    public String getPortName()
    {
        return portName;
    }

    /**
     * @param portName the portName to set
     */
    public void setPortName( String portName )
    {
        this.portName = portName;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp()
    {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    /**
     * @return the txSentPackets
     */
    public long getTxSentPackets()
    {
        return txSentPackets;
    }

    /**
     * @param txSentPackets the txSentPackets to set
     */
    public void setTxSentPackets( long txSentPackets )
    {
        this.txSentPackets = txSentPackets;
    }

    /**
     * @return the txDroppedPackets
     */
    public long getTxDroppedPackets()
    {
        return txDroppedPackets;
    }

    /**
     * @param txDroppedPackets the txDroppedPackets to set
     */
    public void setTxDroppedPackets( long txDroppedPackets )
    {
        this.txDroppedPackets = txDroppedPackets;
    }

    /**
     * @return the txErrors
     */
    public long getTxErrors()
    {
        return txErrors;
    }

    /**
     * @param txErrors the txErrors to set
     */
    public void setTxErrors( long txErrors )
    {
        this.txErrors = txErrors;
    }

    /**
     * @return the rxReceivedPackets
     */
    public long getRxReceivedPackets()
    {
        return rxReceivedPackets;
    }

    /**
     * @param rxReceivedPackets the rxReceivedPackets to set
     */
    public void setRxReceivedPackets( long rxReceivedPackets )
    {
        this.rxReceivedPackets = rxReceivedPackets;
    }

    /**
     * @return the rxDroppedPackets
     */
    public long getRxDroppedPackets()
    {
        return rxDroppedPackets;
    }

    /**
     * @param rxDroppedPackets the rxDroppedPackets to set
     */
    public void setRxDroppedPackets( long rxDroppedPackets )
    {
        this.rxDroppedPackets = rxDroppedPackets;
    }

    /**
     * @return the rxErrors
     */
    public long getRxErrors()
    {
        return rxErrors;
    }

    /**
     * @param rxErrors the rxErrors to set
     */
    public void setRxErrors( long rxErrors )
    {
        this.rxErrors = rxErrors;
    }
}
