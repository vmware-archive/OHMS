/* ********************************************************************************
 * HmsGenericUtilTest.java
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
package com.vmware.vrack.hms.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * Test class for {@link HmsGenericUtil}
 *
 * @author spolepalli
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( { ProcessUtil.class } )
public class HmsGenericUtilTest
{

    @Test
    public void testMaskPassword()
    {
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "1.2.3.4" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );

        List<EthernetController> ethernetControllerLst = new ArrayList<EthernetController>();
        EthernetController ethernetController = new EthernetController();
        ethernetController.setComponent( ServerComponent.CPU );
        ComponentIdentifier componentIdentifier = new ComponentIdentifier();
        componentIdentifier.setDescription( "test" );
        ethernetController.setComponentIdentifier( componentIdentifier );
        ethernetControllerLst.add( ethernetController );

        node.setEthernetControllerList( ethernetControllerLst );

        ServerNode copy = (ServerNode) HmsGenericUtil.maskPassword( node );

        assertEquals( node.getNodeID(), "N1" );
        assertEquals( node.getNodeID(), copy.getNodeID() );
        assertEquals( node.getIbIpAddress(), "1.2.3.4" );
        assertEquals( node.getIbIpAddress(), copy.getIbIpAddress() );
        assertEquals( node.getOsUserName(), "root" );
        assertEquals( node.getOsUserName(), copy.getOsUserName() );
        assertEquals( node.getOsPassword(), "root123" );
        assertNotEquals( node.getOsPassword(), copy.getOsPassword() );
        assertEquals( copy.getOsPassword(), "****" );

        List<EthernetController> ethernetControllerListOriginal = node.getEthernetControllerList();
        List<EthernetController> ethernetControllerListCopy = copy.getEthernetControllerList();

        assertEquals( ethernetControllerListOriginal.get( 0 ).getComponent(), ServerComponent.CPU );
        assertEquals( ethernetControllerListOriginal.get( 0 ).getComponent(),
                      ethernetControllerListCopy.get( 0 ).getComponent() );

        assertEquals( ethernetControllerListOriginal.get( 0 ).getComponentIdentifier().getDescription(), "test" );
        assertEquals( ethernetControllerListOriginal.get( 0 ).getComponentIdentifier().getDescription(),
                      ethernetControllerListCopy.get( 0 ).getComponentIdentifier().getDescription() );
    }

    @Test
    public void testIsHostReachable()
    {
        /*
         * Seeing issues with bash -c not working in SBB environment. But code is working fine when run in dev env.
         */
        PowerMockito.mockStatic( ProcessUtil.class );
        // Below has to be mocked as the command doesn't execute on buildweb
        Mockito.when( ProcessUtil.executeCommand( Mockito.anyListOf( String.class ) ) ).thenReturn( 0 );
        assertTrue( HmsGenericUtil.isHostReachable( "127.0.0.1" ) );
    }
}
