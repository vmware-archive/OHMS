/* ********************************************************************************
 * HmsLogExtractOptions.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
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
