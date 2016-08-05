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

import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.RedfishResource;

import java.net.URI;

public interface IRedfishWebClient
    extends AutoCloseable
{
    RedfishResource get( URI targetUri )
        throws RedfishClientException;

    <T> void post( URI targetUri, T body )
        throws RedfishClientException;

    <T> void patch( URI targetUri, T body )
        throws RedfishClientException;

    @Override
    void close();
}
