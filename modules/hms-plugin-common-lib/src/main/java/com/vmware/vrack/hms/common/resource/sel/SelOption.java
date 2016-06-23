/* ********************************************************************************
 * SelOption.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
