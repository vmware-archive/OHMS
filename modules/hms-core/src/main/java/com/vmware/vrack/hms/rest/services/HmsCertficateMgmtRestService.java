/* ********************************************************************************
 * HmsCertficateMgmtRestService.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.util.ProcessUtil;

/**
 * {@link HmsCertficateMgmtRestService} is responsible for managing the certs for the respective mgmt switch supported
 * proxy server for eg. Lighttpd.
 *
 * @author VMware, Inc.
 */
@Path( "/certificate" )
public class HmsCertficateMgmtRestService
{

    private static final String UPLOAD_EXCEPTION_MSG = "input data is null";

    /** The logger. */
    private final Logger LOG = LoggerFactory.getLogger( HmsCertficateMgmtRestService.class );

    private static final String HMS_CREATE_KEYSTORE = "hms-create-keys.sh";

    private static final String HMS_INSTALL_CERTS_WRAPPER_SH = "hms-install-certs-wrapper.sh";

    private static final String HMS_OOBAGENT_CSR = "hms.oobagent.csr";

    private static final String HMS_OOBAGENT_CRT = "hms.oobagent.crt";

    /**
     * Creates the SSL files hms.oobagent.key and hms.oobagent.csr. Internally invokes the script hms-create-keys.sh.
     * And then returns client signing request to the client to sign the same
     *
     * @return
     * @throws HMSRestException
     */
    @POST
    @Path( "/create" )
    public File createCertificate()
        throws HMSRestException
    {
        LOG.debug( "certificate creation starts" );

        File csrFile = null;
        try
        {
            execute( HMS_CREATE_KEYSTORE );
            csrFile = new File( getHmsCertMgmtWorkDirectory() + HMS_OOBAGENT_CSR );

            if ( !csrFile.exists() )
            {
                LOG.error( "CSR file: {} doesn't exists", getHmsCertMgmtWorkDirectory() + HMS_OOBAGENT_CSR );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "CSR file doen't exists",
                                            "It appears that file is not created successfully" );
            }
            LOG.debug( "keys created successfully" );
        }
        catch ( Throwable e )
        {
            LOG.error( "Exception occured creating the csr file", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage(), e );
        }
        return csrFile;
    }

    /**
     * Creates the signed certificate (hms.oobagent.crt) using the byte[] data provided. <br>
     * Uploads the signed certificate (hms.oobagent.crt) to Mgmt switch. <br>
     * Executes the script hms-install-certs-wrapper.sh to re-install hms.keystore.pem file using hms.oobagent.key and
     * hms.oobagent.crt files. And then restarts the proxy server
     *
     * @param inputData
     * @return
     * @throws HMSRestException
     */
    @POST
    @Path( "/upload" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response uploadSignedCertificate( byte[] inputData )
        throws HMSRestException
    {

        LOG.debug( "upload certificate process starts" );

        if ( inputData != null )
        {
            String fileAbsolutePath = getHmsCertMgmtWorkDirectory() + HMS_OOBAGENT_CRT;
            createFile( fileAbsolutePath, inputData );
            LOG.debug( "Certificate file created successfully" );

            execute( HMS_INSTALL_CERTS_WRAPPER_SH );
            LOG.debug( "certficate initialization process is successful" );

            return Response.status( Status.OK ).build();
        }
        else
        {
            LOG.error( UPLOAD_EXCEPTION_MSG );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), UPLOAD_EXCEPTION_MSG,
                                        UPLOAD_EXCEPTION_MSG );
        }
    }

    /**
     * Method for creating a file with provided inputs
     *
     * @param fileName
     * @param bytes
     * @return
     * @throws HMSRestException
     */
    private void createFile( String fileName, byte[] bytes )
        throws HMSRestException
    {
        try
        {
            File file = new File( fileName );
            String dirAbsPath = file.getParent();
            File dir = new File( dirAbsPath );
            if ( !dir.exists() )
            {
                dir.mkdirs();
            }
            if ( !file.exists() )
            {
                file.createNewFile();
            }
            else
            {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream fop = new FileOutputStream( file );
            fop.write( bytes );
            fop.flush();
            fop.close();
        }
        catch ( IOException e )
        {
            LOG.error( "Error while saving uploaded file and the exception is: '{}'.", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Error while saving file", e );
        }
    }

    /**
     * Executes the scripts files.
     *
     * @param file
     * @throws HMSRestException
     */
    private void execute( String file )
        throws HMSRestException
    {
        try
        {
            String hmsBaseDirectory = getHmsCertMgmtBaseDirectory();
            String scriptFile = hmsBaseDirectory + file;

            List<String> cmdWithArgs = new ArrayList<String>();
            cmdWithArgs.add( 0, scriptFile );

            LOG.debug( "starts executing the script: {}", scriptFile );
            int exitCode = ProcessUtil.getCommandExitValue( cmdWithArgs );
            if ( exitCode != 0 )
            {
                LOG.error( "unsuccessful on executing the script, exit value {}", exitCode );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                            "unsuccessful on executing the script", "exit code: " + exitCode );
            }
            LOG.debug( "script: {} executed successfully", scriptFile );

        }
        catch ( Exception e )
        {
            LOG.error( "Exception occured executing the script", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                        "Exception occured executing the script", e );

        }
    }

    public static String getHmsCertMgmtBaseDirectory()
    {
        return HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS, "hms.certmgmt.base.dir" );
    }

    public static String getHmsCertMgmtWorkDirectory()
    {
        return HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS, "hms.certmgmt.work.dir" );
    }
}