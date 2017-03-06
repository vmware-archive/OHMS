/* ********************************************************************************
 * UpgradeStatus.java
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
import com.vmware.vrack.hms.common.resource.UpgradeStatusCode;

/**
 * <code>OobUpgradeStatus</code> is ... <br>
 *
 * @author VMware, Inc.
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class UpgradeStatus
{

    /** The id. */
    private String id;

    /** The status code. */
    private UpgradeStatusCode statusCode;

    /** The status message. */
    private String statusMessage;

    /** The more info. */
    private String moreInfo;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * Gets the status code.
     *
     * @return the statusCode
     */
    public UpgradeStatusCode getStatusCode()
    {
        return statusCode;
    }

    /**
     * Sets the status code.
     *
     * @param statusCode the statusCode to set
     */
    public void setStatusCode( UpgradeStatusCode statusCode )
    {
        this.statusCode = statusCode;
    }

    /**
     * Gets the status message.
     *
     * @return the statusMessage
     */
    public String getStatusMessage()
    {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statusMessage the statusMessage to set
     */
    public void setStatusMessage( String statusMessage )
    {
        this.statusMessage = statusMessage;
    }

    /**
     * Gets the more info.
     *
     * @return the moreInfo
     */
    public String getMoreInfo()
    {
        return moreInfo;
    }

    /**
     * Sets the more info.
     *
     * @param moreInfo the moreInfo to set
     */
    public void setMoreInfo( String moreInfo )
    {
        this.moreInfo = moreInfo;
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
        sb.append( "statusCode = " + this.statusCode.toString() + ", " );
        sb.append( "statusMessage = " + this.statusMessage + ", " );
        sb.append( "moreInfo = " + this.moreInfo + ", " );
        return sb.toString();
    }
}
