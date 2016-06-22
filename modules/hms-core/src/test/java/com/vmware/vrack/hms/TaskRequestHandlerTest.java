/* ********************************************************************************
 * TaskRequestHandlerTest.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms;

import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

import org.junit.Ignore;

import com.vmware.vrack.hms.task.TaskType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tanvishah
 */
@Ignore
public class TaskRequestHandlerTest
{
    public TaskRequestHandlerTest()
    {
    }

    /**
     * Test of getInstance method, of class TaskRequestHandler.
     */
    @Test
    public void testGetInstance()
    {
        System.out.println( "getInstance" );
        TaskRequestHandler result = TaskRequestHandler.getInstance();
        assertNotNull( result );
    }

    /**
     * Test of executeTask method, of class TaskRequestHandler where task type is PowerUpServer.
     */
    @Test
    @Ignore
    public void testExecutePowerUpTask()
        throws Exception
    {
        System.out.println( "executeTask" );
        TaskResponse obj = new TaskResponse( new ServerNode( "3", "10.28.197.204", "ADMIN", "ADMIN" ) );
        TaskRequestHandler instance = TaskRequestHandler.getInstance();
        instance.executeServerTask( TaskType.PowerUpServer, obj );
        // obj.node.refreshNodeStatus();
        assertTrue( obj.getNode().isPowered() );
    }

    /**
     * Test of executeTask method, of class TaskRequestHandler where task type is set to Null.
     */
    @Test( expected = NullPointerException.class )
    public void testExecuteTaskWhenTaskTypeIsSetToNull()
        throws Exception
    {
        System.out.println( "executeTask" );
        TaskResponse obj = new TaskResponse( new ServerNode( "3", "10.28.197.204", "ADMIN", "ADMIN" ) );
        TaskRequestHandler instance = TaskRequestHandler.getInstance();
        instance.executeServerTask( null, obj );
    }

    /**
     * Test of destroy method, of class TaskRequestHandler.
     */
    @Test
    public void testDestroy()
    {
        System.out.println( "destroy" );
        TaskRequestHandler result = TaskRequestHandler.getInstance();
        result.destroy();
    }
}
