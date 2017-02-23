/* ********************************************************************************
 * HmsSshKeyMgmtRestService.java
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

package com.vmware.vrack.hms.rest.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.ProcessUtil;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnector;

/**
 * {@link HmsSshKeyMgmtRestService} is responsible for generating ssh key set for management switch
 *
 * @author VMware, Inc.
 */
@Path( "/sshkeys" )
public class HmsSshKeyMgmtRestService
{

    /** The logger. */

    private static final Logger LOG = LoggerFactory.getLogger( HmsSshKeyMgmtRestService.class );

    private static final String[] algorithms = new String[] { "rsa", "dsa", "ecdsa" };

    private static final String HMS_MGMT_SWITCH_SSH_KEY_FILE = "hms.mgmt.switch.ssh.key.file";

    private static final String HMS_MGMT_SWITCH_SSH_KEYGEN_SETUP_COMMAND = "hms.mgmt.switch.ssh.keygen.setup.command";

    private static final String HMS_MGMT_SWITCH_SSH_KEYGEN_COMMAND = "hms.mgmt.switch.ssh.keygen.command";

    private static final String HMS_MGMT_SWITCH_SSH_RSA_PUBLIC_KEY_FILE = "hms.mgmt.switch.ssh.rsa.public.key.file";

    private static final String HMS_MGMT_SWITCH_SSH_PUBLIC_KEY = "hms.mgmt.switch.ssh.public.key";

    private static final String SSH_PUBLIC_KEY_FILE_FIELD_SEPARATOR = " ";

    private static final String HMS_MGMT_SWITCH_SSH_KEY_TYPE = "hms.mgmt.switch.ssh.key.type";

    /**
     * Creates the SSH keyset and returns the public key of RSA as the response.
     *
     * @return
     * @throws HMSRestException
     */
    @POST
    @Path( "/create" )
    public Map<String, Object> createKeySet()
        throws HMSRestException
    {
        LOG.debug( "SSH keys creation starts" );
        try
        {
            generateKeyPairs();

            String rsaPublicKeyFile = HmsConfigHolder.getHMSConfigProperty( HMS_MGMT_SWITCH_SSH_RSA_PUBLIC_KEY_FILE );
            File publicKey = new File( rsaPublicKeyFile );

            if ( !publicKey.exists() )
            {
                LOG.error( "RSA public key file: {} doesn't exists", rsaPublicKeyFile );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                            "RSA public key file doen't exists",
                                            "It appears that file is not created successfully" );
            }
            LOG.debug( "SSH keys created successfully" );

            return extractPublicKey( publicKey );

        }
        catch ( Throwable e )
        {
            LOG.error( "Exception occured creating the ssh key files: {}", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "can't create SSH keys", e );
        }
    }

    /**
     * Method to extract ssh key-type and its public key.
     *
     * @param publicKey
     * @return
     * @throws IOException
     * @throws HmsException
     */
    private Map<String, Object> extractPublicKey( File publicKey )
        throws IOException, HmsException
    {
        String content = FileUtils.readFileToString( publicKey, "UTF-8" );
        Map<String, Object> response = new HashMap<String, Object>();

        if ( StringUtils.isNotBlank( content ) )
        {
            String[] contentSplit = content.split( SSH_PUBLIC_KEY_FILE_FIELD_SEPARATOR );
            if ( contentSplit.length >= 2 )
            {
                response.put( HMS_MGMT_SWITCH_SSH_KEY_TYPE, contentSplit[0] );
                response.put( HMS_MGMT_SWITCH_SSH_PUBLIC_KEY, contentSplit[1] );
            }
            else
            {
                throw new HmsException( "ssh key is corrupted" );
            }
        }
        return response;
    }

    /**
     * Generates the key-pairs of all the specified algorithms
     *
     * @throws HMSRestException
     */
    private void generateKeyPairs()
        throws HmsException
    {

        final String password = retrievePassword();
        final String command = buildKeyGenSetupCommand( password ) + buildKeyGenCommand( password );
        List<String> commands = new ArrayList<String>();
        commands.add( "bash" );
        commands.add( "-c" );
        commands.add( command );

        int exitCode = ProcessUtil.getCommandExitValue( commands );

        if ( exitCode != 0 )
        {
            String exceptionMsg = String.format( "unsuccessful on executing the command, exit value %s", exitCode );
            LOG.error( exceptionMsg );
            throw new HmsException( exceptionMsg );
        }
    }

    private String retrievePassword()
        throws HmsException
    {
        Map<String, SwitchNode> switchNodeMap = SwitchNodeConnector.getInstance().switchNodeMap;
        String password = null;

        if ( switchNodeMap != null )
        {
            SwitchNode switchNode = switchNodeMap.get( Constants.HMS_MANAGEMENT_SWITCH_ID );
            password = switchNode.getPassword();
            if ( StringUtils.isBlank( password ) )
            {
                LOG.error( "can't create SSH key-pair as the password is blank for manangmet switch" );
                throw new HmsException( "can't retrieve password of Mgmt swtich" );
            }
        }
        return password;
    }

    /**
     * Command to perform basic clean up
     *
     * @param password
     * @return
     */
    private String buildKeyGenSetupCommand( final String password )
    {
        String command = HmsConfigHolder.getHMSConfigProperty( HMS_MGMT_SWITCH_SSH_KEYGEN_SETUP_COMMAND );
        return String.format( command, password, password );
    }

    /**
     * Command to generate key-pairs
     *
     * @return
     */
    private String buildKeyGenCommand( final String password )
    {
        String keyGenCoreCommand =
            HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS, HMS_MGMT_SWITCH_SSH_KEYGEN_COMMAND );
        StringBuilder strBuilder = new StringBuilder();

        String file = HmsConfigHolder.getHMSConfigProperty( HMS_MGMT_SWITCH_SSH_KEY_FILE );

        for ( String algorithm : algorithms )
        {
            String tmpFile = String.format( file, algorithm );
            strBuilder.append( String.format( keyGenCoreCommand, password, algorithm, tmpFile ) );
        }

        return strBuilder.toString();
    }
}
