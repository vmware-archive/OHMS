/* ********************************************************************************
 * IpmiServiceCompletionCode.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.ipmiservice;

/**
 * IPMI Response Data Completion code and Description/Message Reference: IPMI 2.0 specification
 * 
 * @author VMware Inc
 */
public enum IpmiServiceCompletionCode
{
    /**
     * Command completed normally.
     */
    Ok( IpmiServiceCompletionCode.OK ),
    /**
     * Insufficient resources to create a session.
     */
    InsufficientResources( IpmiServiceCompletionCode.INSUFFICIENTRESOURCES ),
    /**
     * Unauthorized role or privilege level requested.
     */
    UnauthorizedRole( IpmiServiceCompletionCode.UNAUTHORIZEDROLE ),
    /**
     * Insufficient resources to create a session at the requested role.
     */
    InsufficientResourcesForRole( IpmiServiceCompletionCode.INSUFFICIENTRESOURCESFORROLE ), /**
                                                                                             * Invalid name length.
                                                                                             */
    InvalidNameLength( IpmiServiceCompletionCode.INVALIDNAMELENGTH ),
    /**
     * FRU device busy. The requested cannot be completed because the implementation of the logical FRU device is in a
     * state where the FRU information is temporarily unavailable. This could be due to a condition such as a los s of
     * arbitration if the FRU is implemented as a device on a shared bus.
     */
    Frudevicebusy( IpmiServiceCompletionCode.FRUDEVICEBUSY ), /**
                                                               * Unauthorized name.
                                                               */
    UnauthorizedName( IpmiServiceCompletionCode.UNAUTHORIZEDNAME ), /**
                                                                     * Invalid Session ID in request.
                                                                     */
    InvalidSessionId( IpmiServiceCompletionCode.INVALIDSESSIONID ), /**
                                                                     * Invalid Session Handle in request.
                                                                     */
    InvalidSessionHandle( IpmiServiceCompletionCode.INVALIDSESSIONHANDLE ),
    /**
     * GUID that BMC submitted in RAKP Message 2 was not accepted by remote console.
     */
    UnauthorizedGuid( IpmiServiceCompletionCode.UNAUTHORIZEDGUID ), /**
                                                                     * Invalid integrity check value.
                                                                     */
    InvalidIntegrityCheckValue( IpmiServiceCompletionCode.INVALIDINTEGRITYCHECKVALUE ),
    /**
     * Invalid confidentiality algorithm.
     */
    InvalidConfidentialityAlgorithm( IpmiServiceCompletionCode.INVALIDCONFIDENTIALITYALGORITHM ),
    /**
     * No Cipher Suite match with proposed security algorithms.
     */
    NoMatchingCipherSuite( IpmiServiceCompletionCode.NOMATCHINGCIPHERSUITE ), /**
                                                                               * Illegal or unrecognized parameter.
                                                                               */
    IllegalOrUnrecognizedParameter( IpmiServiceCompletionCode.ILLEGALORUNRECOGNIZEDPARAMETER ),
    /**
     * Command could not be processed because command processing resources are temporarily unavailable.
     */
    NodeBusy( IpmiServiceCompletionCode.NODEBUSY ), /**
                                                     * Used to indicate an unrecognized or unsupported command.
                                                     */
    InvalidCommand( IpmiServiceCompletionCode.INVALIDCOMMAND ), /**
                                                                 * Command invalid for given LUN.
                                                                 */
    InvalidLun( IpmiServiceCompletionCode.INVALIDLUN ), /**
                                                         * Timeout while processing command. Response unavailable.
                                                         */
    Timeout( IpmiServiceCompletionCode.TIMEOUT ), /**
                                                   * Out of space. Command could not be completed because of a lack of
                                                   * storage space required to execute the given command operation.
                                                   */
    OutOfSpace( IpmiServiceCompletionCode.OUTOFSPACE ), /**
                                                         * Reservation Canceled or Invalid Reservation ID.
                                                         */
    ReservationCanceled( IpmiServiceCompletionCode.RESERVATIONCANCELED ), /**
                                                                           * Request data truncated.
                                                                           */
    RequestTruncated( IpmiServiceCompletionCode.REQUESTTRUNCATED ), /**
                                                                     * Request data length invalid.
                                                                     */
    InvalidRequestLength( IpmiServiceCompletionCode.INVALIDREQUESTLENGTH ), /**
                                                                             * Invalid session ID.
                                                                             */
    InvalidId( IpmiServiceCompletionCode.INVALIDID ), /**
                                                       * Request data field length limit exceeded.
                                                       */
    LengthLimitExceeded( IpmiServiceCompletionCode.LENGTHLIMITEXCEEDED ),
    /**
     * Parameter out of range. One or more parameters in the data field of the Request are out of range. This is
     * different from {@link #InvalidData} (CCh) code in that it indicates that the erroneous field(s) has a contiguous
     * range of possible values.
     */
    ParameterOutOfRange( IpmiServiceCompletionCode.PARAMETEROUTOFRANGE ),
    /**
     * Cannot return number of requested data bytes.
     */
    CannotRespond( IpmiServiceCompletionCode.CANNOTRESPOND ), /**
                                                               * Requested Sensor, data, or record not present.
                                                               */
    DataNotPresent( IpmiServiceCompletionCode.DATANOTPRESENT ), /**
                                                                 * Invalid data field in Request.
                                                                 */
    InvalidData( IpmiServiceCompletionCode.INVALIDDATA ), /**
                                                           * Command illegal for specified sensor or record type.
                                                           */
    IllegalCommand( IpmiServiceCompletionCode.ILLEGALCOMMAND ), /**
                                                                 * Command response could not be provided.
                                                                 */
    ResponseUnavailable( IpmiServiceCompletionCode.RESPONSEUNAVAILABLE ),
    /**
     * Cannot execute duplicated request. This completion code is for devices which cannot return the response that was
     * returned for the original instance of the request. Such devices should provide separate commands that allow the
     * completion status of the original request to be determined. An Event Receiver does not use this completion code,
     * but returns the 00h completion code in the response to (valid) duplicated requests.
     */
    DuplicatedRequest( IpmiServiceCompletionCode.DUPLICATEDREQUEST ),
    /**
     * Command response could not be provided. SDR Repository in update mode.
     */
    SdrUpdating( IpmiServiceCompletionCode.SDRUPDATING ),
    /**
     * Command response could not be provided. Device in firmware update mode.
     */
    FirmwareUpdating( IpmiServiceCompletionCode.FIRMWAREUPDATING ),
    /**
     * Command response could not be provided. BMC initialization or initialization agent in progress.
     */
    InitializationInProgress( IpmiServiceCompletionCode.INITIALIZATIONINPROGRESS ),
    /**
     * Destination unavailable. Cannot deliver request to selected destination. E.g. this code can be returned if a
     * request message is targeted to SMS, but receive message queue reception is disabled for the particular channel.
     */
    DestinationUnavailable( IpmiServiceCompletionCode.DESTINATIONUNAVAILABLE ),
    /**
     * Cannot execute command due to insufficient privilege level or other security - based restriction (e.g. disabled
     * for 'firmware firewall').
     */
    InsufficentPrivilege( IpmiServiceCompletionCode.INSUFFICENTPRIVILEGE ),
    /**
     * Cannot execute command. Command, or request parameter(s), not supported in present state.
     */
    CommandNotSupported( IpmiServiceCompletionCode.COMMANDNOTSUPPORTED ),
    /**
     * Cannot execute command. Parameter is illegal because command sub-function has been disabled or is unavailable
     * (e.g. disabled for 'firmware firewall').
     */
    IllegalParameter( IpmiServiceCompletionCode.ILLEGALPARAMETER ), /**
                                                                     * Unspecified error.
                                                                     */
    UnspecifiedError( IpmiServiceCompletionCode.UNSPECIFIEDERROR ), /**
                                                                     * Invalid payload type.
                                                                     */
    InvalidPayloadType( IpmiServiceCompletionCode.INVALIDPAYLOADTYPE ), /**
                                                                         * Invalid authentication algorithm.
                                                                         */
    InvalidAuthenticationAlgorithm( IpmiServiceCompletionCode.INVALIDAUTHENTICATIONALGORITHM ),
    /**
     * Invalid integrity algorithm.
     */
    InvalidIntegrityAlgorithm( IpmiServiceCompletionCode.INVALIDINTEGRITYALGORITHM ),
    /**
     * No matching authentication payload.
     */
    NoMatchingAuthenticationPayload( IpmiServiceCompletionCode.NOMATCHINGAUTHENTICATIONPAYLOAD ),
    /**
     * No matching integrity payload.
     */
    NoMatchingIntegrityPayload( IpmiServiceCompletionCode.NOMATCHINGINTEGRITYPAYLOAD ), /**
                                                                                         * Inactive session ID.
                                                                                         */
    InactiveSessionID( IpmiServiceCompletionCode.INACTIVESESSIONID ), /**
                                                                       * Invalid role.
                                                                       */
    InvalidRole( IpmiServiceCompletionCode.INVALIDROLE ),;
    // Completion code/status codes for specific RMCP+ and RAKP messages (00h - 12h))
    private static final int OK = 0;

