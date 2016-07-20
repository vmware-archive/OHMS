/* ********************************************************************************
 * CumulusTorSwitchService.java
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
package com.vmware.vrack.hms.switches.cumulus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsObjectNotFoundException;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkErrorCode;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchHardwareInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchLinkedPort;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNetworkConfiguration;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switches.api.SwitchOsInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfGlobalConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfGlobalConfig.OspfMode;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfInterfaceConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfInterfaceConfig.InterfaceMode;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfNetworkConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortStatus;
import com.vmware.vrack.hms.common.switches.api.SwitchPort.PortType;
import com.vmware.vrack.hms.common.switches.api.SwitchSensorInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchServiceImplementation;
import com.vmware.vrack.hms.common.switches.api.SwitchSession;
import com.vmware.vrack.hms.common.switches.api.SwitchType;
import com.vmware.vrack.hms.common.switches.api.SwitchUpgradeInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchVxlan;
import com.vmware.vrack.hms.common.switches.model.bulk.PluginSwitchBulkConfig;
import com.vmware.vrack.hms.common.util.SshExecResult;
import com.vmware.vrack.hms.switches.cumulus.event.helper.CumulusPortEventHelper;
import com.vmware.vrack.hms.switches.cumulus.event.helper.CumulusSwitchUpDownEventHelper;
import com.vmware.vrack.hms.switches.cumulus.model.Configuration;
import com.vmware.vrack.hms.switches.cumulus.util.CumulusCache;

/**
 * Provides functionality for the entire Cumulus Switch Service.
 *
 * This class provides functionality for the switch service. Acts as a base class, all switch classes extend this class.
 */
@SwitchServiceImplementation(name = CumulusConstants.CUMULUS_SWITCH_TYPE)
public class CumulusTorSwitchService implements ISwitchService {

    /** Static timeout variable used for api functionality */
    private static final int HMS_SWITCH_CONNECTION_TIMEOUT = 20000;

    /**
     * Gets switch type of this instance from the CumulusConstants as a string.
     * @return String of switch type
     */
    @Override
    public String getSwitchType() {
        return CumulusConstants.CUMULUS_SWITCH_TYPE;
    }

    /**
     * Gets supported switch types
     * @return list of all switch types
     */
    @Override
    public List<SwitchType> getSupportedSwitchTypes() {
        SwitchType s1 = new SwitchType();
        s1.setManufacturer("Generic");
        s1.setModel(".*");
        s1.setRegexMatching(true);

        ArrayList<SwitchType> retList = new ArrayList<SwitchType>();
        retList.add(s1);
        return retList;
    }

    /**
     * Discovers switch for provided switch node
     *
     * Checks protocol (SSH), and executes command to discover the existing cumulus switch.
     *
     * @param switchNode switch node object
     * @return True if switch discovered; False if switch is not discovered.
     */
    @Override
    public boolean discoverSwitch(SwitchNode switchNode) {
        boolean isCumulus = false;

        if ("SSH".equalsIgnoreCase(switchNode.getProtocol())) {
            SwitchSession switchSession = getSession (switchNode);
            if (switchSession != null) {
                try {
                    String countStr = switchSession.execute(CumulusConstants.CUMULUS_DISCOVER_COMMAND);
                    if (countStr != null && countStr.length() > 0) {
                        int count = Integer.parseInt(countStr);
                        isCumulus = (count > 0);
                    }
                } catch (Exception e) {
                    logger.debug("Received exception while discovering Cumulus switch.", e);
                }
            }
        }

        return(isCumulus);
    }

    /**
     * Get the switch session based on the switch node
     *
     * @param switchNode switch node object
     * @return Switch Session corresponding to the switch node
     */
    @Override
    public SwitchSession getSession(SwitchNode switchNode) {
        return CumulusUtil.getSession(switchNode);
    }

    /**
     * Determine if switch is powered on
     *
     * For provided switch node object, get the ipAddress and determine if the switch is reachable based on the timeout variable.
     * @param switchNode switch node object
     * @return True if switch is reachable and powered on; False if switch is not reachable(not powered on)
     * @exception ioe Thrown if ip address is unable to determined if reachable or not
     */
    @Override
    public boolean isPoweredOn(SwitchNode switchNode) {
        //		String timeoutStr = HmsConfigHolder.getHMSConfigProperty(HmsConfigHolder.HMS_SWITCH_CONNECTION_TIMEOUT);
        //		int timeout = (timeoutStr != null) ? Integer.parseInt(timeoutStr) : 12000;
        String addr = switchNode.getIpAddress();
        boolean reachable = false;
        InetAddress ipAddress;

        try {
            ipAddress = Inet4Address.getByName(addr);
            reachable = ipAddress.isReachable(HMS_SWITCH_CONNECTION_TIMEOUT);
        } catch (IOException ioe) {
            logger.debug("Received exception while trying to reach " + addr, ioe);
        }

        return (reachable);
    }

    /**
     * Gets the Switch's os information
     *
     * For provided switch node, get the the switch operating system details, get last boot time, and firmware name and version.
     * @param switchNode switch node object
     * @return switch os info object with all details about the os.
     * @exception e Thrown if OS details failed to be gathered, or exception raised during last boot, or if Firmware details failed to be gathered
     */
    @Override
    public SwitchOsInfo getSwitchOsInfo(SwitchNode switchNode) {
        SwitchOsInfo osInfo = osInfoCache.get(switchNode);
        if (osInfo != null) {
            return osInfo;
        } else {
            osInfo = new SwitchOsInfo();
        }

        CumulusTorSwitchSession switchSession = (CumulusTorSwitchSession) getSession (switchNode);
        boolean cacheResult = true;

        /* Get switch operating system details. */
        try {
            SshExecResult result = switchSession.executeEnhanced(CumulusConstants.GET_SWITCH_OS_COMMAND);
            result.logIfError(logger);
            if (result.getExitCode() != 0)
                cacheResult = false;

            String osResult = result.getStdoutAsString();
            Properties osProperties = new Properties();
            osProperties.load(new StringReader(osResult));

            osInfo.setOsName(osProperties.getProperty("NAME").replaceAll("^\"|\"$", ""));
            osInfo.setOsVersion(osProperties.getProperty("VERSION_ID").replaceAll("^\"|\"$", ""));
        } catch (Exception e) {
            logger.warn("Exception received while gathering OS details.", e);
            cacheResult = false;
        }

        /* Get last boot time */
        try {
            SshExecResult result = switchSession.executeEnhanced(CumulusConstants.LAST_BOOT_TIME_COMMAND);
            result.logIfError(logger);
            if (result.getExitCode() != 0)
                cacheResult = false;

            String lastBootResult = result.getStdoutAsString();
            String[] lastBootResultArray = lastBootResult.split("\n");
            Double secondsSinceEpochString = Double.parseDouble(lastBootResultArray[0]);
            Double secondsSinceBoot = Double.parseDouble(lastBootResultArray[1]);
            long msBootTime = Math.round ((secondsSinceEpochString - secondsSinceBoot) * 1000);

            Date lastBootDate = new Date(msBootTime);
            osInfo.setLastBoot(lastBootDate);
        } catch (Exception e) {
            logger.warn("Exception received while getting last boot time.", e);
            cacheResult = false;
        }

        /* Get firmware name and version */
        try {
            String firmwareVersionCommand = CumulusConstants.GET_FIRMWARE_VER_COMMAND
                    .replaceAll("\\{password\\}", qr(switchNode.getPassword()));

            SshExecResult result = switchSession.executeEnhanced(firmwareVersionCommand);
            result.logIfError(logger);
            if (result.getExitCode() != 0)
                cacheResult = false;

            String firmwareVer = result.getStdoutAsString().trim();
            osInfo.setFirmwareName(CumulusConstants.FIRMWARE_NAME);
            osInfo.setFirmwareVersion(firmwareVer);
        } catch (Exception e) {
            logger.warn("Exception received while gathering firmware details.", e);
            cacheResult = false;
        }

        if (cacheResult)
            osInfoCache.set(switchNode, osInfo);

        return (osInfo);
    }

    /**
     * Get the switch hardware info
     *
     * Using the switch node, get the hardware model, the serial ID and the management port name.
     * @param switchNode switch node object
     * @return switch hardware info for the switch node
     * @exception e Thrown if hardware model and serial number on switch failed to received.
     */
    @Override
    public SwitchHardwareInfo getSwitchHardwareInfo(SwitchNode switchNode) {
        SwitchHardwareInfo hwInfo = hwInfoCache.get(switchNode);
        if (hwInfo != null) {
            return hwInfo;
        } else {
            hwInfo = new SwitchHardwareInfo();
        }

        CumulusTorSwitchSession switchSession = (CumulusTorSwitchSession) getSession(switchNode);
        boolean cacheResult = true;

        try {
            SshExecResult result = switchSession.executeEnhanced(CumulusConstants.HARDWARE_MODEL_MANUFACTURER_COMMAND);
            result.logIfError(logger);
            if (result.getExitCode() != 0)
                cacheResult = false;

            String[] hwResult = result.getStdoutAsString().split(",");
            if (hwResult.length >= 2) {
                hwInfo.setManufacturer(hwResult[0].trim());
                hwInfo.setModel(hwResult[1].trim());
            }

            String serialCommand = CumulusConstants.GET_SERIAL_ID_COMMAND.replaceAll("\\{password\\}", qr(switchNode.getPassword()));
            result = switchSession.executeEnhanced(serialCommand);
            result.logIfError(logger);
            if (result.getExitCode() != 0)
                cacheResult = false;

            String serialResult = result.getStdoutAsString().trim();
            hwInfo.setChassisSerialId(serialResult);

            String partNumberCommand = CumulusConstants.GET_PART_NUMBER_COMMAND.replaceAll("\\{password\\}", qr(switchNode.getPassword()));
            result = switchSession.executeEnhanced(partNumberCommand);
            result.logIfError(logger);
            if (result.getExitCode() != 0)
                cacheResult = false;

            String partNumberResult = result.getStdoutAsString().trim();
            hwInfo.setPartNumber(partNumberResult);

            String manufactureDateCommand = CumulusConstants.GET_MANUFACTURE_DATE_COMMAND.replaceAll("\\{password\\}", qr(switchNode.getPassword()));
            result = switchSession.executeEnhanced(manufactureDateCommand);
            result.logIfError(logger);
            if (result.getExitCode() != 0)
                cacheResult = false;

            String manufactureDateResult = result.getStdoutAsString().trim();
            hwInfo.setManufactureDate(manufactureDateResult);

            /* Get MAC address of management port */
            SwitchPort mgmtPort = getSwitchPort(switchNode, CumulusConstants.MANAGEMENT_PORT_NAME);
            if (mgmtPort != null) {
                hwInfo.setManagementMacAddress(mgmtPort.getMacAddress());
            } else {
                logger.warn ("Couldn't determine management MAC address of management port on switch " + switchNode.getSwitchId());
                cacheResult = false;
            }
        } catch (Exception e) {
            logger.warn ("Exception received while getting hardware model and serial number on switch " + switchNode.getSwitchId(), e);
            cacheResult = false;
        }

        if (cacheResult)
            hwInfoCache.set(switchNode, hwInfo);

        return (hwInfo);
    }

