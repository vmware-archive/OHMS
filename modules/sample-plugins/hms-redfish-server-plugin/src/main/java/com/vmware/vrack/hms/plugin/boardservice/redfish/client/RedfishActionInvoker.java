package com.vmware.vrack.hms.plugin.boardservice.redfish.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource.Actions.ResetType;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource.Boot;

import java.net.URI;

import static com.vmware.vrack.hms.plugin.boardservice.redfish.client.UriHelper.toAbsoluteUri;

public class RedfishActionInvoker
    implements AutoCloseable
{
    private final IRedfishWebClient client;

    public RedfishActionInvoker()
    {
        client = createRedfishClient();
    }

    protected IRedfishWebClient createRedfishClient()
    {
        return new RedfishWebClient();
    }

    public void invokeResetAction( ComputerSystemResource system, ResetType resetType )
        throws RedfishClientException
    {
        URI resetActionTarget = toAbsoluteUri( system.getOrigin(), system.getResetActionTarget() );

        client.post( resetActionTarget, new ResetActionJson( resetType ) );
    }

    public void invokeSetBootOptionsAction( ComputerSystemResource system, Boot bootOptions )
        throws RedfishClientException
    {
        URI actionTarget = system.getOrigin();

        client.patch( actionTarget, new SetBootOptionsActionJson( bootOptions ) );
    }

    @Override
    public void close()
    {
        client.close();
    }

    static class SetBootOptionsActionJson
    {
        @JsonProperty( "Boot" )
        private Boot boot;

        public SetBootOptionsActionJson( Boot boot )
        {
            this.boot = boot;
        }

        @Override
        public String toString()
        {
            return "SetBootOptionsActionJson{" + "boot=" + boot + '}';
        }
    }

    static class ResetActionJson
    {
        @JsonProperty( "ResetType" )
        private ResetType resetType;

        public ResetActionJson(
            ResetType resetType )
        {
            this.resetType = resetType;
        }

        @Override
        public String toString()
        {
            return "ResetActionJson{resetType=" + resetType + '}';
        }
    }
}
