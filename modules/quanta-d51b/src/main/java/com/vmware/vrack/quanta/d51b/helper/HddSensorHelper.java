/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.ipmiservice.IpmiService;
import com.vmware.vrack.quanta.d51b.parsers.BoardSensorNumber;
import com.vmware.vrack.quanta.d51b.parsers.ServerComponentSensorParsers;

public class HddSensorHelper {

    private static Logger logger = Logger.getLogger(HddSensorHelper.class);

    /**
     * method to get HDD sensor
     *
     * @param serviceNode
     * @param ipmiServiceExecutor
     * @return List<ServerComponentSensor>
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getHddSensor(ServiceHmsNode serviceNode, IpmiService ipmiServiceExecutor) throws HmsException
    {
        if(serviceNode != null && serviceNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceNode;
            List<ServerComponentEvent> serverComponentSensor =  new ArrayList<>();
            List<Integer> listSensorNumber = new ArrayList<Integer>();
            List<Map<String, String>> sensorData = new ArrayList<>();
            ServerComponentSensorParsers serverComponentSensorParsers = new ServerComponentSensorParsers();

            try
            {

                for (BoardSensorNumber boardSensorNumber: BoardSensorNumber.values())
                {
                    if ((BoardSensorNumber.sensorTypeDriveBay(boardSensorNumber.getCode())) != BoardSensorNumber.Invalid)
                    {
                        listSensorNumber.add(boardSensorNumber.getCode());
                    }
                }
                sensorData = ipmiServiceExecutor.getSensorData(node, listSensorNumber);
                serverComponentSensor = serverComponentSensorParsers.getServerComponentSensor(sensorData);

                for (int i=0; i<serverComponentSensor.size(); i++)
                {
                    if (serverComponentSensor.get(i).getDiscreteValue() != null)
                    {
                        // The DriveFault state means Faulty drive and it's operational status is down
                    	// Commented HDD_DOWN, as HDD events handled in In-band 
                        /* if (serverComponentSensor.get(i).getDiscreteValue().contains("DriveFault"))
                        {
                            serverComponentSensor.get(i).setEventName(NodeEvent.HDD_DOWN);
                        } */
                        //DrivePresence - Event: Device is Present (drive slot is full)
                        if (serverComponentSensor.get(i).getDiscreteValue().equals("DrivePresence"))
                        {
                            serverComponentSensor.get(i).setEventName(NodeEvent.HDD_SLOT_FULL);
                        }
                        //StateDeasserted - Event: Device Not Present (The drive slot/bay is empty)
                        if (serverComponentSensor.get(i).getDiscreteValue().equals("StateDeasserted"))
                        {
                            serverComponentSensor.get(i).setEventName(NodeEvent.HDD_SLOT_EMPTY);
                        }

                    }
                }
                return serverComponentSensor;
            }
            catch(Exception e)
            {
                logger.error("Cannot get HDD sensor Info", e);
                throw new HmsException("Unable to get HDD Sensor information", e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

}
