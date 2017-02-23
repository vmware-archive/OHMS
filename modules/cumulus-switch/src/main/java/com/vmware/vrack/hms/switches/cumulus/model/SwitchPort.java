/* ********************************************************************************
 * SwitchPort.java
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.exception.HmsException;

/**
 * Provides basic implementation for Switch Port objects. (Inner classes for parsing cumulus Interfaces for 2.5) Created
 * by sankala on 12/5/14.
 */
public class SwitchPort
    extends ConfigBlock
{
    /** Variables provided to support the Switchport Object */
    boolean peer = false;

    Bridge parentBridge;

    String accessVlan;

    int mtu = 0;

    private Ipv4DefaultRoute ipv4DefaultRoute;

    private static String TOR_SWITCH_SPINE_BOND_NAME = "bd-spine";

    private static Logger logger = Logger.getLogger( SwitchPort.class );

    /**
     * Creates string with port config and other config details for the specific switch port.
     *
     * @return String for the switch port details
     */
    @Override
    public String toString()
    {
        return "SwitchPort{" + "id=" + name + ", portConfig='" + portConfig + '\'' + ", otherConfig='" + otherConfig
            + '\'' + ipv4DefaultRoute != null ? ", default route='" + ipv4DefaultRoute.toString() + '\'' : "" + '}';
    }

    /**
     * Set parent bridge value with provided bridge
     *
     * @param parent Bridge for the vlans
     */
    public void setParentBridge( Bridge parent )
    {
        parentBridge = parent;
        if ( accessVlan != null )
            return;
        if ( parent == null )
            return;

        if ( ( vlans == null || vlans.size() == 0 ) && parentBridge.vlans != null && parentBridge.vlans.size() > 0 )
        {
            // Inherit all vlans from bridge.
            vlans = new ArrayList<>();
            vlans.addAll( parentBridge.vlans );
        }
        if ( pvid == null || pvid == "" )
        {
            pvid = parentBridge.pvid;
        }
    }

    /**
     * Get parent bridge value
     *
     * @return Bridge
     */
    public Bridge getParentBridge()
    {
        return this.parentBridge;
    }

    public int getMtu()
    {
        return mtu;
    }

    public void setMtu( int mtu )
    {
        this.mtu = mtu;
    }

    /**
     * Get String for (peer, mtu or otherConfig value) for the name and port config details.
     *
     * @return String
     */
    public String getString()
    {
        if ( peer )
            return "";
        // TODO: Address multiple switch ports in slaves.
        String retString = "auto " + name + "\niface " + name + portConfig;
        if ( mtu > 1500 )
        {
            retString = retString + "\n    mtu " + mtu;
        }
        if ( ipv4DefaultRoute != null )
        {
            boolean isMgmtIf = name.equals( "eth0" ) ? true : false;
            retString = retString + "\n    " + ipv4DefaultRoute.getString( isMgmtIf );
        }
        else
            retString += getVlanConfig();

        if ( otherConfig != "" )
            retString += "\n" + otherConfig;

        return retString;
    }

    /**
     * Get vlan config value for both tagged and untagged vlans. Based on parent bridge for the vlans.
     *
     * @return String
     */
    protected String getVlanConfig()
    {
        String retString = "";

        if ( parentBridge == null )
            return "";

        /*
         * Set pvid also here if not already set
         */
        if ( pvid.equals( "" ) || pvid == null )
        {
            pvid = parentBridge.pvid;
        }

        if ( !this.name.equals( TOR_SWITCH_SPINE_BOND_NAME ) )
        {
            if ( accessVlan == null || accessVlan.equals( "" ) )
                accessVlan = pvid;
            if ( accessVlan != null && ( vlans == null || vlans.size() == 0 ) )
            {
                retString += "\n    bridge-access " + accessVlan;
                return retString;
            }
            // Both tagged and Untagged vlans must be same... to not to generate any string...
            if ( ( ( pvid != null && parentBridge.pvid != null && pvid.equals( parentBridge.pvid ) )
                || ( pvid == null && parentBridge.pvid == null ) ) &&
            // vlans must be same (tagged)
                ( ( vlans != null && parentBridge.vlans != null && vlans.equals( parentBridge.vlans ) )
                    || ( vlans == null && parentBridge.vlans == null ) ) )
            {
                // Everything is same as parent bridge. - nothing to configure;
                return retString;
            }
            if ( vlans != null && vlans.size() > 0 )
            {
                if ( pvid != null || pvid.length() > 0 )
                    retString += "\n    bridge-pvid " + pvid;

                retString += "\n    bridge-vids " + Configuration.joinCollection( vlans, " " );
            }
        }

        return retString;
    }

    static Pattern switchPortPattern =
        Pattern.compile( String.format( "\\s*(bridge-vids|bridge-pvid|bridge-access|mtu|%s|%s) (.*)$",
                                        Ipv4DefaultRoute.command, Ipv4DefaultRoute.mgmtGateway ) );

    /**
     * Add other configuration; determine if type is bridge-vids, bridge-access, bridge-pvid, mtu.
     */
    public void addOtherConfig( String aLine, Configuration configuration )
    {
        Matcher matcher = switchPortPattern.matcher( aLine );
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
                    logger.warn( "Error while parsing MTU size : " + matcher.group( 2 ) );
                }
            }
            else if ( matcher.group( 1 ).equals( Ipv4DefaultRoute.command ) )
            {
                try
                {
                    ipv4DefaultRoute = Ipv4DefaultRoute.getIpv4DefaultRoute( matcher.group( 2 ) );
                }
                catch ( Exception e )
                {
                    logger.warn( "Error while parsing IPv4 default route gateway : " + matcher.group( 2 ) );
                }
            }
            else if ( matcher.group( 1 ).equals( Ipv4DefaultRoute.mgmtGateway ) && name.equals( "eth0" ) )
            {
                try
                {
                    ipv4DefaultRoute = Ipv4DefaultRoute.getIpv4DefaultRoute( matcher.group( 2 ) );
                    logger.debug( "Found eth0 gateway = " + ipv4DefaultRoute.getGateway() + "\n" );
                }
                catch ( Exception e )
                {
                    logger.warn( "Error while parsing eth0 IPv4 default route gateway : " + matcher.group( 2 ) );
                }
            }
            else
            {
                logger.warn( "Found unexpected configuration line: " + aLine );
                super.addOtherConfig( aLine, configuration );
            }
        }
        else
            super.addOtherConfig( aLine, configuration );
    }

    /**
     * @return the ipv4Route
     */
    public Ipv4DefaultRoute getIpv4DefaultRoute()
    {
        return ipv4DefaultRoute;
    }

    /**
     * @param ipv4Route the ipv4Route to set
     */
    public void setIpv4DefaultRoute( Ipv4DefaultRoute ipv4Route )
    {
        this.ipv4DefaultRoute = ipv4Route;
    }

    public void deleteIpv4DefaultRoute()
        throws HmsException
    {
        this.ipv4DefaultRoute = null;
        Bridge bridge = this.getParentBridge();

        if ( bridge == null )
        {
            if ( this.parentConfig != null && this.parentConfig.bridges != null
                && !this.parentConfig.bridges.isEmpty() )
            {
                bridge = this.parentConfig.bridges.get( 0 );
                this.setParentBridge( bridge );
            }
            else
                throw new HmsException( "Catastrophic!!! Cumulus configuration may have been damaged. No parent VLAN aware bridge found for port "
                    + this.name );
        }

        if ( !bridge.members.contains( this ) )
            bridge.members.add( this );
    }
}
