/* ********************************************************************************
 * IBoardService.java
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

package com.vmware.vrack.hms.common.boardvendorservice.api;

import java.util.List;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.PowerOperationAction;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.fan.FanInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;

/**
 * Interface for implementing server board service Out Of Band (OOB) plug-in
 * 
 * @author VMware, Inc.
 */
public interface IBoardService
    extends IComponentEventInfoProvider
{

    /**
     * Method to get Server Power Status This is a mandatory method, Out Of Band Implementation is required
     * 
     * @param serviceHmsNode
     */
    public boolean getServerPowerStatus( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to perform Power operations power down, power up, power cycle, reset and cold reset This is a mandatory
     * method, Out Of Band Implementation is required
     * 
     * @param serviceHmsNode
     * @param powerOperationAction (power down/up/reset/cycle/cold reset)
     */
    public boolean powerOperations( ServiceHmsNode serviceHmsNode, PowerOperationAction powerOperationAction )
        throws HmsException;

    /**
     * Method to get Management (BMC) MAC address This is a mandatory method, Out Of Band Implementation is required
     * 
     * @param hmsNode
     */
    public String getManagementMacAddress( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to get the List of BMC Users This is a mandatory method, Out Of Band Implementation is required
     * 
     * @param hmsNode
     */
    public List<BmcUser> getManagementUsers( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method for BMC to do a Self Test This is a optional method.
     * 
     * @param hmsNode
     */
    public SelfTestResults runSelfTest( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to get the ACPI Power State This is a optional method.
     * 
     * @param hmsNode
     */
    public AcpiPowerState getAcpiPowerState( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to get CPU Information This is a mandatory method, Out Of Band or In Band Implementation is required
     * 
     * @param hmsNode
     */
    public List<CPUInfo> getCpuInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to get FAN Information This is a mandatory method, Out Of Band Implementation is required
     * 
     * @param hmsNode
     */
    public List<FanInfo> getFanInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to get EthernetControllers information This is a mandatory method, Out Of Band or In Band Implementation
     * is required
     * 
     * @param hmsNode
     */
    public List<EthernetController> getEthernetControllersInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to get Boot Options This is a mandatory method, Out Of Band Implementation is required
     * 
     * @param hmsNode
     */
    public SystemBootOptions getBootOptions( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to set Boot Options This is a mandatory method, Out Of Band Implementation is required
     * 
     * @param hmsNode
     * @param data (biosBootType- Legacy/EFI, bootFlagsValid, bootOptionsValidity, bootDeviceType and
     *            bootDeviceSelector)
     */
    public boolean setBootOptions( ServiceHmsNode serviceHmsNode, SystemBootOptions data )
        throws HmsException;

    /**
     * Method to get Server board related Information like board manufacturer, board product model This is a mandatory
     * method, Out Of Band Implementation is required
     * 
     * @param hmsNode
     * @return ServerNodeInfo
     */
    public ServerNodeInfo getServerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to get HDD Information This is a mandatory method, Out Of Band or In Band Implementation is required
     * 
     * @param hmsNode
     * @return List<HddInfo>
     * @throws HmsException
     */
    public List<HddInfo> getHddInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to get the Storage controller/HBA adapter information This is a mandatory method, Out Of Band or In Band
     * Implementation is required
     * 
     * @param hmsNode
     * @return List<StorageControllerInfo>
     * @throws HmsException
     */
    public List<StorageControllerInfo> getStorageControllerInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to implement by Board vendor to return the names of the board, which are supported. This is a mandatory
     * method, Out Of Band Implementation is required
     * 
     * @return List<BoardInfo>
     */
    public List<BoardInfo> getSupportedBoard();

    /**
     * Method for BMC Chassis to start identify itself / Stop identification via some kind of mechanism like blinking
     * lights or sounds. This is a mandatory method, Out Of Band Implementation is required
     * 
     * @param serviceHmsNode
     * @param data
     * @return
     */
    public boolean setChassisIdentification( ServiceHmsNode serviceHmsNode, ChassisIdentifyOptions data )
        throws HmsException;

    /**
     * Method to set the management IP address This is a optional method
     * 
     * @param serviceHmsNode
     * @return
     * @throws Exception
     */
    public boolean setManagementIPAddress( ServiceHmsNode serviceHmsNode, String ipAddress )
        throws HmsException;

    /**
     * Method to change user's password
     * 
     * @param serviceHmsNode
     * @param username
     * @param newPassword
     * @return
     * @throws HmsException
     */
    public boolean setBmcPassword( ServiceHmsNode serviceHmsNode, String username, String newPassword )
        throws HmsException;

    /**
     * Method to create new BMC Management User This is a optional method
     * 
     * @param serviceHmsNode
     * @param bmcUser
     * @return
     * @throws Exception
     */
    public boolean createManagementUser( ServiceHmsNode serviceHmsNode, BmcUser bmcUser )
        throws HmsException;

    /**
     * Method to get System Event Log (SEL) Information along with populating entire System Event Log Records List This
     * is a mandatory method, Out Of Band Implementation is required
     * 
     * @param serviceHmsNode
     * @param recordCount - number of System Event Log record to fetch
     * @param direction - start from the recent entries or old entries
     * @return
     * @throws Exception
     */
    public SelInfo getSelDetails( ServiceHmsNode serviceHmsNode, Integer recordCount, SelFetchDirection direction )
        throws HmsException;

    /**
     * Method to check node if it is reachable and responsive. If yes, returns true This is a mandatory method, Out Of
     * Band Implementation is required
     * 
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    public boolean isHostManageable( ServiceHmsNode serviceHmsNode )
        throws HmsException;

    /**
     * Method to get List of Physical memory details in the System This is a mandatory method, Out Of Band or In Band
     * Implementation is required
     * 
     * @param serviceHmsNode
     * @return
     * @throws HmsException
     */
    public List<PhysicalMemory> getPhysicalMemoryInfo( ServiceHmsNode serviceHmsNode )
        throws HmsException;

}
