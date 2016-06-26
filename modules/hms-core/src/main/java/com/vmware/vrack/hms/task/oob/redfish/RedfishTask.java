package com.vmware.vrack.hms.task.oob.redfish;

import com.vmware.vrack.hms.common.ExternalService;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.task.IHmsTask;

public abstract class RedfishTask
    implements IHmsTask
{
    private ExternalService externalService;

    @Override
    public TaskResponse call()
        throws Exception
    {
        executeTask();
        return null;
    }

    public ExternalService getExternalService()
    {
        return externalService;
    }

    public void setExternalService( ExternalService externalService )
    {
        this.externalService = externalService;
    }
}
