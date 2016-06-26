/* ********************************************************************************
 * TaskFactory.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task;

import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;
import com.vmware.vrack.hms.task.oob.ipmi.AcpiPowerStateTask;
import com.vmware.vrack.hms.task.oob.ipmi.ChassisIdentifyTask;
import com.vmware.vrack.hms.task.oob.ipmi.ColdResetBmcTask;
import com.vmware.vrack.hms.task.oob.ipmi.FindMacAddressTask;
import com.vmware.vrack.hms.task.oob.ipmi.GetSupportedAPI;
import com.vmware.vrack.hms.task.oob.ipmi.GetSystemBootOptionsTask;
import com.vmware.vrack.hms.task.oob.ipmi.ListBmcUsersTask;
import com.vmware.vrack.hms.task.oob.ipmi.PowerCycleServerTask;
import com.vmware.vrack.hms.task.oob.ipmi.PowerDownServerTask;
import com.vmware.vrack.hms.task.oob.ipmi.PowerResetServerTask;
import com.vmware.vrack.hms.task.oob.ipmi.PowerStatusServerTask;
import com.vmware.vrack.hms.task.oob.ipmi.PowerUpServerTask;
import com.vmware.vrack.hms.task.oob.ipmi.SelInfoTask;
import com.vmware.vrack.hms.task.oob.ipmi.SelfTestTask;
import com.vmware.vrack.hms.task.oob.ipmi.ServerInfoServerTask;
import com.vmware.vrack.hms.task.oob.ipmi.SetSystemBootOptionsTask;
import com.vmware.vrack.hms.task.oob.redfish.RedfishDiscoverComputerSystemsTask;
import com.vmware.vrack.hms.task.oob.rmm.CpuInformationTask;
import com.vmware.vrack.hms.task.oob.rmm.HddInformationTask;
import com.vmware.vrack.hms.task.oob.rmm.MemoryInformationTask;
import com.vmware.vrack.hms.task.oob.rmm.NicInfoTask;
import com.vmware.vrack.hms.task.oob.rmm.StorageControllerInformationTask;

public class TaskFactory
{

    public static IHmsTask getTask( TaskType taskType, TaskResponse response )
    {
        IHmsTask task = null;
        switch ( taskType )
        {
            case HMSBootUp:
                if ( response.getNode() instanceof ServerNode )
                    task = new BootTaskSuite( response );
                else if ( response.getNode() instanceof HMSSwitchNode )
                    task = new SwitchBootTaskSuite( response );
                break;
            case PowerStatusServer:
                task = new PowerStatusServerTask( response );
                break;
            case MacAddressDiscovery:
                task = new FindMacAddressTask( response );
                break;
            case ServerBoardInfo:
                task = new ServerInfoServerTask( response );
                break;
            case InitMonitorService:
                if ( response.getNode() instanceof HMSSwitchNode )
                    task = new SwitchMonitorTask( (HMSSwitchNode) response.getNode() );
                break;
            case PowerDownServer:
                task = new PowerDownServerTask( response );
                break;
            case PowerResetServer:
                task = new PowerResetServerTask( response );
                break;
            case PowerUpServer:
                task = new PowerUpServerTask( response );
                break;
            case ListBmcUsers:
                task = new ListBmcUsersTask( response );
                break;
            case PowerCycleServer:
                task = new PowerCycleServerTask( response );
                break;
            case ColdResetBmc:
                task = new ColdResetBmcTask( response );
                break;
            case SelfTest:
                task = new SelfTestTask( response );
                break;
            case AcpiPowerState:
                task = new AcpiPowerStateTask( response );
                break;
            case NicInfo:
                task = new NicInfoTask( response );
                break;
            case RmmDimmInfo:
                task = new MemoryInformationTask( response );
                break;
            case RmmCPUInfo:
                task = new CpuInformationTask( response );
                break;
            case GetSystemBootOptions:
                task = new GetSystemBootOptionsTask( response );
                break;
            case HDDInfo:
                task = new HddInformationTask( response );
                break;
            case StorageControllerInfo:
                task = new StorageControllerInformationTask( response );
                break;
            case SwitchMonitorService:
                if ( response.getNode() instanceof HMSSwitchNode )
                {
                    task = new SwitchMonitorTask( (HMSSwitchNode) response.getNode() );
                }
                break;
            case GetSupportedAPI:
                task = new GetSupportedAPI( response );
                break;
            case RedfishDiscoverComputerSystems:
                task = new RedfishDiscoverComputerSystemsTask();
                break;
            default:
                return task;
        }
        return task;
    }

    // IPMI commands can sometime need to send some prepared byte array along with the command.
    public static IHmsTask getTask( TaskType taskType, TaskResponse response, Object data )
    {
        IHmsTask task = null;
        switch ( taskType )
        {
            case SetSystemBootOptions:
                task = new SetSystemBootOptionsTask( response, data );
                break;
            case ChassisIdentify:
                task = new ChassisIdentifyTask( response, data );
                break;
            case SelInfo:
                task = new SelInfoTask( response, data );
                break;
            default:
                return task;
        }
        return task;
    }
}
