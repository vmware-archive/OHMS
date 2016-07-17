/* ********************************************************************************
 * IEventAggregatorTask.java
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
package com.vmware.vrack.hms.aggregator;

import java.util.List;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

/**
 * @author sgakhar Event Aggregator to be used by Event Rest Endpoints.
 */
public interface IEventAggregatorTask
{
    public List<Event> getAggregatedEvents( ServerNode node, ServerComponent component )
        throws HMSRestException;

    public List<Event> getAggregatedSwitchEvents( String switchId, SwitchComponentEnum component )
        throws HMSRestException;
}
