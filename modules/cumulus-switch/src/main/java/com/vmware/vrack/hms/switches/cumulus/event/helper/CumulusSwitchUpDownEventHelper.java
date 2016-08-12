/* ********************************************************************************
 * CumulusSwitchUpDownEventHelper.java
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

/**
 * Cumulus switch up or down server component event event helper
 *
 */
public class CumulusSwitchUpDownEventHelper {

    private static Logger logger = Logger.getLogger(CumulusSwitchUpDownEventHelper.class);

    private static final String SWITCH_UP = "Switch is up";

    private static final String SWITCH_DOWN = "Switch is Down";

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

            try {
                ServerComponentEvent serverComponentEvent = new ServerComponentEvent();

                if ( switchPowerStatus )
                {
                    serverComponentEvent.setDiscreteValue( SWITCH_UP );
                }
                else
                {
                    serverComponentEvent.setDiscreteValue( SWITCH_DOWN );
                }
                serverComponentEvent.setUnit( EventUnitType.DISCRETE );
                serverComponentEvent.setComponentId( switchNode.getSwitchId() );
                serverComponentSensorlist.add( serverComponentEvent );

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
