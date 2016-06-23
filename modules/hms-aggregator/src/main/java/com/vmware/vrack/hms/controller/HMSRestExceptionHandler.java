/* ********************************************************************************
 * HMSRestExceptionHandler.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.controller;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class HMSRestExceptionHandler
{
    @ExceptionHandler( HMSRestException.class )
    @ResponseStatus( value = HttpStatus.INTERNAL_SERVER_ERROR )
    @ResponseBody
    public BaseResponse hmsException( HttpServletRequest req, HMSRestException exception )
    {
        return new BaseResponse( exception.getResponseErrorCode(), exception.getMessage(), exception.getReason() );
    }

    @ExceptionHandler( NoHandlerFoundException.class )
    @ResponseStatus( value = HttpStatus.BAD_REQUEST )
    @ResponseBody
    public BaseResponse hmsException( HttpServletRequest req, NoHandlerFoundException exception )
    {
        return new BaseResponse( HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                 exception.getMessage() );
    }

    @ExceptionHandler( ServletException.class )
    @ResponseStatus( value = HttpStatus.INTERNAL_SERVER_ERROR )
    @ResponseBody
    public BaseResponse hmsException( HttpServletRequest req, ServletException exception )
    {
        return new BaseResponse( HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                 HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), exception.getMessage() );
    }

    @ExceptionHandler( IllegalStateException.class )
    @ResponseStatus( value = HttpStatus.INTERNAL_SERVER_ERROR )
    @ResponseBody
    public BaseResponse hmsException( HttpServletRequest req, IllegalStateException exception )
    {
        return new BaseResponse( HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                 HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), exception.getMessage() );
    }

    @ExceptionHandler( BeanInitializationException.class )
    @ResponseStatus( value = HttpStatus.INTERNAL_SERVER_ERROR )
    @ResponseBody
    public BaseResponse hmsException( HttpServletRequest req, BeanInitializationException exception )
    {
        return new BaseResponse( HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                 HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), exception.getMessage() );
    }

    @ExceptionHandler( Exception.class )
    @ResponseStatus( value = HttpStatus.INTERNAL_SERVER_ERROR )
    @ResponseBody
    public BaseResponse hmsException( HttpServletRequest req, Exception exception )
    {
        return new BaseResponse( HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                 HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), exception.getMessage() );
    }
}