    /**
     * Update the switch IP address with new details
     *
     * For the given switch node, execute command to change the IP address, netmask, and gateway.
     * 	Configure persistence directory and make changes effective immediately.
     * @param switchNode switch node object
     * @param ipAddress IpAddress to be updated to
     * @param netmask Netmask to be updated to
     * @param gateway Gateway to be updated to
     * @return True if update was successful; False if update was unsuccessful
     * @exception HmsException if error in updating the IP address, netmask or gateway values
     */
    @Override
    public boolean updateSwitchIpAddress(SwitchNode switchNode, String ipAddress, String netmask, String gateway) throws HmsException {

        CumulusTorSwitchSession session = (CumulusTorSwitchSession) getSession(switchNode);
        String command = CumulusConstants.CHANGE_SWITCH_IP_COMMAND
                .replaceAll("\\{password\\}", qr(switchNode.getPassword()))
                .replaceAll("\\{address\\}", ipAddress != null ? ipAddress.trim() : "")
                .replaceAll("\\{netmask\\}", netmask != null ? netmask.trim() : "")
                .replaceAll("\\{gateway\\}", gateway != null ? gateway.trim() : "");

        SshExecResult result = session.executeEnhanced(command);
        result.logIfError(logger);
        if (result.getExitCode() != 0) {
            throw new HmsException ("Error updating the IP address of switch " + switchNode.getSwitchId());
        }

        /* Configure persistence directory */
        CumulusUtil.configurePersistenceDirectory(switchNode);

        /* Finally, make change effective immediately */
        command = CumulusConstants.IFUP_COMMAND
                .replaceAll("\\{password\\}", qr(switchNode.getPassword()))
                .replaceAll("\\{interfaces\\}", CumulusConstants.MANAGEMENT_PORT_NAME);

        result = session.executeEnhanced(command);
        result.logIfError(logger);

        return (result != null && result.getExitCode() == 0);
    }

    @Override
    public boolean updateSwitchTimeServer(SwitchNode switchNode, String timeServer) throws HmsException {
        CumulusTorSwitchSession session = (CumulusTorSwitchSession) getSession(switchNode);
        String command = CumulusConstants.SET_NTP_SERVER_COMMAND
                .replaceAll("\\{password\\}", qr(switchNode.getPassword()))
                .replaceAll("\\{timeServer\\}", timeServer.trim());

        SshExecResult result = session.executeEnhanced(command);
        result.logIfError(logger);
        if (result.getExitCode() != 0) {
            throw new HmsException ("Error updating the IP address of switch " + switchNode.getSwitchId());
        }

        /* Configure persistence directory */
        CumulusUtil.configurePersistenceDirectory(switchNode);

        return (result != null && result.getExitCode() == 0);
    }

    /**
     * Get the switch port list
     *
     * For the switch node, execute command to get list of ports.
     * @param switchNode switch node object
     * @return list of switch ports
     * @exception e Thrown when unable to execute get ip list of ports on the Switch Session
     */
    @Override
    public List<String> getSwitchPortList(SwitchNode switchNode) {
        List<String> portList = portListCache.get(switchNode);
        if (portList != null) {
            return portList;
        } else {
            portList = new ArrayList<String>();
        }

        SshExecResult result = null;
        String portResult;

        try {
            CumulusTorSwitchSession switchSession = (CumulusTorSwitchSession) getSession(switchNode);
            result = switchSession.executeEnhanced(CumulusConstants.IP_LIST_PORTS_COMMAND);
            result.logIfError(logger);

            portResult = result.getStdoutAsString();
            for (String portLine : portResult.split("\n")) {
                String[] portArr = portLine.split(":", 3);
                if (portArr.length >= 3) {
                    String p = portArr[1].trim();
                    if (p.matches("eth[0-9]+|swp[0-9]+"))
                        portList.add(p);
                }
            }

            if (portList.size() > 4) {
                portListCache.set(switchNode, portList);
            }
        } catch (Exception e) {
            logger.error("Exception occured while getting switch ports", e);
        }

        return portList;
    }

    /**
     * Get switch port object
     *
     * For the switch node, using the port name - get the switch port
     * @param switchNode switch node object
     * @param portName port name to get
     * @return switch port that was selected
     */
    @Override
    public SwitchPort getSwitchPort(SwitchNode switchNode, String portName) {
        SwitchPort retPort = null;

        if (portName != null && !portName.trim().equals("")) {
            List<SwitchPort> allPorts = getSwitchPortListBulk(switchNode);
            for (SwitchPort port : allPorts) {
                if (portName.trim().equalsIgnoreCase(port.getName())) {
                    retPort = port;
                }
            }
        }

        return (retPort);
    }

    /**
     * Get the switch port status
     *
     * Using the switch node and node name, get the status of the switch port.
     * @param switchNode switch node object
     * @param portName port name to get status for
     * @return port status of the port
     */
    @Override
    public PortStatus getSwitchPortStatus(SwitchNode switchNode, String portName) {
        SwitchPort port = getSwitchPort(switchNode, portName);
        return ((port != null) ? port.getStatus() : null);
    }

    /**
     * Set the switch port status
     *
     * For the switch node, set the switch port status with the new portStatus value.
     * @param switchNode switch node object
     * @param portName port name for which needs to set the port status
     * @param portStatus port status value
     * @return True if the switch node's port status is set; False if the switch node port status fails to get set
     * @exception HmsException Thrown when error in changing port status for the portName
     */
    @Override
    public boolean setSwitchPortStatus(SwitchNode switchNode, String portName, PortStatus portStatus) throws HmsException {
        SwitchSession switchSession = getSession(switchNode);
        boolean success = false;

        String command = CumulusConstants.CHANGE_PORT_STATUS_COMMAND
                .replaceAll("\\{portName\\}", qr(portName))
                .replaceAll("\\{status\\}", portStatus == SwitchPort.PortStatus.UP ? "up" : "down")
                .replaceAll("\\{password\\}", qr(switchNode.getPassword()));

        SwitchPort port = getSwitchPort(switchNode, portName);

        if (port.getType() != PortType.LOOPBACK && port.getType() != PortType.MANAGEMENT) {
            try {
                switchSession.execute(command);
                success = true;
            } catch (Exception e) {
                logger.error("Error while changing port status of " + portName + " to " + portStatus, e);
                throw new HmsException ("Error while changing port status of " + portName + " to " + portStatus, e);
            }
        }

        if (success)
            portsBulkCache.setStale(switchNode);

        return (success);
    }

    private void updateSwitchPortNoRevert(SwitchNode switchNode, String portName, SwitchPort newPort,
            SwitchPort currPort) throws HmsOobNetworkException {
        final String speedDef = "default";
        final String speed1G = "1G";
        final String speed10G = "10G";
        final String speed40G = "40G";
        String mode = "manual";
        String address = "";
        String gateway = "";
        String mtu = "";
        String others = "";
        boolean writeStanza = false;
        CumulusTorSwitchSession session = (CumulusTorSwitchSession) getSession(switchNode);

        if (session == null) {
            error ("Cannot reach switch " + switchNode.getIpAddress(), HmsOobNetworkErrorCode.SWITCH_UNREACHABLE);
        }

        /* Input validation */
        if (switchNode == null) {
            error ("Cannot process null switch.", HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);
        } else if (portName == null || portName.equals("")) {
            error ("Invalid port name " + portName, HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);
        } else if (newPort == null) {
            error ("Cannot update port with null port information", HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);
        }

        /* First check if we are changing the status only */
        if (newPort.getStatus() != null) {
            try {
                setSwitchPortStatus(switchNode, portName, newPort.getStatus());
            } catch (HmsException e) {
                error ("Cannot set port status", e, HmsOobNetworkErrorCode.SET_OPERATION_FAILED);
            }
            if (newPort.getStatus() == PortStatus.DOWN) {
                return;
            }
        }

        /* Change port MTU if specified */
        if (newPort.getMtu() > 0) {
            /* Change the current settings on the port */
            String setMtuCommand = CumulusConstants.SET_MTU_COMMAND
                    .replaceAll("\\{password\\}", qr(switchNode.getPassword()))
                    .replaceAll("\\{name\\}", portName)
                    .replaceAll("\\{mtu\\}", Integer.toString(newPort.getMtu()));

            SshExecResult result = null;
            try {
                result = session.executeEnhanced(setMtuCommand);
            } catch (HmsException e) {
                error ("Failed to set MTU (Reason could not be retrieved. Is Null.)",
                        HmsOobNetworkErrorCode.SET_OPERATION_FAILED);
            }
            if (result != null && result.getExitCode() != 0) {
                if (result.getStdout().length > 0)
                    logger.error("stdout: " + result.getStdoutAsString());

                error ("Failed to set MTU, Reason: " + result.getStderrAsString(),
                        HmsOobNetworkErrorCode.SET_OPERATION_FAILED);
            }

            /* Change the persistent settings in the interfaces file */
            writeStanza = true;
            mtu = CumulusConstants.MTU_LINE
                    .replaceAll("\\{mtu\\}", Integer.toString(newPort.getMtu()))
                    .replaceAll("\\{name\\}", portName);
        }

        /*
         * Invoke ethtool to modify 'all' of the three parameters - speed, duplex and autoneg.
         * If speed is specified as "default", do not pass the speed parameter to ethtool
         * and there should be no speed entry for the port in interfaces file. We should
         * ideally also set the port to the speed specified in /etc/cumulus/ports.conf, but
         * finding it out is not straightforward.
         */
        if ((newPort.getSpeed() != null) && (newPort.getAutoneg() != null) && (newPort.getDuplex() != null)) {
            int speedInMBPerSec = 0;
            String duplex = "full";
            String autoNeg = "on";

            if (newPort.getSpeed().equals(speedDef)) {
                speedInMBPerSec = 0;
            }
            else if (newPort.getSpeed().equals(speed1G)) {
                speedInMBPerSec = 1000;
            }
            else if (newPort.getSpeed().equals(speed10G)) {
                speedInMBPerSec = 10000;
            }
            else if (newPort.getSpeed().equals(speed40G)) {
                speedInMBPerSec = 40000;
            }
            else if (newPort.getSpeed().equals("UNKNOWN")) {
                speedInMBPerSec = -1; /* Indicate that this is unknown but do not throw error at this time */
            }
            else {
                error ("Unsupported port speed. It can have values 1G, 10G, 40G only", HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);
            }

            if (newPort.getDuplex() == SwitchPort.PortDuplexMode.HALF) {
                duplex = "half";
            } else if (newPort.getDuplex() != SwitchPort.PortDuplexMode.FULL) {
                error ("Unsupported duplex mode. It can be FULL or HALF only", HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);
            }

            if (newPort.getAutoneg() == SwitchPort.PortAutoNegMode.OFF) {
                autoNeg = "off";
            } else if (newPort.getAutoneg() != SwitchPort.PortAutoNegMode.ON) {
                error ("Unsupported autoneg mode. It can be ON or OFF only", HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);
            }

            String setCommand = CumulusConstants.SET_SPEED_DUPLEX_AUTONEG_PREAMBLE
                    .replaceAll("\\{password\\}", qr(switchNode.getPassword()))
                    .replaceAll("\\{name\\}", portName);

            // Add each addendum if specified. If speed is specified as 'default', do
            // not pass the speeed parameter to ethtool.
            if (speedInMBPerSec > 0)
                setCommand +=" " + CumulusConstants.SET_SPEED_ADDENDUM
                        .replaceAll("\\{speed\\}", Integer.toString(speedInMBPerSec));

            setCommand += " " + CumulusConstants.SET_DUPLEX_ADDENDUM
                    .replaceAll("\\{duplex\\}", duplex);

            setCommand += " " + CumulusConstants.SET_AUTONEG_ADDENDUM
                    .replaceAll("\\{autoneg\\}", autoNeg);

            SshExecResult result = null;
            try {
                result = session.executeEnhanced(setCommand);
            } catch (HmsException e) {
                error ("Failed to set Speed = " + newPort.getSpeed() + ", Auto Negotation Mode = " +
                                autoNeg + ", Duplex Mode = " + duplex + " on switch port " + portName +
                                " (Reason could not be retrieved. Is Null)",
                        HmsOobNetworkErrorCode.SET_OPERATION_FAILED);
            }
            if (result != null && result.getExitCode() != 0) {
                if (result.getStdout().length > 0)
                    logger.error("stdout: " + result.getStdoutAsString());

                error ("Failed to set Speed = " + newPort.getSpeed() + ", Auto Negotation Mode = " +
                                autoNeg + ", Duplex Mode = " + duplex + " on switch port " + portName +
                                ". Reason: " + result.getStdoutAsString(),
                        HmsOobNetworkErrorCode.SET_OPERATION_FAILED);
            }

            /* Change the persistent settings in the interfaces file */
            writeStanza = true;
            others = CumulusConstants.AUTONEG_DUPLEX_LINES
                    .replaceAll("\\{name\\}", portName)
                    .replaceAll("\\{duplex\\}", duplex)
                    .replaceAll("\\{autoneg\\}", autoNeg);

            if (speedInMBPerSec > 0)
                others += CumulusConstants.SPEED_LINE_ADDENDUM
                        .replaceAll("\\{speed\\}", Integer.toString(speedInMBPerSec));

        } else if (!(newPort.getSpeed() == null && newPort.getAutoneg() == null && newPort.getDuplex() == null)) {
            error ("All of port speed, auto negotiation mode and duplex mode must be present",
                    HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);
        }

        /* 
         * Change/set/reset IP address only inside interfaces configuration file and at the end to an ifreload which takes the
         *  appropriate action
         */
        String ipAddress = newPort.getIpAddress() != null ? newPort.getIpAddress().trim() : "";
        if (!ipAddress.equals("")) {
        	writeStanza = true;
        	address = CumulusConstants.IPV4_ADDRESS_LINE.replaceAll("\\{address\\}", ipAddress);
        }
        else if (currPort.getIpAddress() != null) {
        	writeStanza = true;
        }

        if (writeStanza) {
            String remoteFilename = CumulusUtil.getPortFilename(portName);

            String stanza = CumulusConstants.SWITCH_PORT_STANZA
                    .replaceAll("\\{name\\}", portName)
                    .replaceAll("\\{mode\\}", mode)
                    .replaceAll("\\{ipv4\\}", address)
                    .replaceAll("\\{gateway\\}", gateway)
                    .replaceAll("\\{mtu\\}", mtu)
                    .replaceAll("\\{others\\}", others);

            ByteArrayInputStream bais = new ByteArrayInputStream(stanza.getBytes());

            /* Upload the file */
            /*
             * All these operations are for now translated but in future these methods themselves will throw the specialized exception
             */
            try {
                CumulusUtil.validateSourceClause(switchNode);
            } catch (Exception e) {
                error ("Failed to validate source clause", e, HmsOobNetworkErrorCode.INTERNAL_ERROR);
            }
            try {
                CumulusUtil.uploadAsRoot(switchNode, bais, remoteFilename);
            } catch (Exception e) {
                error ("Failed to upload modified port configuration file", e, HmsOobNetworkErrorCode.UPLOAD_FAILED);
            }
            try {
                CumulusUtil.configurePersistenceDirectory(switchNode);
            } catch (Exception e) {
                error ("Failed to save new configurations inside persistent directory", e, HmsOobNetworkErrorCode.INTERNAL_ERROR);
            }

            /* Activate the changes in the current session */
            try {
                SshExecResult result = session.executeEnhanced(CumulusConstants.RELOAD_INTERFACES
                        .replaceAll("\\{password\\}", CumulusUtil.qr(switchNode.getPassword())));
                result.logIfError(logger);
            } catch (HmsException e) {
                error("Failed to reload interfaces on the switch", e, HmsOobNetworkErrorCode.INTERNAL_ERROR);
            }
        }

        portsBulkCache.setStale(switchNode);
    }

