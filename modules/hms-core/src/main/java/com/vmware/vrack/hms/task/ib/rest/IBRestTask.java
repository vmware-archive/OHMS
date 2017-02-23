/* ********************************************************************************
 * IBRestTask.java
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
package com.vmware.vrack.hms.task.ib.rest;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.notification.EventHolder;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.task.ib.IBTask;
import com.vmware.vrack.hms.utils.EventsRegistrationsHolder;

public abstract class IBRestTask
    extends IBTask
{

    public ServerNode node;

    public TaskResponse response;

    private static Logger logger = Logger.getLogger( IBRestTask.class );

    private String baseUrl;

    public IBRestTask()
    {
        super();
    }

    public IBRestTask( TaskResponse response )
    {
        node = (ServerNode) response.getNode();

        EventHolder holder =
            EventsRegistrationsHolder.getInstance().getEventDetails( HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS,
                                                                                                  "hms_inband_module_app_id" ) );
        if ( holder != null )
            setBaseUrl( holder.getRequester().getBaseUrl() );
    }

    @Override
    public TaskResponse call()
        throws Exception
    {
        // TODO Auto-generated method stub

        executeTask();

        return response;

    }

    @Override
    public void destroy()
        throws Exception
    {
        // TODO Auto-generated method stub

    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl( String baseUrl )
    {
        this.baseUrl = baseUrl;
    }

}
