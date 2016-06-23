/* ********************************************************************************
 * SwitchMonitorTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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