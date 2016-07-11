/* ********************************************************************************
 * CumulativeObject.java
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
package com.vmware.vrack.hms.aggregator.topology.object;

public class CumulativeObject
{
    String nodeName;

    String queryType;

    Object object;

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName( String switchName )
    {
        this.nodeName = switchName;
    }

    public String getQueryType()
    {
        return queryType;
    }

    public void setQueryType( String queryType )
    {
        this.queryType = queryType;
    }

    public Object getObject()
    {
        return object;
    }

    public void setObject( Object object )
    {
        this.object = object;
    }
}
