/* ********************************************************************************
 * SwitchServiceProvider.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.node.switches;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;

/**
 * Class to manage Switch Service provider instance for each switch node.
 */
public class SwitchServiceProvider
{
    public ISwitchService getSwitchService( SwitchNode switchNode )
    {
        ISwitchService switchService = ( switchNode != null ) ? switchServiceMap.get( switchNode.getSwitchId() ) : null;
        return switchService;
    }

    public void put( SwitchNode switchNode, ISwitchService switchService )
    {
        if ( switchNode != null )
        {
            switchNodeMap.put( switchNode.getSwitchId(), switchNode );
            switchServiceMap.put( switchNode.getSwitchId(), switchService );
        }
        else
        {
            logger.warn( "Cannot put null switchNode into map." );
        }
    }

    private Logger logger = Logger.getLogger( SwitchServiceProvider.class );

    private Map<String, ISwitchService> switchServiceMap = new TreeMap<String, ISwitchService>();

    private Map<String, SwitchNode> switchNodeMap = new TreeMap<String, SwitchNode>();
}
