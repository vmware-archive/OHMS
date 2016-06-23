/* ********************************************************************************
 * FruCacheUpdateListener.java
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
 * HMS FRU cache update listener
 */
@Component
public class FruCacheUpdateListener
    implements ApplicationListener<FruDataChangeMessage>
{
    @Autowired
    private HmsDataCache hmsDataCache;

    private static Logger logger = Logger.getLogger( FruCacheUpdateListener.class );

    /**
     * Listener for the HMS to update the server FRU cache
     *
     * @param event
     */
    @Override
    public void onApplicationEvent( FruDataChangeMessage event )
    {
        // read event and update the HMS server cache.
        try
        {
            hmsDataCache.updateServerFruCache( event.getNodeID(), event.getComponent(), event.getFruComponent() );
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
