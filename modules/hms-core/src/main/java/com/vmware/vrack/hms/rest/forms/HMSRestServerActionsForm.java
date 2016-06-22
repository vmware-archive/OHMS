/* ********************************************************************************
 * HMSRestServerActionsForm.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.rest.forms;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

public class HMSRestServerActionsForm
{
    @FormParam( "action" )
    private String action;

    @QueryParam( "id" )
    private String id;

    public String getAction()
    {
        return action;
    }

    public void setAction( String action )
    {
        this.action = action;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }
}
