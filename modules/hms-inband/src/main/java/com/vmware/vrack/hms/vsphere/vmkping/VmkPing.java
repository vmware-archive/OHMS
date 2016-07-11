/* ********************************************************************************
 * VmkPing.java
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
package com.vmware.vrack.hms.vsphere.vmkping;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.vmware.vim.binding.vmodl.reflect.DynamicTypeManager;
import com.vmware.vim.binding.vmodl.reflect.DynamicTypeManager.MoFilterSpec;
import com.vmware.vim.binding.vmodl.reflect.DynamicTypeManager.MoInstance;
import com.vmware.vim.binding.vmodl.reflect.ManagedMethodExecuter;
import com.vmware.vim.binding.vmodl.reflect.ManagedMethodExecuter.SoapArgument;
import com.vmware.vim.binding.vmodl.reflect.ManagedMethodExecuter.SoapResult;
import com.vmware.vrack.hms.vsphere.HostProxy;
import com.vmware.vrack.hms.vsphere.XmlUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Tao Ma Date: 3/3/14
 */
public class VmkPing
{
    /*
     * Re-factor hard code string 2014-08-07
     */
    private static final String SEND_TO = "send to";

    private static final String _5_1 = "5.1";

    private static final String THE_VMKPING_REQUIRED_FIELD_IS_NULL = "the vmkping required field is null.";

    private static final String VIM_ESX_CLI_NETWORK_DIAG_PING = "vim.EsxCLI.network.diag.ping";

    private static final String URN_VIM25 = "urn:vim25/";

    private static final String THERE_IS_NO_AVAILABLE_MO_INSTANCE = "There is no available MoInstance.";

    private static final String VIM_ESX_CLI_NETWORK_DIAG = "vim.EsxCLI.network.diag";

    private static final String UNKNOWN = "Unknown";

    private static final Logger logger = LoggerFactory.getLogger( VmkPing.class );

    public VmkPingOutputSpec execute( HostProxy srcHost, String destIp )
    {
        VmkPingInputSpec inputSpec = new VmkPingInputSpec();
        inputSpec.setHost( destIp );
        return execute( srcHost, inputSpec );
    }

    public VmkPingOutputSpec execute( HostProxy srcHost, String device, String destIp )
    {
        VmkPingInputSpec inputSpec = new VmkPingInputSpec();
        inputSpec.setHost( destIp );
        inputSpec.setNic( device );
        inputSpec.setSize( 56 );
        inputSpec.setCount( 3 );
        return execute( srcHost, inputSpec );
    }

    public VmkPingOutputSpec execute( HostProxy srcHost, VmkPingInputSpec inputSpec )
    {
        SoapArgument[] args = encode( inputSpec, srcHost.getVersion() );
        for ( int i = 0; i < args.length; i++ )
        {
            logger.debug( "SoapArgument {}: \n{}", i, args[i] );
        }
        String apiVersion = getSoapApiVersion( srcHost.getVersion() );
        SoapResult result = executeSoap( srcHost, args, apiVersion );
        if ( null == result )
        {
            throw new NullPointerException();
        }
        if ( null != result.getFault() )
        {
            logger.error( result.getFault().getFaultMsg() );
            logger.error( result.getFault().getFaultDetail() );
            logger.error( "inputSpec: {}", inputSpec );
            String errMsg = UNKNOWN;
            try
            {
                Obj vmkpingFault = XmlUtils.fromXml( Obj.class, result.getFault().getFaultDetail() );
                if ( vmkpingFault.getFault().getErrMsgs() != null && !vmkpingFault.getFault().getErrMsgs().isEmpty() )
                {
                    errMsg = vmkpingFault.getFault().getErrMsgs().get( 0 );
                }
            }
            catch ( Exception e )
            {
                logger.warn( e.getMessage(), e );
            }
            // TODO throw exception for message
        }
        return decode( result ).getDataObject();
    }

