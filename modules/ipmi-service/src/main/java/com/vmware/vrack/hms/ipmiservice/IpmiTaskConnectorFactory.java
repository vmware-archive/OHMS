/* ********************************************************************************
 * IpmiTaskConnectorFactory.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.ipmiservice;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.commands.session.SetSessionPrivilegeLevel;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.task.ipmi.IpmiTaskConnector;

/**
 * Pooled Factory class to create IpmiTaskConnector and validate the existing connections.
 *
 * @author Vmware
 */
public class IpmiTaskConnectorFactory
    extends BaseKeyedPooledObjectFactory<IpmiConnectionSettings, IpmiTaskConnector>
{
    public static Logger logger = Logger.getLogger( IpmiTaskConnectorFactory.class );

    /**
     * Create an instance that can be served by the pool.
     *
     * @param connectionData the key used when constructing the object
     * @return IpmiTaskConnector that can be served by the pool
     * @throws Exception if there is a problem creating a new instance, this will be propagated to the code requesting
     *             an object.
     */
    @Override
    public IpmiTaskConnector create( IpmiConnectionSettings connectionData )
        throws Exception
    {
        ServiceHmsNode node = connectionData.getNode();
        IpmiTaskConnector ipmiTaskConnector =
            new IpmiTaskConnector( node.getManagementIp(), node.getManagementUserName(),
                                   node.getManagementUserPassword(), connectionData.getCipherSuite(),
                                   connectionData.isEncryptData(), connectionData.getSessionOpenPayload() );
        ipmiTaskConnector.createConnection( connectionData.getCipherSuiteIndex() );
        ipmiTaskConnector.getConnector().sendMessage( ipmiTaskConnector.getHandle(),
                                                      new SetSessionPrivilegeLevel( IpmiVersion.V20,
                                                                                    ipmiTaskConnector.getCipherSuite(),
                                                                                    AuthenticationType.RMCPPlus,
                                                                                    PrivilegeLevel.Administrator ) );
        return ipmiTaskConnector;
    }

    /**
     * Reinitialize an instance to be returned by the pool.
     *
     * @param connectionData the key used when selecting the object
     * @param pooledConnector a {@code PooledObject} wrapping the the instance to be activated
     */
    @Override
    public void activateObject( IpmiConnectionSettings connectionData, PooledObject<IpmiTaskConnector> pooledConnector )
        throws Exception
    {
        pooledConnector.getObject().setSessionPrivilege();
    }

    /**
     * Wrap the provided instance with an implementation of {@link PooledObject}.
     *
     * @param connector the instance to wrap
     * @return The provided instance, wrapped by a {@link PooledObject}
     */
    @Override
    public PooledObject<IpmiTaskConnector> wrap( IpmiTaskConnector connector )
    {
        return new DefaultPooledObject<IpmiTaskConnector>( connector );
    }

    /**
     * Ensures that the instance is safe to be returned by the pool.
     *
     * @param connectionData the key used when selecting the object
     * @param pooledConnector a {@code PooledObject} wrapping the the instance to be validated
     * @return always <code>true</code> in the default implementation
     */
    @Override
    public boolean validateObject( IpmiConnectionSettings connectionData,
                                   PooledObject<IpmiTaskConnector> pooledConnector )
    {
        try
        {
            return pooledConnector.getObject().isConnectionResponsive();
        }
        catch ( Exception e )
        {
            logger.debug( "Invalid session for connection", e );
            return false;
        }
    }

    /**
     * Destroy an instance no longer needed by the pool.
     * <p>
     * The default implementation is a no-op.
     *
     * @param connectionData the key used when selecting the instance
     * @param pooledConnector a {@code PooledObject} wrapping the the instance to be destroyed
     */
    @Override
    public void destroyObject( IpmiConnectionSettings connectionData, PooledObject<IpmiTaskConnector> pooledConnector )
        throws Exception
    {
        pooledConnector.getObject().destroy();
    }
}
