/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vmware.vrack.hms.plugin.boardservice.redfish.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class EthernetInterfaceResource
    extends RedfishResource
{
    @JsonProperty( "FQDN" )
    private String fqdn;

    @JsonProperty( "HostName" )
    private String hostName;

    @JsonProperty( "Status" )
    private Status status;

    @JsonProperty( "InterfaceEnabled" )
    private Boolean interfaceEnabled;

    @JsonProperty( "PermanentMACAddress" )
    private String permanentMacAddress;

    @JsonProperty( "MACAddress" )
    private String macAddress;

    @JsonProperty( "LinkTechnology" )
    private String linkTechnology;

    @JsonProperty( "SpeedMbps" )
    private Integer speedMbps;

    @JsonProperty( "AutoNeg" )
    private Boolean autoNeg;

    @JsonProperty( "FullDuplex" )
    private Boolean fullDuplex;

    @JsonProperty( "MTUSize" )
    private Integer mtuSize;

    @JsonProperty( "IPv6DefaultGateway" )
    private String ipV6DefaultGateway;

    @JsonProperty( "MaxIPv6StaticAddresses" )
    private Integer maxIPv6StaticAddresses;

    @JsonProperty( "IPv4Addresses" )
    private List<IpV4Address> ipV4Addresses;

    @JsonProperty( "IPv6Addresses" )
    private List<IpV6Address> ipV6Addresses;

    @JsonProperty( "IPv6StaticAddresses" )
    private List<IpV6Address> ipV6StaticAddresses;

    @JsonProperty( "IPv6AddressPolicyTable" )
    private List<IpV6AddressPolicy> ipV6AddressesPolicies;

    @JsonProperty( "NameServers" )
    private List<String> nameServers;

    @JsonProperty( "VLAN" )
    private Vlan vlan;

    @JsonProperty( "VLANs" )
    private OdataId vlanCollection;

    public String getFqdn()
    {
        return fqdn;
    }

    public String getHostName()
    {
        return hostName;
    }

    public Status getStatus()
    {
        return status;
    }

    public Boolean getInterfaceEnabled()
    {
        return interfaceEnabled;
    }

    public String getPermanentMacAddress()
    {
        return permanentMacAddress;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public String getLinkTechnology()
    {
        return linkTechnology;
    }

    public Integer getSpeedMbps()
    {
        return speedMbps;
    }

    public Boolean getAutoNeg()
    {
        return autoNeg;
    }

    public Boolean getFullDuplex()
    {
        return fullDuplex;
    }

    public Integer getMtuSize()
    {
        return mtuSize;
    }

    public String getIpV6DefaultGateway()
    {
        return ipV6DefaultGateway;
    }

    public Integer getMaxIPv6StaticAddresses()
    {
        return maxIPv6StaticAddresses;
    }

    public List<IpV4Address> getIpV4Addresses()
    {
        return ipV4Addresses;
    }

    public List<IpV6Address> getIpV6Addresses()
    {
        return ipV6Addresses;
    }

    public List<IpV6Address> getIpV6StaticAddresses()
    {
        return ipV6StaticAddresses;
    }

    public List<IpV6AddressPolicy> getIpV6AddressesPolicies()
    {
        return ipV6AddressesPolicies;
    }

    public List<String> getNameServers()
    {
        return nameServers;
    }

    public Vlan getVlan()
    {
        return vlan;
    }

    public OdataId getVlanCollection()
    {
        return vlanCollection;
    }

    @Override
    public Set<OdataId> getRelatedResources()
    {
        Set<OdataId> relatedResources = new HashSet<>();
        if ( vlanCollection != null )
        {
            relatedResources.add( vlanCollection );
        }
        return relatedResources;
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static class IpV4Address
    {
        @JsonProperty( "Address" )
        private String address;

        @JsonProperty( "SubnetMask" )
        private String subnetMask;

        @JsonProperty( "AddressOrigin" )
        private AddressOrigin addressOrigin;

        @JsonProperty( "Gateway" )
        private String gateway;

        public String getAddress()
        {
            return address;
        }

        public String getSubnetMask()
        {
            return subnetMask;
        }

        public AddressOrigin getAddressOrigin()
        {
            return addressOrigin;
        }

        public String getGateway()
        {
            return gateway;
        }

        public enum AddressOrigin
        {
            Static,
            DHCP,
            BOOTP,
            IPv4LinkLocal
        }
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static class IpV6Address
    {
        @JsonProperty( "Address" )
        private String address;

        @JsonProperty( "PrefixLength" )
        private Integer prefixLength;

        @JsonProperty( "AddressOrigin" )
        private AddressOrigin addressOrigin;

        @JsonProperty( "AddressState" )
        private AddressState addressState;

        public String getAddress()
        {
            return address;
        }

        public Integer getPrefixLength()
        {
            return prefixLength;
        }

        public AddressOrigin getAddressOrigin()
        {
            return addressOrigin;
        }

        public AddressState getAddressState()
        {
            return addressState;
        }

        public enum AddressOrigin
        {
            Static,
            DHCPv6,
            LinkLocal,
            SLAAC
        }

        public enum AddressState
        {
            Preferred,
            Deprecated,
            Tentative,
            Failed
        }
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static class IpV6AddressPolicy
    {
        @JsonProperty( "Prefix" )
        private String prefix;

        @JsonProperty( "Precedence" )
        private Integer precedence;

        @JsonProperty( "Label" )
        private Integer label;

        public String getPrefix()
        {
            return prefix;
        }

        public Integer getPrecedence()
        {
            return precedence;
        }

        public Integer getLabel()
        {
            return label;
        }
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static class Vlan
    {
        @JsonProperty( "VLANEnable" )
        private Boolean vlanEnable;

        @JsonProperty( "VLANId" )
        private Integer vlanId;

        public Boolean getVlanEnable()
        {
            return vlanEnable;
        }

        public Integer getVlanId()
        {
            return vlanId;
        }
    }
}
