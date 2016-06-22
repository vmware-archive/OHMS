package com.vmware.vrack.coding.commands.application;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * Wrapper Class for Get User Access Response Data
 * 
 * @author Yagnesh Chawda
 */
public class GetUserAccessCommandResponseData
    implements ResponseData
{
    private byte maxUserIdCode;

    private byte currentlyEnabledUsersCountCode;

    private byte userIdWithFixedNameCountCode;

    private byte channelAccessCode;

    public byte getMaxUserIdCode()
    {
        return maxUserIdCode;
    }

    public void setMaxUserIdCode( byte maxUserIdCode )
    {
        this.maxUserIdCode = maxUserIdCode;
    }

    public byte getCurrentlyEnabledUsersCountCode()
    {
        return currentlyEnabledUsersCountCode;
    }

    public void setCurrentlyEnabledUsersCountCode( byte currentlyEnabledUsersCountCode )
    {
        this.currentlyEnabledUsersCountCode = currentlyEnabledUsersCountCode;
    }

    public byte getUserIdWithFixedNameCountCode()
    {
        return userIdWithFixedNameCountCode;
    }

    public void setUserIdWithFixedNameCountCode( byte userIdWithFixedNameCountCode )
    {
        this.userIdWithFixedNameCountCode = userIdWithFixedNameCountCode;
    }

    public byte getChannelAccessCode()
    {
        return channelAccessCode;
    }

    public void setChannelAccessCode( byte channelAccessCode )
    {
        this.channelAccessCode = channelAccessCode;
    }

    /**
     * Custom functions to return maximum number of users on the channel We know that the maximum numbers of users
     * cannot exceed 64 so we return the unsigned int value of maxUserIdCode
     * 
     * @return
     */
    public int getMaxUserId()
    {
        return ( maxUserIdCode & 0xFF );
    }

    /**
     * Count of currently enabled users count including built in user 1 We know that the maximum numbers of users cannot
     * exceed 64 so we return the unsigned int value byte[6] and byte[7] are excluded while calculating
     * currentlyEnabledUsers
     */
    public int getCurrentlyEnabledUsersCount()
    {
        return ( currentlyEnabledUsersCountCode & 0x2F );
    }

    /**
     * returns the number of users with fixed name including the user 1 We know that the maximum numbers of users cannot
     * exceed 64 so we return the unsigned int value
     * 
     * @return
     */
    public int getUserIdWithFixedNameCount()
    {
        return ( userIdWithFixedNameCountCode & 0xFF );
    }
}
