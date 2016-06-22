package com.vmware.vrack.coding.commands.transport;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * Wrapper Class for Get Channel Info Response
 * 
 * @author Vmware
 */
public class GetChannelInfoCommandResponseData
    implements ResponseData
{
    private int channelNumber; /* channel number */

    private int channelMedium; /* Channel medium type per table 6-3 */

    private int channelProtocol; /* Channel protocol per table 6-2 */

    private int sessionSupport; /* Description of session support */

    private int activeSessions; /* Count of active sessions */

    private int vendorId; /* For OEM that specified the protocol */

    public void setChannelNumber( int channelNumber )
    {
        this.channelNumber = channelNumber;
    }

    public void setChannelMedium( int channelMedium )
    {
        this.channelMedium = channelMedium;
    }

    public void setChannelProtocol( int channelProtocol )
    {
        this.channelProtocol = channelProtocol;
    }

    public void setSessionSupport( int sessionSupport )
    {
        this.sessionSupport = sessionSupport;
    }

    public void setActiveSessions( int activeSessions )
    {
        this.activeSessions = activeSessions;
    }

    public void setVendorId( int vendorId )
    {
        this.vendorId = vendorId;
    }

    public int getChannelNumber()
    {
        return ( channelNumber );
    }

    public int getChannelMedium()
    {
        return ( channelMedium );
    }

    public int getChannelProtocol()
    {
        return ( channelProtocol );
    }

    public int getSessionSupport()
    {
        return ( sessionSupport );
    }

    public int getActiveSessions()
    {
        return ( activeSessions );
    }

    public int getVendorId()
    {
        return ( vendorId );
    }
}
