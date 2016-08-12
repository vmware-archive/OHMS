/* ********************************************************************************
 * SelInfo.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class that holds System Event Log
 * 
 * @author VMware, Inc.
 */
public class SelInfo
{
    private Integer totalSelCount;

    private Integer fetchedSelCount;

    private Integer selVersion;

    private Date lastAddtionTimeStamp;

    private Date lastEraseTimeStamp;

    private List<SelRecord> selRecords = new ArrayList<>();

    public Integer getTotalSelCount()
    {
        return totalSelCount;
    }

    public void setTotalSelCount( Integer totalSelCount )
    {
        this.totalSelCount = totalSelCount;
    }

    public Integer getFetchedSelCount()
    {
        return fetchedSelCount;
    }

    public void setFetchedSelCount( Integer fetchedSelCount )
    {
        this.fetchedSelCount = fetchedSelCount;
    }

    public Integer getSelVersion()
    {
        return selVersion;
    }

    public void setSelVersion( Integer selVersion )
    {
        this.selVersion = selVersion;
    }

    public Date getLastAddtionTimeStamp()
    {
        return lastAddtionTimeStamp;
    }

    public void setLastAddtionTimeStamp( Date lastAddtionTimeStamp )
    {
        this.lastAddtionTimeStamp = lastAddtionTimeStamp;
    }

    public Date getLastEraseTimeStamp()
    {
        return lastEraseTimeStamp;
    }

    public void setLastEraseTimeStamp( Date lastEraseTimeStamp )
    {
        this.lastEraseTimeStamp = lastEraseTimeStamp;
    }

    public List<SelRecord> getSelRecords()
    {
        return selRecords;
    }

    public void setSelRecords( List<SelRecord> selRecords )
    {
        this.selRecords = selRecords;
    }
}
