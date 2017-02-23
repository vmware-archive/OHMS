/* ********************************************************************************
 * DcOutputInfo.java
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
