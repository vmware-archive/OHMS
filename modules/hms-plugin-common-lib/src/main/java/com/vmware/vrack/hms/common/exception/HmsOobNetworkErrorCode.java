/* ********************************************************************************
 * HmsOobNetworkErrorCode.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.exception;

public enum HmsOobNetworkErrorCode
{
    ARGUMENT_SYNTAX_ERROR,
    SWITCH_UNREACHABLE,
    UPLOAD_FAILED,
    DOWNLOAD_FAILED,
    NO_DISK_SPACE,
    SET_OPERATION_FAILED,
    GET_OPERATION_FAILED,
    UPDATE_OPERATION_FAILED,
    DELETE_OPERATION_FAILED,
    INTERNAL_ERROR,
    ROLLBACK_FAILED
}
