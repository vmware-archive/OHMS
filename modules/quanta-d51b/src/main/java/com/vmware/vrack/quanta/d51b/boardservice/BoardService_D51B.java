/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b.boardservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.api.BoardServiceImplementation;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.OperationNotSupportedOOBException;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.PowerOperationAction;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.resource.fru.ChassisInfo;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.resource.fru.FruRecord;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.resource.sel.SelRecord;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodeInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.servernodes.api.fan.FanInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.ipmiservice.IpmiService;
import com.vmware.vrack.hms.ipmiservice.IpmiServiceExecutor;
import com.vmware.vrack.quanta.d51b.helper.BmcSensorHelper;
import com.vmware.vrack.quanta.d51b.helper.CpuSensorHelper;
import com.vmware.vrack.quanta.d51b.helper.FanSensorHelper;
import com.vmware.vrack.quanta.d51b.helper.HddSensorHelper;
import com.vmware.vrack.quanta.d51b.helper.PowerUnitSensorHelper;
import com.vmware.vrack.quanta.d51b.helper.SysMemSensorHelper;
import com.vmware.vrack.quanta.d51b.helper.SystemSensorHelper;
import com.vmware.vrack.quanta.d51b.parsers.BoardSensorNumber;

/**
 * Class for quanta ODM Board D51B (D51B-1U)
 * @author Ashvin Moro
 *
 */
@BoardServiceImplementation(name = "D51B")
public class BoardService_D51B implements IBoardService
{

    private static Logger logger = Logger.getLogger(BoardService_D51B.class);
    IpmiService ipmiServiceExecutor = new IpmiServiceExecutor();
    private List<BoardInfo> supportedBoards;

    public BoardService_D51B()
    {
        super();

        BoardInfo boardInfo = new BoardInfo();
        boardInfo.setBoardManufacturer(QuantaBoardConstants.BOARD_MFG);
        boardInfo.setBoardProductName(QuantaBoardConstants.BOARD_NAME);
        addSupportedBoard(boardInfo);
    }

    public BoardService_D51B(List<BoardInfo> supportedBoards)
    {
        this.supportedBoards = supportedBoards;
    }

    public boolean addSupportedBoard(BoardInfo boardInfo)
    {
        if(supportedBoards == null)
        {
            supportedBoards = new ArrayList<BoardInfo>();
        }

        return supportedBoards.add(boardInfo);
    }

