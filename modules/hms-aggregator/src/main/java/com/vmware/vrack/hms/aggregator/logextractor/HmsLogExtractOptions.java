/* ********************************************************************************
 * HmsLogExtractOptions.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.logextractor;

/**
 * Hms log extraction options for HMS IB and OOB Logs
 * 
 * @author VMware Inc.
 */
public class HmsLogExtractOptions
{
    /**
     * No. of Lines to be extracted from HMS OOB and HMS IB i.e 10000
     */
    private int noOfLines;

    public int getNoOfLines()
    {
        return noOfLines;
    }

    public void setNoOfLines( int noOfLines )
    {
        this.noOfLines = noOfLines;
    }
}
