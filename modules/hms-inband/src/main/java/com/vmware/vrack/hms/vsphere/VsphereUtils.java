/* ********************************************************************************
 * VsphereUtils.java
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

package com.vmware.vrack.hms.vsphere;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim.binding.vim.Task;
import com.vmware.vim.binding.vim.TaskInfo;
import com.vmware.vim.binding.vim.VirtualMachine;
import com.vmware.vim.binding.vim.vm.device.VirtualDevice;
import com.vmware.vim.binding.vim.vm.device.VirtualEthernetCard;
import com.vmware.vim.binding.vmodl.ManagedObject;
import com.vmware.vim.binding.vmodl.ManagedObjectReference;
import com.vmware.vim.vmomi.core.impl.BlockingFuture;

/**
 * Author: Tao Ma Date: 2/26/14
 */
public class VsphereUtils
{
    private static final Logger logger = LoggerFactory.getLogger( VsphereUtils.class );

    private static final int MAX_TASK_RETRY = 300;

    private static final String CERT_BEGIN = "-----BEGIN CERTIFICATE-----\n";

    private static final String CERT_END = "\n-----END CERTIFICATE-----";

    private static final int CERT_BEGIN_LEN = CERT_BEGIN.length();

    private static final int CERT_END_LEN = CERT_END.length();

    private static final char[] hexChars = "0123456789ABCDEF".toCharArray();

    private static final String TASK_RUNNING = "running";

    private VsphereUtils()
    {
    }

    /**
     * compute ESXi host thumbprint.
     *
     * @param cert EXSi host certificate, it is pem format. pem is short name of Privacy-enhanced Electronic Mail,
     *            Base64 encoded DER certificate, enclosed between "-----BEGIN CERTIFICATE-----" and
     *            "-----END CERTIFICATE-----"
     * @return
     */
    public static String computeThumbprint( byte[] cert )
    {
        if ( null == cert )
        {
            throw new IllegalArgumentException( "ESXi host certificate is null." );
        }
        if ( cert.length == 0 )
        {
            throw new IllegalArgumentException( "ESXi host certificate is empty." );
            // logger.debug("ESXi Host Certificate \n{}", new String(cert));
        }

        byte[] certData = new byte[cert.length - CERT_BEGIN_LEN - CERT_END_LEN];
        System.arraycopy( cert, CERT_BEGIN_LEN, certData, 0, certData.length );

        try
        {
            // pem -> der
            certData = javax.xml.bind.DatatypeConverter.parseBase64Binary( new String( certData ) );
            // logger.debug("ESXi Host Certificate Decoded by BASE64 \n{}", certData);

            // sha-1 encoding
            MessageDigest msgDigest = MessageDigest.getInstance( "SHA-1" );
            certData = msgDigest.digest( certData );
            // logger.debug("ESXi Host Certificate Digested by SHA-1 \n{}", certData);

            char[] thumbprintData = new char[certData.length * 3 - 1];
            int b = certData[0] & 0xFF;
            thumbprintData[0] = hexChars[b >>> 4];
            thumbprintData[1] = hexChars[b & 0x0F];
            for ( int i = 1; i < certData.length; i++ )
            {
                thumbprintData[i * 3 - 1] = ':';
                b = certData[i] & 0xFF;
                thumbprintData[i * 3] = hexChars[b >>> 4];
                thumbprintData[i * 3 + 1] = hexChars[b & 0x0F];
            }

            String thumbprint = new String( thumbprintData );
            logger.info( "ESXi Host thumbprint: {}", thumbprint );
            return thumbprint;
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new UnsupportedOperationException( "NO SHA-A Algorithm.", e );
        }
    }

    /**
     * Get local server ipv4 addresses.
     *
     * @return
     */
    public static String[] getServerIps()
    {
        List<String> ipList = new ArrayList<>();
        try
        {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            if ( null != nics )
            {
                while ( nics.hasMoreElements() )
                {
                    NetworkInterface nic = nics.nextElement();
                    Enumeration<InetAddress> ipAddresses = nic.getInetAddresses();
                    while ( ipAddresses.hasMoreElements() )
                    {
                        InetAddress ipAddress = ipAddresses.nextElement();
                        if ( !ipAddress.isLoopbackAddress() )
                        {
                            if ( ipAddress instanceof Inet4Address )
                            {
                                ipList.add( ipAddress.getHostAddress() );
                            }
                        }
                    }
                }
            }
        }
        catch ( SocketException e )
        {
            // TODO: ignore
        }
        return ipList.toArray( new String[0] );
    }

