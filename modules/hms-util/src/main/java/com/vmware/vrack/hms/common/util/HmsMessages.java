/* ********************************************************************************
 * HmsMessages.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

/**
 * Class to hold text constant string for success/failure messages
 * 
 * @author Yagnesh Chawda
 */
public class HmsMessages
{
    public static final String INVALID_REQ = "Invalid Request";

    public static final String SUCCESS_MSG = "Operation completed successfully";

    public static final String NO_DATA_FOUND = "No data found";

    public static final String FAILED_MSG = "Operation failed";

    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    public static final String ONE_OR_MORE_ITEMS_IN_REQ_FAILED = "Operation failed for few items";

    public static final String SUBSCRIPTION_ERROR = "Error while subscribing to events.";

    public static final String SUBSCRIPTION_SUCCESS = "Event Registration suceeded for subcription request.";

    public static final String SUBSCRIPTION_SUCCESS_WITH_ERRORS =
        "Event Registration suceeded with errors for subcription request.";

    public static final String UNSUBSCRIPTION_ERROR = "Error while unsubscribing to events.";

    public static final String UNSUBSCRIPTION_SUCCESS = "Unsubscription suceeded for subcription request.";

    public static final String UNSUBSCRIPTION_SUCCESS_WITH_ERRORS =
        "Event Unsubscription suceeded with errors for subcription request.";

    public static final String NME_SUBSCRIPTION_ERROR = "Error while subscribing to NME events.";

    public static final String NME_SUBSCRIPTION_SUCCESS = "Event subscription suceeded for NME request.";

    public static final String NME_SUBSCRIPTION_SUCCESS_WITH_ERRORS =
        "Event subscription suceeded with errors for NME request.";

    public static final String EVENT_BROADCAST_SUCCESSFUL = "Events Successfully Broadcasted";

    public static final String EVENT_BROADCAST_FAILED = "Events broadcast failed";
}
