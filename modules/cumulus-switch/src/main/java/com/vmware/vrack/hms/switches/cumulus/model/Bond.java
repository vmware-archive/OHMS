/* ********************************************************************************
 * Bond.java
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
package com.vmware.vrack.hms.switches.cumulus.model;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides basic implementation for the object Bond. Extends Switch Port implementation. Created by sankala on 12/5/14.
 */
public class Bond
    extends SwitchPort
{
    /** Set of variables used for create the object bond */
    public List<SwitchPort> slaves = new ArrayList<>();

    static Pattern bondPattern =
        Pattern.compile( "\\s*(bond-slaves|bridge-vlan-aware|bridge-ports|bridge-vids|bridge-pvid|bridge-access|mtu|bond-lacp-bypass-allow|address) (.*)$" );

    private static Logger logger = Logger.getLogger( Bond.class );

    static final String format = "auto %s\n" + "iface %s\n" + "    bond-slaves %s\n" + "    bond-mode 802.3ad\n"
        + "    bond-miimon 100\n" + "    bond-use-carrier 1\n" + "    bond-lacp-rate 0\n" + "    bond-min-links 1\n"
        + "    bond-xmit-hash-policy layer3+4";

    boolean allowLacpBypass = false;

    // IP address may be specified on a bond
    private String ipAddr = null;

    public boolean isAllowLacpBypass()
    {
        return allowLacpBypass;
    }

    public void setAllowLacpBypass( boolean bypass )
    {
        this.allowLacpBypass = bypass;
    }

    public String getIpAddr()
    {
        return ipAddr;
    }

    public void setIpAddr( String ipAddr )
    {
        this.ipAddr = ipAddr;
    }

    /**
     * Function used to get a string, separate the MTU information from slave switch ports.
     *
     * @return String of the mtu value and the vlan config string
     */
    public String getString()
    {
        String slaveNames = "";

        if ( slaves != null )
        {
            for ( SwitchPort s : slaves )
                slaveNames += s.name + " ";
        }

        String retString = String.format( format, name, name, slaveNames.trim() );
        if ( mtu == 0 )
        {
            // Get the mtu information from Slave Switch Ports.
            for ( SwitchPort s : slaves )
            {
                if ( s.mtu > 0 )
                    mtu = s.mtu;
            }
        }
        if ( mtu >= 1500 )
            retString = retString + "\n    mtu " + mtu;

        if ( allowLacpBypass )
            retString = retString + "\n    bond-lacp-bypass-allow 1";

        if ( ipAddr != null && !ipAddr.isEmpty() )
            retString += "\n    address " + ipAddr;

        if ( allowLacpBypass )
        {
            return retString; // Enable all VLANs for POC interfaces
        }

        if ( getIpv4DefaultRoute() != null )
        {
            retString = retString + "\n    " + getIpv4DefaultRoute().getString( false );
        }

        return retString + getVlanConfig();
    }

    /**
     * Function used to add another configuration detail to the bond.
     *
     * @param aLine
     * @param configuration details to add
     * @return void
     */
    public void addOtherConfig( String aLine, Configuration configuration )
    {
        Matcher matcher = bondPattern.matcher( aLine );
        if ( matcher.matches() )
        {
            if ( matcher.group( 1 ).equals( "bridge-vids" ) )
            {
                String[] vlanIds = matcher.group( 2 ).split( " " );
                if ( vlans == null )
                    vlans = new ArrayList<>();
                vlans.addAll( Arrays.asList( vlanIds ) );
            }
            else if ( matcher.group( 1 ).equals( "bridge-access" ) )
            {
                accessVlan = matcher.group( 2 );
                pvid = accessVlan;
            }
            else if ( matcher.group( 1 ).equals( "bridge-pvid" ) )
            {
                pvid = matcher.group( 2 );
            }
            else if ( matcher.group( 1 ).equals( "mtu" ) )
            {
                try
                {
                    mtu = Integer.parseInt( matcher.group( 2 ) );
                }
                catch ( Exception e )
                {
                    logger.warn( "Error while parsing mtu : " + matcher.group( 2 ) );
                }
            }
            else if ( matcher.group( 1 ).equals( "bond-slaves" ) )
            {
                String[] slaveSwps = matcher.group( 2 ).split( " " );
                for ( String slaveSwpName : slaveSwps )
                {
                    SwitchPort slaveSwp = new SwitchPort();
                    slaveSwp.name = slaveSwpName;
                    slaves.add( slaveSwp );
                }
            }
            else if ( matcher.group( 1 ).equals( "bond-lacp-bypass-allow" ) )
            {
                if ( matcher.group( 2 ).equals( "1" ) )
                    allowLacpBypass = true;
            }
            else if ( matcher.group( 1 ).equals( "address" ) )
            {
                if ( !( matcher.group( 2 ) == null || matcher.group( 2 ).isEmpty() ) )
                    ipAddr = matcher.group( 2 );
            }
        }
        else
            super.addOtherConfig( aLine, configuration );
    }

    /**
     * To verify if any of the slave ports are matching with a specific port name or not
     * 
     * @param name
     * @return
     */
    public Boolean containsPortName( String name )
    {
        for ( SwitchPort slave : slaves )
        {
            if ( slave.name.equals( name ) )
                return true;
        }

        return false;
    }
}
