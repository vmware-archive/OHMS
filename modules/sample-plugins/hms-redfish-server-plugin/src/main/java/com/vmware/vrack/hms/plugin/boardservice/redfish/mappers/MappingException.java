package com.vmware.vrack.hms.plugin.boardservice.redfish.mappers;

public class MappingException
    extends Exception
{
    public MappingException( String message )
    {
        super( message );
    }

    public MappingException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
