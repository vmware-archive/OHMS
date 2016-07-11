/* ********************************************************************************
 * SwitchSession.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.switches.api;

import java.io.InputStream;
import java.io.OutputStream;

import com.vmware.vrack.hms.common.exception.HmsException;

/**
 * The TorSwitchSession interface represents a live session object that's connected to the TorSwitch. Each switch should
 * implement this interface based on its connectivity paradigm. For example, on Cumulus Linux based switches, this would
 * be implemented using an SSH session.
 *
 * @author VMware, Inc.
 */
public interface SwitchSession
{
    void setSwitchNode( SwitchNode switchNode );

    SwitchNode getSwitchNode();

    void connect()
        throws HmsException;

    void connect( int timeout )
        throws HmsException;

    boolean isConnected();

    String execute( String command )
        throws HmsException;

    boolean upload( InputStream localInputStream, String remoteFilename )
        throws HmsException;

    boolean download( OutputStream localOutputStream, String remoteFilename )
        throws HmsException;

    void disconnect();
}
