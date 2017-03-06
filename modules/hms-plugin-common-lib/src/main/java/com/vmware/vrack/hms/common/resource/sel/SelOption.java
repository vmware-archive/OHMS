/* ********************************************************************************
 * SelOption.java
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

package com.vmware.vrack.hms.common.resource.sel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Option to pass that decides which SEL entries to fetch(Recent or Oldest)
 * 
 * @author VMware, Inc.
 */
@JsonInclude( JsonInclude.Include.NON_NULL )
public class SelOption
{
    private SelFetchDirection direction;

    private Integer recordCount;

    private SelTask selTask;

    private List<SelRecord> selFilters;

    public SelFetchDirection getDirection()
    {
        return direction;
    }

    public void setDirection( SelFetchDirection direction )
    {
        this.direction = direction;
    }

    public Integer getRecordCount()
    {
        return recordCount;
    }

    public void setRecordCount( Integer recordCount )
    {
        this.recordCount = recordCount;
    }

    public SelTask getSelTask()
    {
        return selTask;
    }

    public void setSelTask( SelTask selTask )
    {
        this.selTask = selTask;
    }

    public List<SelRecord> getSelFilters()
    {
        return selFilters;
    }

    public void setSelFilters( List<SelRecord> selFilters )
    {
        this.selFilters = selFilters;
    }

}
