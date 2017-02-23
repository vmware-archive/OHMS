/* ********************************************************************************
 * NetworkInterfaceUtil.java
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

package com.vmware.vrack.hms.common.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

/**
 * Utility to get the Network Interfaces related details for a specific network interface
 * 
 * @author kprafull
 */
public class NetworkInterfaceUtil
{
    private static final String ANY_VALID_INTERFACE = "ANY";

    private static final Logger log = Logger.getLogger( NetworkInterfaceUtil.class );

    /**
     * Used to get the IPv4 address of a specific network interface. If the name is null or "ANY" the function returns
     * any valid ipV4 address of the host.
     * 
     * @param name - example eth0, eth1
     * @return
     * @throws SocketException
     */
    public static String getByInterfaceName( String name )
        throws SocketException
    {
        log.debug( "Find request for ip address for network interface {}" + name );
        String ipV4Address = null;
        // If name is provided (and not "any"), find the ipV4 address for provided interface
        if ( name != null && !name.equals( "" ) && !name.equals( ANY_VALID_INTERFACE ) )
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            // Check if the configured interface is available
            while ( interfaces.hasMoreElements() )
            {
                NetworkInterface current = interfaces.nextElement();
                if ( current.getName().equals( name ) )
                {
                    // if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;

                    Enumeration<InetAddress> addresses = current.getInetAddresses();
                    while ( addresses.hasMoreElements() )
                    {
                        InetAddress current_addr = addresses.nextElement();
                        // if (current_addr.isLoopbackAddress()) continue;

                        if ( current_addr instanceof Inet4Address )
                            ipV4Address = current_addr.getHostAddress();
                    }
                }
            }
            if ( ipV4Address == null )
            {
                log.warn( "Network interface [{}] NOT found or not of Inet4Address type..." + name );
            }
        }
        else
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            // If configured network interface is not available, get any IPv4 address
            if ( ipV4Address == null )
            {
                interfaces = NetworkInterface.getNetworkInterfaces();
                while ( interfaces.hasMoreElements() )
                {
                    NetworkInterface current = interfaces.nextElement();
                    if ( !current.isUp() || current.isLoopback() || current.isVirtual() )
                        continue;

                    Enumeration<InetAddress> addresses = current.getInetAddresses();
                    while ( addresses.hasMoreElements() )
                    {
                        InetAddress current_addr = addresses.nextElement();
                        if ( current_addr.isLoopbackAddress() )
                            continue;

                        if ( current_addr instanceof Inet4Address )
                            ipV4Address = current_addr.getHostAddress();
                    }
                }
            }
        }

        log.debug( "Returning network interface{" + name + "} request with ip address {" + ipV4Address + "}" );

        return ipV4Address;
    }

    public static boolean isNetworkInterfaceUp( String name )
    {

        try
        {
            if ( name != null && !name.equals( "" ) && !name.equals( ANY_VALID_INTERFACE ) )
            {
                NetworkInterface nic = NetworkInterface.getByName( name );
                if ( nic != null )
                    return nic.isUp();
            }
        }
        catch ( Exception e )
        {
            log.debug( "Error while getting Response from Hms OOB Agent.", e );
        }

        return false;
    }

    public static void main( String[] args )
        throws SocketException
    {
        getByInterfaceName( "eth0" );
        getByInterfaceName( "eth1" );
        getByInterfaceName( "ANY" );
    }
}
