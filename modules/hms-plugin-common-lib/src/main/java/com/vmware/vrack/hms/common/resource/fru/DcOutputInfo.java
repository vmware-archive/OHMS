/* ********************************************************************************
 * DcOutputInfo.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource.fru;

/**
 * DC output Information record from FRU Multi Record Area
 */
public class DcOutputInfo
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
    private Integer maximumNegativeDeviation;

    /**
     * The unit is 10mV.
     */
    private Integer maximumPositiveDeviation;

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

    public Integer getMaximumNegativeDeviation()
    {
        return maximumNegativeDeviation;
    }

    public void setMaximumNegativeDeviation( Integer maximumNegativeDeviation )
    {
        this.maximumNegativeDeviation = maximumNegativeDeviation;
    }

    public Integer getMaximumPositiveDeviation()
    {
        return maximumPositiveDeviation;
    }

    public void setMaximumPositiveDeviation( Integer maximumPositiveDeviation )
    {
        this.maximumPositiveDeviation = maximumPositiveDeviation;
    }
}
