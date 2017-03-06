/* ********************************************************************************
 * DhcpLease.java
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
package com.vmware.vrack.hms.common.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The Class DhcpLease.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class DhcpLease
{

    /** The ip address. */
    private String ipAddress;

    /** The mac address. */
    private String macAddress;

    /** The binding state. */
    private String bindingState;

    /** The starts. */
    private String starts;

    /** The ends. */
    private String ends;

    /** The client hostname. */
    private String clientHostname;

    /**
     * Gets the ip address.
     *
     * @return the ip address
     */
    public String getIpAddress()
    {
        return ipAddress;
    }

    /**
     * Sets the ip address.
     *
     * @param ipAddress the new ip address
     */
    public void setIpAddress( String ipAddress )
    {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets the mac address.
     *
     * @return the mac address
     */
    public String getMacAddress()
    {
        return macAddress;
    }

    /**
     * Sets the mac address.
     *
     * @param macAddress the new mac address
     */
    public void setMacAddress( String macAddress )
    {
        this.macAddress = macAddress;
    }

    /**
     * Gets the starts.
     *
     * @return the starts
     */
    public String getStarts()
    {
        return starts;
    }

    /**
     * Sets the starts.
     *
     * @param starts the new starts
     */
    public void setStarts( String starts )
    {
        this.starts = starts;
    }

    /**
     * Gets the ends.
     *
     * @return the ends
     */
    public String getEnds()
    {
        return ends;
    }

    /**
     * Sets the ends.
     *
     * @param ends the new ends
     */
    public void setEnds( String ends )
    {
        this.ends = ends;
    }

    /**
     * Gets the client hostname.
     *
     * @return the client hostname
     */
    public String getClientHostname()
    {
        return clientHostname;
    }

    /**
     * Sets the client hostname.
     *
     * @param clientHostname the new client hostname
     */
    public void setClientHostname( String clientHostname )
    {
        this.clientHostname = clientHostname;
    }

    /**
     * Gets the binding state.
     *
     * @return the binding state
     */
    public String getBindingState()
    {
        return bindingState;
    }

    /**
     * Sets the binding state.
     *
     * @param bindingState the new binding state
     */
    public void setBindingState( String bindingState )
    {
        this.bindingState = bindingState;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "[ " );
        sb.append( String.format( "ipAddress = %s, ", ipAddress ) );
        sb.append( String.format( "macAddress = %s, ", macAddress ) );
        sb.append( String.format( "starts = %s, ", starts ) );
        sb.append( String.format( "ends = %s, ", ends ) );
        sb.append( String.format( "bindingState = %s, ", bindingState ) );
        sb.append( String.format( "clientHostname = %s", clientHostname ) );
        sb.append( " ]" );
        return sb.toString();
    }
}
