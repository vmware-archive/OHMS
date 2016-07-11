/* ********************************************************************************
 * ThreadLimitExecuterServiceObjectFactory.java
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
package com.vmware.vrack.hms.boardservice;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.log4j.Logger;

public class ThreadLimitExecuterServiceObjectFactory
    extends BaseKeyedPooledObjectFactory<String, ThreadLimitExecuterServiceObject>
{
    private static Logger logger = Logger.getLogger( ThreadLimitExecuterServiceObjectFactory.class );

    @Override
    public ThreadLimitExecuterServiceObject create( String key )
        throws Exception
    {
        ThreadLimitExecuterServiceObject rateLimitExecuterServiceObject = new ThreadLimitExecuterServiceObject();
        rateLimitExecuterServiceObject.setName( key );
        logger.debug( "\n[Created object" + rateLimitExecuterServiceObject + "]" );
        return rateLimitExecuterServiceObject;
    }

    @Override
    public PooledObject<ThreadLimitExecuterServiceObject> wrap( ThreadLimitExecuterServiceObject value )
    {
        return new DefaultPooledObject<ThreadLimitExecuterServiceObject>( value );
    }

    @Override
    public boolean validateObject( String key, PooledObject<ThreadLimitExecuterServiceObject> pooledObject )
    {
        if ( pooledObject.getObject().getName() == null )
        {
            return false;
        }
        return true;
    }
}
