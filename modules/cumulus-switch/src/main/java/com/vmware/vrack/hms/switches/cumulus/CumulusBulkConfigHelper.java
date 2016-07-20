package com.vmware.vrack.hms.switches.cumulus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.vrack.hms.common.exception.HmsOobNetworkErrorCode;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.switches.model.bulk.*;
import com.vmware.vrack.hms.switches.cumulus.model.Configuration;

public class CumulusBulkConfigHelper {

    /**
     * Method to configure bulk level configuration for a specific switch, the
     * configuration of the switch needs to be retrieved by the caller into
     * 'configuration' object so that we again do not need to fetch the info
     * from the switch
     *
     * @param configuration
     * @param config
     * @param allPorts
     * @param allBonds
     * @throws HmsOobNetworkException
     */
    public void applySwitchBulkConfig(Configuration configuration,
    		List<PluginSwitchBulkConfig> configs, List<String> allPorts,
            List<String> allBonds) throws HmsOobNetworkException {

        for (PluginSwitchBulkConfig config : configs) {
            PluginSwitchBulkConfigEnum type = config.getType();
            switch (type) {
            case PHYSICAL_SWITCH_PORT_MTU:
                applySwitchPortBulkMtu(config.getFilters(), config.getValues(), configuration, allPorts);
                break;
            case BOND_MTU:
                applyBondBulkMtu(config.getFilters(), config.getValues(),  configuration, allBonds);
                break;
            default:
                throw new HmsOobNetworkException(
                        "Unsupported configuration type " + type, null,
                        HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);
            }
        }
    }

    /**
     * Method to handle MTU configuration at a bulk level for physical switch-
     * ports of a specific switch, the configuration of the switch needs to be
     * retrieved by the caller into 'configuration' object so that we again do
     * not need to fetch the info from the switch
     *
     * @param configValue
     * @param configuration
     * @param allPorts
     * @throws HmsOobNetworkException
     */
    private void applySwitchPortBulkMtu(List<String> filters,
    		List<String> values,
            Configuration configuration, List<String> allPorts)
                    throws HmsOobNetworkException {
        List<String> portList = null;

        if (filters == null) {
            portList = allPorts;
        } else {
            portList = filters;
        }

        for (String port : portList) {
            com.vmware.vrack.hms.switches.cumulus.model.SwitchPort switchPort = new com.vmware.vrack.hms.switches.cumulus.model.SwitchPort();

            switchPort.setMtu(getBulkMtu(values));
            switchPort.name = port;
            updateSwitchPort(switchPort, configuration, true);
        }
    }

    /**
     * Method to handle MTU configuration at a bulk level for LACP bonds of a
     * specific switch, the configuration of the switch needs to be retrieved by
     * the caller into 'configuration' object so that we again do not need to
     * fetch the info from the switch
     *
     * @param configValue
     * @param configuration
     * @param allBonds
     * @throws HmsOobNetworkException
     */
    private void applyBondBulkMtu(List<String> filters,
    		List<String> values,
            Configuration configuration, List<String> allBonds)
                    throws HmsOobNetworkException {
        List<String> bondList = null;

        if (filters == null) {
            bondList = allBonds;
        } else {
            bondList = filters;
        }

        for (String port : bondList) {
            com.vmware.vrack.hms.switches.cumulus.model.SwitchPort switchPort = new com.vmware.vrack.hms.switches.cumulus.model.SwitchPort();

            switchPort.setMtu(getBulkMtu(values));
            switchPort.name = port;
            updateSwitchPort(switchPort, configuration, false);
        }
    }
    
    /**
     * Gets the MTU value inside SwitchBulkConfigValue object by interpreting
     * the 1st node of the list of values as an Integer
     *
     * @param configValue
     * @return
     * @throws HmsOobNetworkException
     */
    private Integer getBulkMtu(List<String> values)
            throws HmsOobNetworkException {
        Integer mtu = -1;

        if ((values != null)
                && !values.isEmpty()) {
            String value = values.get(
                    0); /* For MTU configuration only 1 value is sufficient */
            try {
                mtu = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new HmsOobNetworkException(
                        "Error parsing MTU value " + value + ". Reason: "
                                + e.getMessage(),
                                e, HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);
            }
        }

        return mtu;
    }

    /**
     * Add/update parameters of a port
     *
     * @param switchPort
     * @param configuration
     * @param addIfNotPresent
     */
    private void updateSwitchPort(
            com.vmware.vrack.hms.switches.cumulus.model.SwitchPort switchPort,
            Configuration configuration, Boolean addIfNotPresent) {
        Boolean modified = false;

        for (com.vmware.vrack.hms.switches.cumulus.model.SwitchPort sp : configuration.switchPorts) {
            if (switchPort.name.equals(sp.name)) {
                if (switchPort.getMtu() != CumulusConstants.DEFAULT_MTU) {
                    sp.setMtu(switchPort
                            .getMtu()); /*
                             * Add more assignments as we start
                             * supporting additional parameters
                             */
                    modified = true;
                }
                break;
            }
        }

        if (!modified && addIfNotPresent) {
            com.vmware.vrack.hms.switches.cumulus.model.SwitchPort newSwitchPort = new com.vmware.vrack.hms.switches.cumulus.model.SwitchPort();

            newSwitchPort.name = switchPort.name;
            newSwitchPort.otherConfig = switchPort.otherConfig;
            newSwitchPort.parentConfig = switchPort.parentConfig;
            if (switchPort.getMtu() != CumulusConstants.DEFAULT_MTU) {
                newSwitchPort.setMtu(switchPort.getMtu());
            }
            newSwitchPort.vlans = getCopyOfListOfString(switchPort.vlans);
            newSwitchPort.portConfig = switchPort.portConfig;
            newSwitchPort.pvid = switchPort.pvid;

            configuration.switchPorts.add(newSwitchPort);
        }
    }

    /**
     * Check if a port is already present inside configuration or not
     *
     * @param switchPort
     * @param configuration
     * @return
     */
    private Boolean verifySwitchPort(
            com.vmware.vrack.hms.switches.cumulus.model.SwitchPort switchPort,
            Configuration configuration) {
        Boolean nameMatched = false;
        for (com.vmware.vrack.hms.switches.cumulus.model.SwitchPort sp : configuration.switchPorts) {
            if (switchPort.name.equals(sp.name)) {
                nameMatched = true;
                if ((switchPort.getMtu() == sp.getMtu()) || ((sp
                        .getMtu() == 0 /* MTU not configured, hence default */)
                        && (switchPort
                                .getMtu() == CumulusConstants.DEFAULT_MTU))) { /*
                                 * Add
                                 * more
                                 * checks
                                 * as
                                 * we
                                 * start
                                 * supporting
                                 * additional
                                 * parameters
                                 */
                    return true;
                }
            }
        }

        /*
         * When a port is not present in the configuration file then it contains
         * default value (like mtu = 1500), so we handle all default value
         * validations here
         */
        if (!nameMatched
                && (switchPort.getMtu() == CumulusConstants.DEFAULT_MTU)) {
            return true;
        }

        return false;
    }

    /**
     * Returns a deep copy of the list of String values
     *
     * @param list
     * @return
     */
    private List<String> getCopyOfListOfString(List<String> list) {
        List<String> newList = null;

        if (list == null) {
            return null;
        }

        newList = new ArrayList<String>();

        for (String value : list) {
            newList.add(value);
        }

        return newList;
    }
}