    private static final int INSUFFICIENTRESOURCES = 1;

    private static final int INVALIDID = 2;

    private static final int INVALIDPAYLOADTYPE = 3;

    private static final int INVALIDAUTHENTICATIONALGORITHM = 4;

    private static final int INVALIDINTEGRITYALGORITHM = 5;

    private static final int NOMATCHINGAUTHENTICATIONPAYLOAD = 6;

    private static final int NOMATCHINGINTEGRITYPAYLOAD = 7;

    private static final int INACTIVESESSIONID = 8;

    private static final int INVALIDROLE = 9;

    private static final int UNAUTHORIZEDROLE = 10;

    private static final int INSUFFICIENTRESOURCESFORROLE = 11;

    private static final int INVALIDNAMELENGTH = 12;

    private static final int UNAUTHORIZEDNAME = 13;

    private static final int UNAUTHORIZEDGUID = 14;

    private static final int INVALIDINTEGRITYCHECKVALUE = 15;

    private static final int INVALIDCONFIDENTIALITYALGORITHM = 16;

    private static final int NOMATCHINGCIPHERSUITE = 17;

    private static final int ILLEGALORUNRECOGNIZEDPARAMETER = 18;

    // Generic completion codes (C0h - D6h))
    private static final int NODEBUSY = 192;

