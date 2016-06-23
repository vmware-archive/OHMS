/* ********************************************************************************
 * SwitchCacheUpdateListener.java
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
 * HMS Switch FRU cache update listener
 */
@Component
public class SwitchCacheUpdateListener
    implements ApplicationListener<SwitchDataChangeMessage>
{
    @Autowired
    private HmsDataCache hmsDataCache;

    private static Logger logger = Logger.getLogger( SwitchCacheUpdateListener.class );

    /**
     * Listener for the SwitchInfo
     *
     * @param event
     */
    @Override
    public void onApplicationEvent( SwitchDataChangeMessage event )
    {
        // read event and update the cache.
        try
        {
            hmsDataCache.updateHmsSwitchDataCache( event.getSwitchInfo().getSwitchId(), event.getComponent(),
                                                   event.getSwitchInfo() );
        }
        catch ( Exception e )
        {
            logger.error( "Error in the HMS Switch FRU cache update listener ", e );
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
