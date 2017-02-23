/* ********************************************************************************
 * SwitchNetworkConfiguration.java
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

package com.vmware.vrack.hms.common.switches.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Deprecated
public class SwitchNetworkConfiguration
{
    private String name;

    private String description;

    private String version;

    private List<String> applicableSwitchTypes;

    private List<Port> ports;

    private List<LacpGroup> lacpGroups;

    private List<Vlan> vlans;

    private List<Ipv4> ipv4;

    private Routes routes;

    private Management management;

    private static Logger logger = Logger.getLogger( SwitchNetworkConfiguration.class );

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public List<String> getApplicableSwitchTypes()
    {
        return applicableSwitchTypes;
    }

    public void setApplicableSwitchTypes( List<String> applicableSwitchTypes )
    {
        this.applicableSwitchTypes = applicableSwitchTypes;
    }

    public List<Port> getPorts()
    {
        return ports;
    }

    public void setPorts( List<Port> ports )
    {
        this.ports = ports;
    }

    public List<Vlan> getVlans()
    {
        return vlans;
    }

    public void setVlans( List<Vlan> vlans )
    {
        this.vlans = vlans;
    }

    public List<LacpGroup> getLacpGroups()
    {
        return lacpGroups;
    }

    public void setLacpGroups( List<LacpGroup> lacpGroups )
    {
        this.lacpGroups = lacpGroups;
    }

    public List<Ipv4> getIpv4()
    {
        return ipv4;
    }

    public void setIpv4( List<Ipv4> ipv4 )
    {
        this.ipv4 = ipv4;
    }

    public Routes getRoutes()
    {
        return routes;
    }

    public void setRoutes( Routes routes )
    {
        this.routes = routes;
    }

    public Management getManagement()
    {
        return management;
    }

    public void setManagement( Management management )
    {
        this.management = management;
    }

    @JsonIgnore
    public Map<String, List<Port>> getLacpGroupPortMap()
    {
        Map<String, List<Port>> lacpGroupPortMap = new TreeMap<String, List<Port>>();

        for ( LacpGroup lg : lacpGroups )
        {
            lacpGroupPortMap.put( lg.getName(), new ArrayList<Port>() );
        }

        for ( Port p : ports )
        {
            if ( p.getLacpMode() )
            {
                if ( !lacpGroupPortMap.containsKey( p.getLacpGroup() ) )
                {
                    lacpGroupPortMap.put( p.getLacpGroup(), new ArrayList<Port>() );
                }

                List<Port> value = lacpGroupPortMap.get( p.getLacpGroup() );
                value.add( p );
            }
        }

        /* Filter out any empty lacp groups. */
        Iterator<Map.Entry<String, List<Port>>> iter = lacpGroupPortMap.entrySet().iterator();
        while ( iter.hasNext() )
        {
            Map.Entry<String, List<Port>> entry = iter.next();
            if ( entry.getValue().isEmpty() )
            {
                logger.warn( "LACP group " + entry.getKey() + " does not have any defined sub-interfaces." );
                iter.remove();
            }
        }

        return lacpGroupPortMap;
    }

    @JsonIgnore
    public Map<String, Ipv4> getPortIpv4Map()
    {
        Map<String, Ipv4> portIpv4Map = new HashMap<String, Ipv4>();

        if ( ipv4 != null )
        {
            for ( Ipv4 i : ipv4 )
            {
                portIpv4Map.put( i.getInterface(), i );
            }
        }

        return portIpv4Map;
    }

    public static class Port
    {
        private String name;

        private SwitchPort.PortType type;

        private Boolean lacpMode;

        private String lacpGroup;

        private Integer mtu;

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public SwitchPort.PortType getType()
        {
            return type;
        }

        public void setType( SwitchPort.PortType type )
        {
            this.type = type;
        }

        public Boolean getLacpMode()
        {
            return lacpMode;
        }

        public void setLacpMode( Boolean lacpMode )
        {
            this.lacpMode = lacpMode;
        }

        public String getLacpGroup()
        {
            return lacpGroup;
        }

        public void setLacpGroup( String lacpGroup )
        {
            this.lacpGroup = lacpGroup;
        }

        public Integer getMtu()
        {
            return mtu;
        }

        public void setMtu( Integer mtu )
        {
            this.mtu = mtu;
        }
    }

    public static class LacpGroup
    {
        public enum LacpGroupType
        {
            LACPGROUP
        };

        private String name;

        private LacpGroupType type;

        private String mode;

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public LacpGroupType getType()
        {
            return type;
        }

        public void setType( LacpGroupType type )
        {
            this.type = type;
        }

        public String getMode()
        {
            return mode;
        }

        public void setMode( String mode )
        {
            this.mode = mode;
        }
    }

    public static class Vlan
    {
        public enum VlanType
        {
            VLAN
        };

        private String name;

        private int vlanId;

        private VlanType type;

        private List<String> taggedPorts;

        private List<String> untaggedPorts;

        private VlanSTP stp;

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public int getVlanId()
        {
            return vlanId;
        }

        public void setVlanId( int vlanId )
        {
            this.vlanId = vlanId;
        }

        public VlanType getType()
        {
            return type;
        }

        public void setType( VlanType type )
        {
            this.type = type;
        }

        public List<String> getTaggedPorts()
        {
            return taggedPorts;
        }

        public void setTaggedPorts( List<String> taggedPorts )
        {
            this.taggedPorts = taggedPorts;
        }

        public List<String> getUntaggedPorts()
        {
            return untaggedPorts;
        }

        public void setUntaggedPorts( List<String> untaggedPorts )
        {
            this.untaggedPorts = untaggedPorts;
        }

        public VlanSTP getStp()
        {
            return stp;
        }

        public void setStp( VlanSTP stp )
        {
            this.stp = stp;
        }
    }

    public static class VlanSTP
    {
        private boolean enabled = false;

        private String protocol = "rstp";

        public boolean getStatus()
        {
            return enabled;
        }

        public void setStatus( boolean status )
        {
            this.enabled = status;
        }

        public boolean getEnabled()
        {
            return getStatus();
        }

        public void setEnabled( boolean status )
        {
            setStatus( status );
        }

        public String getProtocol()
        {
            return protocol;
        }

        public void setProtocol( String protocol )
        {
            this.protocol = protocol;
        }
    }

    public static class Ipv4
    {
        private String interfaceName;

        private String address;

        private String netmask;

        private String gateway;

        public String getInterface()
        {
            return interfaceName;
        }

        public void setInterface( String interfaceName )
        {
            this.interfaceName = interfaceName;
        }

        public String getAddress()
        {
            return address;
        }

        public void setAddress( String address )
        {
            this.address = address;
        }

        public String getNetmask()
        {
            return netmask;
        }

        public void setNetmask( String netmask )
        {
            this.netmask = netmask;
        }

        public String getGateway()
        {
            return gateway;
        }

        public void setGateway( String gateway )
        {
            this.gateway = gateway;
        }
    }

    public static class Routes
    {
        private List<Static> static_route;

        private List<Vrr> vrr;

        private List<Clag> clag;

        private Object ospf;

        private Object bgp;

        public List<Static> getStatic()
        {
            return static_route;
        }

        public void setStatic( List<Static> static_route )
        {
            this.static_route = static_route;
        }

        public List<Vrr> getVrr()
        {
            return vrr;
        }

        public void setVrr( List<Vrr> vrr )
        {
            this.vrr = vrr;
        }

        public List<Clag> getClag()
        {
            return clag;
        }

        public void setClag( List<Clag> clag )
        {
            this.clag = clag;
        }

        public Object getOspf()
        {
            return ospf;
        }

        public void setOspf( Object ospf )
        {
            this.ospf = ospf;
        }

        public Object getBgp()
        {
            return bgp;
        }

        public void setBgp( Object bgp )
        {
            this.bgp = bgp;
        }

        public static class Static
        {
            private String dst;

            private String gateway;

            private String interfaceName;

            public String getDst()
            {
                return dst;
            }

            public void setDst( String dst )
            {
                this.dst = dst;
            }

            public String getGateway()
            {
                return gateway;
            }

            public void setGateway( String gateway )
            {
                this.gateway = gateway;
            }

            public String getInterface()
            {
                return interfaceName;
            }

            public void setInterface( String interfaceName )
            {
                this.interfaceName = interfaceName;
            }
        }

        public static class Vrr
        {
            private String interfaceName;

            private String controlInterface;

            private String sharedIp;

            private String sharedMac;

            public String getInterface()
            {
                return interfaceName;
            }

            public void setInterface( String interfaceName )
            {
                this.interfaceName = interfaceName;
            }

            public String getControlInterface()
            {
                return controlInterface;
            }

            public void setControlInterface( String controlInterface )
            {
                this.controlInterface = controlInterface;
            }

            public String getSharedIp()
            {
                return sharedIp;
            }

            public void setSharedIp( String sharedIp )
            {
                this.sharedIp = sharedIp;
            }

            public String getSharedMac()
            {
                return sharedMac;
            }

            public void setSharedMac( String sharedMac )
            {
                this.sharedMac = sharedMac;
            }
        }

        public static class Clag
        {
            private String interfaceName;

            private String peerIp;

            private String sharedMac;

            public String getInterface()
            {
                return interfaceName;
            }

            public void setInterface( String interfaceName )
            {
                this.interfaceName = interfaceName;
            }

            public String getPeerIp()
            {
                return peerIp;
            }

            public void setPeerIp( String peerIp )
            {
                this.peerIp = peerIp;
            }

            public String getSharedMac()
            {
                return sharedMac;
            }

            public void setSharedMac( String sharedMac )
            {
                this.sharedMac = sharedMac;
            }
        }
    }

    public static class Management
    {
        private String username;

        private String password;

        private String newPassword;

        private String newPasswordConfirm;

        private Ipv4 ipv4;

        public String getUsername()
        {
            return username;
        }

        public void setUsername( String username )
        {
            this.username = username;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword( String password )
        {
            this.password = password;
        }

        public String getNewPassword()
        {
            return newPassword;
        }

        public void setNewPassword( String newPassword )
        {
            this.newPassword = newPassword;
        }

        public String getNewPasswordConfirm()
        {
            return newPasswordConfirm;
        }

        public void setNewPasswordConfirm( String newPasswordConfirm )
        {
            this.newPasswordConfirm = newPasswordConfirm;
        }

        public Ipv4 getIpv4()
        {
            return ipv4;
        }

        public void setIpv4( Ipv4 ipv4 )
        {
            this.ipv4 = ipv4;
        }
    }
}
