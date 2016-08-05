/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
