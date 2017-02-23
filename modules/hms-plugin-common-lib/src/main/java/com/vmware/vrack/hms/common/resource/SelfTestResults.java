/* ********************************************************************************
 * SelfTestResults.java
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
