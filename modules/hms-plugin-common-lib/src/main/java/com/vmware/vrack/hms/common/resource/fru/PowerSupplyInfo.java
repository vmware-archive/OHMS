/* ********************************************************************************
 * PowerSupplyInfo.java
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
package com.vmware.vrack.hms.common.resource.fru;

/**
 * Power Supply Information record from FRU Multi Record Area
 */
public class PowerSupplyInfo
    extends MultiRecordInfo
{
    /**
     * Overall Capacity in Watts
     */
    private Integer capacity;

    /**
     * The highest instantaneous VA value that this supply draws during operation.
     */
    private Integer peakVa;

    /**
     * Maximum inrush of current, in Amps, into the power supply.
     */
    private Integer maximumInrush;

    /**
     * This specifies the low end of acceptable voltage into the power supply. The units are 10mV.
     */
    private Integer lowEndInputVoltage1;

    /**
     * This specifies the high end of acceptable voltage into the power supply. The units are 10mV.
     */
    private Integer highEndInputVoltage1;

    /**
     * This specifies the low end of acceptable voltage into the power supply. This field would be used if the power
     * supply did not support autoswitch. Range 1 would define the 110V range, while range 2 would be used for 220V. The
     * units are 10mV.
     */
    private Integer lowEndInputVoltage2;

    /**
     * This specifies the high end of acceptable voltage into the power supply. This field would be used if the power
     * supply did not support autoswitch. Range 1 would define the 110V range, while range 2 would be used for 220V. The
     * units are 10mV.
     */
    private Integer highEndInputVoltage2;

    /**
     * This specifies the low end of acceptable frequency range into the power supply.
     */
    private Integer lowEndInputFrequencyRange;

    /**
     * This specifies the high end of acceptable frequency range into the power supply.
     */
    private Integer highEndInputFrequencyRange;

    public Integer getCapacity()
    {
        return capacity;
    }

    public void setCapacity( Integer capacity )
    {
        this.capacity = capacity;
    }

    public Integer getPeakVa()
    {
        return peakVa;
    }

    public void setPeakVa( Integer peakVa )
    {
        this.peakVa = peakVa;
    }

    public Integer getMaximumInrush()
    {
        return maximumInrush;
    }

    public void setMaximumInrush( Integer maximumInrush )
    {
        this.maximumInrush = maximumInrush;
    }

    public Integer getLowEndInputVoltage1()
    {
        return lowEndInputVoltage1;
    }

    public void setLowEndInputVoltage1( Integer lowEndInputVoltage1 )
    {
        this.lowEndInputVoltage1 = lowEndInputVoltage1;
    }

    public Integer getHighEndInputVoltage1()
    {
        return highEndInputVoltage1;
    }

    public void setHighEndInputVoltage1( Integer highEndInputVoltage1 )
    {
        this.highEndInputVoltage1 = highEndInputVoltage1;
    }

    public Integer getLowEndInputVoltage2()
    {
        return lowEndInputVoltage2;
    }

    public void setLowEndInputVoltage2( Integer lowEndInputVoltage2 )
    {
        this.lowEndInputVoltage2 = lowEndInputVoltage2;
    }

    public Integer getHighEndInputVoltage2()
    {
        return highEndInputVoltage2;
    }

    public void setHighEndInputVoltage2( Integer highEndInputVoltage2 )
    {
        this.highEndInputVoltage2 = highEndInputVoltage2;
    }

    public Integer getLowEndInputFrequencyRange()
    {
        return lowEndInputFrequencyRange;
    }

    public void setLowEndInputFrequencyRange( Integer lowEndInputFrequencyRange )
    {
        this.lowEndInputFrequencyRange = lowEndInputFrequencyRange;
    }

    public Integer getHighEndInputFrequencyRange()
    {
        return highEndInputFrequencyRange;
    }

    public void setHighEndInputFrequencyRange( Integer highEndInputFrequencyRange )
    {
        this.highEndInputFrequencyRange = highEndInputFrequencyRange;
    }
}
