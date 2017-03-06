/* ********************************************************************************
 * ESXIInfoHelper.java
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
package com.vmware.vrack.hms.boardservice.ib.api;

import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Session;
import com.vmware.vim.binding.vim.AboutInfo;
import com.vmware.vim.binding.vim.ServiceInstanceContent;
import com.vmware.vrack.hms.boardservice.ib.InbandConstants;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.HostNameInfo;
import com.vmware.vrack.hms.common.servernodes.api.esxinfo.OSInfo;
import com.vmware.vrack.hms.common.util.EsxiSshUtil;
import com.vmware.vrack.hms.vsphere.VsphereClient;

/**
 * ESXi Info Helper
 * 
 * @author Vishesh Nirwal
 */
public class ESXIInfoHelper
{

    private static Logger logger = LoggerFactory.getLogger( ESXIInfoHelper.class );

    private static String DOMAIN_NAME_STRING = "Domain Name: ";

    private static String FULLY_QUALIFIED_DOMAIN_NAME_STRING = "Fully Qualified Domain Name: ";

    private static String HOST_NAME_STRING = "Host Name: ";

    private static String ESXI_RESPONSE_SPLITTER = ": ";

    private static int SSH_CONNECTION_TIMEOUT = 30000;

    public static OSInfo getEsxiInfo( VsphereClient client )
        throws Exception
    {
        if ( client != null )
        {
            ServiceInstanceContent sic = client.getServiceInstanceContent();
            if ( sic != null )
            {
                AboutInfo aboutInfo = sic.getAbout();
                if ( aboutInfo != null )
                {
                    OSInfo esxiInfo = new OSInfo();
                    esxiInfo.setBuild( aboutInfo.getVersion() );
                    esxiInfo.setProductName( aboutInfo.getLicenseProductName() );
                    esxiInfo.setVendor( aboutInfo.getVendor() );
                    esxiInfo.setVersion( aboutInfo.getVersion() );
                    return esxiInfo;
                }
                else
                {
                    throw new HmsException( "No about info found." );
                }
            }
            else
            {
                throw new Exception( "Can not get System Info because the ServiceInstanceContent Object is NULL." );
            }
        }
        else
        {
            throw new Exception( "Can not get System Info because the client Object is NULL" );
        }
    }

    /**
     * Return ESXI Host Name Information. Like domain name, fully qualified domain name, and host name
     * 
     * @param node
     * @return
     * @throws HmsException
     */
    public static HostNameInfo getEsxiHostNameInfo( ServiceServerNode node )
        throws HmsException
    {
        if ( node != null )
        {
            Properties config = new java.util.Properties();
            config.put( InbandConstants.STRICT_HOST_KEY_CHECKING, InbandConstants.STRICT_HOST_KEY_CHECK_YES );
            Session session = EsxiSshUtil.getSessionObject( node.getOsUserName(), node.getOsPassword(),
                                                            node.getIbIpAddress(), node.getSshPort(), config );
            String hostNameString = null;

            try
            {
                session.connect( SSH_CONNECTION_TIMEOUT );
                hostNameString = EsxiSshUtil.executeCommand( session, Constants.GET_ESXI_HOSTNAME_COMMAND );
            }
            catch ( Exception e )
            {
                logger.error( "Unable to create jsch CLI session: ", e );
            }
            finally
            {
                if ( session != null )
                {
                    session.disconnect();
                    session = null;
                }
            }
            if ( hostNameString != null )
            {
                return getHostName( hostNameString );
            }
            else
            {
                throw new HmsException( "Unable to get Esxi HostName information" );
            }
        }
        else
        {
            throw new HmsException( "Server Node object can not be null" );
        }
    }

    private static HostNameInfo getHostName( String hostNameString )
        throws HmsException
    {
        if ( hostNameString != null && !"".equals( hostNameString.trim() ) )
        {
            HostNameInfo info = new HostNameInfo();
            Scanner scanner = new Scanner( hostNameString );
            while ( scanner.hasNextLine() )
            {
                String line = scanner.nextLine();

                if ( line.contains( FULLY_QUALIFIED_DOMAIN_NAME_STRING ) )
                {
                    String[] str = line.split( ESXI_RESPONSE_SPLITTER, 2 );
                    if ( str.length > 1 )
                    {
                        info.setFullyQualifiedDomainName( str[1].trim() );
                        continue;
                    }
                }
                if ( line.contains( DOMAIN_NAME_STRING ) )
                {
                    String[] str = line.split( ESXI_RESPONSE_SPLITTER, 2 );
                    if ( str.length > 1 )
                    {
                        info.setDomainName( str[1].trim() );
                        continue;
                    }
                }
                if ( line.contains( HOST_NAME_STRING ) )
                {
                    String[] str = line.split( ESXI_RESPONSE_SPLITTER, 2 );
                    if ( str.length > 1 )
                    {
                        info.setHostName( str[1].trim() );
                        continue;
                    }
                }
            }
            scanner.close();

            return info;
        }
        else
        {
            String err = "Cannot get Esxi Host name details with String.";
            logger.error( err );
            throw new HmsException( err );
        }
    }

    public static String getHostConnectedSwitchPort( ServiceServerNode node )
        throws HmsException
    {
        if ( node != null )
        {
            Properties config = new java.util.Properties();
            config.put( InbandConstants.STRICT_HOST_KEY_CHECKING, InbandConstants.STRICT_HOST_KEY_CHECK_YES );
            Session session = EsxiSshUtil.getSessionObject( node.getOsUserName(), node.getOsPassword(),
                                                            node.getIbIpAddress(), node.getSshPort(), config );
            String result = null;

            try
            {
                session.connect( SSH_CONNECTION_TIMEOUT );
                result = EsxiSshUtil.executeCommand( session, Constants.HOST_CONNECTED_SWITCH_PORT );
                logger.debug( "After executing the command:{} on host:{}, result:{}",
                              Constants.HOST_CONNECTED_SWITCH_PORT, node.getNodeID(), result );
                if ( result != null )
                {
                    if ( result.endsWith( "\n" ) )
                    {
                        result = result.substring( 0, result.length() - 1 );
                    }

                    String[] portNameArray = result.split( "[\n]" );
                    /* Assuming that the host has two physical NICs */
                    if ( portNameArray.length == 2 )
                    {
                        if ( portNameArray[0].equalsIgnoreCase( portNameArray[1] ) )
                        {
                            return portNameArray[0];
                        }
                        else
                        {
                            String err =
                                String.format( "The ports on the two switches through which host:%s is connected, aren't matching. Port names:%s & %s.",
                                               node.getNodeID(), portNameArray[0], portNameArray[1] );
                            throw new HmsException( err );
                        }
                    }
                    else
                    {
                        String err =
                            String.format( "The number of ports through which host is connected to the switch should be two. Instead it is %s. ",
                                           portNameArray.length );
                        throw new HmsException( err );
                    }
                }
                else
                {
                    String err = String.format( "Error while getting portName to which Node: %s, is connected.",
                                                node.getNodeID() );
                    logger.error( err );
                    throw new HmsException( err );
                }
            }
            catch ( Exception e )
            {
                String err = String.format( "Unable to create jsch CLI session for node: %s", node.getNodeID() );
                logger.error( err, e );
                throw new HmsException( err, e );
            }
            finally
            {
                if ( session != null )
                {
                    session.disconnect();
                    session = null;
                }
            }
        }
        else
        {
            throw new HmsException( "Server Node object can not be null" );
        }
    }

}
