/* ********************************************************************************
 * OperationStatus.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.component.lifecycle.resource;

/**
 * <code>OperationStatus</code> is ... <br>
 */
public enum OperationStatus
{
    /** The lifecycle operation has successfully completed. */
    COMPLETED, /** The lifecycle operation is in-progress. */
    INPROGRESS, /** The lifecycle operation has failed. */
    FAILED;
    public static OperationStatus fromValue( String value )
    {
        return valueOf( value );
    }
}
