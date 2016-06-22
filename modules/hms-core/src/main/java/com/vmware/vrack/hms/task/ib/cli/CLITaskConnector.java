/* ********************************************************************************
 * CLITaskConnector.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ib.cli;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vmware.vrack.hms.utils.SshUtil;

public class CLITaskConnector
{
    private static Logger logger = Logger.getLogger( CLITaskConnector.class );

    private Session session = null;

    private String ipAddress = null;

    private int sshPort = 0;

    private String sshUser = null;

    private String sshPwd = null;

    public Session getSession()
    {
        return session;
    }

    public CLITaskConnector( String sshUser, String sshPwd, String ipAddress, int sshPort )
    {
        this.sshUser = sshUser;
        this.sshPwd = sshPwd;
        this.ipAddress = ipAddress;
        this.sshPort = sshPort;
    }

    public void createConnection()
        throws JSchException
    {
        if ( this.ipAddress != null && this.sshPort > 0 && this.sshPort < 65535 )
        {
            Properties config = new java.util.Properties();
            config.put( "StrictHostKeyChecking", "no" );
            session = SshUtil.getSessionObject( this.sshUser, this.sshPwd, this.ipAddress, this.sshPort, config );
            // session.connect(30000);
            try
            {
                session.connect( 30000 );
            }
            catch ( JSchException e )
            {
                if ( session != null )
                {
                    session.disconnect();
                    session = null;
                    throw e;
                }
                logger.error( "Unable to create jsch CLI session: ", e );
            }
        }
    }

    public void destroy()
        throws Exception
    {
        if ( session != null )
            session.disconnect();
        this.session = null;
        this.sshUser = null;
        this.sshPwd = null;
        this.ipAddress = null;
    }
}
