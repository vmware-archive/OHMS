/* ********************************************************************************
 * IpUtils.java
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
package com.vmware.vrack.hms.switches.cumulus.util;

public class IpUtils {
    /**
     * Retrieve the netmask from prefix length.
     * Example: Input: 24, Output: 255.255.255.0
     * @param prefixLen
     * @return
     */
    public static String prefixLenToNetmask(Integer prefixLen) {
        Integer mask = 0x7fffffff; /* To avoid interpretation of this variable as -ve value */
        String netmask = "";

        mask = (mask >> (32 - prefixLen)) << (32 - prefixLen);

        for (Integer i = 3; i >= 0; --i) {
            Integer tmp = (mask & (0xFF << (i * 8))) >> (i * 8);
            /*
             * msb is set to 0 (to avoid invalid sign bit interpretation)
             * The msb can never be 0 in a netmask hence it is always safe to
             * make that bit 1 when determining the mask for the most significant octet
             */
            if (i == 3)
                tmp |=  0x80;
            netmask += tmp;
            if (i > 0)
                netmask += ".";
        }

        return netmask;
    }

    /**
     * Function to convert a valid netmask to its prefix length.
     * Example: Input: 255.255.255.0, Output: 24
     *
     * @param netmask
     * @return
     */
    public static Integer netmaskToPrefixLen(String netmask) {
        Integer mask = 0, prefixLen = 0;
        String tokens[] =  netmask.split("\\.");

        if (tokens.length != 4) {
            return -1;
        }

        for (int i = 3; i >= 0; i--) {
            mask |= (Integer.parseInt(tokens[3 - i]) << (i * 8));
        }

        prefixLen = 32 - Integer.numberOfTrailingZeros(mask);

        return prefixLen;
    }

    /**
     * Function to validate an IPv4 address presented to this function in dotted
     * decimal notation as per https://tools.ietf.org/html/rfc791
     *
     * @param address
     * @return
     */
    public static Boolean isValidIpv4Address(String address) {
        String tokens[] = address.split("\\.");

        if (tokens.length != 4)
            return false;

        for (String token : tokens) {
            Integer val = Integer.parseInt(token);
            if (val < 0 || val > 255)
                return false;
        }

        return true;
    }
}
