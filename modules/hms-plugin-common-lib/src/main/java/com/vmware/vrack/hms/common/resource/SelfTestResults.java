/* ********************************************************************************
 * SelfTestResults.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.resource;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vmware.vrack.hms.common.notification.BaseResponse;

@JsonInclude( JsonInclude.Include.NON_NULL )
public class SelfTestResults
    extends BaseResponse
{
    private byte selfTestResultCode;

    private String selfTestResult = null;

    private byte selfTestResultFailureCode;

    private List<String> errors;

    public byte getSelfTestResultCode()
    {
        return selfTestResultCode;
    }

    public void setSelfTestResultCode( byte selfTestResultCode )
    {
        this.selfTestResultCode = selfTestResultCode;
    }

    public String getSelfTestResult()
    {
        return selfTestResult;
    }

    public void setSelfTestResult( String selfTestResult )
    {
        this.selfTestResult = selfTestResult;
    }

    public byte getSelfTestResultFailureCode()
    {
        return selfTestResultFailureCode;
    }

    public void setSelfTestResultFailureCode( byte selfTestResultFailureCode )
    {
        this.selfTestResultFailureCode = selfTestResultFailureCode;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public void setErrors( List<String> errors )
    {
        this.errors = errors;
    }
}
