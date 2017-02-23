/* ********************************************************************************
 * HostProxy.java
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

import static com.vmware.vim.binding.vim.host.NetStackInstance.SystemStackKey.defaultTcpipStack;
import static com.vmware.vrack.hms.vsphere.Constants.TRAN_NEW_PORT_GROUP_NAME;
import static com.vmware.vrack.hms.vsphere.Constants.TRAN_NEW_VMKNIC_DEVICE;
import static com.vmware.vrack.hms.vsphere.Constants.VRACK_NETWORK_MGMT;
import static com.vmware.vrack.hms.vsphere.Constants.VRACK_NETWORK_VMOTION;
import static com.vmware.vrack.hms.vsphere.Constants.VRACK_NETWORK_VSAN;
import static com.vmware.vrack.hms.vsphere.VLSITools.waitTaskEnd;
import static com.vmware.vrack.hms.vsphere.VsphereUtils.addToList;
import static com.vmware.vrack.hms.vsphere.VsphereUtils.isEmptyArray;
import static com.vmware.vrack.hms.vsphere.VsphereUtils.waitInSeconds;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim.binding.vim.ConfigSpecOperation;
import com.vmware.vim.binding.vim.Datastore;
import com.vmware.vim.binding.vim.HostSystem;
import com.vmware.vim.binding.vim.LicenseManager;
import com.vmware.vim.binding.vim.Task;
import com.vmware.vim.binding.vim.TaskInfo;
import com.vmware.vim.binding.vim.VirtualMachine;
import com.vmware.vim.binding.vim.dvs.HostMember;
import com.vmware.vim.binding.vim.dvs.PortConnection;
import com.vmware.vim.binding.vim.fault.AlreadyExists;
import com.vmware.vim.binding.vim.fault.ConcurrentAccess;
import com.vmware.vim.binding.vim.fault.DuplicateName;
import com.vmware.vim.binding.vim.fault.FileFault;
import com.vmware.vim.binding.vim.fault.GuestOperationsFault;
import com.vmware.vim.binding.vim.fault.HostConfigFault;
import com.vmware.vim.binding.vim.fault.InsufficientResourcesFault;
import com.vmware.vim.binding.vim.fault.InvalidDatastore;
import com.vmware.vim.binding.vim.fault.InvalidName;
import com.vmware.vim.binding.vim.fault.InvalidState;
import com.vmware.vim.binding.vim.fault.NotFound;
import com.vmware.vim.binding.vim.fault.ResourceInUse;
import com.vmware.vim.binding.vim.fault.TaskInProgress;
import com.vmware.vim.binding.vim.fault.ToolsUnavailable;
import com.vmware.vim.binding.vim.fault.VimFault;
import com.vmware.vim.binding.vim.fault.VmConfigFault;
import com.vmware.vim.binding.vim.host.AutoStartManager;
import com.vmware.vim.binding.vim.host.AutoStartManager.AutoPowerInfo;
import com.vmware.vim.binding.vim.host.AutoStartManager.AutoPowerInfo.WaitHeartbeatSetting;
import com.vmware.vim.binding.vim.host.AutoStartManager.Config;
import com.vmware.vim.binding.vim.host.AutoStartManager.SystemDefaults;
import com.vmware.vim.binding.vim.host.ConfigChange;
import com.vmware.vim.binding.vim.host.ConfigManager;
import com.vmware.vim.binding.vim.host.HostProxySwitch;
import com.vmware.vim.binding.vim.host.IpConfig;
import com.vmware.vim.binding.vim.host.IpRouteConfig;
import com.vmware.vim.binding.vim.host.IpRouteEntry;
import com.vmware.vim.binding.vim.host.IpRouteOp;
import com.vmware.vim.binding.vim.host.IpRouteTableConfig;
import com.vmware.vim.binding.vim.host.NetStackInstance;
import com.vmware.vim.binding.vim.host.NetworkConfig;
import com.vmware.vim.binding.vim.host.NetworkInfo;
import com.vmware.vim.binding.vim.host.NetworkPolicy;
import com.vmware.vim.binding.vim.host.NetworkSystem;
import com.vmware.vim.binding.vim.host.PhysicalNic;
import com.vmware.vim.binding.vim.host.PortGroup;
import com.vmware.vim.binding.vim.host.VirtualNic;
import com.vmware.vim.binding.vim.host.VirtualNicManager;
import com.vmware.vim.binding.vim.host.VirtualNicManager.NicType;
import com.vmware.vim.binding.vim.host.VirtualSwitch;
import com.vmware.vim.binding.vim.host.VsanSystem;
import com.vmware.vim.binding.vim.option.OptionManager;
import com.vmware.vim.binding.vim.option.OptionValue;
import com.vmware.vim.binding.vim.vm.ConfigSpec;
import com.vmware.vim.binding.vim.vm.GuestInfo;
import com.vmware.vim.binding.vim.vm.ProfileSpec;
import com.vmware.vim.binding.vim.vm.device.VirtualDevice;
import com.vmware.vim.binding.vim.vm.device.VirtualDeviceSpec;
import com.vmware.vim.binding.vim.vm.device.VirtualEthernetCard;
import com.vmware.vim.binding.vim.vm.device.VirtualVmxnet3;
import com.vmware.vim.binding.vim.vm.guest.FileManager;
import com.vmware.vim.binding.vim.vm.guest.GuestAuthentication;
import com.vmware.vim.binding.vim.vm.guest.GuestOperationsManager;
import com.vmware.vim.binding.vim.vm.guest.ProcessManager;
import com.vmware.vim.binding.vim.vm.guest.ProcessManager.ProcessInfo;
import com.vmware.vim.binding.vim.vm.guest.ProcessManager.ProgramSpec;
import com.vmware.vim.binding.vim.vsan.host.ConfigInfo;
import com.vmware.vim.binding.vmodl.DynamicProperty;
import com.vmware.vim.binding.vmodl.ManagedObjectReference;
import com.vmware.vim.binding.vmodl.query.PropertyCollector;
import com.vmware.vim.binding.vmodl.reflect.DynamicTypeManager;
import com.vmware.vim.binding.vmodl.reflect.ManagedMethodExecuter;
import com.vmware.vim.vmomi.client.common.impl.ClientFutureImpl;
import com.vmware.vim.vmomi.core.Future;
import com.vmware.vrack.hms.vsphere.guest.GuestCredential;
import com.vmware.vrack.hms.vsphere.guest.GuestProgram;
import com.vmware.vrack.hms.vsphere.vmkping.VmkPing;
import com.vmware.vrack.hms.vsphere.vmkping.VmkPingOutputSpec;

/**
 * Author: Tao Ma, Jeffrey Wang Date: 3/12/14
 */
