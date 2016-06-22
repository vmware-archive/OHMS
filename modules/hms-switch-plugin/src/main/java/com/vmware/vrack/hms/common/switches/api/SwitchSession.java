/* ********************************************************************************
 * SwitchSession.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
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
