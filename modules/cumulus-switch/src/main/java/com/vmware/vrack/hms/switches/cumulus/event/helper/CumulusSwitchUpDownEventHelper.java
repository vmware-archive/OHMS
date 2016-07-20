/* ***************************************************************************
 * Copyright 2015 VMware, Inc.  All rights reserved.
 * -- VMware Confidential
 * ***************************************************************************/

package com.vmware.vrack.hms.switches.cumulus.event.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.switches.cumulus.CumulusUtil;

/**
 * Cumulus switch up or down server component event event helper
 *
 */
public class CumulusSwitchUpDownEventHelper {

    private static Logger logger = Logger.getLogger(CumulusSwitchUpDownEventHelper.class);

    /**
     * method to get Switch up or down event helper
     *
     * @param switchNode
     * @param switchPowerStatus
     * @return List<Event>
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getSwitchUpDownEventHelper(SwitchNode switchNode, boolean switchPowerStatus) throws HmsException {

        if (switchNode != null) {
            List<ServerComponentEvent> serverComponentSensorlist = new ArrayList<>();
            SwitchSession switchSession = CumulusUtil.getSession(switchNode);

            try {
                ServerComponentEvent serverComponentEvent = new ServerComponentEvent();

                boolean switchRoleFound = false;
                if (switchPowerStatus && switchSession.getSwitchNode().getRole() != null) {
                    switch (switchSession.getSwitchNode().getRole()) {
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
                    if (switchRoleFound) {
                        serverComponentEvent.setDiscreteValue("Switch is up");
                        serverComponentEvent.setEventId(switchSession.getSwitchNode().getRole().name());
                        serverComponentEvent.setUnit(EventUnitType.DISCRETE);
                        serverComponentEvent.setComponentId(switchSession.getSwitchNode().getSwitchId());

                        serverComponentSensorlist.add(serverComponentEvent);
                    }
                } else if (!switchPowerStatus && switchSession.getSwitchNode().getRole() != null) {
                    switch (switchSession.getSwitchNode().getRole()) {
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
                    if (switchRoleFound) {
                        serverComponentEvent.setDiscreteValue("Switch is Down");
                        serverComponentEvent.setEventId(switchSession.getSwitchNode().getRole().name());
                        serverComponentEvent.setUnit(EventUnitType.DISCRETE);
                        serverComponentEvent.setComponentId(switchSession.getSwitchNode().getSwitchId());

                        serverComponentSensorlist.add(serverComponentEvent);
                    }
                }

                return serverComponentSensorlist;

            } catch (Exception e) {
                logger.error("Cannot get switch Up or Down event Information", e);
                throw new HmsException("Unable to get switch Up or Down event information", e);
            }
        } else {
            throw new HmsException("Switch Node is Null or invalid");
        }
    }

}
