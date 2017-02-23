/* ********************************************************************************
 * HddSMARTData.java
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
