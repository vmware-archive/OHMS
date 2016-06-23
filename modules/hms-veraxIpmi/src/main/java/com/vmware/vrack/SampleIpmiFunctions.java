package com.vmware.vrack;

import java.net.InetAddress;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.veraxsystems.vxipmi.api.async.ConnectionHandle;
import com.veraxsystems.vxipmi.api.sync.IpmiConnector;
import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.commands.chassis.ChassisControl;
import com.veraxsystems.vxipmi.coding.commands.chassis.ChassisControlResponseData;
import com.veraxsystems.vxipmi.coding.commands.chassis.GetChassisStatus;
import com.veraxsystems.vxipmi.coding.commands.chassis.GetChassisStatusResponseData;
import com.veraxsystems.vxipmi.coding.commands.chassis.PowerCommand;
import com.veraxsystems.vxipmi.coding.commands.session.SetSessionPrivilegeLevel;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.vmware.vrack.coding.commands.IpmiCommandParameters;
import com.vmware.vrack.coding.commands.application.ColdResetCommand;
import com.vmware.vrack.coding.commands.application.ColdResetCommandResponseData;
import com.vmware.vrack.coding.commands.application.GetAcpiPowerState;
import com.vmware.vrack.coding.commands.application.GetAcpiPowerStateResponseData;
import com.vmware.vrack.coding.commands.application.GetSelfTestResults;
import com.vmware.vrack.coding.commands.application.GetSelfTestResultsResponseData;

/**
 * Sample Class to perform Basic Ipmi Functions
 * 
 * @author Yagnesh Chawda
 */
public class SampleIpmiFunctions
{
    IpmiConnector connector = null;

    ConnectionHandle handle = null;

    CipherSuite cs = null;

    static String action = null;

    CommandLine commandLine;

    public static void main( String[] args )
        throws Exception
    {
        SampleIpmiFunctions ipmiFunctions = new SampleIpmiFunctions();
        /*
         * Will try to parse command line arguments. If all the essential details are provided, then only it will
         * continue.
         */
        if ( ipmiFunctions.parseCmdLnArguments( args ) == false )
        {
            System.out.println( "Cannot continue. Check again, if you have provided essential details." );
            return;
        }
        int length = args.length;
        if ( length <= 0 )
        {
            System.out.println( "Enter Some action to perform: " );
            ipmiFunctions.printActionsList();
            return;
        }
        else
        {
            action = args[0];
            if ( !ipmiFunctions.isValid( action ) )
            {
                System.out.println( "Enter CORRECT operation to perform: " );
                System.out.println( "USAGE:    SampleIpmiFunctions <IpmiOperation> -H <hostname> -U <username> -P <password> -C <ciphersuiteindex>" );
                ipmiFunctions.printActionsList();
                return;
            }
        }
        try
        {
            ipmiFunctions.setupConnection();
            ipmiFunctions.performAction( action );
        }
        finally
        {
            ipmiFunctions.destroy();
        }
    }

    private boolean parseCmdLnArguments( String[] args )
    {
        CommandLineParser parser = new GnuParser();
        Options cmdOptions = new Options();
        cmdOptions.addOption( "H", true, "Host name of node" );
        cmdOptions.addOption( "U", true, "Username" );
        cmdOptions.addOption( "P", true, "Password" );
        cmdOptions.addOption( "C", true, "CipherSuite" );
        try
        {
            commandLine = parser.parse( cmdOptions, args, false );
            if ( ( commandLine.getOptionValue( "H" ) == null ) || ( commandLine.getOptionValue( "U" ) == null )
                || ( commandLine.getOptionValue( "P" ) == null ) || ( commandLine.getOptionValue( "C" ) == null ) )
            {
                throw new ParseException( "Provide essential details." );
            }
        }
        catch ( ParseException e )
        {
            System.out.println( "Error while parsing command Line Arguments." );
            System.out.println( "USAGE:    SampleIpmiFunctions <IpmiOperation> -H <hostname> -U <username> -P <password> -C <ciphersuiteindex>" );
            return false;
        }
        return true;
    }