    public static boolean retrieveHostCertificate( byte[] certificate )
    {
        try
        {
            CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
            X509Certificate x509CertificateToTrust =
                (X509Certificate) cf.generateCertificate( new ByteArrayInputStream( certificate ) );
            return true;
        }
        catch ( CertificateException ex )
        {
            logger.error( "Failed to retrieve X509 certificate", ex );
            return false;
        }
    }

    public static TaskInfo waitForTask( VsphereClient client, ManagedObjectReference taskRef )
        throws VsphereOperationException
    {
        Task task = client.createStub( Task.class, taskRef );
        String taskRefValue = taskRef.getValue();
        logger.info( "Task (MOR:{}) is started", taskRefValue );
        int retry = 0;
        while ( retry++ < MAX_TASK_RETRY )
        {
            waitInSeconds( 2 );
            TaskInfo info = getTaskInfo( task );
            if ( info == null )
            {
                logger.warn( "Task (MOR:{}) info is null -- retrying: {}", taskRefValue, retry );
                continue;
            }
            String taskType = info.getName().getTypeName().getName();
            String taskName = info.getName().getName();
            TaskInfo.State taskState = info.getState();
            if ( taskState == TaskInfo.State.success )
            {
                logger.info( "Task (MOR:{}) {}:{} is complete", taskRefValue, taskType, taskName );
                return info;
            }
            if ( taskState == TaskInfo.State.error )
            {
                String msg = String.format( "Task (MOR:%s) %s:%s is failed", taskRefValue, taskType, taskName );
                logger.error( msg, info.getError() );
                logger.error( "Task information for future track\n{}", info );
                throw new VsphereOperationException( msg, info.getError() );
            }
            waitInSeconds( 3 );
            logger.info( "Task (MOR:{}) {}:{} status: {}. Waiting for its complete: {}", taskRefValue, taskType,
                         taskName, taskState, retry );
        }
        logger.error( "Task (MOR:{}) is timeout", taskRefValue );
        throw new VsphereOperationException( String.format( "Task (MOR:%s) is timeout", taskRefValue ) );
    }

    private static TaskInfo getTaskInfo( final Task task )
    {
        BlockingFuture<TaskInfo> p = new BlockingFuture();
        task.getInfo( p );
        try
        {
            return p.get( 10, TimeUnit.MINUTES );
        }
        catch ( ExecutionException | InterruptedException | TimeoutException ex )
        {
            logger.error( "Unable to retrieve task (MOR:{}) info: {}", task._getRef().getValue(), ex.getMessage(), ex );
            return null;
        }
    }

    public static <T extends ManagedObject> T getManagedObject( VsphereClient client, Class<T> clazz, String name )
    {
        String type = clazz.getSimpleName();
        ManagedObjectReference mor =
            InventoryService.getInstance().getOneByName( client.getPropertyCollector(), client.getContainerView( type ),
                                                         type, name );
        return ( mor == null ) ? null : client.createStub( clazz, mor );
    }

    public static <T extends ManagedObject> T getUniqueManagedObject( VsphereClient client, Class<T> clazz,
                                                                      String name )
    {
        T object = getManagedObject( client, clazz, name );
        if ( object == null )
        {
            throw new IllegalArgumentException( String.format( "%s:%s is not found", clazz.getSimpleName(), name ) );
        }
        return object;
    }

    public static void waitInSeconds( int second )
    {
        try
        {
            Thread.sleep( second * 1000 );
        }
        catch ( InterruptedException ignored )
        {
        }
    }

    public static void closeQuietly( Closeable c )
    {
        if ( c == null )
        {
            return;
        }
        try
        {
            c.close();
        }
        catch ( IOException ignored )
        {
        }
    }

    public static String[] removeTarget( String[] source, String[] target )
    {
        if ( source == null || source.length == 0 )
        {
            return source;
        }

        List<String> remaining = new ArrayList<>( source.length );
        for ( String s : source )
        {
            remaining.add( s );
        }
        for ( String t : target )
        {
            remaining.remove( t );
        }
        return remaining.toArray( new String[remaining.size()] );
    }

