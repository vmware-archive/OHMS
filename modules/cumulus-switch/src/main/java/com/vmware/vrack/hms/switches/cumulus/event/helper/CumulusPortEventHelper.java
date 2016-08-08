/* ********************************************************************************
 * CumulusPortEventHelper.java
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

package com.vmware.vrack.hms.switches.cumulus.event.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
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

    private static final String SWITCH_PORT_UP = "Switch Port up";

    private static final String SWITCH_PORT_DOWN = "Switch Port Down";

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

            try {
                SwitchSession switchSession = CumulusUtil.getSession( switchNode );

                for ( String port : portList )
                {
                    String command = CumulusConstants.GET_SWITCH_PORT_STATE.replaceAll( "\\{portName\\}", port );
                    String portStatus = switchSession.execute(command);

                    if ( port.equals( "lo" ) )
                        continue;

                    ServerComponentEvent serverComponentEvent = new ServerComponentEvent();

                    if ( portStatus.equals( "UP" ) )
                    {
                        serverComponentEvent.setDiscreteValue( SWITCH_PORT_UP );
                    }
                    else if ( portStatus.equals( "DOWN" ) )
                    {
                        serverComponentEvent.setDiscreteValue( SWITCH_PORT_DOWN );
                    }
                    else
                    {
                        // unknown, do nothing, just log the error
                        logger.error( String.format( "Unable to understand port status %s for port %s", portStatus,
                                                     port ) );
                    }

                    serverComponentEvent.setUnit( EventUnitType.DISCRETE );
                    serverComponentEvent.setComponentId( port );

                    serverComponentSensorlist.add( serverComponentEvent );
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
