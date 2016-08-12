/* ********************************************************************************
 * NBSwitchBulkConfig.java
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
