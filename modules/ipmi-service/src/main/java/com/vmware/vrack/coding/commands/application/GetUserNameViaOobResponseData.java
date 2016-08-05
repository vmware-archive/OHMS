package com.vmware.vrack.coding.commands.application;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * class to Store ResponseData obtained from GetUserNameViaOob
 * 
 * @author Yagnesh Chawda
 */
public class GetUserNameViaOobResponseData
    implements ResponseData
{
    private String userName;

    public String getUserName()
    {
        return userName;
    }

    public void setUserName( String userName )
    {
        this.userName = userName;
    }

    @Override
    public String toString()
    {
        return "GetUserNameViaOobResponseData [userName=" + userName + "]";
    }
}
