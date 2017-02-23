/* ********************************************************************************
 * CpuInfoOperationalStatusHelperTest.java
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
package com.vmware.vrack.hms.boardservice.ib.api.cim;

import static org.junit.Assert.assertEquals;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger16;
import javax.wbem.client.WBEMClient;
import javax.wbem.client.WBEMClientConstants;
import javax.wbem.client.WBEMClientFactory;

import org.junit.Test;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;

/**
 * Unit test cases for CPU Operational status update.
 * 
 * @author VMware Inc.
 */
public class CpuInfoOperationalStatusHelperTest
{

    @Test
    public void testCpuOperationalStatus()
        throws Exception
    {

        WBEMClient cimClient = null;

        ServiceServerNode node = new ServiceServerNode();
        node.setNodeID( "N3" );
        node.setIbIpAddress( "1.2.3.4" );
        node.setOsUserName( "root" );
        node.setOsPassword( "test123" );

        URL cimomUrl = new URL( "https://" + node.getIbIpAddress() + ":" + 5989 );
        cimClient = WBEMClientFactory.getClient( WBEMClientConstants.PROTOCOL_CIMXML );

        List<CIMInstance> instancesList = new ArrayList<CIMInstance>();

        // Constructing OMC_Processor entry CPU SOCKET 0
        CIMProperty<String> cimProperty =
            new CIMProperty<String>( "ElementName", null, "SOCKET 0", true, true, "OMC_Processor" );
        CIMProperty<UnsignedInteger16> cimProperty1 =
            new CIMProperty<UnsignedInteger16>( "HealthState", null, new UnsignedInteger16( "5" ), true, true,
                                                "OMC_Processor" );
        CIMProperty<UnsignedInteger16> cimProperty2 =
            new CIMProperty<UnsignedInteger16>( "CPUStatus", null, new UnsignedInteger16( "4" ), true, true,
                                                "OMC_Processor" );
        CIMProperty cimPropertyList[] = { cimProperty, cimProperty1, cimProperty2 };

        // Constructing OMC_Processor entry CPU SOCKET 1
        CIMProperty<String> cimPropertyNonOperaional =
            new CIMProperty<String>( "ElementName", null, "SOCKET 1", true, true, "OMC_Processor" );
        CIMProperty<UnsignedInteger16> cimPropertyNonOperaional1 =
            new CIMProperty<UnsignedInteger16>( "HealthState", null, new UnsignedInteger16( "30" ), true, true,
                                                "OMC_Processor" );
        CIMProperty<UnsignedInteger16> cimPropertyNonOperaional2 =
            new CIMProperty<UnsignedInteger16>( "CPUStatus", null, new UnsignedInteger16( "3" ), true, true,
                                                "OMC_Processor" );
        CIMProperty cimPropertyList1[] =
            { cimPropertyNonOperaional, cimPropertyNonOperaional1, cimPropertyNonOperaional2 };

        CIMInstance instance = new CIMInstance( new CIMObjectPath( "/root/cimv2:OMC_Processor" ), cimPropertyList );
        CIMInstance instance1 = new CIMInstance( new CIMObjectPath( "/root/cimv2:OMC_Processor" ), cimPropertyList1 );

        instancesList.add( instance );
        instancesList.add( instance1 );

        List<CPUInfo> cpuInfoList = new ArrayList<CPUInfo>();
        CPUInfo cpu1 = new CPUInfo();
        ComponentIdentifier cpuIdentifier1 = new ComponentIdentifier();
        cpuIdentifier1.setManufacturer( "INTEL" );
        cpuIdentifier1.setProduct( "Intel Xeon Processor" );
        cpu1.setComponentIdentifier( cpuIdentifier1 );
        cpu1.setId( "1" );
        cpu1.setMaxClockFrequency( (long) 2600 );
        cpu1.setTotalCpuCores( 4 );
        cpuInfoList.add( cpu1 );

        CPUInfo cpu2 = new CPUInfo();
        ComponentIdentifier cpuIdentifier2 = new ComponentIdentifier();
        cpuIdentifier2.setManufacturer( "INTEL" );
        cpuIdentifier2.setProduct( "Intel Xeon Processor" );
        cpu1.setComponentIdentifier( cpuIdentifier2 );
        cpu2.setId( "2" );
        cpu2.setMaxClockFrequency( (long) 2600 );
        cpu2.setTotalCpuCores( 4 );
        cpuInfoList.add( cpu2 );

        CpuInfoOperationalStatusHelper cpuInfoOperationalStatusHelper = new CpuInfoOperationalStatusHelper( node );
        cpuInfoOperationalStatusHelper.getProcessorInformation( cimClient, instancesList, cpuInfoList );

        assertEquals( cpuInfoList.get( 0 ).getFruOperationalStatus().toString(),
                      FruOperationalStatus.Operational.toString() );
        assertEquals( cpuInfoList.get( 1 ).getFruOperationalStatus().toString(),
                      FruOperationalStatus.NonOperational.toString() );

        // System.out.println("CPU Operational status: " +cpuInfoList.get(0).getFruOperationalStatus());
        // System.out.println("CPU NonOperational status: " +cpuInfoList.get(1).getFruOperationalStatus());
    }

}