    /**
     * Update the switch port details
     *
     * For the switch node object, update details for either(all) items listed in the portInfo object. Updates cover status of port, MTU, and IP address.
     * 	Updates for each type is captured in the portInfo object.
     *
     * This basically overwrites all existing configuration for this port with the new ones passed
     *
     * @param switchNode switch node object
     * @param portName port name of port that needs updates
     * @param portInfo port info contains values associated with the switch node
     * @return True if update on Switch port was successful; False if update was unsuccessful
     * @exception HmsException when switchNode is null, portName is invalid, or failure to update the portInfo details
     */
    @Override
    public boolean updateSwitchPort(SwitchNode switchNode, String portName, SwitchPort portInfo) throws HmsException {
        boolean ret = false;
        SwitchPort earlierPortInfo = getSwitchPort(switchNode, portName);

        try {
            updateSwitchPortNoRevert(switchNode, portName, portInfo, earlierPortInfo);
            ret = true;
        } catch (HmsOobNetworkException e) {
            /*
             * It makes sense to retry with older configuration if and only if setting the new parameters has failed, for other errors
             * the revert process may (very likely) also fail
             */
            if (e.getErrorCode() == HmsOobNetworkErrorCode.SET_OPERATION_FAILED) {
                try {
                    updateSwitchPortNoRevert(switchNode, portName, earlierPortInfo, null);

                } catch (HmsOobNetworkException e1) {
                    error ("Unable to revert to previous snapshot on failure to set port properties", e1, HmsOobNetworkErrorCode.ROLLBACK_FAILED);
                }
            }
            /*
             * Even if we set the earlier properties correctly we still fail the operation and rethrow the
             * exception
             */
            throw e;
        }

        return ret;
    }

    /**
     * Get vlan for specified switch node. (Implementation in CumulusVlanHelper file)
     *
     * Using vlanHelper class, get appropriate switchVlan for switchNode
     *
     * @param switchNode switch node object
     * @param vlanName vlan name (string) to get
     * @return SwitchVlan object
     * @exception HmsException Thrown if SwitchVlan object failed to be found.
     */
    @Override
    public SwitchVlan getSwitchVlan(SwitchNode switchNode, String vlanName) throws HmsException {
        return vlanHelper.getSwitchVlan(switchNode, vlanName);
    }

    /**
     * Get vlans for specified switch node (Implementation in CumulusVlanHelper file)
     *
     * Get list of Vlans for the provided switchNode.
     *
     * @param switchNode switch node object
     * @return list of Strings of all vlans associated with provided switch node
     */
    @Override
    public List<String> getSwitchVlans(SwitchNode switchNode) {
        return vlanHelper.getSwitchVlans(switchNode);
    }

    /**
     * Get vlan in bulk format (Implementation in CumulusVlanHelper file)
     *
     * Get vlan details for provided switchNode in bulk format.
     *
     * @param switchNode switch node object
     * @return List of all switch vlans for provided switch node
     */
    @Override
    public List<SwitchVlan> getSwitchVlansBulk(SwitchNode switchNode) {
        return vlanHelper.getSwitchVlansBulk(switchNode);
    }

    /**
     * Create a vlan for a switch node with the provide vlan. (Implementation in CumulusVlanHelper file)
     *
     * Create Vlan with provided vlan object on the switchNode
     *
     * @param switchNode switch node object
     * @param vlan switch vlan object used to create a vlan
     * @return True if creation of vlan was successful; False if creation of vlan was unsuccessful.
     */
    @Override
    public boolean createVlan(SwitchNode switchNode, SwitchVlan vlan) throws HmsException {
        return vlanHelper.createVlan(switchNode, vlan);
    }

    /**
     * Update vlan details
     *
     * Updates to the vlan object for the switchNode object (Implementation in CumulusVlanHelper file)
     *
     * @param switchNode switch node object
     * @param vlanName vlan name that needs to be updated
     * @param vlan switch vlan object
     * @return True if update to the vlan was successful; False if the update was unsuccessful.
     */
    @Override
    public boolean updateVlan(SwitchNode switchNode, String vlanName, SwitchVlan vlan) throws HmsException {
        return vlanHelper.updateVlan(switchNode, vlanName, vlan);
    }

    /**
     * Delete vlan for provided switch node
     *
     * Deletion of a vlan on the provided switch node
     *
     * @param switchNode switch node object
     * @param vlanName vlan name of vlan to be deleted
     * @return True if deletion of vlan was successful; False if deletion of vlan was unsuccessful
     */
    @Override
    public boolean deleteVlan(SwitchNode switchNode, String vlanName) throws HmsException {
        return vlanHelper.deleteVlan(switchNode, vlanName);
    }

    /**
     * Get lacp groups
     *
     * Get lacp groups for provided switch node (Implementation in CumulusLacpGroupHelper file)
     *
     * @param switchNode switch node object
     * @return list of all lacp groups for provided switch node
     */
    @Override
    public List<String> getSwitchLacpGroups(SwitchNode switchNode) {
        List<String> bondList = bondListCache.get(switchNode);
        if (bondList != null) {
            return bondList;
        }

        bondList = lacpHelper.getSwitchLacpGroups(switchNode);

        bondListCache.set(switchNode, bondList);

        return bondList;
    }

    /**
     * Get Switch Lacp Group
     *
     * Get a lacp group for provided switch node and lacp group name (Implementation in CumulusLacpGroupHelper file)
     *
     * @param switchNode switch node object
     * @param lacpGroupName lacp group name associated with the switch node
     * @return Switch Lacp Group object
     * @exception HmsException Thrown if lacp group is null, and unable to be retrieved for the switchNode
     */
    @Override
    public SwitchLacpGroup getSwitchLacpGroup(SwitchNode switchNode, String lacpGroupName) throws HmsException {
        return lacpHelper.getSwitchLacpGroup(switchNode, lacpGroupName);
    }

    /**
     * Create Lacp Group
     *
     * Create lacp group for the switch node (Implementation in CumulusLacpGroupHelper file)
     *
     * @param switchNode switch node object
     * @param lacpGroup lacp group to be created
     * @return True if creation of lacp group was successful; False if lacp group was not created successfully
     * @exception HmsException Thrown if lacp group is unable to be created for the switchNode
     */
    @Override
    public boolean createLacpGroup(SwitchNode switchNode, SwitchLacpGroup lacpGroup) throws HmsException {
        boolean result = false;

        /*
         * First make sure if a bond exists by this name then we delete it
         * first, there might be a more optimized way of doing it but this will
         * serve all purpose for now
         */
        if (!deleteLacpGroup(switchNode, lacpGroup.getName())) {
            logger.error("Not able to delete existing LACP group as part of updating existing LAG");
            return false;
        }

        result = vlanHelper.updateLAGOnSwitchPorts(switchNode, lacpGroup);

        if (result)
            bondListCache.setStale(switchNode);

        return result;
    }

    /**
     * Delete a lacp group
     *
     * Delete lacp group for provided switch node (Implementation in CumulusLacpGroupHelper file)
     *
     * @param switchNode switch node object
     * @param lacpGroupName lacp group name to be deleted
     * @return True if deletion of lacp group was successful; False if deletion of lacp group was unsuccessful
     * @exception HmsException Thrown if lacp group is null, and unable to be deleted for the switchNode
     */
    @Override
    public boolean deleteLacpGroup(SwitchNode switchNode, String lacpGroupName) throws HmsException {
        boolean result = false;

        result = lacpHelper.deleteLacpGroup(switchNode, lacpGroupName);

        if (result)
            bondListCache.setStale(switchNode);

        return result;
    }

    /**
     * Reboot the switch node object that is provided
     *
     * Execute command to reboot the switch session.
     * @param switchNode switch node to reboot
     * @return True if reboot was successful; False if reboot is unsuccessful
     * @exception HmsException Thrown if reboot of switch receives an error
     */
    @Override
    public boolean reboot(SwitchNode switchNode) throws HmsException {
        SwitchSession switchSession = getSession(switchNode);
        boolean success = false;
        String command = CumulusConstants.SWITCH_REBOOT_COMMAND
                .replaceAll("\\{password\\}", qr(switchNode.getPassword()));

        String rebootOutput;
        try {
            rebootOutput = switchSession.execute(command);
            success = true;
            logger.debug ("Output of reboot command: " + rebootOutput);
        } catch (Exception e) {
            throw new HmsException ("Error received while rebooting switch", e);
        }

        return success;
    }

