/* ********************************************************************************
 * JnlpRemoteConsoleConnectionInfo.java
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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.vrack.hms.common.boardvendorservice.remoteconsole.RemoteConsoleConnectionInfo;

/**
 * Implements the marker interface {@link RemoteConsoleConnectionInfo} This class contains all the information required
 * by the system which calls the StartRemoteConsoleConnection method using the rest service, to create tunnels and
 * enable the KVM Display (for hosts which send a .jnlp file for the same)
 * 
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class JnlpRemoteConsoleConnectionInfo
    implements RemoteConsoleConnectionInfo
{
    /**
     * See JnlpParameters.java
     */
    private String paramerterizedJnlpFile;

    /**
     * HMS starts tunnels which connects to the hosts ports which deal which sending the video information(CD/FLOPPY
     * info if needed)
     */
    private List<JnlpBoardPortInfo> hmsPortsOpenedForCommunication;

    public String getParamerterizedJnlpFile()
    {
        return paramerterizedJnlpFile;
    }

    public void setParamerterizedJnlpFile( String paramerterizedJnlpFile )
    {
        this.paramerterizedJnlpFile = paramerterizedJnlpFile;
    }

    public List<JnlpBoardPortInfo> getHmsPortsOpenedForCommunication()
    {
        return hmsPortsOpenedForCommunication;
    }

    public void setHmsPortsOpenedForCommunication( List<JnlpBoardPortInfo> hmsPortsOpenedForCommunication )
    {
        this.hmsPortsOpenedForCommunication = hmsPortsOpenedForCommunication;
    }

}
