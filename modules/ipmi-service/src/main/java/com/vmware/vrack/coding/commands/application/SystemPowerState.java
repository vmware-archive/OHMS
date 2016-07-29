package com.vmware.vrack.coding.commands.application;

/**
 * Contains different System Power States Code and the code explanation
 * 
 * @author Yagnesh Chawda
 */
public enum SystemPowerState
{
    S0_G0( "Working" ),
    S1( "hardware context maintained, processor/chip set clocks stopped" ),
    S2( "stopped clocks with processor/cache context lost" ),
    S1_S2( "CPU powered off" ),
    S3( "suspend-to-RAM" ),
    S4( "suspend-to-disk" ),
    S5_G2( "soft off" ),
    S4_S5( "soft off, cannot differentiate between S4 and S5" ),
    G3( "mechanical off" ),
    Sleeping( "sleeping - cannot differentiate between S1-S3." ),
    G1_sleeping( "Sleeping - cannot differentiate between S1-S4" ),
    Override( "S5 entered by override" ),
    Legacy_On( "Legacy On" ),
    Legacy_Off( "Legacy Off" ),
    Unknown( "power state has not been initialized, or device lost track of power state." );
    private String value;

    private SystemPowerState( String value )
    {
        this.value = value;
    }

    public String getStatusMessage()
    {
        return value;
    }
}
