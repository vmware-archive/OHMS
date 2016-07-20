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
import com.vmware.vrack.hms.switches.cumulus.CumulusConstants;
import com.vmware.vrack.hms.switches.cumulus.CumulusUtil;

/**
 * Cumulus switch port server component event event helper
 *
 */
public class CumulusPortEventHelper {

    private static Logger logger = Logger.getLogger(CumulusPortEventHelper.class);

    /**
     * method to get Switch Port server component event
     *
     * @param switchNode
     * @param portList
     * @return List<ServerComponentEvent>
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getSwitchPortEventHelper(SwitchNode switchNode, List<String> portList) throws HmsException {

        if (switchNode != null) {
            List<ServerComponentEvent> serverComponentSensorlist = new ArrayList<>();
            SwitchSession switchSession = CumulusUtil.getSession(switchNode);

            try {

                for (int i = 0; i < portList.size(); i++) {
                    String command = CumulusConstants.GET_SWITCH_PORT_STATE.replaceAll("\\{portName\\}", portList.get(i));
                    String portStatus = switchSession.execute(command);

                    if (portList.get(i).equals("lo"))
                        continue;

                    ServerComponentEvent serverComponentEvent = new ServerComponentEvent();

                    boolean switchRoleFound = false;
                    if (portStatus.equals("UP") && switchSession.getSwitchNode().getRole() != null) {
                        switch (switchSession.getSwitchNode().getRole()) {
                        case MANAGEMENT:
                            serverComponentEvent.setEventName(NodeEvent.MANAGEMENT_SWITCH_PORT_UP);
                            switchRoleFound = true;
                            break;
                        case TOR:
                            serverComponentEvent.setEventName(NodeEvent.TOR_SWITCH_PORT_UP);
                            switchRoleFound = true;
                            break;
                        case SPINE:
                            serverComponentEvent.setEventName(NodeEvent.SPINE_SWITCH_PORT_UP);
                            switchRoleFound = true;
                            break;
                        default:
                            break;
                        }
                        if (switchRoleFound) {
                            serverComponentEvent.setDiscreteValue("Switch Port up");
                            serverComponentEvent.setEventId(switchSession.getSwitchNode().getRole().name());
                            serverComponentEvent.setUnit(EventUnitType.DISCRETE);
                            serverComponentEvent.setComponentId(portList.get(i));

                            serverComponentSensorlist.add(serverComponentEvent);
                        }
                    } else if (portStatus.equals("DOWN") && switchSession.getSwitchNode().getRole() != null) {
                        switch (switchSession.getSwitchNode().getRole()) {
                        case MANAGEMENT:
                            serverComponentEvent.setEventName(NodeEvent.MANAGEMENT_SWITCH_PORT_DOWN);
                            switchRoleFound = true;
                            break;
                        case TOR:
                            serverComponentEvent.setEventName(NodeEvent.TOR_SWITCH_PORT_DOWN);
                            switchRoleFound = true;
                            break;
                        case SPINE:
                            serverComponentEvent.setEventName(NodeEvent.SPINE_SWITCH_PORT_DOWN);
                            switchRoleFound = true;
                            break;
                        default:
                            break;
                        }
                        if (switchRoleFound) {
                            serverComponentEvent.setDiscreteValue("Switch Port Down");
                            serverComponentEvent.setEventId(switchSession.getSwitchNode().getRole().name());
                            serverComponentEvent.setUnit(EventUnitType.DISCRETE);
                            serverComponentEvent.setComponentId(portList.get(i));

                            serverComponentSensorlist.add(serverComponentEvent);
                        }
                    }
                }

                return serverComponentSensorlist;

            } catch (Exception e) {
                logger.error("Cannot get switch port event Information", e);
                throw new HmsException("Unable to get switch ports event information", e);
            }
        } else {
            throw new HmsException("Switch Node is Null or invalid");
        }
    }

}
