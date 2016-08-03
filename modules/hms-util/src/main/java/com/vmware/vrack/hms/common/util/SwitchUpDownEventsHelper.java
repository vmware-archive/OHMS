/* ********************************************************************************
 * SwitchUpDownEventsHelper.java
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
package com.vmware.vrack.hms.common.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.switches.api.SwitchNode.SwitchRoleType;

public class SwitchUpDownEventsHelper{

    private static Logger logger = Logger.getLogger(SwitchUpDownEventsHelper.class);
    
    private static final String SWITCH_UP = "Switch is up";
    private static final String SWITCH_DOWN = "Switch is Down";

    /**
     * method to get Switch up or down event
     *
     * @param switchNode
     * @param switchPowerStatus
     * @return List<Event>
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getSwitchUpDownEvents(List<ServerComponentEvent> events, SwitchRoleType switchRole) throws HmsException {

        if (events != null && switchRole != null) {
            List<ServerComponentEvent> serverComponentSensorlist = new ArrayList<>();
            try {
            	for (ServerComponentEvent serverComponentEvent : events) {
                	boolean switchRoleFound = false;
            		if (serverComponentEvent.getDiscreteValue().equals(SWITCH_UP)) {
	            		switch (switchRole) {
	            		case TOR:
	            			serverComponentEvent.setEventName(NodeEvent.TOR_SWITCH_UP);
	            			switchRoleFound = true;
	            			break;
	            		case SPINE:
	            			serverComponentEvent.setEventName(NodeEvent.SPINE_SWITCH_UP);
	            			switchRoleFound = true;
	            			break;
						default:
							break;
	            		}
            		}
            		else if (serverComponentEvent.getDiscreteValue().equals(SWITCH_DOWN)) {
	            		switch (switchRole) {
	            		case TOR:
	            			serverComponentEvent.setEventName(NodeEvent.TOR_SWITCH_DOWN);
	            			switchRoleFound = true;
	            			break;
	            		case SPINE:
	            			serverComponentEvent.setEventName(NodeEvent.SPINE_SWITCH_DOWN);
	            			switchRoleFound = true;
	            			break;
						default:
							break;
	            		}
            		}
            		if (switchRoleFound) {
            			serverComponentEvent.setEventId(switchRole.name());
            			serverComponentSensorlist.add(serverComponentEvent);
            		}
            	}
            	return serverComponentSensorlist;
            } catch (Exception e) {
                logger.error("Cannot get switch Up or Down event Information at getSwitchUpDownEvents", e);
                throw new HmsException("Unable to get switch Up or Down event information at getSwitchUpDownEvents", e);
            }
        } else {
            throw new HmsException("Server Component Event Null to generate the switch up or down or invalid");
        }
    }

}
