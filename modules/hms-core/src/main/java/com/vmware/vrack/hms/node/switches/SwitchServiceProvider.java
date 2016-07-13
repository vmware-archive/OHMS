/* ********************************************************************************
 * SwitchServiceProvider.java
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
