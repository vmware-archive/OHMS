/* ********************************************************************************
 * CumulusVlanHelperTest.java
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
package com.vmware.vrack.hms.switches.cumulus;

import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.switches.cumulus.model.ConfigBlock;
import com.vmware.vrack.hms.switches.cumulus.model.Configuration;
import com.vmware.vrack.hms.switches.cumulus.model.SwitchPort;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by sankala on 12/5/14.
 */
public class CumulusVlanHelperTest
{
    private InputStream interfacesFile;

    private InputStream interfacesFileWithBond;

    @Before
    public void readFile()
    {
        interfacesFile = this.getClass().getClassLoader().getResourceAsStream( "tor-interfaces" );
        interfacesFileWithBond = this.getClass().getClassLoader().getResourceAsStream( "tor-interfaces-with-bond" );
    }

    @Test
    public void testInterfaceFileParsing()
    {
        Configuration configuration = Configuration.parse( interfacesFile );
        System.out.println( "Configuration : " + configuration.bridges.size() );
    }

    @Test
    public void testAddVlanToSwitchPort()
    {
        Configuration configuration = Configuration.parse( interfacesFile );
        CumulusVlanHelper helper = new CumulusVlanHelper( null );

        SwitchVlan vlan = new SwitchVlan();
        vlan.setId( "1200" );
        vlan.setTaggedPorts( new HashSet<String>() );
        vlan.getTaggedPorts().add( "swp2" );
        vlan.getTaggedPorts().add( "swp4" );
        try
        {
            helper.updateVlanConfiguration( vlan, configuration );
        }
        catch ( HmsOobNetworkException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println( configuration.getString() );
    }

    @Test
    public void testDeleteSwitchPortFromVlan()
    {
        Configuration configuration = Configuration.parse( interfacesFile );
        CumulusVlanHelper helper = new CumulusVlanHelper( null );

        SwitchVlan vlan = new SwitchVlan();
        vlan.setId( "1200" );
        vlan.setTaggedPorts( new HashSet<String>() );
        vlan.getTaggedPorts().add( "swp5" );
        vlan.getTaggedPorts().add( "swp6" );
        try
        {
            helper.updateVlanConfiguration( vlan, configuration );
        }
        catch ( HmsOobNetworkException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println( configuration.getString() );

        try
        {
            configuration = helper.deletePortFromVlanConfiguration( vlan, configuration, "swp5" );
            vlan.getTaggedPorts().remove( "swp5" );
        }
        catch ( HmsOobNetworkException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println( configuration.getString() );

        // validate the end result now
        ConfigBlock cb1 = configuration.getConfigBlock( "swp5" );
        ConfigBlock cb2 = configuration.getConfigBlock( "swp6" );

        if ( cb1.vlans != null && cb1.vlans.contains( "1200" ) )
            fail( "The port could not be deleted from VLAN" );
        if ( cb2.vlans != null && !cb2.vlans.contains( "1200" ) )
            fail( "Incorrect port got deleted from VLAN" );

        try
        {
            configuration = helper.deletePortFromVlanConfiguration( vlan, configuration, "swp6" );
        }
        catch ( HmsOobNetworkException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println( configuration.getString() );

        // validate the end result now
        if ( cb2.vlans != null && cb2.vlans.contains( "1200" ) )
            fail( "The port could not be deleted from VLAN" );

        // when both ports are deleted the VLAN shoud also be completely wiped out
        if ( configuration.bridges.get( 0 ).vlans.contains( "1200" ) )
            fail( "Empty VLAN (without any ports) incorrectly retained in configuration" );

        // at the end let's verify that the ports are still part of the bridge or not
        int numMatches = 0;
        if ( configuration.bridges.get( 0 ).members != null )
        {
            for ( SwitchPort switchPort : configuration.bridges.get( 0 ).members )
            {
                if ( switchPort.name.equals( "swp5" ) || switchPort.name.equals( "swp6" ) )
                    ++numMatches;
            }

            if ( numMatches != 2 )
                fail( "Some or all of the ports got incorrectly deleted from the VLAN aware bridge" );
        }
    }

    @Test
    public void testDeleteLacpBondFromVlan()
    {
        Configuration configuration = Configuration.parse( interfacesFileWithBond );
        CumulusVlanHelper helper = new CumulusVlanHelper( null );

        SwitchVlan vlan = new SwitchVlan();
        vlan.setId( "1200" );
        vlan.setTaggedPorts( new HashSet<String>() );
        vlan.getTaggedPorts().add( "bond47" );
        vlan.getTaggedPorts().add( "bond48" );
        try
        {
            helper.updateVlanConfiguration( vlan, configuration );
        }
        catch ( HmsOobNetworkException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println( configuration.getString() );

        try
        {
            configuration = helper.deletePortFromVlanConfiguration( vlan, configuration, "bond47" );
            vlan.getTaggedPorts().remove( "bond47" );
        }
        catch ( HmsOobNetworkException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println( configuration.getString() );

        // validate the end result now
        ConfigBlock cb1 = configuration.getConfigBlock( "bond47" );
        ConfigBlock cb2 = configuration.getConfigBlock( "bond48" );

        if ( cb1.vlans != null && cb1.vlans.contains( "1200" ) )
            fail( "The bond could not be deleted from VLAN" );
        if ( cb2.vlans != null && !cb2.vlans.contains( "1200" ) )
            fail( "Incorrect bond got deleted from VLAN" );

        try
        {
            configuration = helper.deletePortFromVlanConfiguration( vlan, configuration, "bond48" );
        }
        catch ( HmsOobNetworkException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println( configuration.getString() );

        // validate the end result now
        if ( cb2.vlans != null && cb2.vlans.contains( "1200" ) )
            fail( "The bond could not be deleted from VLAN" );

        // when both ports are deleted the VLAN shoud also be completely wiped out
        if ( configuration.bridges.get( 0 ).vlans.contains( "1200" ) )
            fail( "Empty VLAN (without any bonds/ports) incorrectly retained in configuration" );

        // at the end let's verify that the ports are still part of the bridge or not
        int numMatches = 0;
        if ( configuration.bridges.get( 0 ).members != null )
        {
            for ( SwitchPort switchPort : configuration.bridges.get( 0 ).members )
            {
                if ( switchPort.name.equals( "bond47" ) || switchPort.name.equals( "bond48" ) )
                    ++numMatches;
            }

            if ( numMatches != 2 )
                fail( "Some or all of the bonds got incorrectly deleted from the VLAN aware bridge" );
        }

    }

    @Test
    public void testAddLAGOnSwitchPort()
        throws Exception
    {
        Configuration configuration = Configuration.parse( interfacesFileWithBond );
        CumulusVlanHelper helper = new CumulusVlanHelper( null );

        SwitchLacpGroup lacpGroup = new SwitchLacpGroup();

        lacpGroup.setMode( "asdf" );
        lacpGroup.setName( "bond2" );
        lacpGroup.setPorts( new ArrayList<String>() );
        lacpGroup.getPorts().add( "swp2" );
        configuration = helper.updateLAGOnSwitchPorts( lacpGroup, configuration );
        System.out.println( configuration.getString() );
    }
}
