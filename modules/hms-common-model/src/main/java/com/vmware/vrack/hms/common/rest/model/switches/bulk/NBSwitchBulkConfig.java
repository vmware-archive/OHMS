/* ********************************************************************************
 * NBSwitchBulkConfig.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.rest.model.switches.bulk;

import java.util.List;

public class NBSwitchBulkConfig
{
    private NBSwitchBulkConfigEnum type;

    private List<String> values;

    private List<String> filters;

    public NBSwitchBulkConfigEnum getType()
    {
        return type;
    }

    public void setType( NBSwitchBulkConfigEnum type )
    {
        this.type = type;
    }

    public List<String> getValues()
    {
        return values;
    }

    public void setValues( List<String> values )
    {
        this.values = values;
    }

    public List<String> getFilters()
    {
        return filters;
    }

    public void setFilters( List<String> filters )
    {
        this.filters = filters;
    }
}
