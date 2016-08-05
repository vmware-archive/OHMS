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
package com.vmware.vrack.hms.common.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude( JsonInclude.Include.NON_NULL )
@JsonIgnoreProperties( ignoreUnknown = true )
public class ServiceItem
{
    private String serviceType;

    private String serviceEndpoint;

    private List<InBandAccess> inBandAccess = new ArrayList<>();

    public String getServiceType()
    {
        return serviceType;
    }

    public void setServiceType( String serviceType )
    {
        this.serviceType = serviceType;
    }

    public String getServiceEndpoint()
    {
        return serviceEndpoint;
    }

    public List<InBandAccess> getInBandAccess()
    {
        return inBandAccess;
    }

    public void setServiceEndpoint( String serviceEndpoint )
    {
        this.serviceEndpoint = serviceEndpoint;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        ServiceItem that = (ServiceItem) o;
        return Objects.equals( serviceType, that.serviceType ) &&
            Objects.equals( serviceEndpoint, that.serviceEndpoint );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( serviceType, serviceEndpoint );
    }

    public static class InBandAccess
    {
        private String uuid;

        private Integer port;

        private String protocol;

        private String ipAddress;

        private String username;

        private String password;

        private String hypervisorName;

        private String hypervisorProvider;

        public String getUuid()
        {
            return uuid;
        }

        public Integer getPort()
        {
            return port;
        }

        public String getProtocol()
        {
            return protocol;
        }

        public String getIpAddress()
        {
            return ipAddress;
        }

        public String getUsername()
        {
            return username;
        }

        public String getPassword()
        {
            return password;
        }

        public String getHypervisorName()
        {
            return hypervisorName;
        }

        public void setHypervisorName( String hypervisorName )
        {
            this.hypervisorName = hypervisorName;
        }

        public String getHypervisorProvider()
        {
            return hypervisorProvider;
        }

        public void setHypervisorProvider( String hypervisorProvider )
        {
            this.hypervisorProvider = hypervisorProvider;
        }
    }
}
