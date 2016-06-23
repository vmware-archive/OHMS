/* ********************************************************************************
 * ServerCacheUpdateListener.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.inventory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * HMS Server FRU cache update listener
 */
@Component
public class ServerCacheUpdateListener
    implements ApplicationListener<ServerDataChangeMessage>
{
    @Autowired
    private HmsDataCache hmsDataCache;

    private static Logger logger = Logger.getLogger( ServerCacheUpdateListener.class );

    /**
     * Listener for the ServerInfo to update the server cache
     *
     * @param event
     */
    @Override
    public void onApplicationEvent( ServerDataChangeMessage event )
    {
        // read event here and update the cache.
        try
        {
            hmsDataCache.updateHmsDataCache( event.getServerInfo().getNodeId(), event.getComponent(),
                                             event.getServerInfo() );
        }
        catch ( Exception e )
        {
            logger.error( "Error in the HMS Server FRU cache update listener ", e );
        }
    }

    public HmsDataCache getHmsDataCache()
    {
        return hmsDataCache;
    }

    public void setHmsDataCache( HmsDataCache hmsDataCache )
    {
        this.hmsDataCache = hmsDataCache;
    }
}
