/* ********************************************************************************
 * DcLoadInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource.fru;

/**
 * DC output Information record from FRU Multi Record Area<br>
 * This record is used to describe the maximum load that a device requires from a particular DC Output.
 */
public class DcLoadInfo
    extends MultiRecordInfo
{
    private Integer outputNumber;

    /**
     * The unit is 10mV.
     */
    private Integer nominalVoltage;

    /**
     * The unit is 10mV.
     */
    private Integer minimumVoltage;

    /**
     * The unit is 10mV.
     */
    private Integer maximumVoltage;

    /**
     * The unit is 10mA.
     */
    private Integer minimumCurrentLoad;

    /**
     * The unit is 10mA.
     */
    private Integer maximumCurrentLoad;

    public Integer getOutputNumber()
    {
        return outputNumber;
    }

    public void setOutputNumber( Integer outputNumber )
    {
        this.outputNumber = outputNumber;
    }

    public Integer getNominalVoltage()
    {
        return nominalVoltage;
    }

    public void setNominalVoltage( Integer nominalVoltage )
    {
        this.nominalVoltage = nominalVoltage;
    }

    public Integer getMinimumVoltage()
    {
        return minimumVoltage;
    }

    public void setMinimumVoltage( Integer minimumVoltage )
    {
        this.minimumVoltage = minimumVoltage;
    }

    public Integer getMaximumVoltage()
    {
        return maximumVoltage;
    }

    public void setMaximumVoltage( Integer maximumVoltage )
    {
        this.maximumVoltage = maximumVoltage;
    }

    public Integer getMinimumCurrentLoad()
    {
        return minimumCurrentLoad;
    }

    public void setMinimumCurrentLoad( Integer minimumCurrentLoad )
    {
        this.minimumCurrentLoad = minimumCurrentLoad;
    }

    public Integer getMaximumCurrentLoad()
    {
        return maximumCurrentLoad;
    }

    public void setMaximumCurrentLoad( Integer maximumCurrentLoad )
    {
        this.maximumCurrentLoad = maximumCurrentLoad;
    }
}