    private SoapResult executeSoap( HostProxy srcHost, SoapArgument[] args, String apiVersion )
    {
        try
        {
            DynamicTypeManager dynamicTypeManager = srcHost.getDynamicTypeManager();
            MoFilterSpec moFilterSpec = new MoFilterSpec();
            moFilterSpec.setTypeSubstr( VIM_ESX_CLI_NETWORK_DIAG );
            MoInstance[] moInstances = dynamicTypeManager.queryMoInstances( moFilterSpec );
            if ( moInstances == null || moInstances.length < 1 )
            {
                throw new RuntimeException( THERE_IS_NO_AVAILABLE_MO_INSTANCE );
            }
            ManagedMethodExecuter executer = srcHost.getManagedMethodExecuter();
            String version = URN_VIM25 + apiVersion;
            return executer.executeSoap( moInstances[0].getId(), version, VIM_ESX_CLI_NETWORK_DIAG_PING, args );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e.getMessage(), e.getCause() );
        }
    }

    private SoapArgument[] encode( VmkPingInputSpec inputSpec, String hostVersion )
    {
        List<SoapArgument> args = new ArrayList<SoapArgument>();
        Field[] fields = VmkPingInputSpec.class.getDeclaredFields();
        for ( Field field : fields )
        {
            try
            {
                field.setAccessible( true );
                String vmkPingFieldName = field.getName();
                Object value = field.get( inputSpec );
                VmkPingField vmkPingField = field.getAnnotation( VmkPingField.class );
                if ( null != vmkPingField )
                {
                    if ( !vmkPingField.name().equals( "" ) )
                    {
                        vmkPingFieldName = vmkPingField.name();
                    }
                    if ( vmkPingField.required() && null == value )
                    {
                        throw new NullPointerException( THE_VMKPING_REQUIRED_FIELD_IS_NULL );
                    }
                    if ( !vmkPingField.minVersion().isEmpty() && hostVersion != null
                        && !isArgumentSupportedOnHost( hostVersion, vmkPingField.minVersion() ) )
                    {
                        continue;
                    }
                }
                if ( null != value )
                {
                    SoapArgument arg = new SoapArgument();
                    arg.setName( vmkPingFieldName );
                    StringBuilder val = new StringBuilder();
                    val.append( "<" ).append( vmkPingFieldName ).append( ">" );
                    val.append( value );
                    val.append( "</" ).append( vmkPingFieldName ).append( ">" );
                    arg.setVal( val.toString() );
                    args.add( arg );
                }
            }
            catch ( IllegalArgumentException e )
            {
                throw e;
            }
            catch ( IllegalAccessException e )
            {
                throw new RuntimeException( e );
            }
        }
        return args.toArray( new SoapArgument[0] );
    }

    private Obj decode( SoapResult result )
    {
        logger.debug( "VMKPing Output Spec XML: \n{}", result.getResponse() );
        return XmlUtils.fromXml( Obj.class, result.getResponse() );
    }

    /**
     * Returns true if hostVersion is greater than or equal to minimun argument version
     *
     * @param hostVersion
     * @param minArgVersion
     * @return
     */
    private boolean isArgumentSupportedOnHost( String hostVersion, String minArgVersion )
    {
        // TODO: need to implement.
        return true;
    }

    private String getSoapApiVersion( String hostVersion )
    {
        if ( hostVersion == null )
        {
            return _5_1;
        }
        int pos = hostVersion.lastIndexOf( '.' );
        String apiVersion = hostVersion.substring( 0, pos );
        return apiVersion;
    }

    @XStreamAlias( "obj" )
    private static class Obj
    {
        private static final String LOCALIZED_MESSAGE = ", localizedMessage=";

        private static final String FAULT2 = ", fault=";

        private static final String OBJ_DATA_OBJECT = "Obj [dataObject=";

        @XStreamAlias( "DataObject" )
        private VmkPingOutputSpec dataObject;

        private VmkPingFault fault;

        private String localizedMessage;

        /**
         * @return the dataObject
         */
        public VmkPingOutputSpec getDataObject()
        {
            return dataObject;
        }

        /**
         * @param dataObject the dataObject to set
         */
        public void setDataObject( VmkPingOutputSpec dataObject )
        {
            this.dataObject = dataObject;
        }

        /**
         * @return the fault
         */
        public VmkPingFault getFault()
        {
            return fault;
        }

        /**
         * @param fault the fault to set
         */
        public void setFault( VmkPingFault fault )
        {
            this.fault = fault;
        }

        /**
         * @return the localizedMessage
         */
        public String getLocalizedMessage()
        {
            return localizedMessage;
        }

        /**
         * @param localizedMessage the localizedMessage to set
         */
        public void setLocalizedMessage( String localizedMessage )
        {
            this.localizedMessage = localizedMessage;
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append( Obj.OBJ_DATA_OBJECT );
            builder.append( dataObject );
            builder.append( Obj.FAULT2 );
            builder.append( fault );
            builder.append( Obj.LOCALIZED_MESSAGE );
            builder.append( localizedMessage );
            builder.append( "]" );
            return builder.toString();
        }
    }

    private static class VmkPingFault
    {
        @XStreamImplicit( itemFieldName = "errMsg" )
        private List<String> errMsgs;

        /**
         * @return the errMsgs
         */
        public List<String> getErrMsgs()
        {
            return errMsgs;
        }

        /**
         * @param errMsgs the errMsgs to set
         */
        public void setErrMsgs( List<String> errMsgs )
        {
            this.errMsgs = errMsgs;
        }

        @Override
        public String toString()
        {
            return "VmkPingFault [errMsgs=" + errMsgs + "]";
        }
    }

    public static void main( String[] args )
    {
        Obj obj = new Obj();
        VmkPingFault fault = new VmkPingFault();
        obj.setFault( fault );
        List<String> errMsgs = new ArrayList<String>();
        fault.setErrMsgs( errMsgs );
        errMsgs.add( SEND_TO );
        errMsgs.add( SEND_TO );
        errMsgs.add( SEND_TO );
        String body = XmlUtils.toXml( Obj.class, obj );
        logger.info( body );
        Obj o = XmlUtils.fromXml( Obj.class, body );
    }
}
