/* ********************************************************************************
 * HddSMARTData.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.servernodes.api.hdd;

/**
 * HDD SMART (Self-Monitoring, Analysis, and Reporting) Data for HDD
 * 
 * @author VMware, Inc.
 */
public class HddSMARTData
{
    /**
     * Parameter name like Health Status, Write Error Count etc
     */
    private String parameter;

    /**
     * Threshold value of the Parameter
     */
    private String threshold;

    /**
     * Current Value of the parameter
     */
    private String value;

    /**
     * Worst Value of the parameter
     */
    private String worst;

    public String getParameter()
    {
        return parameter;
    }

    public void setParameter( String parameter )
    {
        this.parameter = parameter;
    }

    public String getThreshold()
    {
        return threshold;
    }

    public void setThreshold( String threshold )
    {
        this.threshold = threshold;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getWorst()
    {
        return worst;
    }

    public void setWorst( String worst )
    {
        this.worst = worst;
    }

    @Override
    public String toString()
    {
        return "HddSMARTData [parameter=" + parameter + ", threshold=" + threshold + ", value=" + value + ", worst="
            + worst + "]";
    }
}
