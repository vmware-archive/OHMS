/* ********************************************************************************
 * SwitchCacheUpdateListener.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
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
