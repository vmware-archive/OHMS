package com.vmware.vrack.hms.common.boardvendorservice.api;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;

import java.net.URI;
import java.util.List;

public interface IRedfishService
    extends IBoardService
{
    /**
     * Gets all Redfish ComputerSystems handled by this board service
     *
     * @return
     */
    List<ServiceHmsNode> getNodesForComputerSystems( URI serviceEndpoint )
        throws HmsException;
}