    private void performAction( String act )
    {
        System.out.println( "\n******* RESULT *******\n" );
        switch ( act )
        {
            case "power_status":
                getPowerStatus();
                break;
            case "power_cycle":
                powerCycleServer();
                break;
            case "power_up":
                powerUpServer();
                break;
            case "power_down":
                powerDownServer();
                break;
            case "hard_reset":
                hardResetServer();
                break;
            case "cold_reset":
                coldResetBmc();
                break;
            case "self_test":
                performSelfTest();
                break;
            case "acpi_power_state":
                getAcpiPowerState();
                break;
            case "list_actions":
            default:
                printActionsList();
                break;
        }
        System.out.println( "\n******* END OF RESULT *******\n" );
    }

    private void printActionsList()
    {
        System.out.println( "Choose action from following:" );
        System.out.println( "power_status" );
        System.out.println( "power_cycle" );
        System.out.println( "power_up" );
        System.out.println( "power_down" );
        System.out.println( "hard_reset" );
        System.out.println( "cold_reset" );
        System.out.println( "self_test" );
        System.out.println( "acpi_power_state" );
    }

    private void getAcpiPowerState()
    {
        try
        {
            GetAcpiPowerStateResponseData rd =
                (GetAcpiPowerStateResponseData) connector.sendMessage( handle,
                                                                       new GetAcpiPowerState( IpmiVersion.V20, cs,
                                                                                              AuthenticationType.RMCPPlus,
                                                                                              IpmiCommandParameters.GET_ACPI_POWER_STATE_PARAM ) );
            System.out.println( "System Acpi Power State: " + rd.getSystemAcpiPowerState().toString() );
            System.out.println( "Device Acpi Power State: " + rd.getDeviceAcpiPowerState().toString() );
            System.out.println( "ACPI power State completed successfully." );
        }
        catch ( Exception e )
        {
            System.out.println( "Error while performing ACPI power State" );
            e.printStackTrace();
        }
    }

    private void performSelfTest()
    {
        try
        {
            GetSelfTestResultsResponseData rd =
                (GetSelfTestResultsResponseData) connector.sendMessage( handle,
                                                                        new GetSelfTestResults( IpmiVersion.V20, cs,
                                                                                                AuthenticationType.RMCPPlus,
                                                                                                IpmiCommandParameters.GET_SELF_TEST_RESULTS_PARAM ) );
            System.out.println( "Result Code: " + rd.getSelfTestResultCode() );
            System.out.println( "All Self Test Passed: " + rd.isAllTestPassed() );
        }
        catch ( Exception e )
        {
            System.out.println( "Error while performing Self Test" );
            e.printStackTrace();
        }
    }

    private void hardResetServer()
    {
        try
        {
            ChassisControl chassisControl =
                new ChassisControl( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus, PowerCommand.HardReset );
            ChassisControlResponseData data =
                (ChassisControlResponseData) connector.sendMessage( handle, chassisControl );
            System.out.println( "Performed Hard Reset Node" );
        }
        catch ( Exception e )
        {
            System.out.println( "Error while performing Hard Reset Node" );
            e.printStackTrace();
        }
    }

    private void coldResetBmc()
    {
        try
        {
            ColdResetCommandResponseData rd =
                (ColdResetCommandResponseData) connector.sendMessage( handle,
                                                                      new ColdResetCommand( IpmiVersion.V20, cs,
                                                                                            AuthenticationType.RMCPPlus,
                                                                                            IpmiCommandParameters.COLD_RESET_PARAM ) );
            System.out.println( "Performed Cold Reset" );
        }
        catch ( Exception e )
        {
            System.out.println( "Error while performing Cold Reset: " );
            e.printStackTrace();
        }
    }

    private void powerDownServer()
    {
        try
        {
            ChassisControl chassisControl =
                new ChassisControl( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus, PowerCommand.PowerDown );
            ChassisControlResponseData data =
                (ChassisControlResponseData) connector.sendMessage( handle, chassisControl );
            System.out.println( "Powered Down Node" );
        }
        catch ( Exception e )
        {
            System.out.println( "Error while performing Power Down Node" );
            e.printStackTrace();
        }
    }

    private void powerUpServer()
    {
        try
        {
            ChassisControl chassisControl =
                new ChassisControl( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus, PowerCommand.PowerUp );
            ChassisControlResponseData data =
                (ChassisControlResponseData) connector.sendMessage( handle, chassisControl );
            System.out.println( "Powered Up Node" );
        }
        catch ( Exception e )
        {
            System.out.println( "Error while performing Power Up Node" );
            e.printStackTrace();
        }
    }

