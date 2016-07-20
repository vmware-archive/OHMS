package com.vmware.vrack.hms.switches.cumulus;

import com.vmware.vrack.hms.common.exception.HmsOobNetworkException;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.switches.cumulus.model.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by sankala on 12/5/14.
 */
public class CumulusVlanHelperTest {
    private InputStream interfacesFile;
    private InputStream interfacesFileWithBond;

    @Before
    public void readFile() {
        interfacesFile = this.getClass().getClassLoader().getResourceAsStream("tor-interfaces");
        interfacesFileWithBond = this.getClass().getClassLoader().getResourceAsStream("tor-interfaces-with-bond");
    }

    @Test
    public void testInterfaceFileParsing() {
        Configuration configuration = Configuration.parse(interfacesFile);
        System.out.println("Configuration : " + configuration.bridges.size());
    }

    @Test
    public void testAddVlanToSwitchPort() {
        Configuration configuration = Configuration.parse(interfacesFile);
        CumulusVlanHelper helper = new CumulusVlanHelper( null );

        SwitchVlan vlan = new SwitchVlan();
        vlan.setId("1200");
        vlan.setTaggedPorts(new HashSet<String>());
        vlan.getTaggedPorts().add("swp2");
        vlan.getTaggedPorts().add("swp4");
        try {
            helper.updateVlanConfiguration( vlan, configuration );
        } catch (HmsOobNetworkException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println( configuration.getString() );
    }

    @Test
    public void testAddLAGOnSwitchPort() throws Exception {
        Configuration configuration = Configuration.parse(interfacesFileWithBond);
        CumulusVlanHelper helper = new CumulusVlanHelper( null );

        SwitchLacpGroup lacpGroup = new SwitchLacpGroup();

        lacpGroup.setMode("asdf");
        lacpGroup.setName("bond2");
        lacpGroup.setPorts(new ArrayList<String>());
        lacpGroup.getPorts().add("swp2");
        configuration = helper.updateLAGOnSwitchPorts(lacpGroup, configuration);
        System.out.println( configuration.getString() );
    }
}
