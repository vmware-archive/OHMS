package com.vmware.vrack.coding.commands.application;

import java.util.ArrayList;
import java.util.List;

import com.veraxsystems.vxipmi.coding.commands.ResponseData;

/**
 * Wrapper Class for Get Self Test Results Response
 * 
 * @author Yagnesh Chawda
 */
public class GetSelfTestResultsResponseData
    implements ResponseData
{
    private byte selfTestResultCode;

    private byte selfTestFailureCode;

    private List<String> errors = new ArrayList<String>();

    public byte getSelfTestResultCode()
    {
        return selfTestResultCode;
    }

    // Sets the first byte of the Self Test Results i.e Success Status
    public void setSelfTestResultCode( byte selfTestResultCode )
    {
        this.selfTestResultCode = selfTestResultCode;
    }

    public byte getSelfTestFailureCode()
    {
        return selfTestFailureCode;
    }

    // Sets the Failure Code Defining the possible problems that resulted in failure
    public void setSelfTestFailureCode( byte selfTestFailureCode )
    {
        this.selfTestFailureCode = selfTestFailureCode;
    }

    // Gets the list of errors if All tests are not passed successfully. can be max of 8 errors
    public List<String> getErrors()
    {
        return errors;
    }

    public void setErrors( List<String> errors )
    {
        this.errors = errors;
    }

    // Returns true if All tests are passed successfully
    public boolean isAllTestPassed()
    {
        return ( selfTestResultCode == (byte) 0x55 );
    }

    // Returns true is BMC Self Test in not implemented
    public boolean isSelfTestControllerNotImplemented()
    {
        return ( selfTestResultCode == (byte) 0x56 );
    }

    // In case Some areas of BMC are not accessible, this returns true
    public boolean isCorruptedOrInaccessible()
    {
        return ( selfTestResultCode == (byte) 0x57 );
    }

    // Returns true in case of some fatal error
    public boolean isFatalError()
    {
        return ( selfTestResultCode == (byte) 0x58 );
    }

    // if the error is unknown, following returns true
    public boolean isUnknownError()
    {
        boolean unknownError = ( !isAllTestPassed() && !isSelfTestControllerNotImplemented()
            && !isCorruptedOrInaccessible() && !isFatalError() );
        return ( unknownError );
    }

    @Override
    public String toString()
    {
        return "GetSelfTestResultsResponseData [selfTestResultCode=" + selfTestResultCode + ", selfTestFailureCode="
            + selfTestFailureCode + ", errors=" + errors + "]";
    }
}
