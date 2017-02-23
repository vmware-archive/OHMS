/* ********************************************************************************
 * RemoteConsoleConnectionInfo.java
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
import com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.kvm.jnlpconsole.JnlpRemoteConsoleConnectionInfo;

/**
 * This is a Marker Interface. BoardServices are supposed to return specific implementations of this interface,
 * depending on the type of Remote Console display it uses(KVM OR SOL OR KVMAndSol. If KVM then JnlpDisplay or
 * appletDisplay etc) for e.g : Board which enable KVM Display using jnlp file should return
 * {@link JnlpRemoteConsoleConnectionInfo} when asked to start the remote console connection
 * 
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public interface RemoteConsoleConnectionInfo
{

}
