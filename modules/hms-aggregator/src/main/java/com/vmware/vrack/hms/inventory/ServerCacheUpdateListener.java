/* ********************************************************************************
 * ServerCacheUpdateListener.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
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