    private static final int INVALIDCOMMAND = 193;

    private static final int INVALIDLUN = 194;

    private static final int TIMEOUT = 195;

    private static final int OUTOFSPACE = 196;

    private static final int RESERVATIONCANCELED = 197;

    private static final int REQUESTTRUNCATED = 198;

    private static final int INVALIDREQUESTLENGTH = 199;

    private static final int LENGTHLIMITEXCEEDED = 200;

    private static final int PARAMETEROUTOFRANGE = 201;

    private static final int CANNOTRESPOND = 202;

    private static final int DATANOTPRESENT = 203;

    private static final int INVALIDDATA = 204;

    private static final int ILLEGALCOMMAND = 205;

    private static final int RESPONSEUNAVAILABLE = 206;

    private static final int DUPLICATEDREQUEST = 207;

    private static final int SDRUPDATING = 208;

    private static final int FIRMWAREUPDATING = 209;

    private static final int INITIALIZATIONINPROGRESS = 210;

    private static final int DESTINATIONUNAVAILABLE = 211;

    private static final int INSUFFICENTPRIVILEGE = 212;

    private static final int COMMANDNOTSUPPORTED = 213;

    private static final int ILLEGALPARAMETER = 214;

    // Read FRU data command Response data completion code (81h)
    private static final int FRUDEVICEBUSY = 129;

    // Close Session command Response data completion code (87h, 88h)
    private static final int INVALIDSESSIONID = 135;

    private static final int INVALIDSESSIONHANDLE = 136;

    private static final int UNSPECIFIEDERROR = 255;

    public int code;

