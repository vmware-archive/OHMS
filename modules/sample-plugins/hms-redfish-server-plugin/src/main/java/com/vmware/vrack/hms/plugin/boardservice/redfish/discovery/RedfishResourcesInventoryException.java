package com.vmware.vrack.hms.plugin.boardservice.redfish.discovery;

public class RedfishResourcesInventoryException
    extends Exception
{
    public RedfishResourcesInventoryException( String message )
    {
        super( message );
    }

    public RedfishResourcesInventoryException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
