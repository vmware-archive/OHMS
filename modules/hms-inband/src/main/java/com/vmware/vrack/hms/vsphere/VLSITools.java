/* ********************************************************************************
 * VLSITools.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim.binding.vim.ComputeResource;
import com.vmware.vim.binding.vim.EnvironmentBrowser;
import com.vmware.vim.binding.vim.Task;
import com.vmware.vim.binding.vim.TaskInfo;
import com.vmware.vim.binding.vim.vm.ConfigOption;
import com.vmware.vim.binding.vim.vm.ConfigOptionDescriptor;
import com.vmware.vim.binding.vim.vm.device.VirtualDevice;
import com.vmware.vim.binding.vmodl.ManagedObjectReference;
import com.vmware.vim.vmomi.core.impl.BlockingFuture;

/**
 * Some tools for VLSI users as waiting for task completion, searching for default drivers inside host, etc Author:
 * Konstantin Spirov, Velislav Mitovski Date: 4/22/14 Time: 1:08 PM
 */
public class VLSITools
{
    /*
     * Re-factor hard coded string 2014-08-07
     */
    private static final String RUNNING = "running";

    private static final String COMPUTE_RESOURCE = "ComputeResource";

    public static final Logger log = LoggerFactory.getLogger( VLSITools.class );

    public static void waitTaskEnd( Task task )
    {
        try
        {
            for ( int i = 0; i < 3600; i++ )
            {
                BlockingFuture<TaskInfo> p = new BlockingFuture<TaskInfo>();
                task.getInfo( p );
                TaskInfo info = p.get( 1, TimeUnit.HOURS );
                Thread.sleep( 1000 );
                String state = RUNNING;
                if ( info.getState() != null )
                {
                    state = info.getState().name();
                }
                if ( !RUNNING.equals( state ) )
                {
                    return;
                }
                if ( info.getError() != null )
                {
                    return;
                }
            }
        }
        catch ( InterruptedException | ExecutionException | TimeoutException e )
        {
            log.error( e.getMessage(), e );
            return;
        }
    }

    /**
     * Show the first found default device from the corresponding type
     *
     * @param clazz VirtualDevice to search for
     * @param client Client connection
     * @param host Host to look in
     * @param <T> The first VirtualDevice, assigned to the given host, from the specified type
     * @return
     */
    public static <T extends VirtualDevice> T getDefaultDeviceFromClass( Class<T> clazz, VsphereClient client,
                                                                         ManagedObjectReference host )
    {
        Collection<ComputeResource> res = getComputeResources( client );
        for ( ComputeResource r : res )
        {
            VirtualDevice[] vd = getDefaultDevices( client, r, host );
            for ( VirtualDevice v : vd )
            {
                if ( clazz.isInstance( v ) )
                {
                    return (T) v;
                }
            }
        }
        return null;
    }

    public static <T extends VirtualDevice> Collection<T> getAllDevicesFromClass( Class<T> clazz, VsphereClient client,
                                                                                  ManagedObjectReference host )
    {
        ArrayList<T> list = new ArrayList<>();
        Collection<ComputeResource> res = getComputeResources( client );
        for ( ComputeResource r : res )
        {
            VirtualDevice[] vd = getDefaultDevices( client, r, host );
            for ( VirtualDevice v : vd )
            {
                if ( clazz.isInstance( v ) )
                {
                    list.add( (T) v );
                }
            }
        }
        return list;
    }

    private static VirtualDevice[] getDefaultDevices( VsphereClient client, ComputeResource computeResMor,
                                                      com.vmware.vim.binding.vmodl.ManagedObjectReference r )
    {
        EnvironmentBrowser envb = client.createStub( EnvironmentBrowser.class, computeResMor.getEnvironmentBrowser() );
        ConfigOptionDescriptor[] key = envb.queryConfigOptionDescriptor();
        LinkedHashSet<VirtualDevice> list = new LinkedHashSet<>();
        for ( ConfigOptionDescriptor k : key )
        {
            ConfigOption opts = envb.queryConfigOption( k.getKey(), r );
            Collections.addAll( list, opts.getDefaultDevice() );
        }
        return list.toArray( new VirtualDevice[0] );
    }

    /**
     * Find all ComputeResource groups, for the current vSphere
     *
     * @param client
     * @return
     */
    public static Collection<ComputeResource> getComputeResources( VsphereClient client )
    {
        ManagedObjectReference[] crRef =
            InventoryService.getInstance().findAll( client.getPropertyCollector(),
                                                    client.getContainerView( COMPUTE_RESOURCE ), COMPUTE_RESOURCE );
        ArrayList<ComputeResource> res = new ArrayList<ComputeResource>();
        for ( ManagedObjectReference ref : crRef )
        {
            res.add( client.createStub( ComputeResource.class, ref ) );
        }
        return res;
    }
}