    /**
     * Upgrade the switch node
     *
     * For passed in upgrade info, validate HTTP and FTP links first before executing upgrade command to install the new image on the switch.
     * 	Reboot the node so that the new upgraded details will take effect.
     *
     * @param switchNode switch node object that will receive upgrade
     * @param upgradeInfo upgrade info details
     * @return True if upgrade is successful; False if upgrade is unsuccessful
     * @exception HmsException Thrown if upgrade of the switch fails when upgrade package URL is null
     */
    @Override
    public boolean upgrade(SwitchNode switchNode, SwitchUpgradeInfo upgradeInfo) throws HmsException {
        SshExecResult result;

        /* Validate input. */
        if (upgradeInfo == null || upgradeInfo.getPackageUrl() == null)
            throw new HmsException ("Cannot upgrade without an upgrade package URL.");

        String lcurl = upgradeInfo.getPackageUrl().toLowerCase();

        /* Validate HTTP and FTP links */
        if (lcurl.startsWith("http://") || lcurl.startsWith("ftp://")) {
            String validateCommand = CumulusConstants.SWITCH_UPGRADE_CHECK_PKG_COMMAND
                    .replaceAll("\\{package\\}", upgradeInfo.getPackageUrl());

            CumulusTorSwitchSession session = (CumulusTorSwitchSession) getSession(switchNode);

            result = session.executeEnhanced(validateCommand);
            result.logIfError(logger);

            if (result.getExitCode() != 0) {
                throw new HmsObjectNotFoundException ("Cannot upgrade because package URL is either invalid or inaccessible: " + upgradeInfo.getPackageUrl());
            }
        }

        /* Step 1: Install new image */
        String command = CumulusConstants.SWITCH_UPGRADE_COMMAND
                .replaceAll("\\{password\\}", qr(switchNode.getPassword()))
                .replaceAll("\\{package\\}", upgradeInfo.getPackageUrl());

        boolean success = false;
        CumulusTorSwitchSession session = (CumulusTorSwitchSession) getSession(switchNode);

        try {
            result = session.executeEnhanced(command);
            result.logIfError(logger);
            success = result.getExitCode() == 0;
        } catch (Exception e) {
            throw new HmsException ("Error received while installing new OS image on switch " + switchNode.getSwitchId(), e);
        }

        /* Save persistent configuration before rebooting. */
        CumulusUtil.configurePersistenceDirectory(switchNode);

        /* Step 2: Reboot the node for the new changes to take effect. */
        if (success)
            reboot (switchNode);
        else
            throw new HmsException ("Error received while installing new OS image on switch " + switchNode.getSwitchId());

        return success;
    }

    /**
     * Apply network configuration updates to the switch node
     *
     * For existing switch node, apply command to update the network configuration details and upload interfaces file.
     *
     * @param switchNode switch node object
     * @param networkConfiguration network configuration details
     * @return True if network configuration applied successfully; False if network configuration is not applied successfully
     * @exception HmsException Thrown uploading and applying network configuration for the switchNode fails
     */
    @Deprecated
    @Override
    public boolean applyNetworkConfiguration(SwitchNode switchNode, SwitchNetworkConfiguration networkConfiguration)
            throws HmsException {
        boolean success = true;
        SwitchSession switchSession = getSession (switchNode);
        CumulusEtcNetworkInterfaces etcNetworkInterfacesFile =
                CumulusEtcNetworkInterfaces.parseNetworkConfiguration(networkConfiguration);

        logger.debug("Contents of interfaces file: \n" + etcNetworkInterfacesFile);

        String remoteTmpFile = CumulusConstants.PERSISTENT_TMP_DIR + "/interfaces." + System.currentTimeMillis();
        InputStream localInputStream = etcNetworkInterfacesFile.getInputStream();

        /* Apply network configuration */
        String command = CumulusConstants.APPLY_NETWORK_CFG_COMMAND
                .replaceAll("\\{file\\}", qr(remoteTmpFile))
                .replaceAll("\\{password\\}", qr(switchNode.getPassword()));

        try {
            switchSession.upload(localInputStream, remoteTmpFile);

            logger.debug("Uploaded interfaces file to " + remoteTmpFile);

            switchSession.execute(command);
        } catch (Exception e) {
            success = false;
            throw new HmsException ("Error uploading and applying network configuration", e);
        }

        return(success);
    }

    /**
     * Get Switch Vxlans
     *
     * Get vxlan details details for switch node (Implementation in CumulusVxlanHelper file)
     *
     * @param switchNode switch node object
     * @return list of all vxlans associated with this switch node
     */
    @Override
    public List<SwitchVxlan> getSwitchVxlans(SwitchNode switchNode) {
        return vxlanHelper.getSwitchVxlans(switchNode);
    }

    /**
     * Get Switch vxlans matching vlan
     *
     * Get vxlans associated with a vlan (Implementation in CumulusVxlanHelper file)
     *
     * @param switchNode switch node object
     * @param vlanName vlan name
     * @return list of all vxlans associated with the vlan name provided
     */
    @Override
    public List<SwitchVxlan> getSwitchVxlansMatchingVlan(SwitchNode switchNode, String vlanName) {
        return vxlanHelper.getSwitchVxlansMatchingVlan(switchNode, vlanName);
    }

    /**
     * Create Vxlan
     *
     * Create new vxlan for switch node (Implementation in CumulusVxlanHelper file)
     *
     * @param switchNode switch node object
     * @param vxlan Vxlan object to be created
     * @return True if creation of vxlan successful; False if creation of vxlan was unsuccessful
     * @exception HmsException Thrown if Vxlan creation fails on switchNode
     */
    @Override
    public boolean createVxlan(SwitchNode switchNode, SwitchVxlan vxlan) throws HmsException {
        return vxlanHelper.createVxlan(switchNode, vxlan);
    }

    /**
     * Delete Vxlan
     *
     * Delete vxlan for switch node (Implementation in CumulusVxlanHelper file)
     *
     * @param switchNode switch node object
     * @param vxlanName Vxlan to be deleted
     * @param vlanName associated vlan to the vxlan
     * @return True if deletion of vxlan was successful; False if deletion of vxlan is unsuccessful
     * @exception HmsException Thrown when deletion of vxlan fails
     */
    @Override
    public boolean deleteVxlan(SwitchNode switchNode, String vxlanName, String vlanName) throws HmsException {
        return vxlanHelper.deleteVxlan(switchNode, vxlanName, vlanName);
    }