    public static boolean isEmptyArray( Object[] obj )
    {
        return ( obj == null ) || ( obj.length == 0 );
    }

    public static <T> List<T> addToList( List<T> list, T[] array )
    {
        if ( array != null )
        {
            for ( T a : array )
            {
                if ( !list.add( a ) )
                {
                    throw new IllegalArgumentException( "Failed to add array into list" );
                }
            }
        }
        return list;
    }

    public static <T> List<T> asList( List<T> list, T[] array )
    {
        if ( array != null )
        {
            for ( T a : array )
            {
                if ( !list.add( a ) )
                {
                    throw new IllegalArgumentException( "Failed to add array into list" );
                }
            }
        }
        return list;
    }

    public static boolean isTargetType( Object obj, Class targetClass )
    {
        return ( obj instanceof ManagedObjectReference )
            && targetClass.getSimpleName().equals( ( ( (ManagedObjectReference) obj ).getType() ) );
    }

    /**
     * Populate a map from a text which format is like '10.28.197.10-vmnic4,10.28.197.15-vmnic1'
     * 
     * @param prop The text which format is like '10.28.197.10-vmnic4,10.28.197.15-vmnic1'
     * @return a map which key is the IP address and value is vmnic name
     */
    public static Map<String, String> populatePair( String prop )
    {
        if ( StringUtils.isBlank( prop ) )
        {
            logger.info( "No property specified to populate" );
            return Collections.emptyMap();
        }
        logger.info( "Property needs to be populated: {}", prop );

        String[] items = prop.split( "," );
        Map<String, String> map = new HashMap<>( items.length );
        for ( String item : items )
        {
            String[] pair = item.split( "-" );
            if ( pair.length != 2 )
            {
                logger.warn( "Skipping unrecognized pair: {}", item );
                continue;
            }
            if ( StringUtils.isNotBlank( pair[0] ) && StringUtils.isNotBlank( pair[1] ) )
            {
                String p0 = pair[0].trim();
                String p1 = pair[1].trim();
                logger.info( "Populating pair {}:{}", p0, p1 );
                map.put( p0, p1 );
            }
        }
        return map;
    }

    public static List<VirtualEthernetCard> sortVirtualEthernetCard( final VirtualMachine vm )
    {
        VirtualDevice[] vmDevices = vm.getConfig().getHardware().getDevice();
        if ( vmDevices == null || vmDevices.length == 0 )
        {
            logger.warn( "No any device found in VM {}", vm.getName() );
            return Collections.emptyList();
        }
        List<VirtualEthernetCard> ethList = new ArrayList<>( 4 );
        for ( VirtualDevice vmDevice : vmDevices )
        {
            if ( vmDevice instanceof VirtualEthernetCard )
            {
                logger.info( "VirtualEthernetCard {} is found in VM {}", vmDevice.getDeviceInfo().getLabel(),
                             vm.getName() );
                ethList.add( (VirtualEthernetCard) vmDevice );
            }
        }
        logger.info( "Sorting VirtualEthernetCard by their names..." );
        Collections.sort( ethList, new Comparator<VirtualEthernetCard>()
        {
            @Override
            public int compare( final VirtualEthernetCard c1, final VirtualEthernetCard c2 )
            {
                return c1.getDeviceInfo().getLabel().compareTo( c2.getDeviceInfo().getLabel() );
            }
        } );
        return ethList;
    }

    public static void sortVirtualMachine( final List<VirtualMachine> vmList, final String no1VmName )
    {
        logger.info( "Sorting VirtualMachine by their names. Keep {} as the first one", no1VmName );
        Collections.sort( vmList, new Comparator<VirtualMachine>()
        {
            @Override
            public int compare( final VirtualMachine vm1, final VirtualMachine vm2 )
            {
                String vm1Name = vm1.getName();
                String vm2Name = vm2.getName();
                if ( vm1Name.equals( vm2Name ) )
                {
                    return 0;
                }
                if ( vm1Name.equals( no1VmName ) )
                {
                    return -1;
                }
                if ( vm2Name.equals( no1VmName ) )
                {
                    return 1;
                }
                return vm1.getName().compareTo( vm2.getName() );
            }
        } );
    }
}