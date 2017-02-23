/* ********************************************************************************
 * CumulusLacpGroupHelperTest.java
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

import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.switches.cumulus.model.Bond;
import com.vmware.vrack.hms.switches.cumulus.model.ConfigBlock;
import com.vmware.vrack.hms.switches.cumulus.model.Configuration;
import com.vmware.vrack.hms.switches.cumulus.model.SwitchPort;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rsen on 28 Jan 2016
 */
public class CumulusLacpGroupHelperTest
{
    private InputStream interfacesFileWithBondAnd2Ports;

    @Before
    public void readFile()
    {
        interfacesFileWithBondAnd2Ports =
            this.getClass().getClassLoader().getResourceAsStream( "tor-interfaces-with-bond-2-ports" );
    }

    @Test
    public void testInterfaceFileParsing()
    {
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );
        System.out.println( "Configuration : " + configuration.bridges.size() );
    }

    /*
     * PURPOSE:
     * ========================================================================================================= Makes
     * sure that the interfaces file provided do not contain any duplicate port or bond entries which are typically
     * defined with auto <port/bond> iface <port/bond>
     */
    @Test
    public void testConfigurationSanctity()
    {
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );

        if ( !checkCumulusConfigurationSanctity( configuration ) )
            fail( "Corrupted the configuration because of which at least 1 port or a bond is present more than once." );
    }

    /*
     * PURPOSE:
     * ========================================================================================================= Deletes
     * 1 port from a LAG with 2 ports and verifies that after the operation is done the deleted port is present in VLAN
     * aware bridge stanza and as in port specific stanza but not inside the LACP group/bond. Also verifies that the
     * VLANs of the deleted port have been inherited from its parent LAG.
     */
    @Test
    public void deletePortFromLag()
    {
        CumulusLacpGroupHelper lacpHelper = new CumulusLacpGroupHelper( null );
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );
        ConfigBlock cb = configuration.getConfigBlock( "bond47" );
        Bond bond = (Bond) cb;

        SwitchLacpGroup lag = new SwitchLacpGroup();

        lag.setName( cb.name );
        lag.setPorts( Arrays.asList( "swp47", "swp48" ) );

        try
        {
            lacpHelper.deleteSwitchPortFromLacpGroup( lag, Arrays.asList( "swp47" ), configuration );
        }
        catch ( HmsException e )
        {
            fail( "Failed to delete port from LACP group. Reason: " + e.getMessage() );
        }

        // validate now
        int numMatch = 0;
        for ( SwitchPort switchPort : bond.slaves )
        {
            if ( switchPort.name.equals( "swp47" ) )
                fail( "Failed to delete port swp47 from LACP Group bond47" );
            else if ( switchPort.name.equals( "swp48" ) )
                ++numMatch;
        }

        if ( numMatch != 1 )
        {
            fail( "Deleted all ports from the Bond bond47 incorrectly!!!" );
        }

        // also validate the VLAN aware bridge

        SwitchPort portSwp47 = configuration.bridges.get( 0 ).getMemberByName( "swp47" );
        if ( portSwp47 == null || configuration.bridges.get( 0 ).getMemberByName( "bond47" ) == null )
        {
            fail( "VLAN aware bridge settings are not proper. Either the deleted port or the bond is absent from its configuration" );
        }

        // now validate that the VLANs which were with the LAG has been propagated to the deleted port
        if ( portSwp47.vlans != cb.vlans && !portSwp47.vlans.contains( cb.vlans ) )
        {
            fail( "VLANs of the parent LACP bond have not propagated to the deleted port" );
        }

        if ( !checkCumulusConfigurationSanctity( configuration ) )
            fail( "Corrupted the configuration because of which at least 1 port or a bond is present more than once." );

        System.out.println( configuration.getString() );
    }

    /*
     * PURPOSE:
     * ========================================================================================================= Delete
     * all the ports (2 in this case) from an existing LAG one at a time and after each operation check: A. The
     * operation succeeded properly B. Other configurations are left intact At the end verify that the LAG without any
     * ports has been removed completely from configuration
     */
    @Test
    public void deleteAllPortsFromLag()
    {
        CumulusLacpGroupHelper lacpHelper = new CumulusLacpGroupHelper( null );
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );
        ConfigBlock cb = configuration.getConfigBlock( "bond47" );
        Bond bond = (Bond) cb;
        List<String> bondVlans = bond.vlans;

        SwitchLacpGroup lag = new SwitchLacpGroup();

        lag.setName( cb.name );
        lag.setPorts( Arrays.asList( "swp47", "swp48" ) );

        System.out.println( configuration.getString() );

        try
        {
            lacpHelper.deleteSwitchPortFromLacpGroup( lag, Arrays.asList( "swp47" ), configuration );
            System.out.println( configuration.getString() );
            lacpHelper.deleteSwitchPortFromLacpGroup( lag, Arrays.asList( "swp48" ), configuration );
        }
        catch ( HmsException e )
        {
            fail( "Failed to delete port from LACP group. Reason: " + e.getMessage() );
        }

        // validate now
        cb = configuration.getConfigBlock( "bond47" );

        if ( cb != null )
        {
            fail( "Incorrectly retained LAG without any slave ports." );
        }

        // also validate the VLAN aware bridge
        SwitchPort portSwp47 = configuration.bridges.get( 0 ).getMemberByName( "swp47" );
        SwitchPort portSwp48 = configuration.bridges.get( 0 ).getMemberByName( "swp48" );
        if ( portSwp47 == null || portSwp48 == null
            || configuration.bridges.get( 0 ).getMemberByName( "bond47" ) != null )
        {
            fail( "VLAN aware bridge settings are not proper. Either deleted ports are absent or the LAG is present" );
        }

        // now validate that the VLANs which were with the LAG has been propagated to the deleted port
        if ( portSwp47.vlans != bondVlans && !portSwp47.vlans.contains( bondVlans ) )
        {
            fail( "VLANs of the parent LACP bond have not propagated to the deleted port" );
        }
        if ( portSwp48.vlans != bondVlans && !portSwp48.vlans.contains( bondVlans ) )
        {
            fail( "VLANs of the parent LACP bond have not propagated to the deleted port" );
        }

        if ( !checkCumulusConfigurationSanctity( configuration ) )
            fail( "Corrupted the configuration because of which at least 1 port or a bond is present more than once." );

        System.out.println( configuration.getString() );
    }

    /*
     * PURPOSE:
     * =========================================================================================================
     * Verifies that if a LAG which has 2 ports is again attempted to be updated with 1 of its existing slave ports then
     * the resultant configuration has the earlier 2 ports and no side-effects have been caused due to this operation
     */
    @Test
    public void testUpdateLagWithSubsetOfPorts()
    {
        CumulusVlanHelper vlanHelper = new CumulusVlanHelper( null );
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );
        SwitchLacpGroup lacpGroup = new SwitchLacpGroup();

        lacpGroup.setName( "bond47" );
        lacpGroup.setPorts( Arrays.asList( "swp47" ) );

        try
        {
            configuration = vlanHelper.updateLAGOnSwitchPorts( lacpGroup, configuration );
        }
        catch ( HmsException e )
        {
            fail( "Failed to update an existing LAG with a subset of ports. Reason: " + e.getMessage() );
        }

        /* Now validate */
        ConfigBlock cb = configuration.getConfigBlock( "bond47" );
        Bond bond = (Bond) cb;

        if ( bond == null )
            fail( "Incorrectly deleted the existing Bond during the update process." );

        if ( bond.vlans == null || !bond.vlans.containsAll( Arrays.asList( "10", "20", "30", "40" ) ) )
            fail( "Incorrectly deleted/corrupted the VLANs with the existing Bond during the update process." );

        if ( bond.slaves == null || !bond.containsPortName( "swp47" ) || !bond.containsPortName( "swp48" ) )
            fail( "Incorrectly deleted/corrupted the slave ports with the existing Bond during the update process." );

        if ( !checkCumulusConfigurationSanctity( configuration ) )
            fail( "Corrupted the configuration because of which at least 1 port or a bond is present more than once." );

    }

    /*
     * PURPOSE:
     * =========================================================================================================
     * Verifies that if a LAG which has 2 ports is again attempted to be updated with the same set of slave ports then
     * the resultant configuration has the earlier 2 ports and no side-effects have been caused due to this operation.
     * This test basically makes sure that the update LACP API is truly idempotent. This is a common case when a task of
     * a workflow which is suposed to update LACP bond fails and VRM retries the task firing the same API with same
     * values again to HMS (observed specifically during IAAS workload creation)
     */
    @Test
    public void testUpdateLagWithSamePorts()
    {
        CumulusVlanHelper vlanHelper = new CumulusVlanHelper( null );
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );
        SwitchLacpGroup lacpGroup = new SwitchLacpGroup();

        lacpGroup.setName( "bond47" );
        lacpGroup.setPorts( Arrays.asList( "swp47", "swp48" ) );

        try
        {
            configuration = vlanHelper.updateLAGOnSwitchPorts( lacpGroup, configuration );
        }
        catch ( HmsException e )
        {
            fail( "Failed to update an existing LAG with a same set of ports. Reason: " + e.getMessage() );
        }

        /* Now validate */
        ConfigBlock cb = configuration.getConfigBlock( "bond47" );
        Bond bond = (Bond) cb;

        if ( bond == null )
            fail( "Incorrectly deleted the existing Bond during the update process." );

        if ( bond.vlans == null || !bond.vlans.containsAll( Arrays.asList( "10", "20", "30", "40" ) ) )
            fail( "Incorrectly deleted/corrupted the VLANs with the existing Bond during the update process." );

        if ( bond.slaves == null || !bond.containsPortName( "swp47" ) || !bond.containsPortName( "swp48" ) )
            fail( "Incorrectly deleted/corrupted the slave ports with the existing Bond during the update process." );

        if ( !checkCumulusConfigurationSanctity( configuration ) )
            fail( "Corrupted the configuration because of which at least 1 port or a bond is present more than once." );

    }

    /*
     * PURPOSE:
     * ========================================================================================================= This
     * test verifies that if a LAG with 2 ports exists and then an update is attempted on the same LAG but this time
     * with some of its existing slave ports and few new ports then the resultant LAG contains the old + new ports and
     * the new ports specific stanzas are deleted from configuration and also from VLAN aware bridge set of members.
     */
    @Test
    public void testUpdateLagWithFewNewPorts()
    {
        CumulusVlanHelper vlanHelper = new CumulusVlanHelper( null );
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );
        SwitchLacpGroup lacpGroup = new SwitchLacpGroup();

        lacpGroup.setName( "bond47" );
        lacpGroup.setPorts( Arrays.asList( "swp47", "swp50", "swp51" ) );

        try
        {
            configuration = vlanHelper.updateLAGOnSwitchPorts( lacpGroup, configuration );
        }
        catch ( HmsException e )
        {
            fail( "Failed to update an existing LAG with few new ports. Reason: " + e.getMessage() );
        }

        /* Now validate */
        ConfigBlock cb = configuration.getConfigBlock( "bond47" );
        Bond bond = (Bond) cb;

        if ( bond == null )
            fail( "Incorrectly deleted the existing Bond during the update process." );

        if ( bond.vlans == null || !bond.vlans.containsAll( Arrays.asList( "10", "20", "30", "40" ) ) )
            fail( "Incorrectly deleted/corrupted the VLANs with the existing Bond during the update process." );

        if ( bond.slaves == null || !bond.containsPortName( "swp47" ) || !bond.containsPortName( "swp48" )
            || !bond.containsPortName( "swp50" ) || !bond.containsPortName( "swp51" ) )
            fail( "Incorrectly deleted/corrupted the slave ports with the existing Bond during the update process." );

        if ( configuration.bridges.get( 0 ).getMemberByName( "swp50" ) != null
            || configuration.bridges.get( 0 ).getMemberByName( "swp51" ) != null )
            fail( "Incorrectly corrupted the member ports with the VLAN aware bridge during the update process." );

        if ( !checkCumulusConfigurationSanctity( configuration ) )
            fail( "Corrupted the configuration because of which at least 1 port or a bond is present more than once." );

    }

    /*
     * PURPOSE:
     * ========================================================================================================= Just to
     * make sure that when a LACP group is created with multiple ports with exactly same VLANs the LACP group that gets
     * created function properly. The logic that we have currently is clearly stated in the following test (*) hence the
     * same logic picking VLANs from the 1st port in the list will surely work for this scenario specially since all the
     * other ports in the list contain the same set of VLANs as the 1st port
     */
    @Test
    public void testCreateNewLagWithPortsWithSameVlan()
    {
        CumulusVlanHelper vlanHelper = new CumulusVlanHelper( null );
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );
        SwitchLacpGroup lacpGroup = new SwitchLacpGroup();

        lacpGroup.setName( "bondtest" );
        lacpGroup.setPorts( Arrays.asList( "swp50", "swp51" ) );

        try
        {
            configuration = vlanHelper.updateLAGOnSwitchPorts( lacpGroup, configuration );
        }
        catch ( HmsException e )
        {
            fail( "Failed to update an existing LAG with few new ports. Reason: " + e.getMessage() );
        }

        /* Now validate */
        ConfigBlock cb = configuration.getConfigBlock( "bondtest" );
        Bond bond = (Bond) cb;

        if ( bond == null )
            fail( "Not able to create the new bond." );

        if ( bond.vlans == null || bond.vlans.size() != 2 || !bond.vlans.containsAll( Arrays.asList( "10", "20" ) ) )
            fail( "The VLANs of the slave ports were not correctly propagated to the new bond" );

        if ( bond.slaves == null || !bond.containsPortName( "swp50" ) || !bond.containsPortName( "swp51" ) )
            fail( "The list of slave ports for the new LAG is not correct." );

        if ( configuration.bridges.get( 0 ).getMemberByName( "swp50" ) != null
            || configuration.bridges.get( 0 ).getMemberByName( "swp51" ) != null )
            fail( "Incorrectly corrupted the member ports with the VLAN aware bridge during the update process." );

        if ( !checkCumulusConfigurationSanctity( configuration ) )
            fail( "Corrupted the configuration because of which at least 1 port or a bond is present more than once." );
    }

    /*
     * PURPOSE: (*)
     * ========================================================================================================= This
     * test simply confirms the current behaviour of creating a LAG with 2 ports which have different VLAN membership.
     * Currently the VLAN membership of the 1st port in the list of slave ports is only considered as the VLANs of the
     * LACP group, hence if the 1st port has 3 VLANs and the 2nd port has 2 VLANs the LACP group created with these 2
     * ports will contain 3 VLANs --> exactly those from port 1 in the list
     */
    @Test
    public void testCreateNewLagWithPortsWithDifferentVlan()
    {
        CumulusVlanHelper vlanHelper = new CumulusVlanHelper( null );
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );
        SwitchLacpGroup lacpGroup = new SwitchLacpGroup();

        lacpGroup.setName( "bondtest" );

        /*
         * We have an issue here actually, when the bond gets created the last port in the list of ports passed if has
         * any vlans then only those vlans get set in the bond and not the ones in the ports before the last port in the
         * list. The fix is not straightforward. I brought this up sometimes back with Raja but we could not reach
         * conclusive decision because the choices were between smart API in Cumulus vs doing all the heavylifting in
         * VRM
         */
        lacpGroup.setPorts( Arrays.asList( "swp52", "swp50" ) );

        try
        {
            configuration = vlanHelper.updateLAGOnSwitchPorts( lacpGroup, configuration );
        }
        catch ( HmsException e )
        {
            fail( "Failed to update an existing LAG with few new ports. Reason: " + e.getMessage() );
        }

        /* Now validate */
        ConfigBlock cb = configuration.getConfigBlock( "bondtest" );
        Bond bond = (Bond) cb;

        if ( bond == null )
            fail( "Not able to create the new bond." );

        if ( bond.vlans == null || bond.vlans.size() != 3
            || !bond.vlans.containsAll( Arrays.asList( "10", "20", "30" ) ) )
            fail( "The VLANs of the slave ports were not correctly propagated to the new bond" );

        if ( bond.slaves == null || !bond.containsPortName( "swp50" ) || !bond.containsPortName( "swp52" ) )
            fail( "The list of slave ports for the new LAG is not correct." );

        if ( configuration.bridges.get( 0 ).getMemberByName( "swp50" ) != null
            || configuration.bridges.get( 0 ).getMemberByName( "swp52" ) != null )
            fail( "Incorrectly corrupted the member ports with the VLAN aware bridge during the update process." );

        if ( !checkCumulusConfigurationSanctity( configuration ) )
            fail( "Corrupted the configuration because of which at least 1 port or a bond is present more than once." );

    }

    /*
     * PURPOSE:
     * ========================================================================================================= This
     * test verifies that if an existing LAG is attempted to be updated with a completely new set of ports the resultant
     * LAG after the operation contains the existing ports and the newly added ones. These new ports are now not present
     * inside the VLAN aware bridge and nor in their own stanzas.
     */
    @Test
    public void testUpdateLagWithAllNewPorts()
    {
        CumulusVlanHelper vlanHelper = new CumulusVlanHelper( null );
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );
        SwitchLacpGroup lacpGroup = new SwitchLacpGroup();

        lacpGroup.setName( "bond47" );
        lacpGroup.setPorts( Arrays.asList( "swp50", "swp51" ) );

        try
        {
            configuration = vlanHelper.updateLAGOnSwitchPorts( lacpGroup, configuration );
        }
        catch ( HmsException e )
        {
            fail( "Failed to update an existing LAG with few new ports. Reason: " + e.getMessage() );
        }

        /* Now validate */
        ConfigBlock cb = configuration.getConfigBlock( "bond47" );
        Bond bond = (Bond) cb;

        if ( bond == null )
            fail( "Incorrectly deleted the existing Bond during the update process." );

        if ( bond.vlans == null || !bond.vlans.containsAll( Arrays.asList( "10", "20", "30", "40" ) ) )
            fail( "Incorrectly deleted/corrupted the VLANs with the existing Bond during the update process." );

        if ( bond.slaves == null || !bond.containsPortName( "swp47" ) || !bond.containsPortName( "swp48" )
            || !bond.containsPortName( "swp50" ) || !bond.containsPortName( "swp51" ) )
            fail( "Incorrectly deleted/corrupted the slave ports with the existing Bond during the update process." );

        if ( configuration.bridges.get( 0 ).getMemberByName( "swp50" ) != null
            || configuration.bridges.get( 0 ).getMemberByName( "swp51" ) != null )
            fail( "Incorrectly corrupted the member ports with the VLAN aware bridge during the update process." );

        if ( !checkCumulusConfigurationSanctity( configuration ) )
            fail( "Corrupted the configuration because of which at least 1 port or a bond is present more than once." );

    }

    /*
     * PURPOSE:
     * ========================================================================================================= This
     * test validates that if a LAG is attempted to be created or updated with a set of ports and if even one port in
     * that list of ports passed as slave ports already belong to another existing LAG exception is thrown from the API
     * and no change to configuration is done i.e. operation is aborted.
     */
    @Test
    public void testUpdateLagWithOneForeignPort()
    {
        CumulusVlanHelper vlanHelper = new CumulusVlanHelper( null );
        Configuration configuration = Configuration.parse( interfacesFileWithBondAnd2Ports );
        SwitchLacpGroup lacpGroup = new SwitchLacpGroup();

        lacpGroup.setName( "bond47" );
        lacpGroup.setPorts( Arrays.asList( "swp50", "swp51", "swp49" ) ); // port swp49 belongs to bond48

        try
        {
            configuration = vlanHelper.updateLAGOnSwitchPorts( lacpGroup, configuration );

            fail( "Incorrectly updated the LAG with a port swp49 which belongs to a different LAG bond48." );
        }
        catch ( HmsException e )
        {
            // this is a pass
        }

        if ( !checkCumulusConfigurationSanctity( configuration ) )
            fail( "Corrupted the configuration because of which at least 1 port or a bond is present more than once." );

    }

    /**
     * Method to be used only for these tests but can be made accessible to all tests to make sure that at the end of
     * each test there are no duplicate entries present in the configuration file for any ports or LACP bonds.
     * IMPORTANT:
     * =================================================================================================================
     * ========= In this test file at the end of each test we call this API to check sanctity of the configuration file
     * after making the changes.
     * 
     * @param configuration
     * @return
     */
    private boolean checkCumulusConfigurationSanctity( Configuration configuration )
    {
        Map<String, SwitchPort> portMap = new HashMap<String, SwitchPort>();
        Set<String> vlanAwareBridgeMembers = new HashSet<String>();
        boolean status = true;

        for ( SwitchPort switchPort : configuration.switchPorts )
        {
            if ( portMap.containsKey( switchPort.name ) )
            {
                System.out.println( String.format( "Port/Bond %s is found more than once in the configuration",
                                                   switchPort.name ) );
                status = false; // keep getting all such occurence
            }
            else
            {
                portMap.put( switchPort.name, switchPort );
            }

            if ( switchPort instanceof Bond )
            {
                Bond bond = (Bond) switchPort;
                // verify that the ports mentioned inside the bond are not present inside VLAN aware bridge
                for ( SwitchPort slave : bond.slaves )
                {
                    if ( configuration.bridges.get( 0 ).members.contains( slave ) )
                    {
                        System.out.println( String.format( "Port %s belonging to bond %s is also present inside VLAN aware bridge incorrectly.",
                                                           slave.name, bond.name ) );
                        status = false; // keep getting all such occurence
                    }
                }
            }
        }

        for ( SwitchPort switchPort : configuration.bridges.get( 0 ).members )
        {
            if ( !portMap.containsKey( switchPort.name ) )
            {
                System.out.println( String.format( "Port/Bond %s is part of the VLAN aware bridge but does not have its own stanza",
                                                   switchPort.name ) );
                status = false;
            }

            if ( vlanAwareBridgeMembers.contains( switchPort.name ) )
            {
                System.out.println( String.format( "Port/Bond %s is repeated inside the members section of VLAN aware bridge. They should be unique",
                                                   switchPort.name ) );
                status = false;
            }
            else
            {
                vlanAwareBridgeMembers.add( switchPort.name );
            }
        }

        return status;
    }
}