    private void powerCycleServer()
    {
        try
        {
            ChassisControl chassisControl =
                new ChassisControl( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus, PowerCommand.PowerCycle );
            ChassisControlResponseData data =
                (ChassisControlResponseData) connector.sendMessage( handle, chassisControl );
            System.out.println( "Powered Cycle Node" );
        }
        catch ( Exception e )
        {
            System.out.println( "Error while performing Power Cycle Node" );
            e.printStackTrace();
        }
    }

    private void getPowerStatus()
    {
        GetChassisStatusResponseData rd;
        try
        {
            rd = (GetChassisStatusResponseData) connector.sendMessage( handle,
                                                                       new GetChassisStatus( IpmiVersion.V20, cs,
                                                                                             AuthenticationType.RMCPPlus ) );
            Boolean status = rd.isPowerOn();
            rd = null;
            System.out.println( "Power Status: " + status );
        }
        catch ( Exception e )
        {
            System.out.println( "Error while performing Power status: " );
            e.printStackTrace();
        }
    }

    /*
     * @Deprecated public void getProperties() { final Path path = Paths.get(configFile); try { if (Files.exists(path,
     * LinkOption.NOFOLLOW_LINKS)) { System.out.println("Loading properties from File Path"); properties.load(new
     * FileInputStream(configFile)); } else { System.out.println("Loading properties from ClassPath");
     * properties.load(this.getClass().getClassLoader().getResourceAsStream(configFile)); } } catch (Exception e) {
     * System.out.println("Received exception while loading Properties file " + e); } }
     */
    public void setupConnection()
    {
        try
        {
            connector = new IpmiConnector( 6000 );
            String ipAddress = null;
            String username = null;
            String password = null;
            int cipherSuiteIndex = 3;
            if ( commandLine.hasOption( "H" ) )
            {
                ipAddress = commandLine.getOptionValue( "H" );
            }
            if ( commandLine.hasOption( "U" ) )
            {
                username = commandLine.getOptionValue( "U" );
            }
            if ( commandLine.hasOption( "P" ) )
            {
                password = commandLine.getOptionValue( "P" );
            }
            if ( commandLine.hasOption( "C" ) )
            {
                cipherSuiteIndex = Integer.parseInt( commandLine.getOptionValue( "C" ) );
            }
            handle = connector.createConnection( InetAddress.getByName( ipAddress ) );
            System.out.println( "Connection created for IP: " + ipAddress );
            cs = connector.getAvailableCipherSuites( handle ).get( cipherSuiteIndex );
            System.out.println( "Cipher suite picked: " + cipherSuiteIndex );
            connector.getChannelAuthenticationCapabilities( handle, cs, PrivilegeLevel.Administrator );
            System.out.println( "Channel authentication capabilities receivied" );
            connector.openSession( handle, username, password, null );
            System.out.println( "Session open" );
            connector.sendMessage( handle,
                                   new SetSessionPrivilegeLevel( IpmiVersion.V20, cs, AuthenticationType.RMCPPlus,
                                                                 PrivilegeLevel.Administrator ) );
        }
        catch ( Exception e )
        {
            if ( handle != null )
            {
                try
                {
                    connector.closeSession( handle );
                }
                catch ( Exception e1 )
                {
                    e1.printStackTrace();
                }
                System.out.println( "Session closed" );
            }
            if ( connector != null )
            {
                connector.tearDown();
                System.out.println( "Connection manager closed" );
            }
        }
    }

    private void destroy()
    {
        if ( handle != null )
        {
            try
            {
                connector.closeSession( handle );
            }
            catch ( Exception e1 )
            {
                e1.printStackTrace();
            }
            System.out.println( "Session closed" );
        }
        if ( connector != null )
        {
            connector.tearDown();
            System.out.println( "Connection manager closed" );
        }
    }

    private boolean isValid( String str )
    {
        if ( str != null )
        {
            boolean result = false;
            switch ( str )
            {
                case "power_status":
                case "power_cycle":
                case "power_up":
                case "power_down":
                case "hard_reset":
                case "cold_reset":
                case "self_test":
                case "acpi_power_state":
                    result = true;
                    break;
            }
            return result;
        }
        return false;
    }
}