    /**
     * Get switch port list (bulk format)
     *
     * Using the switch node provided, get the switch port list (list of switch ports). Gather the lldp, fdb, and port results.
     *  Parse for MTU, Mac Address, link speed, RX and TX packet counts, add to specific port and return list of switch ports.
     *
     * @param switchNode switch node object
     * @return list of switchport objects
     */
    @Override
    public List<SwitchPort> getSwitchPortListBulk(SwitchNode switchNode) {
        final Pattern phySettingsPattern = Pattern.compile("(Settings for) (swp.*|eth.*):$");
        List<SwitchPort> portList = portsBulkCache.get(switchNode);

        if (portList != null) {
            return portList;
        } else {
            portList = new ArrayList<SwitchPort>();
        }

        SwitchSession switchSession = getSession(switchNode);
        String[] portResultArr = null;
        String[] ipAddressResultLines = null;
        String[] fdbResultLines = null;
        String[] lldpResultLines = null;
        String[] portPhysicalPropertiesArr = null;
        Date runDate = null;
        SwitchPort port = null;
        int blockStart = 0, blockEnd = 0;
        Map<String, Set<String>> fdb = new HashMap<String, Set<String>>();
        Map<String, SwitchLinkedPort> lldpDb = new HashMap<String, SwitchLinkedPort>();
        Map<String, SwitchPort> physicalPortPropertiesDb = new HashMap<String, SwitchPort>();
        Map<String, String> portIpAddressDb = new HashMap<String, String>();

        String[] commandSet = {
                "echo '{password}' | sudo -S lldpcli show neighbors".replaceAll("\\{password\\}",
                        switchNode.getPassword()),
                "/bin/bridge fdb show",
                "ip -s link show",
                "ip -br addr show",
                /*
                 * Get per port details, due to limitation of 'ethtool' command which works only with a port name
                 * and not on a list of interfaces/wildcard. Hence, this is the only way to preserve efficiency of this
                 * method and yet achieve end goal
                 */
                ("for port in `ls -l /sys/class/net/ | awk '{if (/.*swp.*/) print $9; else if (/.*eth.*/) print $9}'`; do echo '{password}' | sudo -S ethtool $port; done").replaceAll("\\{password\\}", switchNode.getPassword())
        };

        String[][] combinedOutput = GetMultipleCmdResults(switchSession, commandSet);

        if (combinedOutput.length < 4) {
            logger.error("Failed to retrieve necessary information from switch");
            return null;
        }

        /* Get all the output in pre-formatted avatar now */
        lldpResultLines = combinedOutput[0];
        fdbResultLines = combinedOutput[1];
        portResultArr = combinedOutput[2];
        ipAddressResultLines = combinedOutput[3];
        portPhysicalPropertiesArr = combinedOutput[4];

        runDate = new Date(System.currentTimeMillis());

        /*
         * O(N1.log(N1)) loop where N1 = number of lines in forwarding database
         */
        if (fdbResultLines != null && fdbResultLines.length > 0) {
            for (String fdbLine : fdbResultLines) {
                Matcher mac2m = Pattern.compile("^([0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2}){5})").matcher(fdbLine.trim());
                if (mac2m.matches()) {
                    String mac = mac2m.group(1).trim(); /* This is the MAC Address */
                    String[] tokens = fdbLine.split("\\s+");
                    String portName = "";

                    if (tokens.length >= 6) {
                        /* We have enogh tokens to get our desired values */
                        String[] tokens2 = tokens[2].split("\\.");

                        if (tokens2.length > 1) {
                            portName = tokens2[0];
                        }
                        else {
                            portName = tokens[2];
                        }

                        /* Now get MAC */
                        if (fdb.get(portName) == null) {
                            fdb.put(portName, new HashSet<String>());
                        }
                        Set<String> macs = fdb.get(portName);
                        macs.add(mac);
                    }
                }
            }
        }

        /* Parse through the LLDP output */
        if (lldpResultLines != null && lldpResultLines.length > 0) {
            String val = "";
            String portname = "";
            SwitchLinkedPort lp = null;
            for (String lldpLine : lldpResultLines) {
                if (lldpLine.matches(".*Interface:.*")) {
                    val = lldpLine.split(":")[1].trim();
                    portname = val.split(",")[0];

                    if (lldpDb.get(portname) == null) {
                        lp = new SwitchLinkedPort();
                        lldpDb.put(portname, lp);
                    }
                    else {
                        lp = lldpDb.get(portname);
                    }
                } else if (lldpLine.matches(".*SysName:.*")) {
                    val = lldpLine.split(":")[1].trim();
                    lp.setDeviceName(val);
                } else if (lldpLine.matches("\\s+MgmtIP:.*")) {
                    String ipAddress = lldpLine.split(":")[1].trim();
                    if (ipAddress != null && ipAddress.length() > 0 && !ipAddress.equals("0.0.0.0")) {
                        lp.setDeviceName(ipAddress);
                    }
                }
                else if (lldpLine.matches(".*PortID:.*") || lldpLine.matches(".*ChassisID:.*")) {
                    val = lldpLine.split(":", 2)[1].trim();
                    /*
                     * Cumulus has this weird way of prepending "iface" to the actual port name.
                     * Let's make sure we get the actual port id
                     */
                    String[] tokens = val.split("\\s+");

                    if ((tokens.length > 1) && (tokens[0].equals("ifname"))) {
                        val = tokens[1];
                        lp.setPortName(val); /* Store exactly what we get in database */
                    }
                }
            }
        }

        /*
         * Parse through physical properties of each port
         * Example:
            Settings for swp8:
                Supported ports: [ ]
                Supported link modes:   Not reported
                Supported pause frame use: No
                Supports auto-negotiation: No
                Advertised link modes:  Not reported
                Advertised pause frame use: No
                Advertised auto-negotiation: No
                Speed: Unknown!
                Duplex: Half
                Port: Twisted Pair
                PHYAD: 0
                Transceiver: internal
                Auto-negotiation: off
                MDI-X: Unknown
                Current message level: 0x00000000 (0)

                Link detected: no
         */
        if (portPhysicalPropertiesArr != null && portPhysicalPropertiesArr.length > 0) {
            SwitchPort info = null;
            String portName = null;
            for (String aLine : portPhysicalPropertiesArr) {
                Matcher matcher = phySettingsPattern.matcher(aLine);
                if ( matcher.matches() ) {
                    int count = matcher.groupCount();

                    if (count < 2)
                        continue;/* What is this line anyway? */

                    portName = matcher.group(2);
                    if (!physicalPortPropertiesDb.containsKey(portName)) {
                        physicalPortPropertiesDb.put(portName, new SwitchPort());
                    }
                    info = physicalPortPropertiesDb.get(portName);
                }
                else if (aLine.matches(".*Speed:.*")) {
                    String speed = (aLine.split(":")[1]).trim();

                    if (speed.equals("40000Mb/s")) {
                        info.setSpeed("40G");
                    }
                    else if (speed.equals("10000Mb/s")) {
                        info.setSpeed("10G");
                    }
                    else if (speed.equals("1000Mb/s")) {
                        info.setSpeed("1G");
                    }
                    else {
                        info.setSpeed("UNKNOWN");
                    }
                }
                else if (aLine.matches(".*Auto-negotiation:.*")) {
                    String autoneg = (aLine.split(":")[1]).trim();

                    if (autoneg.equals("on"))
                        info.setAutoneg(SwitchPort.PortAutoNegMode.ON);
                    else
                        info.setAutoneg(SwitchPort.PortAutoNegMode.OFF);
                }
                else if (aLine.matches(".*Duplex:.*")) {
                    String duplex = (aLine.split(":")[1]).trim();

                    if (duplex.equals("half"))
                        info.setDuplex(SwitchPort.PortDuplexMode.HALF);
                    else
                        info.setDuplex(SwitchPort.PortDuplexMode.FULL);
                }
                else if (aLine.matches(".*Link detected:.*")) {
                    String linkDetected = (aLine.split(":")[1]).trim();

                    if (linkDetected.equals("no"))
                        info.setStatus(PortStatus.DOWN);
                    else
                        info.setStatus(PortStatus.UP);
                }
            }
        }

        if (ipAddressResultLines != null && ipAddressResultLines.length > 0) {
            for (String line : ipAddressResultLines) {
                String tokens[] = line.trim().split("\\s+");

                if (tokens.length >= 3) {
                    portIpAddressDb.put(tokens[0], tokens[2]);
                }
            }
        }

        /*
         * O(N3.(log(N1)+log(N2)+log(N4)) loop where N3 = number of lines when listing all ports
         */
        for (	blockStart = 0, blockEnd = blockStart + 6;
                blockEnd < portResultArr.length;
                blockStart += 6, blockEnd += 6) {

            int i = blockStart;
            port = new SwitchPort(); /* Create new port to start with */

            String firstLine = portResultArr[i];
            String secondLine = portResultArr[i+1];
            String fourthLine = portResultArr[i+3];
            String sixthLine = portResultArr[i+5];

            String[] portArr = firstLine.split(":", 3);
            if (portArr.length <= 1) {
                logger.error ("Error parsing output line: " + firstLine);
                continue;
            }

            String portName = portArr[1].trim();
            
            if (portName.equalsIgnoreCase("lo")) {
            	continue; /* skip loopback ports */
            }

            if (!portName.matches("swp[0-9]+") && !portName.matches("eth[0-9]+"))
                continue; /* Skip ports on which are logical */

            port.setIfNumber(Integer.parseInt(portArr[0]));
            port.setName(portName);

            if (portName.startsWith("eth")) {
                port.setType(PortType.MANAGEMENT);
            } else if (portName.startsWith("swp")) {
                port.setType(PortType.SERVER);
            }

            // Parse flags
            Matcher m1 = Pattern.compile("^<([^ ]*)>.*").matcher(portArr[2].trim());

            if (m1.matches()) {
                port.setFlags(m1.group(1));
            }

            // Parse MTU
            Matcher m1mtu = Pattern.compile(".* mtu[ ]+([0-9]+) .*").matcher(portArr[2].trim());
            if (m1mtu.matches()) {
                port.setMtu(Integer.parseInt(m1mtu.group(1)));
            }

            // Parse MAC address
            Matcher m2 = Pattern.compile(".* ([0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2}){5}) .*").matcher(secondLine);
            if (m2.matches()) {
                port.setMacAddress(m2.group(1).trim());
            }

            // Get Rx packet counts
            String[] rxCounts = fourthLine.split("\\s+");
            port.getStatistics().setRxReceivedPackets(Long.parseLong(rxCounts[2]));
            port.getStatistics().setRxErrors(Long.parseLong(rxCounts[3]));
            port.getStatistics().setRxDroppedPackets(Long.parseLong(rxCounts[4]));

            // Get Tx packet counts
            String[] txCounts = sixthLine.split("\\s+");
            port.getStatistics().setTxSentPackets(Long.parseLong(txCounts[2]));
            port.getStatistics().setTxErrors(Long.parseLong(txCounts[3]));
            port.getStatistics().setTxDroppedPackets(Long.parseLong(txCounts[4]));
            port.getStatistics().setTimestamp(runDate);

            /*
             * Conitnue with filling in port with more information
             */
            if (!fdb.isEmpty()) {
                port.setLinkedMacAddresses(fdb.get(portName));
            }

            if (!lldpDb.isEmpty()) {
                port.setLinkedPort(lldpDb.get(portName));
            }

            /*
             * Fetch and set physical properties now
             */
            if (physicalPortPropertiesDb.containsKey(portName)) {
                SwitchPort portInfo = physicalPortPropertiesDb.get(portName);
                port.setAutoneg(portInfo.getAutoneg());
                port.setDuplex(portInfo.getDuplex());
                port.setSpeed(portInfo.getSpeed());
                port.setStatus(portInfo.getStatus());
            }

            if (portIpAddressDb.containsKey(portName)) {
                port.setIpAddress(portIpAddressDb.get(portName));
            }

            portList.add(port);
        }

        if (portList.size() > 4) {
            portsBulkCache.set(switchNode, portList);
        }

        return (portList);
    }

    /**
     * Configure the OSPF
     *
     * Using OSPF details, configure the OSPF daemons. Execute command to enable or disable the quagga daemons.
     *
     * @param switchNode switch node object
     * @param ospf Switch ospf configuration object
     * @return True if the configuration of Ospf was successful; False if the configuration of ospf is unsuccessful
     * @exception HmsException Thrown if OSPF configuration value is null
     */
    @Override
    public boolean configureOspf(SwitchNode switchNode, SwitchOspfConfig ospf) throws HmsException {
        /* Configure OSPF daemons first */
        if (ospf == null)
            throw new HmsException ("OSPF configuration input cannot be null.");

        if (ospf.isEnabled())
            configureOspfParameters(switchNode, ospf);

        SwitchSession switchSession = getSession (switchNode);
        String command = ospf.isEnabled() ?
                CumulusConstants.ENABLE_QUAGGA_DAEMONS_COMMAND
                .replaceAll("\\{password\\}", qr(switchNode.getPassword())) :
                    CumulusConstants.DISABLE_QUAGGA_DAEMONS_COMMAND
                    .replaceAll("\\{password\\}", qr(switchNode.getPassword()));

                String result = switchSession.execute(command);

                logger.debug("Command output: " + result);
                return true;
    }

    /**
     * Get the OSPF details
     *
     * For provided switchnode, pull bulk data from the switch and parse for router id, mode, network details, interface names and details.
     *
     * @param switchNode switch node object
     * @return switch ospf configuration object
     * @exception HmsException Thrown if info failed to be retrieved from switch
     */
    @Override
    public SwitchOspfConfig getOspf(SwitchNode switchNode) throws HmsException {
        SwitchOspfConfig ospf = new SwitchOspfConfig();
        SwitchOspfGlobalConfig globalConfig = new SwitchOspfGlobalConfig();
        SwitchOspfNetworkConfig networkConfig = null;
        SwitchOspfInterfaceConfig interfaceConfig = null;
        List<SwitchOspfNetworkConfig> networkConfigList = new ArrayList<SwitchOspfNetworkConfig>();
        List<SwitchOspfInterfaceConfig> interfaceConfigList = new ArrayList<SwitchOspfInterfaceConfig>();
        SwitchSession switchSession = getSession(switchNode);
        String[] quaggaConfig = null;
        String[] quaggaDaemonConfig = null;
        String[] tokens = null;

        /* Prepare the mammoth object */
        ospf.setGlobal(globalConfig);
        globalConfig.setInterfaces(interfaceConfigList);
        globalConfig.setNetworks(networkConfigList);

        /*
         * Let's start by pulling in bulk data from the switch
         */
        String[] commandSet = {
                "echo '{password}' | sudo -S cat /etc/quagga/Quagga.conf"
                .replaceAll("\\{password\\}", switchNode.getPassword()),
                CumulusConstants.LIST_QUAGGA_DAEMONS_COMMAND.replaceAll("\\{password\\}", switchNode.getPassword())
        };

        String[][] combinedOutput = GetMultipleCmdResults(switchSession, commandSet);

        if (combinedOutput.length < 1) {
            throw new HmsException("Failed to retrieve necessary information from switch");
        }

        /* Get all the output in pre-formatted avatar now */
        quaggaConfig = combinedOutput[0];
        quaggaDaemonConfig = combinedOutput[1];

        if ((quaggaConfig.length == 0) ||
                quaggaConfig[0].matches(".*No such file or directory.*")) {
            return ospf; /* Could not find anything */
        }

        /*
         * Start parsing the file line by line now
         */
        for (String line : quaggaConfig) {
            line = line.trim(); /* Parse the line properly */
            if (line.matches("^router-id.*")) {
                /* Global router-id */
                tokens = line.split("\\s+");
                if (tokens.length >= 2)
                    globalConfig.setRouterId(tokens[1]);

                ospf.setEnabled(true);
            }
            else if (line.matches(".*passive-interface default.*")) {
                /* Global Mode */
                if (line.startsWith("no"))
                    globalConfig.setDefaultMode(OspfMode.ACTIVE);
                else
                    globalConfig.setDefaultMode(OspfMode.PASSIVE);
            }
            else if (line.matches(".*network .*area.*")) {
                /* Network and area details */
                networkConfig = new SwitchOspfNetworkConfig();
                tokens = line.split("\\s+");
                if (tokens.length < 4)
                    continue; // Skip, cannot parse this

                networkConfig.setArea(tokens[3]);
                networkConfig.setNetwork(tokens[1]);
                networkConfigList.add(networkConfig);
            }
            else if (line.matches(".*passive-interface.*swp[0-9]+.*")) {
                /* Interface name and mode */
                interfaceConfig = new SwitchOspfInterfaceConfig();
                tokens = line.split("\\s+");
                if (tokens.length < 2)
                    continue; // Skip, cannot parse this

                if (line.startsWith("no"))
                    interfaceConfig.setMode(InterfaceMode.ACTIVE);
                else
                    interfaceConfig.setMode(InterfaceMode.PASSIVE);

                interfaceConfig.setName(tokens[tokens.length - 1]); /* Last token is interface name */
                interfaceConfigList.add(interfaceConfig);
            }
        }

        /* If either ospfd or zebra daemon has a no status, set ospf as not enabled */
        for (String line : quaggaDaemonConfig) {
            line = line.trim();
            if (line.startsWith("no"))
                ospf.setEnabled(false);
        }

        return ospf;
    }

    /**
     * Configure Bgp
     *
     * Configure Bgp object with the switch bgp config details. (Implementation in CumulusLacpGroupHelper file)
     *
     * @param switchNode switch node object
     * @param bgp Switch bpg config object
     * @return True if Bgp config successful
     * @exception HmsException Thrown if Bgp configuration fails
     */
    @Override
    public boolean configureBgp(SwitchNode switchNode, SwitchBgpConfig bgp) throws HmsException {
        return (bgpHelper.configureBgp(switchNode, bgp));
    }