    IpmiServiceCompletionCode( int code )
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static IpmiServiceCompletionCode parseInt( int value )
    {
        switch ( value )
        {
            case OK:
                return Ok;
            case INSUFFICIENTRESOURCES:
                return InsufficientResources;
            case UNAUTHORIZEDROLE:
                return UnauthorizedRole;
            case INSUFFICIENTRESOURCESFORROLE:
                return InsufficientResourcesForRole;
            case INVALIDNAMELENGTH:
                return InvalidNameLength;
            case FRUDEVICEBUSY:
                return Frudevicebusy;
            case UNAUTHORIZEDNAME:
                return UnauthorizedName;
            case INVALIDSESSIONID:
                return InvalidSessionId;
            case INVALIDSESSIONHANDLE:
                return InvalidSessionHandle;
            case UNAUTHORIZEDGUID:
                return UnauthorizedGuid;
            case INVALIDINTEGRITYCHECKVALUE:
                return InvalidIntegrityCheckValue;
            case INVALIDCONFIDENTIALITYALGORITHM:
                return InvalidConfidentialityAlgorithm;
            case NOMATCHINGCIPHERSUITE:
                return NoMatchingCipherSuite;
            case ILLEGALORUNRECOGNIZEDPARAMETER:
                return IllegalOrUnrecognizedParameter;
            case NODEBUSY:
                return NodeBusy;
            case INVALIDCOMMAND:
                return InvalidCommand;
            case INVALIDLUN:
                return InvalidLun;
            case TIMEOUT:
                return Timeout;
            case OUTOFSPACE:
                return OutOfSpace;
            case RESERVATIONCANCELED:
                return ReservationCanceled;
            case REQUESTTRUNCATED:
                return RequestTruncated;
            case INVALIDREQUESTLENGTH:
                return InvalidRequestLength;
            case INVALIDID:
                return InvalidId;
            case LENGTHLIMITEXCEEDED:
                return LengthLimitExceeded;
            case PARAMETEROUTOFRANGE:
                return ParameterOutOfRange;
            case CANNOTRESPOND:
                return CannotRespond;
            case DATANOTPRESENT:
                return DataNotPresent;
            case INVALIDDATA:
                return InvalidData;
            case ILLEGALCOMMAND:
                return IllegalCommand;
            case RESPONSEUNAVAILABLE:
                return ResponseUnavailable;
            case DUPLICATEDREQUEST:
                return DuplicatedRequest;
            case SDRUPDATING:
                return SdrUpdating;
            case FIRMWAREUPDATING:
                return FirmwareUpdating;
            case INITIALIZATIONINPROGRESS:
                return InitializationInProgress;
            case DESTINATIONUNAVAILABLE:
                return DestinationUnavailable;
            case INSUFFICENTPRIVILEGE:
                return InsufficentPrivilege;
            case COMMANDNOTSUPPORTED:
                return CommandNotSupported;
            case ILLEGALPARAMETER:
                return IllegalParameter;
            case UNSPECIFIEDERROR:
                return UnspecifiedError;
            case INVALIDPAYLOADTYPE:
                return InvalidPayloadType;
            case INVALIDAUTHENTICATIONALGORITHM:
                return InvalidAuthenticationAlgorithm;
            case INVALIDINTEGRITYALGORITHM:
                return InvalidIntegrityAlgorithm;
            case NOMATCHINGAUTHENTICATIONPAYLOAD:
                return NoMatchingAuthenticationPayload;
            case NOMATCHINGINTEGRITYPAYLOAD:
                return NoMatchingIntegrityPayload;
            case INACTIVESESSIONID:
                return InactiveSessionID;
            case INVALIDROLE:
                return InvalidRole;
            default:
                throw new IllegalArgumentException( "Invalid value: " + value );
        }
    }

