/* ********************************************************************************
 * NBSwitchNetworkPrefix.java
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
package com.vmware.vrack.hms.common.rest.model.switches;

public class NBSwitchNetworkPrefix
{
    private String prefix;

    private Integer prefixLen;

    /**
     * @return the prefix
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }

    /**
     * @return the prefixLen
     */
    public Integer getPrefixLen()
    {
        return prefixLen;
    }

    /**
     * @param prefixLen the prefixLen to set
     */
    public void setPrefixLen( Integer prefixLen )
    {
        this.prefixLen = prefixLen;
    }
}
