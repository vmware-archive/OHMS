/* ********************************************************************************
 * ServerComponentAggregator.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.aggregator.util.AggregatorUtil;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

public class ServerComponentAggregator
{
    private static Logger logger = Logger.getLogger( ServerComponentAggregator.class );

    /**
     * Sets Server Component Info in node object
     * 
     * @param node
     * @param component
     * @throws HmsException
     */
    public void setServerComponentInfo( ServerNode node, ServerComponent component )
        throws HmsException
    {
        if ( AggregatorUtil.isComponentAvilableOOB( node, component ) )
            AggregatorUtil.getServerComponentOOB( node, component );
        else
            AggregatorUtil.getServerComponentIB( node, component );
    }
}