    public String getMessage()
    {
        switch ( code )
        {
            case OK:
                return "Command completed normally.";
            case INSUFFICIENTRESOURCES:
                return "Insufficient resources to create a session.";
            case UNAUTHORIZEDROLE:
                return "Unauthorized role or privilege level requested.";
            case INSUFFICIENTRESOURCESFORROLE:
                return "Insufficient resources to create a session at the requested role.";
            case INVALIDNAMELENGTH:
                return "Invalid name length.";
            case FRUDEVICEBUSY:
                return "FRU device busy. The requested cannot be completed because the implementation of the logical FRU device is in a state where the FRU information is temporarily unavailable. This could be due to a condition such as a los s of arbitration if the FRU is implemented as a device on a shared bus.";
            case UNAUTHORIZEDNAME:
                return "Unauthorized name.";
            case INVALIDSESSIONID:
                return "Invalid Session ID in request.";
            case INVALIDSESSIONHANDLE:
                return "Invalid Session Handle in request.";
            case UNAUTHORIZEDGUID:
                return "GUID that BMC submitted in RAKP Message 2 was not accepted by remote console.";
            case INVALIDINTEGRITYCHECKVALUE:
                return "Invalid integrity check value.";
            case INVALIDCONFIDENTIALITYALGORITHM:
                return "Invalid confidentiality algorithm.";
            case NOMATCHINGCIPHERSUITE:
                return "No Cipher Suite match with proposed security algorithms.";
            case ILLEGALORUNRECOGNIZEDPARAMETER:
                return "Illegal or unrecognized parameter.";
            case NODEBUSY:
                return "Node Busy. Command could not be processed because command processing resources are temporarily unavailable.";
            case INVALIDCOMMAND:
                return "Invalid Command. Used to indicate an unrecognized or unsupported command.";
            case INVALIDLUN:
                return "Command invalid for given LUN.";
            case TIMEOUT:
                return "Timeout while processing command. Response unavailable.";
            case OUTOFSPACE:
                return "Out of space. Command could not be completed because of a lack of storage space required to execute the given command operation.";
            case RESERVATIONCANCELED:
                return "Reservation Canceled or Invalid Reservation ID.";
            case REQUESTTRUNCATED:
                return "Request data truncated.";
            case INVALIDREQUESTLENGTH:
                return "Request data length invalid";
            case INVALIDID:
                return "Invalid session ID.";
            case LENGTHLIMITEXCEEDED:
                return "Request data field length limit exceeded.";
            case PARAMETEROUTOFRANGE:
                return "Parameter out of range. One or more parameters in the data field of the Request are out of range. This is different from {@link #InvalidData} (CCh) code in that it indicates that the erroneous field(s) has a contiguous range of possible values.";
            case CANNOTRESPOND:
                return "Cannot return number of requested data bytes.";
            case DATANOTPRESENT:
                return "Requested Sensor, data, or record not present.";
            case INVALIDDATA:
                return "Invalid data field in Request.";
            case ILLEGALCOMMAND:
                return "Command illegal for specified sensor or record type.";
            case RESPONSEUNAVAILABLE:
                return "Command response could not be provided.";
            case DUPLICATEDREQUEST:
                return "Cannot execute duplicated request. This completion code is for devices which cannot return the response that was returned for the original instance of the request. Such devices should provide separate commands that allow the completion status of the original request to be determined. An Event Receiver does not use this completion code, but returns the 00h completion code in the response to (valid) duplicated requests.";
            case SDRUPDATING:
                return "Command response could not be provided. SDR Repository in update mode.";
            case FIRMWAREUPDATING:
                return "Command response could not be provided. Device in firmware update mode.";
            case INITIALIZATIONINPROGRESS:
                return "Command response could not be provided. BMC initialization or initialization agent in progress.";
            case DESTINATIONUNAVAILABLE:
                return "Destination unavailable. Cannot deliver request to selected destination. E.g. this code can be returned if a request message is targeted to SMS, but receive message queue reception is disabled for the particular channel.";
            case INSUFFICENTPRIVILEGE:
                return "Cannot execute command due to insufficient privilege level or other security - based restriction (e.g. disabled for 'firmware firewall').";
            case COMMANDNOTSUPPORTED:
                return "Cannot execute command. Command, or request parameter(s), not supported in present state.";
            case ILLEGALPARAMETER:
                return "Cannot execute command. Parameter is illegal because command sub-function has been disabled or is unavailable (e.g. disabled for 'firmware firewall').";
            case UNSPECIFIEDERROR:
                return "Unspecified error.";
            case INVALIDPAYLOADTYPE:
                return "Invalid payload type.";
            case INVALIDAUTHENTICATIONALGORITHM:
                return "Invalid authentication algorithm.";
            case INVALIDINTEGRITYALGORITHM:
                return "Invalid integrity algorithm.";
            case NOMATCHINGAUTHENTICATIONPAYLOAD:
                return "No matching authentication payload.";
            case NOMATCHINGINTEGRITYPAYLOAD:
                return "No matching integrity payload.";
            case INACTIVESESSIONID:
                return "Inactive session ID.";
            case INVALIDROLE:
                return "Invalid role.";
            default:
                throw new IllegalArgumentException( "Invalid value: " + code );
        }
    }
}