    /**
     * Get Bgp Configuration
     *
     * Get the Bgp configuration for specified switch node (Implementation in CumulusLacpGroupHelper file)
     *
     * @param switchNode switch node object
     * @return switch bgp config object
     */
    @Override
    public SwitchBgpConfig getBgpConfiguration(SwitchNode switchNode) {
        return (bgpHelper.getBgpConfiguration(switchNode));
    }

    /**
     * Get Switch Sensor Info
     *
     * Get switch sensor information for specified switch node (Implementation in CumulusSensorHelper file)
     *
     * @param switchNode switch node object
     * @return switch sensor info object
     * @exception HmsException Thrown if Switch sensor info fails to be retrieved
     */
    @Override
    public SwitchSensorInfo getSwitchSensorInfo(SwitchNode switchNode) throws HmsException {
        return (sensorHelper.getSwitchSensorInfo(switchNode));
    }

    /**
     * Configure Mclag details for provided switch node
     *
     * For the provided switch node and mclag details, update the current configuration with the new IPv4 address lines. Create, upload, and start
     * 	the mclag interfaces to enable and configure on the switch node.
     *
     * @param switchNode switch node object
     * @param mclag Switch mclag info details
     * @return True if configuration was successful for mclag; False if configuration for Mclag is not successful
     * @exception HmsException Thrown if MC-LAG interface name, IP address, or Mac address is empty
     */
    @Override
    public boolean configureMclag(SwitchNode switchNode, SwitchMclagInfo mclag) throws HmsException {

        if (mclag.isEnabled()) {
            /* Validate input */
            if (mclag.getInterfaceName() == null || "".equals(mclag.getInterfaceName().trim())) {
                throw new HmsException ("Cannot create MC-LAG with empty interface name.");
            } else if (mclag.getPeerIp() == null || "".equals(mclag.getPeerIp().trim())) {
                throw new HmsException("Cannot create MC-LAG with empty peer IP address.");
            } else if (mclag.getSharedMac() == null || "".equals(mclag.getSharedMac().trim())) {
                throw new HmsException("Cannot create MC-LAG with empty shared MAC address.");
            }
        }

        /* Update current configuration */
        CumulusTorSwitchSession session = (CumulusTorSwitchSession) getSession(switchNode);
        String mclagCommand = null;
        if (mclag.isEnabled()) {

            /* Fill out IPv4 address line(s) */
            String ipv4 = "";
            if (mclag.getIpAddress() != null) {
                if (mclag.getNetmask() != null) {
                    ipv4 = CumulusConstants.IPV4_LINE
                            .replaceAll("\\{address\\}", mclag.getIpAddress())
                            .replaceAll("\\{netmask\\}", mclag.getNetmask());
                } else {
                    ipv4 = CumulusConstants.IPV4_ADDRESS_LINE
                            .replaceAll("\\{address\\}", mclag.getIpAddress());
                }
            }

            /* First create the interface stanza */
            String peerlinkStanza = CumulusConstants.CLAG_STANZA
                    .replaceAll("\\{name\\}", mclag.getInterfaceName())
                    .replaceAll("\\{ipv4\\}", ipv4)
                    .replaceAll("\\{sharedMac\\}", mclag.getSharedMac())
                    .replaceAll("\\{peerIp\\}", mclag.getPeerIp())
                    .replaceAll("\\{enabled\\}", mclag.isEnabled() ? "yes" : "no");

            /* Upload the file */
            ByteArrayInputStream bais = new ByteArrayInputStream (peerlinkStanza.getBytes());
            String filename = CumulusUtil.getMclagFilename();
            CumulusUtil.uploadAsRoot(switchNode, bais, filename);
            CumulusUtil.configurePersistenceDirectory(switchNode);

            /* Start the interface */
            mclagCommand = CumulusConstants.ENABLE_MCLAG_COMMAND
                    .replaceAll("\\{password\\}", switchNode.getPassword())
                    .replaceAll("\\{interfaces\\}", mclag.getInterfaceName());

            SshExecResult result = session.executeEnhanced(mclagCommand);
            result.log(logger, Level.DEBUG);
            result.logIfError(logger);

            if (result.getExitCode() != 0) {
                throw new HmsException ("Failed to enable MC-LAG on switch " + switchNode.getSwitchId() + " with interface " + mclag.getInterfaceName());
            }
        } else {
            mclagCommand = CumulusConstants.DISABLE_MCLAG_COMMAND
                    .replaceAll("\\{password\\}", qr(switchNode.getPassword()))
                    .replaceAll("\\{filename\\}", CumulusUtil.getMclagFilename());

            SshExecResult result = session.executeEnhanced(mclagCommand);
            result.log(logger, Level.DEBUG);
            result.logIfError(logger);

            CumulusUtil.configurePersistenceDirectory(switchNode);

            if (result.getExitCode() != 0) {
                throw new HmsException ("Failed to disable MC-LAG on switch " + switchNode.getSwitchId() + " interface " + mclag.getInterfaceName());
            }
        }

        return true;
    }

    @Override
    public SwitchMclagInfo getMclagStatus(SwitchNode switchNode) {
        return null;
    }

    @Override
    public void applySwitchBulkConfigs(SwitchNode switchNode, List<PluginSwitchBulkConfig> switchBulkConfigs)
            throws HmsOobNetworkException {
        Configuration configuration = null;
        List<String> allPorts = null;
        List<String> allSwitchPorts = new ArrayList<String>();
        List<String> allBonds = null;

        CumulusTorSwitchSession session = (CumulusTorSwitchSession) CumulusUtil.getSession(switchNode);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            session.download(baos, CumulusConstants.INTERFACES_FILE);
            configuration = Configuration.parse(new ByteArrayInputStream(baos.toByteArray()));
        } catch (Exception e) {
            error("Error in reading/parsing interfaces file on switch " + switchNode.getSwitchId() + ". Reason: "
                    + e.getMessage(), e, HmsOobNetworkErrorCode.INTERNAL_ERROR);
        }

        /*
         * Bulk configs are centered around these
         */
        allPorts = getSwitchPortList(switchNode);
        allBonds = getSwitchLacpGroups(switchNode);

        /*
         * Only concentrate on the switch ports. Management port should not be
         * configured via bulk configuration methods
         */
        for (String port : allPorts) {
            if (port.matches("eth.*"))
                continue;
            allSwitchPorts.add(port);
        }

        /* Set the configuration */
        bulkConfigHelper.applySwitchBulkConfig(configuration, switchBulkConfigs, allSwitchPorts, allBonds);

