/* ********************************************************************************
 * CumulusSviHelper.java
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

import java.util.Map;

import com.vmware.vrack.hms.common.exception.HmsOobNetworkErrorCode;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.switches.cumulus.util.IpUtils;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.common.switches.api.SwitchVlanIgmp;
import com.vmware.vrack.hms.switches.cumulus.model.Configuration;
import com.vmware.vrack.hms.switches.cumulus.model.Svi;

public class CumulusSviHelper {

    /**
     * Function to retrieve SVI configurations from configuration and put them inside corresponding
     * VLANs already present inside vlansMap. if the entry is not present inside the map then we throw Exception.
     *
     * @param vlansMap
     * @param configuration
     */
    public static void getVlansFromConfigurarationIntoMap(Map<String, SwitchVlan> vlansMap, Configuration configuration) throws HmsOobNetworkException {

        if (configuration == null)
            throw new HmsOobNetworkException("Configuration cannot be NULL", HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);

        if (configuration.svis != null && !configuration.svis.isEmpty()) {
            for (Svi svi : configuration.svis) {
                SwitchVlan vlan = vlansMap.get(svi.pvid);
                SwitchVlanIgmp vlanIgmp = new SwitchVlanIgmp();
                String netmask = "";

                if (vlan == null) {
                    throw new HmsOobNetworkException(
                            "The VLAN Map must contain the VLANs for which SVI exists in Cumulus configuration",
                            HmsOobNetworkErrorCode.GET_OPERATION_FAILED);
                }

                vlanIgmp.setIgmpQuerier(false);
                if (svi.getIgmpQuerierAddress() != null) {
                    vlanIgmp.setIgmpQuerier(true);
                }
                vlan.setIgmp(vlanIgmp); /* We always give back a valid object with appropriate value */
                vlan.setIpAddress(svi.getAddress());
                netmask = IpUtils.prefixLenToNetmask(svi.getPrefixLen());
                vlan.setNetmask(netmask);
            }
        }
    }

    /**
     * Function to update Cumulus specific persistent configuration inside configuration object from vlan
     * mainly values related to IP address, IGMP support etc.
     *
     * @param vlan
     * @param configuration
     */
    public static void updateVlanInConfiguration(String vlanAwareBridgeName, SwitchVlan vlan, Configuration configuration) throws HmsOobNetworkException {
        Boolean sviFound = false;
        Integer prefixLen = 0;

        if (configuration == null)
            throw new HmsOobNetworkException("Configuration cannot be NULL", HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);

        if (vlan.getNetmask() != null) {
            prefixLen = IpUtils.netmaskToPrefixLen(vlan.getNetmask());
        }

        for (Svi svi : configuration.svis) {
            if (svi.pvid.equals(vlan.getId())) {
                sviFound = true;
                if (vlan.getIpAddress() != null && vlan.getNetmask() != null) {
                    svi.setAddress(vlan.getIpAddress());
                    svi.setPrefixLen(prefixLen);
                }
                else if (vlan.getIpAddress() == null) {
                    /* Signaling this SVI to be deleted because an SVI cannot exist without an IP address */
                    svi.setAddress(null);
                }
                if (vlan.getIgmp() != null && vlan.getIgmp().getIgmpQuerier() == true) {
                    svi.setIgmpQuerierAddress(vlan.getIpAddress()); /* IGMP uses the IP address configured in the VLAN */
                }
                else {
                    svi.setIgmpQuerierAddress(null); /* To be removed */
                }
                break; /* There cannot be multiple SVI stanza for the same VLAN */
            }
        }

        if (sviFound == false) {
            Svi svi = new Svi();

            /* Non protocol specific (generic) fields first */
            svi.name = vlanAwareBridgeName;
            svi.pvid = vlan.getId();

            /* Layer 3 specific fields now */
            if (vlan.getIpAddress() != null && vlan.getNetmask() != null) {
                svi.setAddress(vlan.getIpAddress());
                svi.setPrefixLen(prefixLen);
            }
            if (vlan.getIgmp() != null && vlan.getIgmp().getIgmpQuerier() == true) {
                svi.setIgmpQuerierAddress(vlan.getIpAddress()); /* IGMP uses the IP address configured in the VLAN */
            }

            configuration.svis.add(svi); /* New SVI added */
        }
    }

    /**
     * Function to delete inside persistent configuration 'configuration' the vid matching with vlan
     *
     * @param vlan
     * @param configuration
     */
    public static void deleteVlanInConfiguration(SwitchVlan vlan, Configuration configuration) throws HmsOobNetworkException {

        if (configuration == null)
            throw new HmsOobNetworkException("Configuration cannot be NULL", HmsOobNetworkErrorCode.ARGUMENT_SYNTAX_ERROR);

        if (configuration.svis != null && !configuration.svis.isEmpty()) {
            for (Svi svi : configuration.svis) {
                if (svi.pvid.equals(vlan.getId())) {
                    configuration.svis.remove(svi);
                    break;
                }
            }
        }
    }
}
