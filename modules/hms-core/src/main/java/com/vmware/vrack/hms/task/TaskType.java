/* ********************************************************************************
 * TaskType.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task;

public enum TaskType
{
    HMSBootUp,
    DiscoverServer,
    PowerStatusServer,
    PowerDownServer,
    PowerResetServer,
    PowerUpServer,
    ValidateServerOS,
    DiscoverSwitch,
    PowerStatusSwitch,
    MacAddressDiscovery,
    ServerBoardInfo,
    InitMonitorService,
    ListBmcUsers,
    PowerCycleServer,
    ColdResetBmc,
    SelfTest,
    AcpiPowerState,
    HMSResourceMonitor,
    FruInfo,
    NicInfo,
    RmmCPUInfo,
    RmmDimmInfo,
    GetSystemBootOptions,
    SetSystemBootOptions,
    ChassisIdentify,
    HDDInfo,
    StorageControllerInfo,
    SelInfo,
    SwitchMonitorService,
    GetSupportedAPI
}
