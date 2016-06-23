/* ********************************************************************************
 * ThreadLimitExecuterServiceObject.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.boardservice;

public class ThreadLimitExecuterServiceObject
{
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "Object [name=" + name + "]";
    }
}
