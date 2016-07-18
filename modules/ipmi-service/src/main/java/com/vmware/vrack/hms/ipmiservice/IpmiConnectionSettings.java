/* ********************************************************************************
 * IpmiConnectionSettings.java
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
package com.vmware.vrack.hms.ipmiservice;


import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;

public class IpmiConnectionSettings
{
    private ServiceServerNode node;

    private int cipherSuiteIndex;

    private CipherSuite cipherSuite;

    public ServiceServerNode getNode()
    {
        return node;
    }

    public void setNode( ServiceServerNode node )
    {
        this.node = node;
    }

    public int getCipherSuiteIndex()
    {
        return cipherSuiteIndex;
    }

    public void setCipherSuiteIndex( int cipherSuiteIndex )
    {
        this.cipherSuiteIndex = cipherSuiteIndex;
    }

    public CipherSuite getCipherSuite()
    {
        return cipherSuite;
    }

    public void setCipherSuite( CipherSuite cipherSuite )
    {
        this.cipherSuite = cipherSuite;
    }

    @Override
    public int hashCode()
    {
        if ( node != null && node.getNodeID() != null )
            return node.getNodeID().hashCode();
        else
            return 0;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        IpmiConnectionSettings other = (IpmiConnectionSettings) obj;
        if ( this.node.getNodeID().equals( other.node.getNodeID() ) )
            return true;
        else
            return false;
    }
}
