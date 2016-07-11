/* ********************************************************************************
 * UpgradeStatusCode.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource;

/**
 * <code>UpgradeStatusCode</code><br>
 *
 * @author VMware, Inc.
 * @version <Implementation version of this type>
 * @since <Product revision>
 */
public enum UpgradeStatusCode
{
    /*
     * ******************************************************************************** ***** HMS OOB STAUTS CODES *****
     * ********************************************************************************
     */
    /** The hms oob upgrade success. */
    HMS_OOB_UPGRADE_INITIATED( "HMS Out-of-band agent upgrade initiated." ),
    /** The hms oob upgrade script invalid arguments. */
    HMS_OOB_UPGRADE_SCRIPT_INVALIDARGUMENTS( "Invalid arguments passed to HMS Out-of-band agent upgrade script." ),
    /** The hms oob upgrade script validation failed. */
    HMS_OOB_UPGRADE_SCRIPT_VALIDATION_FAILED( "HMS Out-of-band agent upgrade script validation failed." ),
    /** The hms oob upgrade success. */
    HMS_OOB_UPGRADE_SUCCESS( "HMS Out-of-band agent upgrade succeeded." ), /** The hms oob upgrade fail. */
    HMS_OOB_UPGRADE_FAILED( "HMS Out-of-band agent upgrade failed." ), /** The hms oob backup failed. */
    HMS_OOB_BACKUP_FAILED( "HMS Out-of-band agent backup failed." ), /** The hms oob rollback failed. */
    HMS_OOB_ROLLBACK_FAILED( "HMS Out-of-band agent rollback failed." ), /** The hms oob rollback failed. */
    HMS_OOB_ROLLBACK_SUCCESS( "HMS Out-of-band agent rollback succeeded." ), /** The hms oob upgrade invalid request. */
    HMS_OOB_UPGRADE_INVALID_REQUEST( "Invalid request to initiate HMS Out-of-band agent upgrade." ),
    /** The hms oob upgrade forbidden. */
    HMS_OOB_UPGRADE_FORBIDDEN( "HMS Out-of-band agent upgrade forbidden." ), /** The hms oob upgrade internal error. */
    HMS_OOB_UPGRADE_INTERNAL_ERROR( "An internal error occurred while initiating HMS Out-of-band agent upgrade." ),
    /*
     * ******************************************************************************** ***** HMS AGGREGATOR STAUTS
     * CODES ***** ********************************************************************************
     */
    /** The hms upgrade success. */
    HMS_UPGRADE_INITIATED( "HMS upgrade initiated." ), /** The hms upgrade script invalid arguments. */
    HMS_UPGRADE_SCRIPT_INVALIDARGUMENTS( "Invalid arguments passed to HMS upgrade script." ),
    /** The hms upgrade script validation failed. */
    HMS_UPGRADE_SCRIPT_VALIDATION_FAILED( "HMS upgrade script validation failed." ), /** The hms upgrade success. */
    HMS_UPGRADE_SUCCESS( "HMS upgrade succeeded." ), /** The hms upgrade fail. */
    HMS_UPGRADE_FAILED( "HMS upgrade failed." ), /** The hms upgrade fail. */
    HMS_UPGRADE_FAILED_RESTART_REQUIRED( "HMS upgrade failed. HMS to be restarted manually." ),
    /** The hms backup failed. */
    HMS_BACKUP_FAILED( "HMS backup failed." ), /** The hms rollback failed. */
    HMS_ROLLBACK_FAILED( "HMS rollback failed." ), /** The hms rollback failed. */
    HMS_ROLLBACK_SUCCESS( "HMS rollback succeeded." ), /** The hms upgrade invalid request. */
    HMS_UPGRADE_INVALID_REQUEST( "Invalid request to initiate HMS upgrade." ), /** The hms upgrade forbidden. */
    HMS_UPGRADE_FORBIDDEN( "HMS upgrade forbidden." ), /** The hms upgrade internal error. */
    HMS_UPGRADE_INTERNAL_ERROR( "An internal error occurred while initiating HMS upgrade." ),;
    /** The status message. */
    private String statusMessage;

    /**
     * Instantiates a new upgrade status code.
     *
     * @param statusMessage the status message
     */
    private UpgradeStatusCode( String statusMessage )
    {
        this.setStatusMessage( statusMessage );
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public String getStatusMessage()
    {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statusMessage the new status message
     */
    public void setStatusMessage( String statusMessage )
    {
        this.statusMessage = statusMessage;
    }
}