public class HostProxy
    implements AutoCloseable, Closeable
{
    private static final Logger logger = LoggerFactory.getLogger( HostProxy.class );

    private static final ProfileSpec[] EMPTY_PS = new ProfileSpec[] { new ProfileSpec() };

    /*
     * Re-factor hard coded String 2014-08-07
     */
    private final static String UNSUPOORTED_OPERATION_EXCEPTION =
        "Only Evaluation, Enterprise and Enterprise Plus editions are supported";

    private final static String ESX_ENTERPRISE = "esx.enterprisePlus";

    private final static String EVAL = "eval";

    private final static String ESX_EVAL = "esxEval";

    private final static String HOST_STR = "Host %s (%s)";

    private final static String UNABLE_TO_ADD_PORTGROUP_ON_SWITCH =
        "Unable to add new PortGroup %s on standard switch %s";

    private final static String VSPHERE_OPERATION_EXCEPTION_UNABLE_TO_ADD_VMKNIC =
        "Unable to add vmknic on PortGroup %s and standard switch %s";

    private final static String VSPHERE_OPERATION_EXCEPTION_UNABLE_TO_UPDATE_IP_ROUTE =
        "Unable to update IP route config on PortGroup %s and standard switch %s";

    private final static String UNABLE_TO_CONFIG_VMKNIC_ON_PORTGROUP =
        "Unable to configure vmknic on PortGroup %s and standard switch %s";

    private final static String MODIFY = "modify";

    private final static String NAME_STR = "name";

    private final static String NO_AVAILABLE_PHYSICAL_NIC_IN_ESXI_HOST = "No available physical NIC in ESXi host %s";

    private final static String SWITCH_NOT_FOUND = "No any switch found";

    private final static String FAILED_TO_CREATE_VMKNIC = "Failed to create vmknic";

    private final static String UNABLE_TO_REMOVE_VIRTUALSWITCH_FROM_HOST =
        "Unable to remove VirtualSwitch %s from host %s";

    private final static String NO_SUCH_STANDARD_VSWITCH_ON_HOST = "No such standard vSwitch %s on host %s";

    private final static String NO_ANY_STANDARD_VSWITCH_FOUND = "No any standard vSwitch found on host %s";

    private final static String HOST_DOES_NOT_CONTAIN_MULTIPLE_PHYSICAL_ADAPTOR =
        "%s on host %s doesn't contain multiple physical adapters";

    private final static String TARGET_VMNIC_NOT_IN_USE = "The target vmnic %s is not in use";

    private final static String NO_VMNIC_ASSIGNED = "No vmnic assigned";

    private final static String UNABLE_TO_ADD_VMNIC_INTO_DVSWITCH = "Unable to add vmnic %s into DvSwitch";

    private final static String FAILED_TO_REBOOT_GUEST = "Failed to reboot guest: ";

    private final static String NO_GUEST_PROGRAM_SPECIFIED = "No any guest program specified";

    private final static String FAILED_TO_UPDATE_VCENTER_IP_ADDRESS = "Failed to update vCenter IP address";

    private final static String FAILED_TO_MIGRATE_VM_TO_DVSWITCH = "Failed to migrate VM %s to DvSwitch %s";

    private final static String UNABLE_TO_MIGGRATE_VIRTUALETHERNETCARD_IN_VM_TO_DVSWITCH =
        "Unable to migrate VirtualEthernetCard#%s %s in VM %s to DvSwitch";

    private static final String ADD_DEFAULT_GW = "add default gw ";

    private static final String SBIN_ROUTE = "/sbin/route";

    private static final String NETMASK = " netmask ";

    private static final String SBIN_IFCONFIG = "/sbin/ifconfig";

    private final String ipAddress;

    private final VsphereClient client;

    private final HostSystem hostSystem;

    private String thumbprint;

    private final String version;

    private final ConfigManager confMgr;

    private final NetworkSystem netSys;

    private VirtualNicManager vNicMgr;

    private VsanSystem vSanSystem;

    private String license;

    HostProxy( String ipAddress, VsphereClient client, HostSystem hostSystem )
    {
        this.ipAddress = ipAddress;
        this.client = client;
        this.hostSystem = hostSystem;
        this.version = this.hostSystem.getConfig().getProduct().getVersion();
        this.confMgr = this.hostSystem.getConfigManager();
        this.netSys = getNetworkSystem();
    }

    private NetworkSystem getNetworkSystem()
    {
        ManagedObjectReference netSysRef = confMgr.getNetworkSystem();
        return client.createStub( NetworkSystem.class, netSysRef );
    }

    public VirtualNicManager getVirtualNicManager()
    {
        if ( null == vNicMgr )
        {
            vNicMgr = client.createStub( VirtualNicManager.class, confMgr.getVirtualNicManager() );
        }
        return vNicMgr;
    }

    public VsanSystem getVsanSystem()
    {
        if ( null == vSanSystem )
        {
            vSanSystem = client.createStub( VsanSystem.class, confMgr.getVsanSystem() );
        }
        return vSanSystem;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public String getThumbprint()
    {
        if ( thumbprint == null )
        {
            thumbprint = VsphereUtils.computeThumbprint( hostSystem.getConfig().getCertificate() );
        }
        return thumbprint;
    }

    public String getVersion()
    {
        return version;
    }

    public String getLicense()
    {
        if ( license == null )
        {
            this.license = getEnterpriseLicense();
        }
        return license;
    }

    private String getEnterpriseLicense()
    {
        ManagedObjectReference licenseManagerMor = client.getServiceInstanceContent().getLicenseManager();
        LicenseManager licenseManager = client.createStub( LicenseManager.class, licenseManagerMor );
        for ( LicenseManager.LicenseInfo licenseInfo : licenseManager.getLicenses() )
        {
            String editionKey = licenseInfo.getEditionKey();
            String licenseKey = licenseInfo.getLicenseKey();
            logger.info( "Retrieving ESXi license {}. Edition: {}. License: {}", licenseInfo.getName(), editionKey,
                         licenseKey );
            if ( isEnterpriseOrEvalEdition( editionKey ) )
            {
                logger.info( "An Enterprise or Enterprise Plus or Evaluation license found" );
                return licenseKey;
            }
        }
        throw new UnsupportedOperationException( UNSUPOORTED_OPERATION_EXCEPTION );
    }

    private boolean isEnterpriseOrEvalEdition( String editionKey )
    {
        return editionKey.startsWith( ESX_ENTERPRISE ) || editionKey.startsWith( EVAL )
            || editionKey.startsWith( ESX_EVAL );
    }

    public DynamicTypeManager getDynamicTypeManager()
    {
        return client.createStub( DynamicTypeManager.class, hostSystem.retrieveDynamicTypeManager() );
    }

    public ManagedMethodExecuter getManagedMethodExecuter()
    {
        return client.createStub( ManagedMethodExecuter.class, hostSystem.retrieveManagedMethodExecuter() );
    }

    @Override
    public String toString()
    {
        return String.format( HOST_STR, ipAddress, thumbprint );
    }

    @Override
    public void close()
    {
        logger.info( "Disconnecting host {}", this.ipAddress );
        if ( null != client )
        {
            client.shutdown();
        }
    }

    public boolean ping( String destination )
    {
        VmkPing vmkPing = new VmkPing();
        VmkPingOutputSpec outputSpec = vmkPing.execute( this, destination );
        if ( null != outputSpec.getSummary() )
        {
            if ( outputSpec.getSummary().getReceived() > 0 )
            {
                return true;
            }
        }
        return false;
    }

    public boolean ping( String device, String destination )
    {
        VmkPing vmkPing = new VmkPing();
        VmkPingOutputSpec outputSpec = vmkPing.execute( this, device, destination );
        if ( null != outputSpec.getSummary() )
        {
            if ( outputSpec.getSummary().getReceived() > 0 )
            {
                return true;
            }
        }
        logger.info( "vmkping output\n {}", outputSpec );
        return false;
    }

    public String addPortGroup( String pgName, String switchName )
    {
        addPortGroup( pgName, switchName, 0 );
        return pgName;
    }

    public String addPortGroup( String pgName, String switchName, int vlanId )
        throws VsphereOperationException
    {
        NetworkPolicy networkPolicy = new NetworkPolicy();

        PortGroup.Specification pgSpec = new PortGroup.Specification();
        pgSpec.setName( pgName );
        pgSpec.setVlanId( vlanId );
        pgSpec.setVswitchName( switchName );
        pgSpec.setPolicy( networkPolicy );

        try
        {
            netSys.addPortGroup( pgSpec );
        }
        catch ( AlreadyExists ignored )
        {
        }
        catch ( NotFound | HostConfigFault ex )
        {
            throw new VsphereOperationException( String.format( UNABLE_TO_ADD_PORTGROUP_ON_SWITCH, pgName, switchName ),
                                                 ex );
        }
        return pgName;
    }

    public void removePortGroup( String pgName )
    {
        if ( StringUtils.isBlank( pgName ) )
        {
            logger.info( "Port group is null in ESXi host {}. Removing will be skipped", this.ipAddress );
            return;
        }
        try
        {
            netSys.removePortGroup( pgName );
            logger.info( "Port group {} is removed from ESXi host {}", pgName, this.ipAddress );
        }
        catch ( NotFound | ResourceInUse | HostConfigFault e )
        {//
            throw new VsphereOperationException( e );
        }
    }

    public String addVmknic( IpAllocation prodMgmtIp, String switchName, NicType nicType, String portGroupName )
    {
        return addVmknic( prodMgmtIp, switchName, nicType, portGroupName, 0 );
    }

    public String addVmknic( IpAllocation prodMgmtIp, String switchName, NicType nicType, String portGroupName,
                             int vlanId )
    {
        String nicTypeName = nicType.name();
        logger.info( "Creating {} vmknic on vSwitch {} with IP address {}", nicTypeName, switchName,
                     prodMgmtIp.getAddress() );

        // TODO: It depends on precofiguration.
        // String pgName = addPortGroup(VRACK_MGMT_NETWORK_PREFIX + " " + UUID.randomUUID(), switchName);
        // TransactionContext.getInstance().set(TRAN_NEW_PORT_GROUP_NAME, pgName);
        String pgName = addPortGroup( portGroupName, switchName, vlanId );
        TransactionContext.getInstance().set( TRAN_NEW_PORT_GROUP_NAME, pgName );

        IpConfig ipConf = new IpConfig();
        if ( prodMgmtIp.isDhcpEnabled() )
        {
            ipConf.setDhcp( true );
        }
        else
        {
            ipConf.setIpAddress( prodMgmtIp.getAddress() );
            ipConf.setSubnetMask( prodMgmtIp.getNetmask() );
        }

        VirtualNic.Specification vNicSpec = new VirtualNic.Specification();
        vNicSpec.setIp( ipConf );
        vNicSpec.setPortgroup( pgName );
        String device = null;
        try
        {
            device = netSys.addVirtualNic( pgName, vNicSpec );
        }
        catch ( AlreadyExists ignored )
        {
            logger.warn( "Portgroup {} already has a virtual network", pgName );
        }
        catch ( HostConfigFault | InvalidState ex )
        {
            throw new VsphereOperationException( String.format( VSPHERE_OPERATION_EXCEPTION_UNABLE_TO_ADD_VMKNIC,
                                                                pgName, switchName ),
                                                 ex );
        }
        logger.info( "New device vmknic {} has been added", device );
        TransactionContext.getInstance().set( TRAN_NEW_VMKNIC_DEVICE, device );

        String newGateway = prodMgmtIp.getGateway();
        logger.debug( "New gateway: {}", newGateway );
        if ( !StringUtils.isBlank( newGateway ) )
        {
            IpRouteConfig ipRouteConf = netSys.getIpRouteConfig();
            String oldGateway = ipRouteConf.getDefaultGateway();
            logger.debug( "Old gateway: {}", oldGateway );
            if ( newGateway.equals( oldGateway ) )
            {
                logger.info( "Old gateway is same as new gateway. IP route config will not be updated" );
            }
            else
            {
                ipRouteConf.setDefaultGateway( newGateway );
                logger.info( "Updating IP route config to {}", newGateway );
                try
                {
                    netSys.updateIpRouteConfig( ipRouteConf );
                }
                catch ( HostConfigFault | InvalidState ex )
                {
                    throw new VsphereOperationException( String.format( VSPHERE_OPERATION_EXCEPTION_UNABLE_TO_UPDATE_IP_ROUTE,
                                                                        pgName, switchName ),
                                                         ex );
                }
                logger.info( "IP route config is updated to {}", newGateway );
            }
        }

        VirtualNic vmknic = findVmknicByIp( prodMgmtIp.getAddress() );
        try
        {
            if ( nicType == NicType.management )
            {
                getVirtualNicManager().queryNetConfig( nicTypeName ).setMultiSelectAllowed( true );
            }
            getVirtualNicManager().selectVnic( nicTypeName, vmknic.getDevice() );
            return device;
        }
        catch ( HostConfigFault ex )
        {
            throw new VsphereOperationException( String.format( UNABLE_TO_CONFIG_VMKNIC_ON_PORTGROUP, pgName,
                                                                switchName ),
                                                 ex );
        }
    }

    public void changeManagementIp( final IpAllocation newIp )
    {
        final VirtualNic virtualNic = findVmknicByIp( ipAddress );
        if ( virtualNic == null )
        {
            logger.warn( "The vmknic with IP address {} is not found", newIp.getAddress() );
            return;
        }

        IpConfig ipConf = virtualNic.getSpec().getIp();
        IpRouteConfig ipRouteConf = netSys.getIpRouteConfig();
        String oldGateway = ipRouteConf.getDefaultGateway();
        String newGateway = newIp.getGateway();

        logger.info( "Old subnet mask: {}, new subnet mask: {}", ipConf.getSubnetMask(), newIp.getNetmask() );
        logger.info( "Old gateway: {}, new gateway: {}", oldGateway, newGateway );

        if ( oldGateway != null && newGateway != null && !newGateway.equals( oldGateway ) )
        {
            throw new UnsupportedOperationException( "New IP address cannot be assigned to vmknic in this way" );
        }

        ipConf.setIpAddress( newIp.getAddress() );
        ipConf.setSubnetMask( newIp.getNetmask() );
        final VirtualNic.Specification spec = virtualNic.getSpec();
        spec.setIp( ipConf );
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute( new Runnable()
        {
            @Override
            public void run()
            {
                logger.info( "Changing vmknic IP address from {} to {}", ipAddress, newIp.getAddress() );
                try
                {
                    netSys.updateVirtualNic( virtualNic.getDevice(), spec );
                }
                catch ( Exception ex )
                {
                    // an exception will happen as the the connection which based on original IP address has been
                    // changed
                }
            }
        } );
        executorService.shutdown();
        VsphereUtils.waitInSeconds( 30 );
        logger.info( "Vmknic {} IP address has been changed to {}", virtualNic.getDevice(), newIp.getAddress() );
        return;
    }

    public String addManagementVmknic( IpAllocation ip, String switchName )
        throws HostConfigFault, AlreadyExists, InvalidState, NotFound
    {
        return addManagementVmknic( ip, switchName, 0 );
    }

    public String addManagementVmknic( IpAllocation ip, String switchName, int vlanId )
        throws HostConfigFault, AlreadyExists, InvalidState, NotFound
    {
        return this.addVmknic( ip, switchName, NicType.management, VRACK_NETWORK_MGMT, vlanId );
    }

    public String addVmotionVmknic( IpAllocation ip, String switchName )
        throws HostConfigFault, AlreadyExists, InvalidState, NotFound
    {
        return this.addVmknic( ip, switchName, NicType.vmotion, VRACK_NETWORK_VMOTION );
    }

    public String addVsanVmknic( IpAllocation ip, String switchName )
        throws HostConfigFault, AlreadyExists, InvalidState, NotFound
    {
        return this.addVmknic( ip, switchName, NicType.vsan, VRACK_NETWORK_VSAN );
    }

    public void removeVmknicByIp( String ipAddress )
    {
        VirtualNic virtualNic = findVmknicByIp( ipAddress );
        if ( virtualNic == null )
        {
            return;
        }

        String device = virtualNic.getDevice();
        String portGroupName = virtualNic.getPortgroup();
        logger.debug( "Vmknic {} {}", device, ipAddress );
        logger.info( "Removing vmknic {} {} from host", device, ipAddress );
        try
        {
            netSys.removeVirtualNic( device );
            logger.info( "Vmknic {} is removed from host", device );
        }
        catch ( NotFound ignored )
        {
            logger.warn( "Vmknic {} doesn't exist", device );
        }
        catch ( HostConfigFault ex )
        {
            throw new VsphereOperationException( ex );
        }

        logger.info( "Removing PortGroup {} from host", portGroupName );
        try
        {
            netSys.removePortGroup( portGroupName );
            logger.info( "PortGroup {} is removed from host", portGroupName );
        }
        catch ( NotFound ignored )
        {
            logger.warn( "PortGroup {} doesn't exist", portGroupName );
        }
        catch ( HostConfigFault | ResourceInUse ex )
        {
            throw new VsphereOperationException( ex );
        }
    }

    public void addStaticRoute( String network, int prefixLength, String gateway )
    {
        addOrRemoveStaticRoute( network, prefixLength, gateway, ConfigChange.Operation.add );
        logger.info( "A static route with the network {}/{} and the gateway {} is added.", network, prefixLength,
                     gateway );
    }

    public void removeStaticRoute( String network, int prefixLength, String gateway )
    {
        addOrRemoveStaticRoute( network, prefixLength, gateway, ConfigChange.Operation.remove );
    }

    private void addOrRemoveStaticRoute( String network, int prefixLength, String gateway,
                                         ConfigChange.Operation operation )
    {
        try
        {
            IpRouteTableConfig ipRouteTableConfig = new IpRouteTableConfig( new IpRouteOp[] {
                new IpRouteOp( operation.name(), new IpRouteEntry( network, prefixLength, gateway, "" ) ) },
                                                                            new IpRouteOp[0] );

            NetStackInstance netStackInstance = new NetStackInstance();
            netStackInstance.setKey( defaultTcpipStack.name() );
            netStackInstance.setRouteTableConfig( ipRouteTableConfig );

            NetworkConfig networkConfig = new NetworkConfig();
            networkConfig.setNetStackSpec( new NetworkConfig.NetStackSpec[] {
                new NetworkConfig.NetStackSpec( netStackInstance, ConfigSpecOperation.edit.name() ) } );

            netSys.updateNetworkConfig( networkConfig, MODIFY );
        }
        catch ( AlreadyExists | NotFound | HostConfigFault | ResourceInUse e )
        {
            throw new VsphereOperationException( e.getMessage(), e );
        }
    }

    public void removeVmknicByDevice( String device )
    {
        if ( StringUtils.isBlank( device ) )
        {
            logger.info( "Vmknic is not set in ESXi host {}. Skip removing", this.ipAddress );
            return;
        }
        logger.info( "Removing vmknic {} from ESXi host {}", device, this.ipAddress );
        try
        {
            netSys.removeVirtualNic( device );
            logger.info( "Vmknic {} is removed from ESXi host {}", device, this.ipAddress );
        }
        catch ( NotFound ignore )
        {
            // the device may not be created on this host so ignore the exception
            logger.warn( "Vmknic {} is not found on ESXi host {}. Skip removing", device, this.ipAddress );
        }
        catch ( HostConfigFault ex )
        {
            throw new VsphereOperationException( ex );
        }
    }

    public VirtualNic findVmknicByIp( String ipAddress )
    {
        VirtualNic[] vNics = netSys.getNetworkInfo().getVnic();
        if ( null != vNics )
        {
            for ( VirtualNic vNic : vNics )
            {
                if ( null != vNic.getSpec() )
                {
                    if ( null != vNic.getSpec().getIp() )
                    {
                        if ( ipAddress.equals( vNic.getSpec().getIp().getIpAddress() ) )
                        {
                            return vNic;
                        }
                    }
                }
            }
        }
        return null;
    }

    public PortGroup findPortgroupByName( String pgName )
    {
        PortGroup[] portGroups = netSys.getNetworkInfo().getPortgroup();
        if ( null != portGroups )
        {
            for ( PortGroup portGroup : portGroups )
            {
                if ( null != portGroup.getSpec() )
                {
                    if ( pgName.equals( portGroup.getSpec().getName() ) )
                    {
                        return portGroup;
                    }
                }
            }
        }
        return null;
    }

    public void renamePortGroup( String ipAddress, String pgName )
    {
        try
        {
            VirtualNic vmknic = findVmknicByIp( ipAddress );
            PortGroup portGroup = findPortgroupByName( vmknic.getPortgroup() );
            portGroup.getSpec().setName( pgName );
            netSys.updatePortGroup( vmknic.getPortgroup(), portGroup.getSpec() );
        }
        catch ( AlreadyExists alreadyExists )
        {
            logger.error( "Error renaming the Port Group:  already exists", alreadyExists );

        }
        catch ( NotFound notFound )
        {
            logger.error( "Error renaming the Port Group: Not Found", notFound );
        }
        catch ( HostConfigFault hostConfigFault )
        {
            logger.error( "Error renaming the Port Group: host Config Fault", hostConfigFault );
        }
    }

    public List<String> listDatastoreName()
    {
        final String type = Datastore.class.getSimpleName();
        final List<String> nameList = new ArrayList<>();
        final String hostMorValue = hostSystem._getRef().getValue();
        InventoryService.getInstance().handleObjectContent( client.getPropertyCollector(),
                                                            client.getContainerView( type ), type,
                                                            new InventoryService.ObjectContentCallback()
                                                            {
                                                                @Override
                                                                public void handle( PropertyCollector.ObjectContent objectContent )
                                                                {
                                                                    for ( DynamicProperty property : getPropSet( objectContent ) )
                                                                    {
                                                                        if ( NAME_STR.equals( property.getName() ) )
                                                                        {
                                                                            Datastore datastore =
                                                                                client.createStub( Datastore.class,
                                                                                                   objectContent.getObj() );
                                                                            Datastore.HostMount[] hostMounts =
                                                                                datastore.getHost();
                                                                            for ( Datastore.HostMount hostMount : hostMounts )
                                                                            {
                                                                                if ( hostMount.getKey().getValue().equals( hostMorValue ) )
                                                                                {
                                                                                    nameList.add( property.getVal().toString() );
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } );
        return nameList;
    }

    public List<String> listVirtualMachinePortGroupName()
    {
        PortGroup[] portGroups = netSys.getNetworkInfo().getPortgroup();
        if ( isEmptyArray( portGroups ) )
        {
            return Collections.emptyList();
        }

        List<String> vmKernelPortGroup = listVmKernelPortGroupName();
        List<String> nameList = new ArrayList<>();
        for ( PortGroup portGroup : portGroups )
        {
            String portGroupName = portGroup.getSpec().getName();
            if ( isVirtualMachinePortGroup( portGroupName, vmKernelPortGroup ) )
            {
                logger.debug( "Virtual Machine port group: {}", portGroupName );
                nameList.add( portGroupName );
            }
        }
        return nameList;
    }

    public List<String> listVmKernelPortGroupName()
    {
        VirtualNic[] vnics = netSys.getNetworkInfo().getVnic();
        if ( isEmptyArray( vnics ) )
        {
            return Collections.emptyList();
        }

        List<String> vmKernelPortGroup = new ArrayList<>( vnics.length );
        for ( VirtualNic vnic : vnics )
        {
            logger.debug( "VM Kernel port group: {}", vnic.getPortgroup() );
            vmKernelPortGroup.add( vnic.getPortgroup() );
        }
        return vmKernelPortGroup;
    }

    private boolean isVirtualMachinePortGroup( final String portGroupName, final List<String> vmKernelPortGroup )
    {
        return !vmKernelPortGroup.contains( portGroupName );
    }

    public void addStandardSwitch( String vswitchName )
    {
        List<String> notInUseVmnics = listNotInUseVmnics();
        if ( notInUseVmnics.size() == 0 )
        {
            throw new UnsupportedOperationException( String.format( NO_AVAILABLE_PHYSICAL_NIC_IN_ESXI_HOST,
                                                                    this.ipAddress ) );
        }

        VirtualSwitch.BeaconConfig beaconConfig = new VirtualSwitch.BeaconConfig();
        beaconConfig.setInterval( 10 );
        VirtualSwitch.BondBridge bridge = new VirtualSwitch.BondBridge();
        bridge.setNicDevice( notInUseVmnics.toArray( Constants.ENPTY_STRING_ARRAY ) );
        bridge.setBeacon( beaconConfig );

        VirtualSwitch.Specification spec = new VirtualSwitch.Specification();
        spec.setNumPorts( 128 );
        spec.setBridge( bridge );

        logger.info( "Creating standard switch {}", vswitchName );
        try
        {
            netSys.addVirtualSwitch( vswitchName, spec );
            logger.info( "Standard switch {} is created", vswitchName );
        }
        catch ( AlreadyExists | ResourceInUse | HostConfigFault ex )
        {
            throw new VsphereOperationException( ex );
        }
    }

    public List<String> listNotInUseVmnics()
    {
        NetworkInfo networkInfo = netSys.getNetworkInfo();
        VirtualSwitch[] allVswitches = networkInfo.getVswitch();
        PhysicalNic[] allVmnics = networkInfo.getPnic();
        if ( isEmptyArray( allVswitches ) || isEmptyArray( allVmnics ) )
        {
            return Collections.emptyList();
        }

        List<String> inUseVmnics = new ArrayList<>( 8 );
        for ( VirtualSwitch virtualSwitch : allVswitches )
        {
            String[] vmnicsInVswitch = virtualSwitch.getPnic();
            if ( vmnicsInVswitch == null )
            {
                continue;
            }
            for ( String pnicInVswitch : vmnicsInVswitch )
            {
                logger.debug( "Physical nic {} is in use", pnicInVswitch );
                inUseVmnics.add( pnicInVswitch );
            }
        }

        List<String> notInUseVmnics = new ArrayList<>( 8 );
        logger.debug( "Listing all host physical nics" );
        for ( PhysicalNic pnic : allVmnics )
        {
            logger.debug( "Checking physical nic {}", pnic.getDevice() );
            if ( !isVmnicInUse( inUseVmnics, pnic ) )
            {
                logger.info( "Physical nic {} is not in use", pnic.getDevice() );
                notInUseVmnics.add( pnic.getDevice() );
            }
        }
        logger.info( "In total {} vmnics are not in-use: {}", notInUseVmnics.size(), notInUseVmnics.toArray() );
        return notInUseVmnics;
    }

    public List<String> listInUseVmnics()
    {
        NetworkInfo networkInfo = netSys.getNetworkInfo();
        VirtualSwitch[] virtualSwitches = networkInfo.getVswitch();
        if ( isEmptyArray( virtualSwitches ) )
        {
            throw new IllegalArgumentException( SWITCH_NOT_FOUND );
        }

        List<String> inUseVmnics = new ArrayList<>( 8 );
        for ( VirtualSwitch virtualSwitch : virtualSwitches )
        {
            String[] vmnics = virtualSwitch.getPnic();
            for ( String vmnic : vmnics )
            {
                inUseVmnics.add( vmnic.substring( vmnic.lastIndexOf( '-' ) + 1 ) );
            }
        }
        logger.info( "In total {} vmnics are in-use: {}", inUseVmnics.size(), inUseVmnics.toArray() );
        return inUseVmnics;
    }

    private boolean isVmnicInUse( List<String> allInUseVmnics, PhysicalNic targetVmnic )
    {
        for ( String pnic : allInUseVmnics )
        {
            if ( pnic.endsWith( targetVmnic.getDevice() ) || pnic.equals( targetVmnic.getKey() ) )
            {
                return true;
            }
        }
        return false;
    }

    // "available" means vmnic is connected but not in use
    public List<String> listAvailableVmnics()
    {
        NetworkInfo networkInfo = netSys.getNetworkInfo();
        VirtualSwitch[] virtualSwitches = networkInfo.getVswitch();
        if ( isEmptyArray( virtualSwitches ) )
        {
            logger.warn( "No any standard switch found" );
            return Collections.EMPTY_LIST;
        }

        PhysicalNic[] allVmnics = networkInfo.getPnic();
        if ( isEmptyArray( allVmnics ) )
        {
            logger.warn( "No any vmnic found" );
            return Collections.EMPTY_LIST;
        }

        List<String> inUseVmnics = listInUseVmnics();
        List<String> availableVmnics = new ArrayList<>( 8 );
        logger.debug( "Listing all vmnics..." );
        for ( PhysicalNic pnic : allVmnics )
        {
            logger.debug( "Checking physical nic {}", pnic.getDevice() );
            if ( !isVmnicInUse( inUseVmnics, pnic ) && ( pnic.getLinkSpeed() != null ) )
            {
                logger.info( "Physical nic {} is available (connected and not in-use), speed: {}MB", pnic.getDevice(),
                             pnic.getLinkSpeed().getSpeedMb() );
                availableVmnics.add( pnic.getDevice() );
            }
        }
        logger.info( "In total {} vmnics are available (connected and not in-use): {}", availableVmnics.size(),
                     availableVmnics.toArray() );
        return availableVmnics;
    }

    public List<String> listVmnics( String vswitchName )
    {
        VirtualSwitch vswitch = getVswitch( vswitchName );
        VirtualSwitch.BondBridge bondBridge = getBondBridge( vswitch );
        String[] vmnics = bondBridge.getNicDevice();
        logger.info( "Listing vmnics {} under standard switch {}", vmnics, vswitchName );
        return addToList( new ArrayList<String>(), vmnics );
    }

    public void addHostNetwork( HostConfig.NetworkConfig networkConfig )
    {
        logger.info( "Creating port group {}", networkConfig.getName() );
        addPortGroup( networkConfig.getName(), Constants.DEFAULT_VSWITCH_NAME, networkConfig.getVlandId() );
        logger.info( "Port group {} is created", networkConfig.getName() );

        String vnicName = networkConfig.getName();
        String vnicIpAddress = networkConfig.getIpAllocation().getAddress();
        String vnicSubnetMask = networkConfig.getIpAllocation().getNetmask();
        addVirtualNic( vnicName, vnicIpAddress, vnicSubnetMask );
        updateDefaultGateway( networkConfig.getIpAllocation().getGateway() );
    }

    private void addVirtualNic( String vnicName, String vnicIpAddress, String vnicSubnetMask )
    {
        VirtualNic.Specification virtualNicSpec = new VirtualNic.Specification();
        IpConfig ipConfig = new IpConfig();
        ipConfig.setDhcp( Boolean.FALSE );
        ipConfig.setIpAddress( vnicIpAddress );
        ipConfig.setSubnetMask( vnicSubnetMask );
        virtualNicSpec.setIp( ipConfig );

        Future<String> taskFuture = new ClientFutureImpl<>();
        logger.info( "Start to create virtual NIC with IP address {} and subnet mask {}", vnicIpAddress,
                     vnicSubnetMask );
        try
        {
            netSys.addVirtualNic( vnicName, virtualNicSpec, taskFuture );
            String nicName = taskFuture.get();
            logger.info( "Virtual nic {} is created on ESXi host {}", nicName, this.ipAddress );
        }
        catch ( InterruptedException | ExecutionException ex )
        {
            throw new VsphereOperationException( ex );
        }
    }

    public boolean migrateVmknicToDvSwitch( String vmkIpAddress, String dvSwitchUuid, String dvPortgroupKey )
        throws VsphereOperationException
    {
        VirtualNic vmknic = findVmknicByIp( vmkIpAddress );

        IpConfig ipConfig = new IpConfig();
        ipConfig.setDhcp( Boolean.FALSE );
        ipConfig.setIpAddress( vmknic.getSpec().getIp().getIpAddress() );
        ipConfig.setSubnetMask( vmknic.getSpec().getIp().getSubnetMask() );

        PortConnection portConnection = new PortConnection();
        portConnection.setSwitchUuid( dvSwitchUuid );
        portConnection.setPortgroupKey( dvPortgroupKey );

        VirtualNic.Specification virtualNicSpec = new VirtualNic.Specification();
        virtualNicSpec.setIp( ipConfig );
        virtualNicSpec.setDistributedVirtualPort( portConnection );

        String nicName = vmknic.getDevice();
        String nicTypeName = NicType.management.name();
        logger.info( "Migrating vmknic {} to DvSwitch {} with IP address: {}", nicName, dvSwitchUuid, vmkIpAddress );

        int numTries = 0;
        while ( numTries++ < 10 )
        {
            try
            {
                netSys.updateVirtualNic( nicName, virtualNicSpec );
                getVirtualNicManager().queryNetConfig( nicTypeName ).setMultiSelectAllowed( true );
                getVirtualNicManager().selectVnic( nicTypeName, vmknic.getDevice() );
                logger.info( "Vmknic {} with IP address {} is migrated to DvSwitch", nicName, vmkIpAddress );
                return true;
            }
            catch ( Exception ex )
            {
                waitInSeconds( 2 );
                logger.error( "Unable to migrate vmknic {} to DvSwitch -- retrying {}", nicName, numTries, ex );
            }
        }
        logger.warn( "Unable to migrate vmknic {} to DvSwitch", nicName );
        return false;
    }

    public void addVmknicOnDvSwitch( String dvSwitchUuid, IpAllocation newIp, String dvPortgroupKey, NicType nicType )
        throws VsphereOperationException
    {
        addVmknicOnDvSwitch( dvSwitchUuid, newIp, dvPortgroupKey, nicType, null, null );
    }

    // upstreamIpAddress, downstreamIpAddress => apply only to VSAN vmknics
    public void addVmknicOnDvSwitch( String dvSwitchUuid, IpAllocation newIp, String dvPortgroupKey, NicType nicType,
                                     String upstreamIpAddress, String downstreamIpAddress )
        throws VsphereOperationException
    {
        IpConfig ipConfig = new IpConfig();
        ipConfig.setDhcp( Boolean.FALSE );
        ipConfig.setIpAddress( newIp.getAddress() );
        ipConfig.setSubnetMask( newIp.getNetmask() );

        PortConnection portConnection = new PortConnection();
        portConnection.setSwitchUuid( dvSwitchUuid );
        portConnection.setPortgroupKey( dvPortgroupKey );
        logger.info( "DvSwitch UUID: {} and DvPortgroupKey: {} will be used to create vmknic", dvSwitchUuid,
                     dvPortgroupKey );
        // not necessary for a early-binding dvPortgroupKey and is not allowed for a late-binding portgroup.
        // portConnection.setPortKey("9");

        VirtualNic.Specification virtualNicSpec = new VirtualNic.Specification();
        virtualNicSpec.setIp( ipConfig );
        virtualNicSpec.setDistributedVirtualPort( portConnection );
        // virtualNicSpec.setPortgroup("vRack-DPortGroup-Mgmt");
        logger.info( "Creating {} vmknic on DvSwitch {} with IP address {}", nicType.name(), dvSwitchUuid,
                     newIp.getAddress() );
        try
        {
            String nicName = createVmknic( virtualNicSpec );
            logger.info( "Vmknic {} created on DistributedVirtualPort {}", nicName, dvPortgroupKey );

            VirtualNic vmknic = findVmknicByIp( newIp.getAddress() );
            String nicTypeName = nicType.name();
            if ( nicType == NicType.vsan )
            {
                ConfigInfo oldConfigInfo = getVsanSystem().getConfig();
                logger.debug( "Old vSan config: " + oldConfigInfo.toString() );
                /*
                 * logger.debug("Old num ports: " + oldConfigInfo.getNetworkInfo().getPort().length); for (int i=0;
                 * i<oldConfigInfo.getNetworkInfo().getPort().length; i++) { ConfigInfo.NetworkInfo.PortConfig
                 * portConfig = oldConfigInfo.getNetworkInfo().getPort()[i]; logger.debug("Port (upstream): " +
                 * portConfig.getIpConfig().getUpstreamIpAddress()); logger.debug("Port (downstream): " +
                 * portConfig.getIpConfig().getDownstreamIpAddress()); }
                 */
                com.vmware.vim.binding.vim.vsan.host.IpConfig vsanHostIpConfig = null;
                if ( upstreamIpAddress != null && !upstreamIpAddress.isEmpty() && downstreamIpAddress != null
                    && !downstreamIpAddress.isEmpty() )
                {
                    vsanHostIpConfig =
                        new com.vmware.vim.binding.vim.vsan.host.IpConfig( upstreamIpAddress, downstreamIpAddress );
                }
                else if ( oldConfigInfo != null && oldConfigInfo.getNetworkInfo() != null
                    && oldConfigInfo.getNetworkInfo().getPort() != null
                    && oldConfigInfo.getNetworkInfo().getPort().length > 0 )
                {
                    vsanHostIpConfig = oldConfigInfo.getNetworkInfo().getPort()[0].getIpConfig();
                }

                ConfigInfo.NetworkInfo.PortConfig portConfig =
                    new ConfigInfo.NetworkInfo.PortConfig( vsanHostIpConfig, vmknic.getDevice() );
                ConfigInfo.NetworkInfo networkInfo =
                    new ConfigInfo.NetworkInfo( new ConfigInfo.NetworkInfo.PortConfig[] { portConfig } );
                ConfigInfo configInfo =
                    new ConfigInfo( oldConfigInfo.getEnabled(), hostSystem._getRef(), null, null, networkInfo );
                getVsanSystem().update( configInfo );
            }
            else if ( nicType == NicType.management )
            {
                getVirtualNicManager().queryNetConfig( nicTypeName ).setMultiSelectAllowed( true );
                getVirtualNicManager().selectVnic( nicTypeName, vmknic.getDevice() );
            }
            else
            {
                getVirtualNicManager().selectVnic( nicTypeName, vmknic.getDevice() );
            }
        }
        catch ( AlreadyExists | HostConfigFault | InvalidState ex )
        {
            throw new VsphereOperationException( ex );
        }
    }

    public void setVMStartupShutdownOrder( int startDelay, String... vmNames )
        throws Exception
    {
        ManagedObjectReference autoStartMor = hostSystem.getConfigManager().autoStartManager;
        AutoStartManager autoStartMgr = client.createStub( AutoStartManager.class, autoStartMor );

        String vmTypeName = VirtualMachine.class.getSimpleName();
        AutoPowerInfo[] powerInfo = new AutoPowerInfo[vmNames.length];

        for ( int i = 0; i < vmNames.length; i++ )
        {
            ManagedObjectReference vmMor =
                InventoryService.getInstance().getUniqueByName( client.getPropertyCollector(),
                                                                client.getContainerView( vmTypeName ), vmTypeName,
                                                                vmNames[i] );

            powerInfo[i] = new AutoPowerInfo();
            powerInfo[i].setKey( vmMor );

            powerInfo[i].setStartAction( "powerOn" );
            powerInfo[i].setStartDelay( startDelay );
            powerInfo[i].setStartOrder( i + 1 );

            powerInfo[i].setWaitForHeartbeat( WaitHeartbeatSetting.no );

            powerInfo[i].setStopAction( "powerOff" );
            powerInfo[i].setStopDelay( -1 ); // System Default
        }

        SystemDefaults sDefault = new SystemDefaults();
        sDefault.setEnabled( true );
        sDefault.setStartDelay( new Integer( startDelay ) );

        Config configSpec = new Config();
        configSpec.setDefaults( sDefault );
        configSpec.setPowerInfo( powerInfo );

        autoStartMgr.reconfigure( configSpec );
    }

    public void updateSystemSetting( OptionValue... ov )
        throws InvalidName
    {
        ManagedObjectReference optionManagerMor = hostSystem.getConfigManager().advancedOption;
        OptionManager optionManager = client.createStub( OptionManager.class, optionManagerMor );
        optionManager.updateValues( ov );
    }

    private String createVmknic( VirtualNic.Specification virtualNicSpec )
        throws InvalidState, AlreadyExists, HostConfigFault
    {
        String nicName = null;
        // The following block is to workaround nic creation failure - see if retries help
        boolean nicCreated = false;
        int numTries = 0;
        while ( !nicCreated && numTries++ < 10 )
        {
            try
            {
                nicName = netSys.addVirtualNic( "", virtualNicSpec );
                nicCreated = true;
            }
            catch ( Exception ex )
            {
                logger.error( "Failed to create the nic -- retrying", ex );
                VsphereUtils.waitInSeconds( 2 );
            }
        }

        if ( nicName == null )
        {
            throw new VsphereOperationException( FAILED_TO_CREATE_VMKNIC );
        }
        return nicName;
    }

    private void updateDefaultGateway( String defaultGateway )
    {
        if ( StringUtils.isEmpty( defaultGateway ) )
        {
            logger.info( "Default gateway is not set. Skipped" );
            return;
        }

        IpRouteConfig ipRouteConf = netSys.getIpRouteConfig();
        ipRouteConf.setDefaultGateway( defaultGateway );
        logger.info( "Updating default gateway {} on host {}", defaultGateway, this.ipAddress );
        try
        {
            netSys.updateIpRouteConfig( ipRouteConf );
            logger.info( "Default gateway {} is set", defaultGateway );
        }
        catch ( HostConfigFault | InvalidState ex )
        {
            throw new VsphereOperationException( ex );
        }
    }

    public void removeVswitch( String vswitchName )
    {
        logger.info( "Removing standard switch {} from host", vswitchName );
        try
        {
            netSys.removeVirtualSwitch( vswitchName );
        }
        catch ( NotFound ignored )
        {
            logger.warn( "Standard switch {} doesn't exist", vswitchName );
        }
        catch ( ResourceInUse | HostConfigFault ex )
        {
            throw new VsphereOperationException( String.format( UNABLE_TO_REMOVE_VIRTUALSWITCH_FROM_HOST, vswitchName,
                                                                this.ipAddress ),
                                                 ex );
        }
        logger.info( "Standard switch {} is removed from host", vswitchName );
    }

    public void detachVmnic( String vswtichName, String... vmnic )
    {
        if ( vmnic == null || vmnic.length == 0 )
        {
            logger.warn( "No target vmnics assigned. Operation skipped" );
            return;
        }

        VirtualSwitch vswitch = getVswitch( vswtichName );
        VirtualSwitch.BondBridge bondBridge = getBondBridge( vswitch );
        String[] vmnicsInUse = bondBridge.getNicDevice();
        if ( vmnicsInUse == null || vmnicsInUse.length == 0 )
        {
            logger.warn( "No any vmnics used by {}", vswitch.getName() );
            return;
        }
        logger.info( "Detaching vmnics {} from {}", Arrays.toString( vmnicsInUse ), vswitch.getName() );

        String[] remainingNicDevice = excludeTargetVmnics( vmnicsInUse, vmnic );
        if ( remainingNicDevice.length == 0 )
        {
            vswitch.getSpec().setBridge( null );
        }
        else
        {
            bondBridge.setNicDevice( remainingNicDevice );
        }
        removeVmnicsFromNicTeaming( vswitch, vmnic );
        try
        {
            netSys.updateVirtualSwitch( vswitch.getName(), vswitch.getSpec() );
            logger.info( "Vmnics {} are detached from {}", Arrays.toString( vmnic ), vswitch.getName() );
        }
        catch ( ResourceInUse | NotFound | HostConfigFault ex )
        {
            logger.error( "Failed to detach vmnics {} from vSwitch", Arrays.toString( vmnic ), ex );
        }
    }

    public VirtualSwitch getVswitch( String vswitchName )
    {
        VirtualSwitch[] vswitch = listVswitch();
        for ( VirtualSwitch vs : vswitch )
        {
            if ( vs.getName().equals( vswitchName ) )
            {
                return vs;
            }
        }
        throw new IllegalArgumentException( String.format( NO_SUCH_STANDARD_VSWITCH_ON_HOST, vswitchName,
                                                           this.ipAddress ) );
    }

    public VirtualSwitch[] listVswitch()
    {
        VirtualSwitch[] vswitch = netSys.getNetworkInfo().getVswitch();
        if ( vswitch == null || vswitch.length == 0 )
        {
            throw new IllegalArgumentException( String.format( NO_ANY_STANDARD_VSWITCH_FOUND, this.ipAddress ) );
        }
        return vswitch;
    }

    private VirtualSwitch.BondBridge getBondBridge( final VirtualSwitch vswitch )
    {
        VirtualSwitch.Bridge bridge = vswitch.getSpec().getBridge();
        if ( !( bridge instanceof VirtualSwitch.BondBridge ) )
        {
            throw new IllegalArgumentException( String.format( HOST_DOES_NOT_CONTAIN_MULTIPLE_PHYSICAL_ADAPTOR,
                                                               vswitch.getName(), this.ipAddress ) );
        }
        return (VirtualSwitch.BondBridge) bridge;
    }

    private String[] excludeTargetVmnics( final String[] vmnicsInUse, final String[] targetVmnic )
    {
        List<String> remainingVmnics = new ArrayList<>( vmnicsInUse.length );
        for ( String vmnicInUse : vmnicsInUse )
        {
            remainingVmnics.add( vmnicInUse );
        }

        for ( String target : targetVmnic )
        {
            if ( !remainingVmnics.remove( target ) )
            {
                throw new IllegalArgumentException( String.format( TARGET_VMNIC_NOT_IN_USE, target ) );
            }
        }
        return remainingVmnics.toArray( new String[remainingVmnics.size()] );
    }

    private void removeVmnicsFromNicTeaming( final VirtualSwitch vswitch, final String[] vmnic )
    {
        NetworkPolicy.NicOrderPolicy nicOrderPolicy = vswitch.getSpec().getPolicy().getNicTeaming().getNicOrder();
        String[] activeNics = nicOrderPolicy.getActiveNic();
        nicOrderPolicy.setActiveNic( VsphereUtils.removeTarget( activeNics, vmnic ) );
        String[] standByNics = nicOrderPolicy.getStandbyNic();
        nicOrderPolicy.setStandbyNic( VsphereUtils.removeTarget( standByNics, vmnic ) );
    }

    public void attachVmnicToDvSwitch( String dvSwitchUuid, String uplinkPortgroupKey, String... vmnic )
    {
        if ( isEmptyArray( vmnic ) )
        {
            throw new IllegalArgumentException( NO_VMNIC_ASSIGNED );
        }

        HostMember.PnicSpec[] pnicSpecs = new HostMember.PnicSpec[vmnic.length];
        for ( int i = 0; i < vmnic.length; i++ )
        {
            pnicSpecs[i] = new HostMember.PnicSpec();
            pnicSpecs[i].setPnicDevice( vmnic[i] );
            pnicSpecs[i].setUplinkPortgroupKey( uplinkPortgroupKey );
        }
        logger.info( "Attaching {} to DvSwitch {} and UplinkPorgroup {}", vmnic, dvSwitchUuid, uplinkPortgroupKey );

        HostMember.PnicBacking pnicBacking = new HostMember.PnicBacking();
        pnicBacking.setPnicSpec( pnicSpecs );

        HostProxySwitch.Specification hostProxySwitchSpec = new HostProxySwitch.Specification();
        hostProxySwitchSpec.setBacking( pnicBacking );

        HostProxySwitch.Config hostProxySwitchConfig = new HostProxySwitch.Config();
        hostProxySwitchConfig.setChangeOperation( ConfigChange.Operation.edit.name() );
        hostProxySwitchConfig.setUuid( dvSwitchUuid );
        hostProxySwitchConfig.setSpec( hostProxySwitchSpec );

        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setProxySwitch( new HostProxySwitch.Config[] { hostProxySwitchConfig } );
        try
        {
            netSys.updateNetworkConfig( networkConfig, ConfigChange.Mode.modify.name() );
            logger.info( "{} are attached to DvSwitch {}", vmnic, dvSwitchUuid );
        }
        catch ( AlreadyExists ignored )
        {
            logger.warn( "Vmnics {} is being updated by another process", vmnic );
        }
        catch ( NotFound | HostConfigFault | ResourceInUse ex )
        {
            throw new VsphereOperationException( String.format( UNABLE_TO_ADD_VMNIC_INTO_DVSWITCH,
                                                                Arrays.toString( vmnic ) ) );
        }
    }

    public void rebootGuest( GuestCredential guestCredential )
    {
        String vmTypeName = VirtualMachine.class.getSimpleName();
        String vmName = guestCredential.getVmName();
        logger.info( "Rebooting VM {}", vmName );
        ManagedObjectReference vmMor =
            InventoryService.getInstance().getUniqueByName( client.getPropertyCollector(),
                                                            client.getContainerView( vmTypeName ), vmTypeName, vmName );
        VirtualMachine vm = client.createStub( VirtualMachine.class, vmMor );
        try
        {
            vm.rebootGuest();
        }
        catch ( ToolsUnavailable | InvalidState ex )
        {
            logger.error( "Failed to reboot guest: {}", ex.getMessage(), ex );
            throw new VsphereOperationException( FAILED_TO_REBOOT_GUEST + ex.getMessage(), ex );
        }
        catch ( TaskInProgress ignore )
        {
            logger.warn( "The guest {} is being rebooted by another process", vmName );
        }
    }

    public boolean powerOnGuest( GuestCredential guestCredential )
    {
        String vmTypeName = VirtualMachine.class.getSimpleName();
        String vmName = guestCredential.getVmName();
        logger.info( "Power-on VM {}", vmName );
        ManagedObjectReference vmMor =
            InventoryService.getInstance().getUniqueByName( client.getPropertyCollector(),
                                                            client.getContainerView( vmTypeName ), vmTypeName, vmName );
        VirtualMachine vm = client.createStub( VirtualMachine.class, vmMor );
        try
        {
            ManagedObjectReference tr = vm.powerOn( vm.getRuntime().getHost() );

            Task t = client.createStub( Task.class, tr );
            waitTaskEnd( t );
            Exception err = t.getInfo().getError();
            TaskInfo inf = t.getInfo();
            if ( err != null )
            {
                logger.error( err.getMessage(), err );
                // err.printStackTrace();
                return false;
            }
            return true;

        }
        catch ( FileFault | InvalidState | VmConfigFault | InsufficientResourcesFault ex )
        {
            logger.error( "Failed to power on guest: {}", ex.getMessage(), ex );
            throw new VsphereOperationException( FAILED_TO_REBOOT_GUEST + ex.getMessage(), ex );
        }
        catch ( TaskInProgress ignore )
        {
            logger.warn( "The guest {} is being powered-on by another process", vmName );
            return false;
        }
    }

    public void addNicToVirtualMachine( GuestCredential guestCredential )
    {
        String vmTypeName = VirtualMachine.class.getSimpleName();
        String vmName = guestCredential.getVmName();
        ManagedObjectReference vmMor =
            InventoryService.getInstance().getUniqueByName( client.getPropertyCollector(),
                                                            client.getContainerView( vmTypeName ), vmTypeName, vmName );
        VirtualMachine vm = client.createStub( VirtualMachine.class, vmMor );
        // Fetch existing NIC
        GuestInfo.NicInfo[] nicInfos = vm.getGuest().getNet();
        if ( nicInfos.length > 1 )
        {
            logger.warn( "Two NICs already exist!" );
            return;
        }
        try
        {
            logger.debug( nicInfos[0].toString() );
            // Create NIC device spec
            VirtualDeviceSpec nicSpec = new VirtualDeviceSpec();
            VirtualEthernetCard card = new VirtualVmxnet3();
            VirtualEthernetCard.NetworkBackingInfo info = new VirtualEthernetCard.NetworkBackingInfo();
            info.setDeviceName( nicInfos[0].getNetwork() );
            card.setBacking( info );
            nicSpec.operation = VirtualDeviceSpec.Operation.add;
            nicSpec.setDevice( card );

            // Add NIC
            com.vmware.vim.binding.vim.vm.ConfigSpec spec = new ConfigSpec();
            spec.setDeviceChange( new VirtualDeviceSpec[] { nicSpec } );
            ManagedObjectReference taskRef = vm.reconfigure( spec );

            logger.debug( "Waiting for second NIC to be added to {} ...", vmName );
            VsphereUtils.waitForTask( client, taskRef );
            logger.debug( "Second NIC added to VM {}", vmName );
        }
        catch ( TaskInProgress | InsufficientResourcesFault | DuplicateName | VmConfigFault | InvalidName
                        | ConcurrentAccess | InvalidDatastore | FileFault | InvalidState ex )
        {
            logger.error( "Failed to add NIC: {}", ex.getMessage(), ex );
            throw new VsphereOperationException( FAILED_TO_REBOOT_GUEST + ex.getMessage(), ex );
        }
    }

    public void addNicToVirtualMachine( GuestCredential guestCredential, String portGroup )
    {
        String vmTypeName = VirtualMachine.class.getSimpleName();
        String vmName = guestCredential.getVmName();
        ManagedObjectReference vmMor =
            InventoryService.getInstance().getUniqueByName( client.getPropertyCollector(),
                                                            client.getContainerView( vmTypeName ), vmTypeName, vmName );
        VirtualMachine vm = client.createStub( VirtualMachine.class, vmMor );
        try
        {
            // Create NIC device spec
            VirtualDeviceSpec nicSpec = new VirtualDeviceSpec();
            VirtualEthernetCard card = new VirtualVmxnet3();
            VirtualEthernetCard.NetworkBackingInfo info = new VirtualEthernetCard.NetworkBackingInfo();
            info.setDeviceName( portGroup );
            card.setBacking( info );
            nicSpec.operation = VirtualDeviceSpec.Operation.add;
            nicSpec.setDevice( card );

            // Add NIC
            com.vmware.vim.binding.vim.vm.ConfigSpec spec = new ConfigSpec();
            spec.setDeviceChange( new VirtualDeviceSpec[] { nicSpec } );
            ManagedObjectReference taskRef = vm.reconfigure( spec );

            logger.debug( "Waiting for second NIC to be added to {} ...", vmName );
            VsphereUtils.waitForTask( client, taskRef );
            logger.debug( "Second NIC added to VM {}", vmName );
        }
        catch ( TaskInProgress | InsufficientResourcesFault | DuplicateName | VmConfigFault | InvalidName
                        | ConcurrentAccess | InvalidDatastore | FileFault | InvalidState ex )
        {
            logger.error( "Failed to add NIC: {}", ex.getMessage(), ex );
            throw new VsphereOperationException( FAILED_TO_REBOOT_GUEST + ex.getMessage(), ex );
        }
    }

    public Boolean getGuestOperationsReady( GuestCredential guestCredential )
    {
        String vmTypeName = VirtualMachine.class.getSimpleName();
        String vmName = guestCredential.getVmName();
        ManagedObjectReference vmMor =
            InventoryService.getInstance().getUniqueByName( client.getPropertyCollector(),
                                                            client.getContainerView( vmTypeName ), vmTypeName, vmName );
        VirtualMachine vm = client.createStub( VirtualMachine.class, vmMor );
        // Fetch existing NIC
        Boolean guestOperationsReady = vm.getGuest().getGuestOperationsReady();
        logger.debug( "Guest {} guestOperationsReady: {}, interactiveGuestOperationsReady: {}, guestState: {}", vmName,
                      guestOperationsReady, vm.getGuest().getInteractiveGuestOperationsReady(),
                      vm.getGuest().getGuestState() );
        return guestOperationsReady;
    }

    public static enum GuestType
    {
        POSIX, WINDOWS,
    };

    public void uploadFileToGuest( GuestCredential guestCredential, GuestType guestType, String localFilePath,
                                   String guestFilePath )
        throws VsphereOperationException
    {
        uploadFileToGuest( guestCredential, guestType, localFilePath, guestFilePath, true );
    }

    public void uploadFileToGuest( GuestCredential guestCredential, GuestType guestType, String localFilePath,
                                   String guestFilePath, boolean overwrite )
        throws VsphereOperationException
    {
        if ( StringUtils.isBlank( localFilePath ) )
        {
            throw new IllegalArgumentException( "Local file is not specified" );
        }
        File localFile = new File( localFilePath );
        if ( !localFile.exists() || localFile.isDirectory() )
        {
            throw new IllegalArgumentException( "Local file '" + localFilePath + "' does not exist or is a directory" );
        }

        ManagedObjectReference fileManagerMor = getGuestOperationsManager().getFileManager();
        FileManager fileManager = client.createStub( FileManager.class, fileManagerMor );

        String vmName = guestCredential.getVmName();
        ManagedObjectReference vmMor = getVmMorByName( vmName );
        GuestAuthentication guestAuthentication = guestCredential.getGuestAuthentication();

        logger.info( "Starting to upload file {} to guest VM {}", localFilePath, vmName );
        logger.info( "Target guest type: {}, target file path: {}, is overwrite: {}", guestType.name(), guestFilePath,
                     overwrite );

        FileManager.FileAttributes fileAttributes = ( guestType == GuestType.POSIX )
                        ? new FileManager.PosixFileAttributes() : new FileManager.WindowsFileAttributes();
        fileAttributes.setAccessTime( Calendar.getInstance() );
        fileAttributes.setModificationTime( Calendar.getInstance() );
        String fileUploadUrl;
        try
        {
            fileUploadUrl = fileManager.initiateFileTransferToGuest( vmMor, guestAuthentication, guestFilePath,
                                                                     fileAttributes, localFile.length(), overwrite );
            fileUploadUrl = fileUploadUrl.replaceAll( "\\*", this.ipAddress );
        }
        catch ( GuestOperationsFault | InvalidState | TaskInProgress | FileFault ex )
        {
            String msg =
                String.format( "Failed to initiate FileTransfer for file %s in guest VM %s", localFilePath, vmName );
            logger.error( msg, ex );
            throw new VsphereOperationException( msg, ex );
        }

        VsphereUtils.retrieveHostCertificate( hostSystem.getConfig().getCertificate() );
        logger.info( "Certificate of the host {} is successfully retrieved", this.ipAddress );
        try
        {
            uploadFileToGuestVm( fileUploadUrl, localFilePath, localFile.length() );
            logger.info( "Target file {} created in guest VM {}", guestFilePath, vmName );
        }
        catch ( IOException ex )
        {
            String msg = String.format( "Failed to upload file %s to guest VM %s", localFilePath, vmName );
            logger.error( msg, ex );
            throw new VsphereOperationException( msg, ex );
        }
    }

    private void uploadFileToGuestVm( String urlString, String fileName, long fileSize )
        throws IOException, VsphereOperationException
    {
        logger.info( "URL for file to be uploaded: {}", urlString );
        URL urlSt = new URL( urlString );
        HttpURLConnection conn = (HttpURLConnection) urlSt.openConnection();
        conn.setDoInput( true );
        conn.setDoOutput( true );

        conn.setRequestProperty( "Content-Type", "application/octet-stream" );
        conn.setRequestMethod( "PUT" );
        conn.setRequestProperty( "Content-Length", Long.toString( fileSize ) );
        OutputStream out = conn.getOutputStream();
        InputStream in = new FileInputStream( fileName );
        byte[] buf = new byte[102400];
        int len = 0;
        try
        {
            while ( ( len = in.read( buf ) ) > 0 )
            {
                out.write( buf, 0, len );
            }
        }
        finally
        {
            VsphereUtils.closeQuietly( in );
            VsphereUtils.closeQuietly( out );
        }

        int responseCode = conn.getResponseCode();
        String responseMessage = conn.getResponseMessage();
        conn.disconnect();

        if ( HttpsURLConnection.HTTP_OK == responseCode )
        {
            logger.info( "Successfully upload file {}", fileName );
            return;
        }
        String msg = String.format( "Failed to upload file %s to guest VM. Status code: %s. Message: %s", fileName,
                                    responseCode, responseMessage );
        logger.error( msg );
        throw new VsphereOperationException( msg );
    }

    private ManagedObjectReference getVmMorByName( String vmName )
    {
        String vmTypeName = VirtualMachine.class.getSimpleName();
        return InventoryService.getInstance().getUniqueByName( client.getPropertyCollector(),
                                                               client.getContainerView( vmTypeName ), vmTypeName,
                                                               vmName );
    }

    private GuestOperationsManager getGuestOperationsManager()
    {
        ManagedObjectReference guestOperationsManagerMor =
            client.getServiceInstanceContent().getGuestOperationsManager();
        return client.createStub( GuestOperationsManager.class, guestOperationsManagerMor );
    }

    public int[] runGuestProgram( GuestCredential guestCredential, GuestProgram... guestProgram )
        throws VsphereOperationException
    {
        if ( guestProgram == null || guestProgram.length < 1 )
        {
            throw new IllegalArgumentException( NO_GUEST_PROGRAM_SPECIFIED );
        }
        String vmName = guestCredential.getVmName();
        ManagedObjectReference vmMor = getVmMorByName( vmName );
        ManagedObjectReference processManagerMor = getGuestOperationsManager().getProcessManager();
        ProcessManager processManager = client.createStub( ProcessManager.class, processManagerMor );

        logger.info( "Guest programs will be started within VirtualMachine {}", guestCredential.getVmName() );

        int[] exitCodes = new int[guestProgram.length];
        int i = 0;
        try
        {
            for ( GuestProgram p : guestProgram )
            {
                int exitCode = executeProgram( guestCredential.getGuestAuthentication(), p, vmMor, processManager );
                exitCodes[i++] = exitCode;
            }
            logger.info( "Guest programs are completed within VirtualMachine {}", vmName );
        }
        catch ( GuestOperationsFault | InvalidState | TaskInProgress | FileFault ex )
        {
            logger.error( ex.getMessage(), ex );
            throw new VsphereOperationException( FAILED_TO_UPDATE_VCENTER_IP_ADDRESS, ex );
        }
        return exitCodes;
    }

    public int[] runGuestProgram( GuestCredential guestCredential, List<GuestProgram> guestProgramList )
        throws VsphereOperationException
    {
        return runGuestProgram( guestCredential,
                                guestProgramList.toArray( new GuestProgram[guestProgramList.size()] ) );
    }

    private int executeProgram( final GuestAuthentication guestAuthentication, final GuestProgram guestProgram,
                                final ManagedObjectReference vmMor, final ProcessManager processManager )
        throws GuestOperationsFault, InvalidState, TaskInProgress, FileFault
    {
        ProgramSpec programSpec = new ProgramSpec();
        programSpec.setArguments( guestProgram.getArguments() );
        programSpec.setProgramPath( guestProgram.getPath() );
        long pid = startProgram( processManager, vmMor, guestAuthentication, programSpec );
        logger.info( "Guest program [{} {}] is started with pid {}", programSpec.getProgramPath(),
                     programSpec.getArguments(), pid );
        return waitForProgramCompleted( vmMor, processManager, guestAuthentication, pid );
    }

    private long startProgram( ProcessManager processManager, ManagedObjectReference vmMor,
                               GuestAuthentication guestAuthentication, ProgramSpec programSpec )
        throws GuestOperationsFault, InvalidState, TaskInProgress, FileFault
    {
        long pid = -1;
        while ( true )
        {
            int numRetries = 0;
            try
            {
                pid = processManager.startProgram( vmMor, guestAuthentication, programSpec );
                break;
            }
            catch ( GuestOperationsFault ex )
            {
                logger.error( ex.getMessage(), ex );
                if ( numRetries++ > 10 )
                {
                    throw ex;
                }
                VsphereUtils.waitInSeconds( 30 );
                logger.debug( "Retry guest operation: #" + numRetries );
            }
        }
        return pid;
    }

    private int waitForProgramCompleted( final ManagedObjectReference vmMor, final ProcessManager processManager,
                                         final GuestAuthentication guestAuthentication, final long pid )
        throws GuestOperationsFault, InvalidState, TaskInProgress
    {
        ProcessInfo processInfo = null;
        while ( true )
        {
            VsphereUtils.waitInSeconds( 2 );
            processInfo = processManager.listProcesses( vmMor, guestAuthentication, new long[] { pid } )[0];
            if ( ( processInfo.getEndTime() != null ) )
            {
                break;
            }

            logger.info( "Guest program [pid:{}] is running", pid );
        }

        int exitCode = processInfo.getExitCode();
        if ( exitCode == 0 )
        {
            logger.info( "Guest program [pid:{}] is completed with exit code 0", pid );
        }
        else
        {
            logger.warn( "Guest program [pid:{}] is finished with error. Exit code: {}", pid, exitCode );
        }
        return exitCode;
    }

    public void updateLinuxVmNetwork( GuestCredential guestCredential, String eth, IpAllocation newNetwork )
        throws VsphereOperationException
    {
        if ( newNetwork.isDhcpEnabled() )
        {
            throw new UnsupportedOperationException( "Only static IP address can be accepted" );
        }

        logger.info( "Start to assign IP address {} to Linux VM {}", newNetwork.getAddress(),
                     guestCredential.getVmName() );

        List<GuestProgram> guestProgramList = new ArrayList<>();

        String ifconfigParam =
            String.format( "%s %s netmask %s", eth, newNetwork.getAddress(), newNetwork.getNetmask() );
        guestProgramList.add( new GuestProgram( SBIN_IFCONFIG, ifconfigParam ) );

        if ( StringUtils.isNotBlank( newNetwork.getGateway() ) )
        {
            String routeParam = String.format( "add default gw %s", newNetwork.getGateway() );
            guestProgramList.add( new GuestProgram( SBIN_ROUTE, routeParam ) );
        }
        runGuestProgram( guestCredential, guestProgramList );
        logger.info( "IP address {} assigned to Linux VM {} successfully", newNetwork.getAddress(),
                     guestCredential.getVmName() );
    }

    public List<VirtualMachine> listVirtualMachine()
    {
        ManagedObjectReference[] vmMors = hostSystem.getVm();
        if ( vmMors == null || vmMors.length == 0 )
        {
            return Collections.emptyList();
        }

        List<VirtualMachine> vms = new ArrayList<>( vmMors.length );
        for ( ManagedObjectReference vmMor : vmMors )
        {
            vms.add( client.createStub( VirtualMachine.class, vmMor ) );
        }
        return vms;
    }

    public boolean migrateVmToDvSwitch( VirtualMachine vm, String dvSwitchUuid, String... dvPortgroupKey )
    {
        String vmName = vm.getName();
        logger.info( "Migrating VM {} to DvSwitch {}", vmName, dvSwitchUuid );

        if ( dvPortgroupKey == null || dvPortgroupKey.length == 0 )
        {
            logger.warn( "No any target DvPortgroup specified. Migration to DistributedVirtualSwitch is skipped" );
            return false;
        }
        int dvPortgroupCount = dvPortgroupKey.length;
        List<VirtualEthernetCard> ethCardList = VsphereUtils.sortVirtualEthernetCard( vm );
        int ethCardCount = ethCardList.size();
        if ( ethCardCount == 0 )
        {
            logger.warn( "No any EthernetCard found in VM {}. Migration to DvSwitch is skipped", vmName );
            return false;
        }
        if ( ethCardCount > dvPortgroupCount )
        {
            logger.warn( "There are {} EthernetCards found in VM {}, but only {} DvPorgroups specified. Cannot determine how to migrate",
                         ethCardCount, dvPortgroupCount );
            return false;
        }

        List<VirtualDeviceSpec> virtualDeviceSpecs = new ArrayList<>();
        for ( int ethCardIndex = 0; ethCardIndex < ethCardCount; ethCardIndex++ )
        {
            PortConnection portConnection = new PortConnection();
            portConnection.setSwitchUuid( dvSwitchUuid );
            portConnection.setPortgroupKey( dvPortgroupKey[ethCardIndex] );
            VirtualEthernetCard.DistributedVirtualPortBackingInfo dvPortBackingInfo =
                new VirtualEthernetCard.DistributedVirtualPortBackingInfo();
            dvPortBackingInfo.setPort( portConnection );

            VirtualDevice targetEthernetCard = ethCardList.get( ethCardIndex );
            String ethCardLabel = targetEthernetCard.getDeviceInfo().getLabel();
            targetEthernetCard.setBacking( dvPortBackingInfo );

            logger.info( "Migrating VirtualEthernetCard#{} [{}] to DvPortgruop {}", ethCardIndex, ethCardLabel,
                         dvPortgroupKey[ethCardIndex] );
            virtualDeviceSpecs.add( new VirtualDeviceSpec( VirtualDeviceSpec.Operation.edit, null, targetEthernetCard,
                                                           EMPTY_PS ) );
        }

        com.vmware.vim.binding.vim.vm.ConfigSpec vmConfigSpec = new com.vmware.vim.binding.vim.vm.ConfigSpec();
        vmConfigSpec.setDeviceChange( virtualDeviceSpecs.toArray( new VirtualDeviceSpec[virtualDeviceSpecs.size()] ) );
        try
        {
            ManagedObjectReference taskRef = vm.reconfigure( vmConfigSpec );
            VsphereUtils.waitForTask( client, taskRef );
            logger.info( "VM {} has been migrated to DvSwitch {}", vmName, dvSwitchUuid );
            return true;
            // } catch (TaskInProgress tip) {
            // VsphereUtils.waitForTask(client, tip.getTask());
        }
        catch ( TaskInProgress | VmConfigFault | ConcurrentAccess | FileFault | InvalidName | DuplicateName
                        | InvalidState | InsufficientResourcesFault | InvalidDatastore ex )
        {
            String msg = String.format( FAILED_TO_MIGRATE_VM_TO_DVSWITCH, vmName, dvSwitchUuid );
            throw new VsphereOperationException( msg, ex );
        }
    }

    public boolean migrateVmToDvSwitch( String vmName, String dvSwitchUuid, String... dvPortgroupKey )
    {
        ManagedObjectReference targetVmMor = getVmByName( vmName );
        if ( targetVmMor == null )
        {
            logger.warn( "VM {} not found on host {}. Migration to DvSwitch is skipped", vmName, this.ipAddress );
            return false;
        }

        VirtualMachine vm = client.createStub( VirtualMachine.class, targetVmMor );
        return migrateVmToDvSwitch( vm, dvSwitchUuid, dvPortgroupKey );
    }

    private ManagedObjectReference getVmByName( final String vmName )
    {
        // All VMs on this host
        ManagedObjectReference[] vmsOnHost = hostSystem.getVm();
        if ( vmsOnHost == null || vmsOnHost.length == 0 )
        {
            logger.warn( "This is no any VM on host {}", this.ipAddress );
            return null;
        }
        String vmType = VirtualMachine.class.getSimpleName();
        // Target VM in vCenter cluster
        ManagedObjectReference targetVm =
            InventoryService.getInstance().getOneByName( client.getPropertyCollector(),
                                                         client.getContainerView( vmType ), vmType, vmName );
        if ( targetVm == null )
        {
            logger.warn( "VM {} is not found on cluster", vmName );
            return null;
        }
        for ( ManagedObjectReference mor : vmsOnHost )
        {
            if ( mor.getValue().equals( targetVm.getValue() ) )
            {
                return targetVm;
            }
        }
        // VM is not on this host
        logger.warn( "VM {} is not found on host {}", vmName, this.ipAddress );
        return null;
    }

    public void destroyVm( String vmName )
    {
        ManagedObjectReference vmMor = getVmByName( vmName );
        if ( vmMor == null )
        {
            return;
        }
        VirtualMachine vm = client.createStub( VirtualMachine.class, vmMor );
        logger.info( "Powering off VirtualMachine {}", vmName );
        try
        {
            if ( runTaskQuietly( vm.powerOff() ) )
            {
                logger.info( "VirtualMachine {} is powered off", vmName );
            }
            else
            {
                logger.warn( "VirtualMachine {} is not powered off due to exception, hence could not be destroyed",
                             vmName );
            }
            if ( runTaskQuietly( vm.destroy() ) )
            {
                logger.info( "VirtualMachine {} is destroyed", vmName );
            }
            else
            {
                logger.warn( "VirtualMachine {} is not destroyed, try to destroy it manually", vmName );
            }
        }
        catch ( VimFault ex )
        {
            logger.error( "Error when destroying VirtualMachine {}", vmName, ex );
        }
    }

    private boolean runTaskQuietly( ManagedObjectReference taskMor )
    {
        try
        {
            VsphereUtils.waitForTask( client, taskMor );
            return true;
        }
        catch ( Exception ex )
        {
            logger.error( "Failed to execute task {}:{}", taskMor.getType(), taskMor.getValue(), ex );
            return false;
        }
    }

    public void migrateVmToDvSwitch( int vmEthernetCardIndex, String dvSwitchUuid, String dvPortgroupKey )
    {
        ManagedObjectReference[] vmMors = hostSystem.getVm();
        if ( vmMors == null || vmMors.length == 0 )
        {
            logger.warn( "No any VMs found on host {}. Migration to DvSwitch is skipped", this.ipAddress );
            return;
        }

        PortConnection portConnection = new PortConnection();
        portConnection.setSwitchUuid( dvSwitchUuid );
        portConnection.setPortgroupKey( dvPortgroupKey );
        VirtualEthernetCard.DistributedVirtualPortBackingInfo dvPortBackingInfo =
            new VirtualEthernetCard.DistributedVirtualPortBackingInfo();
        dvPortBackingInfo.setPort( portConnection );

        for ( ManagedObjectReference vmMor : vmMors )
        {
            VirtualMachine vm = client.createStub( VirtualMachine.class, vmMor );
            List<VirtualEthernetCard> ethCardList = VsphereUtils.sortVirtualEthernetCard( vm );
            VirtualDevice targetEthernetCard =
                ( vmEthernetCardIndex < ethCardList.size() ) ? ethCardList.get( vmEthernetCardIndex ) : null;
            if ( targetEthernetCard == null )
            {
                logger.warn( "VirtualEthernetCard#{} is not found in VM {}. Migration to DvSwitch is skipped",
                             vmEthernetCardIndex, vm.getName() );
                continue;
            }
            String vmEthernetCardLabel = targetEthernetCard.getDeviceInfo().getLabel();
            logger.info( "Migrating VirtualEthernetCard#{} {} to DvSwitch", vmEthernetCardIndex, vmEthernetCardLabel );
            targetEthernetCard.setBacking( dvPortBackingInfo );
            com.vmware.vim.binding.vim.vm.ConfigSpec vmConfigSpec = new com.vmware.vim.binding.vim.vm.ConfigSpec();
            vmConfigSpec.setDeviceChange( new VirtualDeviceSpec[] {
                new VirtualDeviceSpec( VirtualDeviceSpec.Operation.edit, null, targetEthernetCard, EMPTY_PS ) } );

            try
            {
                ManagedObjectReference taskRef = vm.reconfigure( vmConfigSpec );
                VsphereUtils.waitForTask( client, taskRef );
            }
            catch ( TaskInProgress tip )
            {
                VsphereUtils.waitForTask( client, tip.getTask() );
            }
            catch ( VmConfigFault | ConcurrentAccess | FileFault | InvalidName | DuplicateName | InvalidState
                            | InsufficientResourcesFault | InvalidDatastore ex )
            {
                String msg = String.format( UNABLE_TO_MIGGRATE_VIRTUALETHERNETCARD_IN_VM_TO_DVSWITCH,
                                            vmEthernetCardIndex, vmEthernetCardLabel, vm.getName() );
                throw new VsphereOperationException( msg, ex );
            }
            logger.info( "VirtualEthernetCard#{} {} in VM {} has been migrated to DvSwitch {} on DvPortgruop {}",
                         vmEthernetCardIndex, vmEthernetCardLabel, vm.getName(), dvSwitchUuid, dvPortgroupKey );
        }
    }

    public static class ManagedHostProxy
        extends HostProxy
    {
        ManagedHostProxy( String ipAddress, VsphereClient client, HostSystem hostSystem )
        {
            super( ipAddress, client, hostSystem );
        }

        @Override
        public void close()
        {
            // do nothing here to avoid the client being closed
        }
    }

    // Getters added by Yagnesh because HMS inBand operations requires them.
    public HostSystem getHostSystem()
    {
        return this.hostSystem;
    }

    public VsphereClient getVsphereClient()
    {
        return this.client;
    }

    public NetworkSystem getNetworkSystemObj()
    {
        return this.netSys;
    }
}
