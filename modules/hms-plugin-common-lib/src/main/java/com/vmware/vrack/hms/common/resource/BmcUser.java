/* ********************************************************************************
 * BmcUser.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Class for Host Node Available user Name along with their user ID
 *
 * @author VMware, Inc.
 */
@JsonInclude( JsonInclude.Include.NON_NULL )
public class BmcUser
{
    private int userId;

    private String userName;

    public int getUserId()
    {
        return userId;
    }

    public void setUserId( int userId )
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName( String userName )
    {
        this.userName = userName;
    }
}
