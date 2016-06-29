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

package com.vmware.vrack.hms.plugin.boardservice.redfish.discovery;

import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ComputerSystemResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.MemoryResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.EthernetInterfaceResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ManagerResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.OdataId;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.ProcessorResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.RedfishCollection;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.RedfishResource;
import com.vmware.vrack.hms.plugin.boardservice.redfish.resources.SimpleStorageResource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.vmware.vrack.hms.plugin.boardservice.redfish.client.UriHelper.toAbsoluteUri;

public class InventoryTraverser
{
    private final RedfishResourcesInventory inventory;

    public InventoryTraverser( RedfishResourcesInventory redfishInventory )
    {
        this.inventory = redfishInventory;
    }

    public List<ProcessorResource> getProcessors( ComputerSystemResource computerSystem )
        throws RedfishResourcesInventoryException
    {
        return getCollection( computerSystem.getOrigin(), computerSystem.getProcessors() );
    }

    public List<MemoryResource> getMemory( ComputerSystemResource computerSystem )
        throws RedfishResourcesInventoryException
    {
        return getCollection( computerSystem.getOrigin(), computerSystem.getMemory() );
    }

    public List<SimpleStorageResource> getSimpleStorages( ComputerSystemResource computerSystem )
        throws RedfishResourcesInventoryException
    {
        return getCollection( computerSystem.getOrigin(), computerSystem.getSimpleStorages() );
    }

    public List<EthernetInterfaceResource> getEthernetInterfaces( ComputerSystemResource computerSystem )
        throws RedfishResourcesInventoryException
    {
        return getCollection( computerSystem.getOrigin(), computerSystem.getEthernetInterfaces() );
    }

    public List<EthernetInterfaceResource> getEthernetInterfaces( ManagerResource manager )
        throws RedfishResourcesInventoryException
    {
        return getCollection( manager.getOrigin(), manager.getEthernetInterfaces() );
    }

    public List<ManagerResource> getManagers( ComputerSystemResource computerSystem )
        throws RedfishResourcesInventoryException
    {
        return asResources( computerSystem.getOrigin(), computerSystem.getManagedBy() );
    }

    private <T extends RedfishResource> List<T> getCollection( URI origin, OdataId collectionUri )
        throws RedfishResourcesInventoryException
    {
        URI targetUri = toAbsoluteUri( origin, collectionUri );
        RedfishCollection collection = inventory.getResourceByURI( targetUri );

        List<OdataId> members = collection.getMembers();
        URI collectionOrigin = collection.getOrigin();
        return asResources( collectionOrigin, members );
    }

    private <T extends RedfishResource> List<T> asResources( URI collectionOrigin, List<OdataId> members )
        throws RedfishResourcesInventoryException
    {
        List<T> resources = new ArrayList<>( members.size() );
        for ( OdataId odataId : members )
        {

            URI resourceUri = toAbsoluteUri( collectionOrigin, odataId );
            T resource = inventory.getResourceByURI( resourceUri );
            resources.add( resource );
        }
        return resources;
    }
}
