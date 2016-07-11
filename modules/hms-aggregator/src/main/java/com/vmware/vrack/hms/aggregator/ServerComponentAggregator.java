/* ********************************************************************************
 * ServerComponentAggregator.java
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
