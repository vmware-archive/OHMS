/* ********************************************************************************
 * ThreadLimitExecuterServiceObjectPool.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.boardservice;

import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * This class represents a thread pool for each node
 *
 * @author Vmware Inc
 */
public class ThreadLimitExecuterServiceObjectPool
{
    private KeyedObjectPool<String, ThreadLimitExecuterServiceObject> pool;

    public static int maxObjectPerKey;

    private static int maxTotalPerKey;

    private static int minIdlePerKey;

    private static int maxIdlePerKey;

    public static ThreadLimitExecuterServiceObjectPool getInstance()
    {
        ThreadLimitExecuterServiceObjectPool INSTANCE = new ThreadLimitExecuterServiceObjectPool();
        return INSTANCE;
    }

    public static ThreadLimitExecuterServiceObjectPool getInstance( int totalObjectPerKey )
    {
        maxObjectPerKey = totalObjectPerKey;
        maxTotalPerKey = totalObjectPerKey;
        minIdlePerKey = totalObjectPerKey;
        maxIdlePerKey = totalObjectPerKey;
        ThreadLimitExecuterServiceObjectPool INSTANCE = new ThreadLimitExecuterServiceObjectPool();
        return INSTANCE;
    }

    private ThreadLimitExecuterServiceObjectPool()
    {
        startPool();
    }

    /**
     * @return the org.apache.commons.pool.KeyedObjectPool class
     */
    public KeyedObjectPool<String, ThreadLimitExecuterServiceObject> getPool()
    {
        return pool;
    }

    /**
     * Apply Pool Configurations read from config/connection.properties Start Keyed pool for IpmiTaskConnectorFactory
     */
    public void startPool()
    {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxTotalPerKey( maxObjectPerKey );
        config.setMaxTotal( maxTotalPerKey );
        config.setMinIdlePerKey( minIdlePerKey );
        config.setMaxIdlePerKey( maxIdlePerKey );
        config.setBlockWhenExhausted( false );
        pool =
            new GenericKeyedObjectPool<String, ThreadLimitExecuterServiceObject>( new ThreadLimitExecuterServiceObjectFactory(),
                                                                                  config );
    }
}
