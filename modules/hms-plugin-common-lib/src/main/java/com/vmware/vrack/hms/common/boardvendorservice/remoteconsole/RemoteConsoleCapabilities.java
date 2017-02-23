/* ********************************************************************************
 * RemoteConsoleCapabilities.java
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
package com.vmware.vrack.hms.common.boardvendorservice.remoteconsole;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Remote Console can be either KVM or SOL or BOTH. Kvm Remote Console can either be Jnlp based, applet based or html5
 * based.
 * 
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class RemoteConsoleCapabilities
{
    private RemoteConsoleType remoteConsoleType;

    private KvmConsoleType kvmConsoleType;

    public RemoteConsoleType getRemoteConsoleType()
    {
        return remoteConsoleType;
    }

    public void setRemoteConsoleType( RemoteConsoleType remoteConsoleType )
    {
        this.remoteConsoleType = remoteConsoleType;
    }

    public KvmConsoleType getKvmConsoleType()
    {
        return kvmConsoleType;
    }

    public void setKvmConsoleType( KvmConsoleType kvmConsoleType )
    {
        this.kvmConsoleType = kvmConsoleType;
    }

}
