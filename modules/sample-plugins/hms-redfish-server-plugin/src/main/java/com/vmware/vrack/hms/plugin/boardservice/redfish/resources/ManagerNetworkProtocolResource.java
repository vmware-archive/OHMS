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

import java.util.Collections;
import java.util.Set;

@JsonIgnoreProperties( ignoreUnknown = true )
public final class ManagerNetworkProtocolResource
    extends RedfishResource
{
    @JsonProperty( "Status" )
    private Status status;

    @JsonProperty( "HostName" )
    private String hostName;

    @JsonProperty( "FQDN" )
    private String fqdn;

    @JsonProperty( "HTTP" )
    private Protocol http = new Protocol();

    @JsonProperty( "HTTPS" )
    private Protocol https = new Protocol();

    @JsonProperty( "IPMI" )
    private Protocol ipmi = new Protocol();

    @JsonProperty( "SSH" )
    private Protocol ssh = new Protocol();

    @JsonProperty( "SNMP" )
    private Protocol snmp = new Protocol();

    @JsonProperty( "VirtualMedia" )
    private Protocol virtualMedia = new Protocol();

    @JsonProperty( "SSDP" )
    private SSDP ssdp = new SSDP();

    @JsonProperty( "Telnet" )
    private Protocol telnet = new Protocol();

    @JsonProperty( "KVMIP" )
    private Protocol kvmip = new Protocol();

    public Status getStatus()
    {
        return status;
    }

    public String getHostName()
    {
        return hostName;
    }

    public String getFqdn()
    {
        return fqdn;
    }

    public Protocol getHttp()
    {
        return http;
    }

    public Protocol getHttps()
    {
        return https;
    }

    public Protocol getIpmi()
    {
        return ipmi;
    }

    public Protocol getSsh()
    {
        return ssh;
    }

    public Protocol getSnmp()
    {
        return snmp;
    }

    public Protocol getVirtualMedia()
    {
        return virtualMedia;
    }

    public SSDP getSsdp()
    {
        return ssdp;
    }

    public Protocol getTelnet()
    {
        return telnet;
    }

    public Protocol getKvmip()
    {
        return kvmip;
    }

    @Override
    public Set<OdataId> getRelatedResources()
    {
        return Collections.emptySet();
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static class Protocol
    {
        @JsonProperty( "ProtocolEnabled" )
        private Boolean protocolEnabled;

        @JsonProperty( "Port" )
        private Integer port;

        public Boolean getProtocolEnabled()
        {
            return protocolEnabled;
        }

        public Integer getPort()
        {
            return port;
        }
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static final class SSDP
        extends Protocol
    {
        @JsonProperty( "NotifyMulticastIntervalSeconds" )
        private Integer notifyMulticastIntervalSeconds;

        @JsonProperty( "NotifyTTL" )
        private Integer notifyTtl;

        @JsonProperty( "NotifyIPv6Scope" )
        private NotifyIpV6Scope notifyIpV6Scope;

        public Integer getNotifyMulticastIntervalSeconds()
        {
            return notifyMulticastIntervalSeconds;
        }

        public Integer getNotifyTtl()
        {
            return notifyTtl;
        }

        public NotifyIpV6Scope getNotifyIpV6Scope()
        {
            return notifyIpV6Scope;
        }

        public enum NotifyIpV6Scope
        {
            Link,
            Site,
            Organization
        }
    }
}
