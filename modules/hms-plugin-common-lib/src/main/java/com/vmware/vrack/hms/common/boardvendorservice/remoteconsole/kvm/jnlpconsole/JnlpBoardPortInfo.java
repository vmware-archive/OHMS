/* ********************************************************************************
 * JnlpBoardPortInfo.java
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

package com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.kvm.jnlpconsole;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Maps the port type to the port opened for the specific service.
 * 
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class JnlpBoardPortInfo
{
    private JnlpPortType portType;

    private Integer portOpened;

    public JnlpPortType getPortType()
    {
        return portType;
    }

    public void setPortType( JnlpPortType portType )
    {
        this.portType = portType;
    }

    public Integer getPortOpened()
    {
        return portOpened;
    }

    public void setPortOpened( Integer portOpened )
    {
        this.portOpened = portOpened;
    }

}
