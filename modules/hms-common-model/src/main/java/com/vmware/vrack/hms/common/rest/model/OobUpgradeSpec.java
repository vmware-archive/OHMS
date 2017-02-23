/* ********************************************************************************
 * OobUpgradeSpec.java
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
 * <code>UpgradeSpec</code> is the model that HMS Aggregator uses while invoking Upgrade service of OOB Agent to upgrade
 * OOB Agent.<br>
 * <p>
 * Spec will have all the required parameters that OOB agent needs to upgrade itself.
 *
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class OobUpgradeSpec
{

    /** The HMS token. */
    private String id;

    /** Upgrade binary file name. */
    private String fileName;

    /** Upgrade binary checksum value. */
    private String checksum;

    /**
     * Gets the HMS token.
     *
     * @return the upgrade id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id for the upgrade.
     *
     * @param id Unique id to track the upgrade
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Sets the file name.
     *
     * @param fileName the new file name
     */
    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }

    /**
     * Gets the checksum.
     *
     * @return the checksum.
     */
    public String getChecksum()
    {
        return checksum;
    }

    /**
     * Sets the checksum.
     *
     * @param checksum the new checksum
     */
    public void setChecksum( String checksum )
    {
        this.checksum = checksum;
    }

    /**
     * To string.
     *
     * @return the string
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {

        StringBuffer sb = new StringBuffer();
        sb.append( "[ " );
        sb.append( "id = " + this.id + ", " );
        sb.append( "fileName = " + this.fileName + ", " );
        sb.append( "checksum = " + this.checksum );
        sb.append( " ]" );
        return sb.toString();
    }

}
