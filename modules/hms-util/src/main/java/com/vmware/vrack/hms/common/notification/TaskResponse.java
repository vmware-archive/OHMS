/* ********************************************************************************
 * TaskResponse.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.notification;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.monitoring.ITaskResponseLifecycleHandler;

@JsonIgnoreProperties( ignoreUnknown = true )
@Deprecated
public class TaskResponse
{
    protected HmsNode node;

    protected long timeStamp;

    protected String taskType;

    protected NodeActionStatus status;

    protected String callbackEndpoint;

    protected ITaskResponseLifecycleHandler taskHandler;

    public TaskResponse()
    {
        super();
        this.timeStamp = ( new Date() ).getTime();
        this.status = NodeActionStatus.INVALID;
    }

    public TaskResponse( HmsNode node )
    {
        this();
        this.node = node;
    }

    public TaskResponse( HmsNode node, String taskType )
    {
        this( node );
        this.taskType = taskType;
    }

    public TaskResponse( HmsNode node, String taskType, String callbackEndpoint )
    {
        this( node, taskType );
        this.callbackEndpoint = callbackEndpoint;
    }

    public TaskResponse( HmsNode node, String taskType, String callbackEndpoint,
                         ITaskResponseLifecycleHandler taskHandler )
    {
        this( node, taskType, callbackEndpoint );
        if ( taskHandler != null )
        {
            this.taskHandler = taskHandler;
            taskHandler.init( this );
        }
    }

    @Override
    public int hashCode()
    {
        int hash = new Long( timeStamp ).hashCode();
        if ( node.getNodeID() != null )
        {
            hash = 31 * hash + node.getNodeID().hashCode();
        }
        if ( taskType != null )
        {
            hash = 31 * hash + taskType.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }
        TaskResponse compareTo = (TaskResponse) obj;
        if ( compareTo.timeStamp != this.timeStamp )
        {
            return false;
        }
        // We must have node object and nodeId in it being NOT null
        if ( !( compareTo.node != null && this.node != null && compareTo.node.getNodeID() != null
            && compareTo.node.getNodeID().equals( this.node.getNodeID() ) ) )
        {
            return false;
        }
        if ( !( compareTo.taskType.equals( this.taskType ) ) )
        {
            return false;
        }
        return true;
    }

    public HmsNode getNode()
    {
        return node;
    }

    public void setNode( HmsNode node )
    {
        this.node = node;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp( long timeStamp )
    {
        this.timeStamp = timeStamp;
    }

    public String getTaskType()
    {
        return taskType;
    }

    public void setTaskType( String taskType )
    {
        this.taskType = taskType;
    }

    public NodeActionStatus getStatus()
    {
        return status;
    }

    public void setStatus( NodeActionStatus status )
    {
        this.status = status;
    }

    @JsonIgnore
    public String getCallbackEndpoint()
    {
        return callbackEndpoint;
    }

    public void setCallbackEndpoint( String callbackEndpoint )
    {
        this.callbackEndpoint = callbackEndpoint;
    }

    @JsonIgnore
    public void processTaskCompletion()
    {
        if ( taskHandler != null )
            taskHandler.onTaskComplete( this );
    }

    @JsonIgnore
    public ITaskResponseLifecycleHandler getTaskHandler()
    {
        return taskHandler;
    }

    public void setTaskHandler( ITaskResponseLifecycleHandler taskHandler )
    {
        this.taskHandler = taskHandler;
    }
}
