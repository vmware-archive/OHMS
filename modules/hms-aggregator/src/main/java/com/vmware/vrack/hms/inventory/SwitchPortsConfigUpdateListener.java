/* ********************************************************************************
 * SwitchPortsConfigUpdateListener.java
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
 * Listener for the Switch config change - Switch port Information
 */
@Component
public class SwitchPortsConfigUpdateListener
    implements ApplicationListener<SwitchPortsConfigChangeMessage>
{
    @Autowired
    private HmsDataCache hmsDataCache;

    private static Logger logger = Logger.getLogger( SwitchPortsConfigUpdateListener.class );

    /**
     * Listener for the Switch config change - Switch port Information
     *
     * @param event
     */
    @Override
    public void onApplicationEvent( SwitchPortsConfigChangeMessage event )
    {
        // read event and update the cache.
        try
        {
            hmsDataCache.updateHmsSwitchDataCachePortsConfig( event.getSwitchID(), event.getPortsList() );
        }
        catch ( Exception e )
        {
            logger.error( "Error in the HMS Switch FRU cache update listener to update the Switch Port information",
                          e );
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
