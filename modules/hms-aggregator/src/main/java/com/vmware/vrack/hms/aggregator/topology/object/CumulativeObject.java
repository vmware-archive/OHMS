/* ********************************************************************************
 * CumulativeObject.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
