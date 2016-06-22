/* ********************************************************************************
 * HostNameRetreival.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.topology;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.aggregator.topology.object.CumulativeObject;
import com.vmware.vrack.hms.common.boardvendorservice.api.ib.IInbandService;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.HostNameInfo;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

public class HostNameRetreival
    implements Callable<CumulativeObject>
{
    static final String HOSTNAME = "HostName";

    ServerNode node;

    private static Logger logger = Logger.getLogger( HostNameRetreival.class );

    public HostNameRetreival( ServerNode node )
    {
        this.node = node;
    }

    public CumulativeObject call()
        throws Exception
    {
        CumulativeObject object = new CumulativeObject();
        object.setObject( getHostName() );
        object.setQueryType( HOSTNAME );
        object.setNodeName( node.getNodeID() );
        return object;
    }

    public HostNameInfo getHostName()
    {
        HostNameInfo hostnameInfo = null;
        try
        {
            IInbandService service = InBandServiceProvider.getBoardService( node.getServiceObject() );
            if ( service == null )
            {
                logger.error( "Couldn't determine board service for host " + node.getNodeID() );
                return null;
            }
            hostnameInfo = service.getHostName( node.getServiceObject() );
            if ( hostnameInfo == null )
            {
                logger.error( "Couldn't determine hostname info for host " + node.getNodeID() );
                return null;
            }
        }
        catch ( Exception e )
        {
            logger.warn( "Received exception while getting hostname of node " + node.getNodeID() );
        }
        return hostnameInfo;
    }
}