    @Override
    public boolean getServerPowerStatus(ServiceHmsNode serviceHmsNode) throws HmsException
    {

        if (serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;

            try
            {
                boolean powerStatus = ipmiServiceExecutor.getServerPowerStatus(node);
                return powerStatus;
            }
            catch (HmsException e)
            {
                logger.error("Exception while getting Server Power Status for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Server Power Status for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    /**
     * Power operations (power down/up/reset/cycle/coldreset) method
     *
     */
    @Override
    public boolean powerOperations(ServiceHmsNode serviceHmsNode, PowerOperationAction powerOperationAction) throws HmsException
    {
        if (serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            boolean status = false;

            try
            {

                if (powerOperationAction == PowerOperationAction.POWERDOWN)
                {
                    status = ipmiServiceExecutor.powerDownServer(node);
                }
                if (powerOperationAction == PowerOperationAction.POWERUP)
                {
                    status = ipmiServiceExecutor.powerUpServer(node);
                }
                if (powerOperationAction == PowerOperationAction.POWERCYCLE)
                {
                    status = ipmiServiceExecutor.powerCycleServer(node);
                }
                if (powerOperationAction == PowerOperationAction.HARDRESET)
                {
                    status = ipmiServiceExecutor.powerResetServer(node);
                }
                if (powerOperationAction == PowerOperationAction.COLDRESET)
                {
                    status = ipmiServiceExecutor.coldResetServer(node);
                }

                return status;

            }
            catch (HmsException e)
            {
                logger.error("Exception while triggering power operation for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while triggering power operation for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }


    /**
     * Get Management MAC address of BMC
     * @param serviceHmsNode
     */
    @Override
    public String getManagementMacAddress(ServiceHmsNode serviceHmsNode) throws HmsException
    {

        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            String macAddress = null;
            try
            {
                macAddress = ipmiServiceExecutor.getMacAddress(node);
                return macAddress;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Mac Address for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Mac Address for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }

    }

    /**
     * BMC to do a Self Test
     * @param serviceHmsNode
     * @return SelfTestResults
     * @throws HmsException
     */

    @Override
    public SelfTestResults runSelfTest(ServiceHmsNode serviceHmsNode) throws HmsException
    {

        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SelfTestResults selfTestResults = null;

            try
            {
                selfTestResults = ipmiServiceExecutor.selfTest(node);
                return selfTestResults;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Self Test Results for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Self Test Results for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }


    @Override
    public AcpiPowerState getAcpiPowerState(ServiceHmsNode serviceHmsNode) throws HmsException
    {

        if (serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            AcpiPowerState acpiPowerState = null;

            try
            {
                acpiPowerState = ipmiServiceExecutor.getAcpiPowerState(node);
                return acpiPowerState;
            }
            catch (HmsException e)
            {
                logger.error("Exception while getting Acpi Power State for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Acpi Power State for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    @Override
    public List<CPUInfo> getCpuInfo(ServiceHmsNode serviceHmsNode) throws HmsException
    {

        throw new OperationNotSupportedOOBException("Operation getCpuInfo not supported");
    }

    /**
     * Get Fru Information for the board
     * @param serviceHmsNode
     * @return List<Object>
     */
    public List<Object> getFruInfo(ServiceHmsNode serviceHmsNode) throws HmsException
    {

        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            List<Object> fruInfo = new ArrayList<>();

            try
            {
                fruInfo = ipmiServiceExecutor.getFruInfo(node);
                return fruInfo;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Fru data for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Fru Data for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    /**
     * Get already set Boot Options
     * @param serviceHmsNode
     * @return systemBootOptions
     */
    @Override
    public SystemBootOptions getBootOptions(ServiceHmsNode serviceHmsNode) throws HmsException
    {
        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SystemBootOptions systemBootOptions = null;

            try
            {
                systemBootOptions = ipmiServiceExecutor.getBootOptions(node);
                return systemBootOptions;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting System Boot Options for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting System Boot Options for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    /**
     * Set Boot Options
     * @param serviceHmsNode
     * @param data
     * @return status
     */
    @Override
    public boolean setBootOptions(ServiceHmsNode serviceHmsNode, SystemBootOptions data) throws HmsException
    {
        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode && data != null)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;

            try
            {
                boolean status = ipmiServiceExecutor.setBootOptions(node, data);
                return status;
            }
            catch(HmsException e)
            {
                logger.error("Exception while setting System Boot Options for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while setting System Boot Options for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    /**
     * Get Sensor Data for the current node
     *
     **/

    public List<Map<String, String>> getSensorData(ServiceHmsNode serviceHmsNode) throws HmsException
    {

        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            List<Map<String, String>> sensorData = new ArrayList<>();
            List<Integer> listSensorNumber = new ArrayList<Integer>();

            for (BoardSensorNumber boardSensorNumber: BoardSensorNumber.values())
            {
                if (boardSensorNumber.getCode() != 0)
                {
                    listSensorNumber.add(boardSensorNumber.getCode());
                }
            }

            try
            {
                sensorData = ipmiServiceExecutor.getSensorData(node, listSensorNumber);
                return sensorData;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Sensor Data for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Sensor Data for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    @Override
    public ServerNodeInfo getServerInfo(ServiceHmsNode serviceHmsNode) throws HmsException
    {

        if (serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            ServerNodeInfo nodeInfo = null;

            try
            {
                nodeInfo = ipmiServiceExecutor.getServerInfo(node);
                return nodeInfo;
            }
            catch (HmsException e)
            {
                logger.error("Exception while getting Server Node Info for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Server Node Info for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    @Override
    public List<HddInfo> getHddInfo(ServiceHmsNode serviceHmsNode) throws HmsException
    {
        throw new OperationNotSupportedOOBException("Operation getHddInfo not supported");
    }

    @Override
    public boolean setChassisIdentification(ServiceHmsNode serviceHmsNode, ChassisIdentifyOptions data) throws HmsException
    {

        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode && data != null)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;

            try
            {
                boolean status = ipmiServiceExecutor.performChassisIdentification(node, data);
                return status;
            }
            catch(HmsException e)
            {
                logger.error("Exception while performing Chassis Identification operation for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while performing Chassis Identification operation for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    /**
     * Get the List of BMC Users
     * @param serviceHmsNode
     * @return List<BmcUser>
     *
     */
    @Override
    public List<BmcUser> getManagementUsers(ServiceHmsNode serviceHmsNode) throws HmsException
    {

        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            List<BmcUser> bmcUsers = new ArrayList<>();
            try
            {
                bmcUsers = ipmiServiceExecutor.getBmcUsers(node);
                return bmcUsers;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Bmc users for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Bmc users for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }

    }

    /**
     * Get Chasssis Serial Number for the board
     * @param serviceHmsNode
     * @return String
     */
    public String getChasssisSerialNumber(ServiceHmsNode serviceHmsNode) throws HmsException
    {

        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            List<Object> fruInfo = new ArrayList<>();
            FruRecord record = null;
            String chasssisSerialNumber = null;

            try
            {
                fruInfo = ipmiServiceExecutor.getFruInfo(node);

                for (int i=0; i < fruInfo.size(); i++)
                {
                    record = (FruRecord) fruInfo.get(i);

                    if (record instanceof ChassisInfo)
                    {
                        ChassisInfo ci = (ChassisInfo) record;
                        chasssisSerialNumber = ci.getChassisSerialNumber();
                        break;
                    }
                }
                return chasssisSerialNumber;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Fru data for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Fru Data for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    /**
     * Get Board Serial Number for the board
     * @param serviceHmsNode
     * @return String
     */

    public String getBoardSerialNumber(ServiceHmsNode serviceHmsNode) throws HmsException {

        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            List<Object> fruInfo = new ArrayList<>();
            FruRecord record = null;
            String boardSerialNumber = null;

            try
            {
                fruInfo = ipmiServiceExecutor.getFruInfo(node);

                for (int i=0; i < fruInfo.size(); i++)
                {
                    record = (FruRecord) fruInfo.get(i);

                    if (record instanceof BoardInfo)
                    {
                        BoardInfo bi = (BoardInfo) record;
                        boardSerialNumber = bi.getBoardSerialNumber();
                        break;
                    }
                }
                return boardSerialNumber;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Fru data for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Fru Data for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    @Override
    public boolean setManagementIPAddress(ServiceHmsNode serviceHmsNode,
            String ipAddress) throws HmsException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean createManagementUser(ServiceHmsNode serviceHmsNode,
            BmcUser bmcUser) throws HmsException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<EthernetController> getEthernetControllersInfo (ServiceHmsNode serviceHmsNode) throws HmsException
    {
        throw new OperationNotSupportedOOBException("NIC info not supported for Quanta Board");
    }

    /**
     * Get System Event Log Information Only. Gives idea about total entries count, last addition time, last erase time.
     * Skips the actual entries
     * @param serviceHmsNode
     */
    public SelInfo getSelInfo(ServiceHmsNode serviceHmsNode) throws HmsException
    {
        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SelInfo selInfo = null;

            try
            {
                selInfo = ipmiServiceExecutor.getSelInfo(node);
                return selInfo;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Sel Info for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Sel Info for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    /**
     * Get System Event Log Information Only. Gives idea about total entries count, last addition time, last erase time.
     * Also gives Sel Records List
     */
    public SelInfo getSelDetails(ServiceHmsNode serviceHmsNode) throws HmsException
    {
        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SelInfo selInfo = null;

            try
            {
                selInfo = ipmiServiceExecutor.getSelDetails(node);
                return selInfo;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Sel Details for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Sel Details for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }

    }

    /**
     * Get System Event Logs Information along with populating entire Sel Records List
     */
    @Override
    public SelInfo getSelDetails(ServiceHmsNode serviceHmsNode, Integer recordCount, SelFetchDirection direction)
            throws HmsException
    {
        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SelInfo selInfo = null;

            try
            {
                selInfo = ipmiServiceExecutor.getSelDetails(node, recordCount, direction);
                return selInfo;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Sel Details for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Sel Details for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }

    }

    /**
     * Get System Event Log Information. Gives idea about total entries count, last addition time, last erase time.
     * Also gives Sel Records List
     */
    public SelInfo getSelDetails(ServiceHmsNode serviceHmsNode, Integer recordCount, SelFetchDirection direction, List<SelRecord> selFilters)
            throws HmsException
    {
        if(serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;
            SelInfo selInfo = null;

            try
            {
                selInfo = ipmiServiceExecutor.getSelDetails(node, recordCount, direction, selFilters);
                return selInfo;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting Sel Details for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Sel Details for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }

    }

    /**
     * Check if the host is reachable or not; If yes, returns true
     * @param serviceHmsNode
     * @return status
     * @throws HmsException
     */
    @Override
    public boolean isHostManageable(ServiceHmsNode serviceHmsNode) throws HmsException
    {
        if (serviceHmsNode != null && serviceHmsNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceHmsNode;

            try
            {
                boolean status = ipmiServiceExecutor.isHostAvailable(node);
                return status;
            }
            catch (HmsException e)
            {
                logger.error("Exception while getting Server Power Status for node:" + serviceHmsNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting Server Power Status for node:" + serviceHmsNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    @Override
    public List<HmsApi> getSupportedHmsApi(ServiceHmsNode serviceNode) throws HmsException
    {
        if (serviceNode != null && serviceNode instanceof ServiceServerNode)
        {
            List<HmsApi>  supportedAPI = new ArrayList<HmsApi>();

            try
            {
                supportedAPI.add(HmsApi.CPU_SENSOR_INFO);
                supportedAPI.add(HmsApi.MEMORY_SENSOR_INFO);
                supportedAPI.add(HmsApi.SYSTEM_SENSOR_INFO);
                supportedAPI.add(HmsApi.STORAGE_SENSOR_INFO);
                return supportedAPI;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting getSupportedHmsApi Status for node:" + serviceNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    /**
     * Get Server Component Specific Sensor Data
     *
     * @param serviceNode
     * @param component
     * @return List<ServerComponentEvent>
     * @throws HmsException
     */
    @Override
    public List<ServerComponentEvent> getComponentEventList(ServiceHmsNode serviceNode, ServerComponent component) throws HmsException
    {
        if(serviceNode != null && serviceNode instanceof ServiceServerNode)
        {
            ServiceServerNode node = (ServiceServerNode) serviceNode;
            List<ServerComponentEvent> serverComponentSensor =  new ArrayList<>();

            try
            {
                switch (component) {
                case CPU:
                    serverComponentSensor = CpuSensorHelper.getCpuSensor(node, ipmiServiceExecutor);
                    break;
                case MEMORY:
                    serverComponentSensor = SysMemSensorHelper.getSysMemSensor(node, ipmiServiceExecutor);
                    break;
                case SYSTEM:
                    serverComponentSensor = SystemSensorHelper.getSystemSensor(node, ipmiServiceExecutor);
                    break;
                case STORAGE:
                    serverComponentSensor = HddSensorHelper.getHddSensor(node, ipmiServiceExecutor);
                    break;
                case BMC:
                    serverComponentSensor = BmcSensorHelper.getBmcSensor(node, ipmiServiceExecutor);
                    break;
                default:
                    break;
                }
                return serverComponentSensor;
            }
            catch(HmsException e)
            {
                logger.error("Exception while getting getComponentSensorList Data for node:" + serviceNode.getNodeID(), e);
                throw e;
            }
            catch (Exception e)
            {
                logger.error("Exception while getting getComponentSensorList Data for node:" + serviceNode.getNodeID(), e);
                throw new HmsException(e);
            }
        }
        else
        {
            throw new HmsException("Node is Null or invalid");
        }
    }

    /**
     * Get FAN Information
     * @param serviceHmsNode
     */
    @Override
    public List<FanInfo> getFanInfo(ServiceHmsNode serviceHmsNode) throws HmsException {
        throw new OperationNotSupportedOOBException("Operation get FAN info not supprted");

    }

    /**
     * Return the names of the board, which are supported
     * @return List<BoardInfo>
     */
    @Override
    public List<BoardInfo> getSupportedBoard() {

        return supportedBoards;
    }

    @Override
    public List<PhysicalMemory> getPhysicalMemoryInfo(
            ServiceHmsNode serviceHmsNode) throws HmsException {

        throw new OperationNotSupportedOOBException("Operation get Physical Memory Info not supprted");
    }

    public void setIpmiServiceExecutor(IpmiService ipmiServiceExecutor) {
        this.ipmiServiceExecutor = ipmiServiceExecutor;
    }

    @Override
    public List<StorageControllerInfo> getStorageControllerInfo(
            ServiceHmsNode serviceHmsNode) throws HmsException {
        throw new OperationNotSupportedOOBException("Operation get Storage Controller Info not supported");

    }

}
