/* ********************************************************************************
 * JnlpParameters.java
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

/**
 * Board provider who sends a JNLP file to enable KVM Display should have to parameterize the JNLP file content with
 * these parameters. JNLP file contains arguments such as IPAddress and Ports where the host services listen and send
 * video and other information to enable the KVM display. These arguments have to be changed depending on the TCPTunnel
 * service created at calling system where the jnlpDisplay is requested.
 * 
 * @author VMware, Inc.
 */
public enum JnlpParameters
{

    PARAMETER_IPADDRESS, PARAMETER_CODEBASE_IPADDRESS_PORT, PARAMETER_VIDEOPORT, PARAMETER_CDPORT, PARAMETER_FLOPPYPORT
}
