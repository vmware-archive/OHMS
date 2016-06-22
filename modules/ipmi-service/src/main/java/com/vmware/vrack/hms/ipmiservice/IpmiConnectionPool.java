/* ********************************************************************************
 * IpmiConnectionPool.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.ipmiservice;

import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.log4j.Logger;

import com.vmware.vrack.hms.task.ipmi.IpmiTaskConnector;

public class IpmiConnectionPool
{
    public static Logger logger = Logger.getLogger( IpmiConnectionPool.class );

    private KeyedObjectPool<IpmiConnectionSettings, IpmiTaskConnector> pool;

    private int maxTotalPerNode = 3;

    private int maxTotal = 200;

    private int minIdlePerNode = 1;

    private int maxIdlePerNode = -1;

    private static class SingletonHolder
    {
        public static final IpmiConnectionPool INSTANCE = new IpmiConnectionPool();
    }

    public static IpmiConnectionPool getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private IpmiConnectionPool()
    {
        try
        {
            maxTotalPerNode =
                Integer.parseInt( IpmiPropertiesManager.getInstance().getProperty( "perNodeConnections" ) );
            maxTotal = Integer.parseInt( IpmiPropertiesManager.getInstance().getProperty( "poolSize" ) );
            minIdlePerNode = Integer.parseInt( IpmiPropertiesManager.getInstance().getProperty( "minIdlePerNode" ) );
            maxIdlePerNode = Integer.parseInt( IpmiPropertiesManager.getInstance().getProperty( "maxIdlePerNode" ) );
        }
        catch ( Exception e )
        {
            logger.error( "error reading connection pool properties setting default", e );
            maxTotalPerNode = 3;
            maxTotal = 200;
            minIdlePerNode = 1;
            maxIdlePerNode = -1;
        }
        startPool();
    }

    /**
     * @return the org.apache.commons.pool.KeyedObjectPool class
     */
    public KeyedObjectPool<IpmiConnectionSettings, IpmiTaskConnector> getPool()
    {
        return pool;
    }

    /**
     * Apply Pool Configurations read from config/connection.properties Start Keyed pool for IpmiTaskConnectorFactory
     */
    public void startPool()
    {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxTotalPerKey( maxTotalPerNode );
        config.setMaxTotal( maxTotal );
        config.setMinIdlePerKey( minIdlePerNode );
        config.setMaxIdlePerKey( maxIdlePerNode );
        logger.debug( "maxTotalPerNode : " + config.getMaxTotalPerKey() );
        logger.debug( "maxTotal : " + config.getMaxTotal() );
        logger.debug( "minIdlePerNode : " + config.getMinIdlePerKey() );
        logger.debug( "maxIdlePerNode : " + config.getMaxIdlePerKey() );
        pool = new GenericKeyedObjectPool<IpmiConnectionSettings, IpmiTaskConnector>( new IpmiTaskConnectorFactory(),
                                                                                      config );
    }
}
