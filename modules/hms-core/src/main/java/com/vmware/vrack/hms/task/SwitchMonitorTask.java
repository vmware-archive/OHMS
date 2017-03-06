/* ********************************************************************************
 * SwitchMonitorTask.java
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
package com.vmware.vrack.hms.task;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnector;

public class SwitchMonitorTask
    implements IHmsTask
{
    public SwitchMonitorTask( HMSSwitchNode node )
    {
        if ( node != null )
        {
            this.switchId = node.getNodeID();
        }
    }

    @Override
    public TaskResponse call()
        throws Exception
    {
        TaskResponse response = new TaskResponse();
        boolean done = false;

        SwitchNodeConnector snc = SwitchNodeConnector.getInstance();
        SwitchNode node = snc.getSwitchNode( switchId );
        ISwitchService service = snc.getSwitchService( switchId );

        /* Read the sleep time from the properties file. */
        String frequencyProperty = HmsConfigHolder.getHMSConfigProperty( "hms.switch.monitor.frequency" );
        long sleepMs = ( frequencyProperty != null ) ? Long.parseLong( frequencyProperty ) : 5000;

        SwitchSession session = service.getSession( node );
        boolean currentStatus = session != null && session.isConnected();
        logger.info( "Starting monitor thread for switch " + switchId + " with initial status = "
            + ( currentStatus ? "UP" : "DOWN" ) );

        while ( !done )
        {
            try
            {
                Thread.sleep( sleepMs );

                boolean newStatus = session != null && session.isConnected();

                if ( newStatus != currentStatus )
                {
                    logger.info( "ALERT! Status of switch " + switchId + " changed to "
                        + ( newStatus ? "UP" : "DOWN" ) );
                    currentStatus = newStatus;
                    /* TODO: When event framework is ready, integrate it here. */
                }

                session = service.getSession( node );
            }
            catch ( InterruptedException ie )
            {
                done = true;
                logger.debug( "Thread interrupted." );
            }
        }

        return response;
    }

    @Override
    public void executeTask()
        throws Exception
    {
        call();
    }

    private String switchId;

    private Logger logger = Logger.getLogger( SwitchMonitorTask.class );
}
