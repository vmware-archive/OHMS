/* ********************************************************************************
 * CpuInfoHelper.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.boardservice.ib.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vim.binding.vim.host.CpuInfo;
import com.vmware.vim.binding.vim.host.CpuPackage;
import com.vmware.vim.binding.vim.host.HardwareInfo;
import com.vmware.vrack.hms.boardservice.ib.InbandServiceImpl;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.event.ServerComponentEvent;

/**
 * CPU Info Helper
 * 
 * @author Yagnesh Chawda
 */
public class CpuInfoHelper
{
    private static Logger logger = Logger.getLogger( CpuInfoHelper.class );

    public static List<CPUInfo> getCpuInfo( HardwareInfo hardwareInfo )
        throws Exception
    {
        if ( hardwareInfo != null )
        {
            List<CPUInfo> cpuInfos = new ArrayList<>();
            CpuPackage[] cpuPkgs = hardwareInfo.getCpuPkg();
            CpuInfo ci = hardwareInfo.getCpuInfo();
            int numCoresPerCpu = ci.getNumCpuCores() / ci.getNumCpuPackages();
            if ( cpuPkgs != null )
            {
                for ( CpuPackage cp : cpuPkgs )
                {
                    CPUInfo cpuInfo = new CPUInfo();
                    ComponentIdentifier componentIdentifier = new ComponentIdentifier();
                    long maxClockFrequency = ( cp.getHz() ) / 1000000;
                    cpuInfo.setMaxClockFrequency( maxClockFrequency );
                    cpuInfo.setTotalCpuCores( numCoresPerCpu );
                    cpuInfo.setLocation( String.valueOf( cp.getIndex() ) );
                    cpuInfo.setId( String.valueOf( cp.getIndex() ) );
                    componentIdentifier.setManufacturer( cp.getVendor() );
                    componentIdentifier.setProduct( cp.getDescription() );
                    cpuInfo.setComponentIdentifier( componentIdentifier );
                    cpuInfos.add( cpuInfo );
                }
            }
            return cpuInfos;
        }
        else
        {
            throw new Exception( "Can not get CPU Info because the Hardware info object is NULL" );
        }
    }

    /**
     * Returns CPU specific sensor data for the node
     * 
     * @param serviceNode
     * @param component
     * @param inbandServiceImpl
     * @return
     * @throws HmsException
     */
    public static List<ServerComponentEvent> getCpuSensor( ServiceHmsNode serviceNode, ServerComponent component,
                                                           InbandServiceImpl inbandServiceImpl )
                                                               throws HmsException
    {
        List<CPUInfo> cpuInfos = null;
        List<ServerComponentEvent> componentSensors = new ArrayList<ServerComponentEvent>();
        try
        {
            if ( inbandServiceImpl != null )
            {
                cpuInfos = inbandServiceImpl.getCpuInfo( serviceNode );
            }
            else
            {
                String err = "Error while getting CPU INFO because InbandServiceImpl object was found NULL";
                logger.error( err );
                throw new HmsException( err );
            }
        }
        catch ( HmsException e )
        {
            logger.error( "Unable to get CPU Info List while trying to get CPU Sensors for node [" + serviceNode != null
                            ? serviceNode.getNodeID() : serviceNode + "]" );
            throw e;
        }
        // Currently this cpu sensor is used to generate HOST_OS_NOT_RESPONSIVE events.
        // if this is returning null or empty list, it means Node Inband is properly operating, otherwise it would have
        // thrown HmsException.
        return componentSensors;
    }
}
