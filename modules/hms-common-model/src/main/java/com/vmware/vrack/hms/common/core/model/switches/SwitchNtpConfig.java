/* ********************************************************************************
 * SwitchNtpConfig.java
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
package com.vmware.vrack.hms.common.core.model.switches;

import java.util.List;

public class SwitchNtpConfig
{
    private List<ServerEntry> timeServerIpAddress;

    public static class ServerEntry
    {
        private String ipAddress;

        private Boolean bulkMode;

        /**
         * @return the ipAddress
         */
        public String getIpAddress()
        {
            return ipAddress;
        }

        /**
         * @param ipAddress the ipAddress to set
         */
        public void setIpAddress( String ipAddress )
        {
            this.ipAddress = ipAddress;
        }

        /**
         * @return the bulkMode
         */
        public Boolean getBulkMode()
        {
            return bulkMode;
        }

        /**
         * @param bulkMode the bulkMode to set
         */
        public void setBulkMode( Boolean bulkMode )
        {
            this.bulkMode = bulkMode;
        }

    }

    /**
     * @return the timeServerIpAddress
     */
    public List<ServerEntry> getTimeServerIpAddress()
    {
        return timeServerIpAddress;
    }

    /**
     * @param timeServerIpAddress the timeServerIpAddress to set
     */
    public void setTimeServerIpAddress( List<ServerEntry> timeServerIpAddress )
    {
        this.timeServerIpAddress = timeServerIpAddress;
    }
}
