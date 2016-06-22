/* ********************************************************************************
 * ThreadLimitExecuterServiceObjectFactory.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
