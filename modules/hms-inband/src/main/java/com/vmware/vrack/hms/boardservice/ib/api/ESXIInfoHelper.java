/* ********************************************************************************
 * ESXIInfoHelper.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.boardservice.ib.api;

import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Session;
import com.vmware.vim.binding.vim.AboutInfo;
import com.vmware.vim.binding.vim.ServiceInstanceContent;
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
    private static Logger logger = Logger.getLogger( ESXIInfoHelper.class );

    private static String DOMAIN_NAME_STRING = "Domain Name: ";

    private static String FULLY_QUALIFIED_DOMAIN_NAME_STRING = "Fully Qualified Domain Name: ";

    private static String HOST_NAME_STRING = "Host Name: ";

    private static String ESXI_RESPONSE_SPLITTER = ": ";

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
            config.put( "StrictHostKeyChecking", "no" );
            Session session = EsxiSshUtil.getSessionObject( node.getOsUserName(), node.getOsPassword(),
                                                            node.getIbIpAddress(), node.getSshPort(), config );
            String hostNameString = null;
            try
            {
                session.connect( 30000 );
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
}
