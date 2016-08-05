package com.vmware.vrack.coding.commands.application;

/**
 * Contains different Device Power states
 * 
 * @author Yagnesh Chawda
 */
public enum DevicePowerState
{
    D0( "D0" ),
    D1( "D1" ),
    D2( "D2" ),
    D3( "D3" ),
    Unknown( "power state has not been initialized, or device lost track of power state." );
    private String value;

    private DevicePowerState( String value )
    {
        this.value = value;
    }

    public String getStatusMessage()
    {
        return value;
    }
}
