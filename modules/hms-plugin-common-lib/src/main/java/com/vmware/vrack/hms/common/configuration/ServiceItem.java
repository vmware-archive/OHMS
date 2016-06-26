package com.vmware.vrack.hms.common.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude( JsonInclude.Include.NON_NULL )
@JsonIgnoreProperties( ignoreUnknown = true )
public class ServiceItem
{
    private String serviceType;

    private String serviceEndpoint;

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
}