        /* Upload the revised configuration file now */
        /* Upload the file */
        ByteArrayInputStream bais = new ByteArrayInputStream(configuration.getString().getBytes());
        try {
            CumulusUtil.validateSourceClause(switchNode);
            CumulusUtil.uploadAsRoot(switchNode, bais, CumulusConstants.INTERFACES_FILE);

            CumulusUtil.configurePersistenceDirectory(switchNode);

            /* Activate the configuration in the current session */
            SshExecResult sshExecResult = session.executeEnhanced(CumulusConstants.RELOAD_INTERFACES
                    .replaceAll("\\{password\\}", CumulusUtil.qr(switchNode.getPassword())));
            sshExecResult.logIfError(logger);

            /* Invalidate the cache now */
            portsBulkCache.setStale(switchNode);
        } catch (HmsException e) {
            throw new HmsOobNetworkException("Failed to upload new configuration file to switch "
                    + switchNode.getSwitchId() + ". Reason: " + e.getMessage(), e,
                    HmsOobNetworkErrorCode.UPLOAD_FAILED);
        }
    }

    @Override
    public void configureIpv4DefaultRoute(SwitchNode switchNode, String gateway, String portId)
            throws HmsOobNetworkException {
        routeHelper.configureIpv4DefaultRoute(switchNode, gateway, portId);
    }

    @Override
    public void deleteIpv4DefaultRoute(SwitchNode switchNode)
            throws HmsOobNetworkException {
        try {
			routeHelper.deleteIpv4DefaultRoute(switchNode);
		} catch (HmsException e) {
			throw new HmsOobNetworkException(e.getMessage(), HmsOobNetworkErrorCode.DELETE_OPERATION_FAILED);
		}

    }

    /**
     * Qr function for quote replacement
     *
     * Using a quote replacement tool on a string - returns literal string.
     * @param str string to replace
     * @return the quoteReplaced string.
     */
    private static String qr(String str) {
        return Matcher.quoteReplacement(str);
    }

    /**
     * Find pattern in an array of strings
     *
     * Tries to find a regular expression pattern inside an array of strings
     * The complexity of search operation is O(N) and return the index in the array matching the pattern
     *
     * @param haystack array of strings
     * @param needle string representing the pattern to search for
     * @param abortPattern String used to match abort operation (pattern comparison)
     * @param startIndex From which index to start search
     * @param endIndex Untill which index to keep searching
     * @return int The index inside the array where the pattern matches -1, if no match is found
     * @exception HmsException Thrown if pattern conditions violated
     */
    private static int ArrayFindPatternIndex(	String[] haystack,
            String needle,
            String abortPattern,
            int startIndex,
            int endIndex) throws HmsException
    {
        Pattern pt = Pattern.compile(needle);
        Pattern abortPt = null;

        if (abortPattern != null)
            abortPt = Pattern.compile(abortPattern);

        if (endIndex == -1)
            endIndex = haystack.length - 1;

        if ((startIndex < 0) || (endIndex > haystack.length) || (startIndex > endIndex))
            throw new HmsException(
                    "Condition: 0 <= startIndex <= endIndex < haystack.length : Violated!");

        for (int i = startIndex; i <= endIndex; ++i) {
            // Abort search if pattern related to aborting operation is found
            if ((abortPt != null) && abortPt.matcher(haystack[i]).find())
                return -1;

            // Now perform actual matching
            if (pt.matcher(haystack[i]).find())
                return i;
        }
        return -1;
    }

    /**
     * Functionality - efficient tool to call a set of commands and capture their results.
     *
     * This function is a time saver. When with no dependency among each other a set of 'N' commands are to be fired
     * and each of their output stored for future analysis this function can do the work.
     *
     * @param switchSession the session to execute the SSH commands
     * @param commands an array of commands to be executed sequentially regardless of a command status.
     * @return An array of array of strings. The output of each command is given as array of strings. So it is an array of such arrays.
     * 		NULL, if any abnormal situation like SSH termination, improper un-parsable output is encountered
     */
    private static String[][] GetMultipleCmdResults(SwitchSession switchSession, String[] commands)
    {
        String[][] ret = new String[commands.length][];
        final String delimiter = "delim";
        final String delimCmd = "echo " + delimiter + ";";
        String execCommand = delimCmd;
        String result = "";


        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].equals(""))
                return null; // We do not support empty commands
            execCommand += commands[i] + ";" + delimCmd;
        }

        try {
            int indicesSize = (commands.length * 2) + 1;
            int indices[] = new int[indicesSize];
            int ai = 0;

            result = switchSession.execute(execCommand);

            logger.info(result);

            String[] arr = result.split("\n");

            for (int i = 0; i < arr.length; ++i) {
                if (arr[i].equals(delimiter)) {
                    indices[ai++] = i;
                }
            }
            for (int i = 0; i < commands.length; ++i) {
                ret[i] = Arrays.copyOfRange(arr, indices[i]+1, indices[i+1]);
            }
        } catch (Exception e) {
            return ret; // Simply abort
        }

        return ret;
    }

    /**
     * Upload temp configuration file
     *
     * Function to upload local file into /etc/network/interfaces via a temporary file since
     * this file has rw permission only for root, for other users it has only r permission
     *
     * @param switchSession the session to use to communicate with the switch
     * @param switchNode access credentials to perform sudo operation
     * @param filename name of the file to be uploaded present locally
     * @return Nothing
     * @exception Throws exception if local file not found or upload fails or any subcommand fails to achieve end result
     */
    private static void uploadConfigFile(	SwitchSession switchSession,
            SwitchNode switchNode,
            String localFilename,
            String remoteFilename) throws HmsException
    {
        String tmpCumulusFile = "/tmp/tmpconfigfile";

        try {
            FileInputStream fis = new FileInputStream(localFilename);

            // Let's upload the local configuration file onto Cumulus Switch now
            switchSession.upload(fis, tmpCumulusFile);

            String r = switchSession.execute("echo '{password}' | sudo -S mv -f {tmpfile} {interfaces}"
                    .replaceAll("\\{password\\}", switchNode.getPassword())
                    .replaceAll("\\{tmpfile\\}", tmpCumulusFile)
                    .replaceAll("\\{interfaces\\}", remoteFilename));

            if (r.length() > 0) {
                throw new HmsException ("Error uploading and applying configuration. Reason: " + r);
            }
        } catch (Exception e) {
            throw new HmsException ("Error uploading and applying configuration", e);
        }
    }

    /**
     * Function to configure OSPF parameters inside /etc/quagga/Quagga.conf only when OSPF is to be enabled
     * with the parameters present inside 'TorSwitchOspfConfig ospf' object passed.
     *
     * This function attempts to retain file section positional sanctity. This function can be used to
     * create brand new configuration if none exists but if existing configuration is found it smartly merges
     * the new configuration into the existing one without deleting existing interface and network parameters.
     * Global parameters however are overwritten with new values in 'ospf'.
     *
     * @param switchNode the switch node object where the configuration should be done
     * @param ospf the entire configuration of OSPF
     * @return None
     * @exception HmsException Thrown if Configuration object contains empty Globals/Interfaces & Networks
     */
    private void configureOspfParameters(
            SwitchNode switchNode,
            SwitchOspfConfig ospf) throws HmsException
    {
        int index = 0;
        SwitchSession switchSession = getSession(switchNode);
        String[] quaggaConfig = null;
        List<String> persistentNewLinesQuaggaInterfaces = new ArrayList<String>();
        List<String> persistentNewLinesQuaggaHeader = new ArrayList<String>();
        List<String> persistentNewLinesQuaggaFooter = new ArrayList<String>();

        /*
         * Make sure the configuration is usable
         */
        if (
                (ospf.getGlobal() == null) ||
                ((ospf.getGlobal().getInterfaces() == null) &&
                        (ospf.getGlobal().getNetworks() == null))) {
            throw new HmsException("Configuration object contains empty Globals/Interfaces & Networks");
        }

        String routerId = ospf.getGlobal().getRouterId();
        List<SwitchOspfInterfaceConfig> torSpineInterfaces = ospf.getGlobal().getInterfaces();
        List<SwitchOspfNetworkConfig> torSpineNetworks = ospf.getGlobal().getNetworks();
        String[] appendSectionInterfaces = new String[ospf.getGlobal().getInterfaces().size()];
        String[] appendSectionNetworks = new String[ospf.getGlobal().getNetworks().size()];
        String[] appendPatternsInterfaces = new String[ospf.getGlobal().getInterfaces().size()];
        String[] appendPatternsNetworks = new String[ospf.getGlobal().getNetworks().size()];
        String LOCAL_QUAGGA_CONFIG_FILE = "cumulus_quagga";
        PrintWriter outQuagga = null;
        int rtrIdLineIndex = -1;
        int rtrOspfLineIndex = -1;
        int ospfRtrIdLineIndex = -1;
        int passiveInterfaceLineIndex = -1;
        int rtrSubSectionInsertLineIndex = -1;

        /*
         * Let's define set of constants here
         */
        String GLOBAL_PASSIVE_MODE_LINE = ospf.getGlobal().getDefaultMode() == SwitchOspfGlobalConfig.OspfMode.PASSIVE?
                " passive-interface default\n":" no passive-interface default\n";
        String GLOBAL_ROUTER_ID_LINE = "router-id {routerId}\n".replaceAll("\\{routerId\\}", routerId);
        String GLOBAL_ROUTER_OSPF_LINE = "router ospf\n";
        String GLOBAL_OSPF_ROUTER_ID_LINE = "ospf router-id {routerId}\n".replaceAll("\\{routerId\\}", routerId);
        String OSPF_INTERFACE_DEFAULT_TYPE = " ip ospf network point-to-point\n";

        /*
         * Let's start by pulling in bulk data from the switch
         */
        String[] commandSet = {
                "echo '{password}' | sudo -S cat /etc/quagga/Quagga.conf"
                .replaceAll("\\{password\\}", switchNode.getPassword())
        };

        String[][] combinedOutput = GetMultipleCmdResults(switchSession, commandSet);

        if (combinedOutput.length < 1) {
            throw new HmsException("Failed to retrieve necessary information from switch");
        }

        /* Get all the output in pre-formatted avatar now */
        quaggaConfig = combinedOutput[0];

        if ((quaggaConfig.length == 0) ||
                quaggaConfig[0].matches(".*No such file or directory.*")) {
            logger.warn("Empty or non-existent " + CumulusConstants.CUMULUS_QUAGGA_CONFIG_FILE);
            quaggaConfig = new String[] {""};
        }

        /*
         * Add global configuration first
         */
        rtrIdLineIndex = headerLineCreate(
                quaggaConfig,
                0,
                "^router-id.*",
                GLOBAL_ROUTER_ID_LINE,
                persistentNewLinesQuaggaHeader, true);
        rtrOspfLineIndex = headerLineCreate(
                quaggaConfig,
                0,
                "^router ospf.*",
                GLOBAL_ROUTER_OSPF_LINE,
                persistentNewLinesQuaggaFooter, false);
        ospfRtrIdLineIndex = headerLineCreate(
                quaggaConfig,
                rtrIdLineIndex + 1,
                ".*ospf router-id.*",
                GLOBAL_OSPF_ROUTER_ID_LINE,
                persistentNewLinesQuaggaFooter, true);
        passiveInterfaceLineIndex = headerLineCreate(
                quaggaConfig,
                rtrIdLineIndex + 1,
                ".*passive-interface default.*",
                GLOBAL_PASSIVE_MODE_LINE,
                null, true);

        // Let's do some sanity checks here?
        // if file is messed up beyond repair let's ask user to empty the file and again call this API
        // We could have done this ourselves but deleting existing configuration (even if improper)
        // does not sound that good.
        if ((rtrIdLineIndex == -1) && ((rtrOspfLineIndex != -1) || (ospfRtrIdLineIndex != -1) ||
                (passiveInterfaceLineIndex != -1))) {
            throw new HmsException("Please delete " + CumulusConstants.CUMULUS_QUAGGA_CONFIG_FILE + " and rerun the query");
        }

        /*
         * Find where we can insert the subsection for interface and network configuration lines
         */
        if (ospfRtrIdLineIndex > -1) {
            rtrSubSectionInsertLineIndex = ospfRtrIdLineIndex;
        }
        if (passiveInterfaceLineIndex > -1) {
            rtrSubSectionInsertLineIndex = passiveInterfaceLineIndex;
        }
        else if (ospfRtrIdLineIndex > -1) {
            quaggaConfig[ospfRtrIdLineIndex] =
                    quaggaConfig[ospfRtrIdLineIndex].concat("\n" + GLOBAL_PASSIVE_MODE_LINE);
        }

        /*
         * Create OSPF Interfaces
         */
        index = 0;
        for (SwitchOspfInterfaceConfig ifconfig : torSpineInterfaces) {
            /*
             * Interface configuration in /etc/quagga/Quagga.conf
             */
            String[] patternsQ = {
                    ".*ip ospf network.*"
            };
            String[] substValuesQ = {
                    OSPF_INTERFACE_DEFAULT_TYPE
            };

            if (ifconfig.getMode() == SwitchOspfInterfaceConfig.InterfaceMode.ACTIVE) {
                appendSectionInterfaces[index] = " no passive-interface {ifName}\n"
                        .replaceAll("\\{ifName\\}", ifconfig.getName());
            }
            else {
                appendSectionInterfaces[index] = " passive-interface {ifName}\n"
                        .replaceAll("\\{ifName\\}", ifconfig.getName());
            }
            appendPatternsInterfaces[index] = ".*interface.*{ifName}.*"
                    .replaceAll("\\{ifName\\}", ifconfig.getName());

            int found = headerLineCreate(
                    quaggaConfig,
                    rtrIdLineIndex + 1,
                    "^interface.*{ifName}.*".replaceAll("\\{ifName\\}", ifconfig.getName()),
                    "!\ninterface {ifName}\n".replaceAll("\\{ifName\\}", ifconfig.getName()),
                    persistentNewLinesQuaggaInterfaces, true);

            String av = headerSubsectionCreate(
                    quaggaConfig,
                    found + 1,
                    patternsQ,
                    substValuesQ,
                    ".*interface.*");

            if (found == -1) {
                persistentNewLinesQuaggaInterfaces.add(av);
            }
            else if (av.length() > 0) {
                quaggaConfig[found] = quaggaConfig[found].concat("\n"+av);
            }

            ++index;
        }

        /*
         * Create Network subsection
         */
        index = 0;
        for (SwitchOspfNetworkConfig networkconfig : torSpineNetworks) {

            /* Validate the network here, if found invalid, return */

            appendSectionNetworks[index] = " network {network} area {areaId}\n"
                    .replaceAll("\\{network\\}", networkconfig.getNetwork())
                    .replaceAll("\\{areaId\\}", networkconfig.getArea());
            appendPatternsNetworks[index] = ".*network {networkName}.*"
                    .replaceAll("\\{networkName\\}", networkconfig.getNetwork());
            ++index;
        }

        /*
         * Create full section under "router ospf"
         */
        String appendValueOspfNetworks = headerSubsectionCreate(
                quaggaConfig,
                rtrSubSectionInsertLineIndex + 1,
                appendPatternsNetworks,
                appendSectionNetworks,
                null);
        String appendValueOspfInterfaces = headerSubsectionCreate(
                quaggaConfig,
                rtrSubSectionInsertLineIndex + 1,
                appendPatternsInterfaces,
                appendSectionInterfaces,
                null);

        if (rtrSubSectionInsertLineIndex == -1) {
            persistentNewLinesQuaggaFooter.add(appendValueOspfInterfaces);
            persistentNewLinesQuaggaFooter.add(appendValueOspfNetworks);
        }
        else if ((appendValueOspfNetworks.length() > 0) || (appendValueOspfInterfaces.length() > 0)) {
            // This line we do not add to list if the right parent line is available
            quaggaConfig[rtrSubSectionInsertLineIndex] =
                    quaggaConfig[rtrSubSectionInsertLineIndex].concat(
                            "\n"
                                    + appendValueOspfInterfaces + appendValueOspfNetworks);
        }

        /*
         *  Open a temporary file to store all persistent configuration
         *  This file will be uploaded at the end after making changes to it
         */
        try {
            outQuagga = new PrintWriter(LOCAL_QUAGGA_CONFIG_FILE);
        } catch (Exception e) {
            throw new HmsException ("Failed to make changes persistent", e);
        }

        /*
         * Dump all accumulated persistent settings into the local file
         */
        for (int i = 0; i < quaggaConfig.length; ++i) {
            outQuagga.print(quaggaConfig[i]);

            // lines we added contain '\n' but unmodified lines do not contain it, hence the check
            if (!quaggaConfig[i].endsWith("\n"))
                outQuagga.print("\n");

            // Add the newly handled interfaces right after 'router-id xxx' line before the OSPF specific section
            if ((i == rtrIdLineIndex) && (persistentNewLinesQuaggaInterfaces.size() > 0)) {
                for (String line : persistentNewLinesQuaggaInterfaces) {
                    outQuagga.print(line);
                }
            }
        }
        if (rtrIdLineIndex == -1) {
            for (String line : persistentNewLinesQuaggaHeader) {
                outQuagga.print(line);
            }
            for (String line : persistentNewLinesQuaggaInterfaces) {
                outQuagga.print(line);
            }
        }

        for (String line : persistentNewLinesQuaggaFooter) {
            outQuagga.print(line);
        }
        outQuagga.close();

        // Finally, upload the modified persistent configuration file
        uploadConfigFile(	switchSession,
                switchNode,
                LOCAL_QUAGGA_CONFIG_FILE,
                CumulusConstants.CUMULUS_QUAGGA_CONFIG_FILE);

        CumulusUtil.configurePersistenceDirectory(switchNode);

        try {
            (new File(LOCAL_QUAGGA_CONFIG_FILE)).delete();
        } catch (Exception e) {}
    }


    /**
     * Creates loopback (acts as wrapper)
     *
     * Function to create IP Loopback Interface with a specific name and IP address
     * This function is a wrapper around 'createInterface' so both of them have same semantics
     *
     * @param persistentConfigFile An array of String (lines) representing the contents of /etc/network/interfaces
     * @param newLines If new lines are to be added to create the interface then they will be added to this list
     * @param loopbackName Value of loopback name
     * @param loopbackIp Loopback Ip address
     * @return None
     * @exception HmsException Thrown if Configuration object contains empty Globals/Interfaces & Networks
     */
    @SuppressWarnings("unused")
    private void createLoopback(String[] persistentConfigFile,
            List<String> newLines,
            String loopbackName,
            String loopbackIp) throws HmsException
    {
        createInterface(
                persistentConfigFile,
                newLines,
                loopbackName,
                "",
                loopbackIp,
                32);
    }

    /**
     * This function can be used to create associate an IPv4 address with a port
     * We don't care if the port name is a physical one or a logical one.
     *
     * @param persistentConfigFile An array of String (lines) representing the contents of /etc/network/interfaces
     * @param newLines If new lines are to be added to create the interface then they will be added to this list
     * @param ifName name of the port/interface
     * @param ifQualifier any additional option we need to put next to the interface name in line "iface swpx"
     * 				  e.g. iface swp5 inet manual, here this arg should be set to " inet manual"
     * @param ifIp valid IP address in dotted decimal notation
     * @param ifMaskLen mask length for the network in the range 1<=ifMaskLen<=32.
     * @return void
     * @exception HmsException If invalid argument is passed or something unexpected happens during the function execution
     */
    private void createInterface(	String[] persistentConfigFile,
            List<String> newLines,
            String ifName,
            String ifQualifier,
            String ifIp,
            int ifMaskLen) throws HmsException
    {
        int index = -1;

        if ((ifMaskLen < 1) || (ifMaskLen > 32))
            throw new HmsException("Invalid mask length: " + ifMaskLen);

        index = ArrayFindPatternIndex(persistentConfigFile,
                ".*auto {ifName}.*".replaceAll("\\{ifName\\}", ifName), null,
                0, -1);

        if (index == -1) {
            newLines.add(("auto {ifName}\n" + "iface {ifName}{ifQualifier}\n"
                    + "\taddress {ifIp}/32\n")
                    .replaceAll("\\{ifName\\}", ifName)
                    .replaceAll("\\{ifQualifier\\}", ifQualifier)
                    .replaceAll("\\{ifIp\\}", ifIp));
        } else {
            /* We already have some configuration, what should we do now? */
            String appendLines = "";
            String[] patterns = { ".*iface.*", ".*address.*" };
            String[] substitutionStrings = {
                    "iface {ifName}{ifQualifier}\n".replaceAll("\\{ifName\\}",
                            ifName)
                    .replaceAll("\\{ifQualifier\\}", ifQualifier),
                    "\taddress {ifIp}/{ifMaskLen}\n".replaceAll(
                            "\\{loopbackIp\\}", ifIp).replaceAll(
                                    "\\{ifMaskLen\\}", String.valueOf(ifMaskLen)) };
            String stopString = ".*auto.*";

            appendLines = headerSubsectionCreate(persistentConfigFile,
                    index + 1, patterns, substitutionStrings, stopString);

            if (appendLines.length() > 0) {
                persistentConfigFile[index] = persistentConfigFile[index]
                        .concat("\n" + appendLines);
            }
        }
    }

    /**
     * Create a subsection following principle of set intersection as follows:
     * Set A = set of lines in file
     * Set B = set of patterns passed
     * Set C = set of substitution values
     *
     * C and B are mapped one-to-one
     * set D = A ^ B --> set of matching patterns (^ signifies intersection)
     * set E = B - D --> set of patterns which did not match
     * For elements in C with matching indices in E we return them as a single concatenated String
     *
     * The search starts from the index 'srchFromIndex'
     *
     * @param file File that will be used for the subsection creation
     * @param srchFromIndex Index/pointer to the location in the file - where search begins
     * @param patterns Pattern used to compare values
     * @param substitutionStrings String used for a pattern
     * @param stopString String use for a pattern
     * @return Concatenated String value indicating the exact section to be inserted
     * @exception HmsException Thrown when patterns and substitution string arrays must be of same length
     */
    private String headerSubsectionCreate(	String[] file,
            int srchFromIndex,
            String[] patterns,
            String[] substitutionStrings,
            String stopString)
                    throws HmsException {

        int numSubstitutions = substitutionStrings.length;
        int index = -1;
        String appendLines = "";

        if (patterns.length != numSubstitutions)
            throw new HmsException(
                    "Both patterns and substitutionStrings array MUST be of same length");

        for (int i = 0; i < numSubstitutions; ++i) {
            index = ArrayFindPatternIndex(file, patterns[i], stopString,
                    srchFromIndex, -1);

            if (index == -1) {
                appendLines = appendLines.concat(substitutionStrings[i]);
            } else {
                file[index] = substitutionStrings[i];
            }
        }

        return appendLines;
    }

    /**
     * Searches for the pattern in the array of lines of file from index noted by 'srchFromIndex' and when found
     * substitutes the passed 'substitutionString' into the matching index of the array. The substitution happens
     * only when 'skipIfFound' is set to false.
     *
     * If no match is found then it may also add the 'substitutionString' into the linked list 'newlyAddedLines' but
     * only when it is not NULL. 'pattern' can be any regular expression.
     *
     * @param file File used for the searc
     * @param srchFromIndex Search index where the search will begin
     * @param pattern Pattern used in the function (regEx expression)
     * @param substitutionString String used as a pattern match
     * @param newlyAddedLines Linkedlis when no match is found
     * @param skipIfFound Substitution happens if skipIfFound is false.
     * @return The index if a match is found, else -1
     * @exception Thrown if invalid args are passed
     * @exception HmsException Thrown if Pattern in array at a given index fails
     */
    private int headerLineCreate(	String[] file,
            int srchFromIndex,
            String pattern,
            String substitutionString,
            List<String> newlyAddedLines,
            Boolean skipIfFound) throws HmsException {
        int hdrLine = -1;
        int index = ArrayFindPatternIndex(file, pattern, null, srchFromIndex,
                -1);

        if (index == -1) {
            if (newlyAddedLines != null)
                newlyAddedLines.add(substitutionString);
        } else {
            if (!skipIfFound)
                file[index] = substitutionString;
            hdrLine = index;
        }

        return hdrLine;
    }

    private void error(String msg, Exception e, HmsOobNetworkErrorCode error) throws HmsOobNetworkException {
        String finalMsg = msg;

        if (e != null)
            finalMsg += ", Exception: " + e.getLocalizedMessage();

        logger.error(finalMsg);

        /* Also throw exception */
        if (e != null)
            throw new HmsOobNetworkException(finalMsg, e, error);
        else
            throw new HmsOobNetworkException(finalMsg, error);
    }

    private void error(String msg, HmsOobNetworkErrorCode error) throws HmsOobNetworkException {
        error(msg, null, error);
    }

    /**
     * Set of variables that will be used to call helper functions from the respective helper classes.
     */
    private static Logger logger = Logger.getLogger(CumulusTorSwitchService.class);
    private CumulusSensorHelper sensorHelper = new CumulusSensorHelper(this);
    private CumulusLacpGroupHelper lacpHelper = new CumulusLacpGroupHelper(this);
    private CumulusVlanHelper vlanHelper = new CumulusVlanHelper(this);
    private CumulusVxlanHelper vxlanHelper = new CumulusVxlanHelper(this);
    private CumulusBgpHelper bgpHelper = new CumulusBgpHelper(this);
    private CumulusBulkConfigHelper bulkConfigHelper = new CumulusBulkConfigHelper();
    private CumulusRouteHelper routeHelper = new CumulusRouteHelper();

    /**
     * Set of variables to gather port list details, os info and hardware information.
     */
    private static CumulusCache<List<String>> portListCache = new CumulusCache<List<String>>();
    private static CumulusCache<List<SwitchPort>> portsBulkCache = new CumulusCache<List<SwitchPort>>(300);
    private static CumulusCache<SwitchOsInfo> osInfoCache = new CumulusCache<SwitchOsInfo>();
    private static CumulusCache<SwitchHardwareInfo> hwInfoCache = new CumulusCache<SwitchHardwareInfo>();
    private CumulusCache<List<String>> bondListCache = new CumulusCache<List<String>>(300);

    /**
     *
     * Get Component Event List
     *
     * Get Sensor list (component event list) for a particular sensor component.
     * (Implementation in CumulusSensorHelper file)
     *
     * @param serviceNode service node object
     * @param component Server component or sensor component
     * @return list of 'server component event' or all sensor components.
     * @exception HmsException Thrown if Component Sensor List fails to be
     *                retrieved for provided component
     */
    @Override
    public List<ServerComponentEvent> getComponentSwitchEventList(ServiceHmsNode serviceNode, SwitchComponentEnum component) throws HmsException {

        if (serviceNode != null) {
            List<ServerComponentEvent> serverComponentEventList = new ArrayList<ServerComponentEvent>();

            try {
                switch (component) {
                    case SWITCH_PORT:
                        serverComponentEventList = CumulusPortEventHelper.getSwitchPortEventHelper(new SwitchNode(serviceNode), getSwitchPortList(new SwitchNode(
                                serviceNode)));
                        break;
                    case SWITCH:
                        serverComponentEventList = CumulusSwitchUpDownEventHelper.getSwitchUpDownEventHelper(new SwitchNode(serviceNode),
                                isPoweredOn(new SwitchNode(serviceNode)));
                        break;
                    default:
                        break;
                }
                return serverComponentEventList;

            } catch (HmsException e) {
                logger.error("Exception while getting getComponentSensorList Data for switch:" + serviceNode.getNodeID(), e);
                throw e;
            } catch (Exception e) {
                logger.error("Exception while getting getComponentSensorList Data for switch:" + serviceNode.getNodeID(), e);
                throw new HmsException(e);
            }
        } else {
            throw new HmsException("Switch Node is Null or invalid");
        }

    }

    /**
     *
     * Get Supported Hms Apis
     *
     * Get the supported HMS Apis from constant variable. (Switch apis: info,
     * port info, sensor info, port sensor info, fan sensor info, powerunit
     * info)
     *
     * @param serviceNode service node object
     * @return list of HMS APIs
     * @exception HmsException Thrown if static API values changed, and fails to
     *                meet API values
     */
    @Override
    public List<HmsApi> getSupportedHmsSwitchApi(ServiceHmsNode serviceNode) throws HmsException {
        List<HmsApi> apiList = new ArrayList<HmsApi>();
        apiList.add(HmsApi.SWITCH_INFO);
        apiList.add(HmsApi.SWITCH_PORT_INFO);
        apiList.add(HmsApi.SWITCH_SENSOR_INFO);
        apiList.add(HmsApi.SWITCH_PORT_SENSOR_INFO);
        apiList.add(HmsApi.SWITCH_FAN_SENSOR_INFO);
        apiList.add(HmsApi.SWITCH_POWERUNIT_SENSOR_INFO);
        return apiList;
    }

}
