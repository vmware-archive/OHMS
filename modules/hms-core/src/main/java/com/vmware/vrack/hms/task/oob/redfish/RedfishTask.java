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
