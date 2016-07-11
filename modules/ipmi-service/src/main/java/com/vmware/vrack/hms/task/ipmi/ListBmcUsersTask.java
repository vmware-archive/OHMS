/* ********************************************************************************
 * ListBmcUsersTask.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.task.ipmi;

import java.util.ArrayList;
import java.util.List;

import com.veraxsystems.vxipmi.common.TypeConverter;
import com.vmware.vrack.coding.commands.transport.GetChannelInfoCommand;
import com.vmware.vrack.coding.commands.transport.GetChannelInfoCommandResponseData;
import org.apache.log4j.Logger;

import com.veraxsystems.vxipmi.coding.commands.IpmiVersion;
import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.commands.session.SetSessionPrivilegeLevel;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.vmware.vrack.coding.commands.IpmiCommandParameters;
import com.vmware.vrack.coding.commands.application.GetUserAccessCommand;
import com.vmware.vrack.coding.commands.application.GetUserAccessCommandResponseData;
import com.vmware.vrack.coding.commands.application.GetUserNameViaOob;
import com.vmware.vrack.coding.commands.application.GetUserNameViaOobResponseData;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.ipmiservice.exception.IpmiServiceResponseException;

/**
 * This will populate the BMC user list, based on the maximum number of enabled users for that bmc
 * 
 * @author Vmware
 */
public class ListBmcUsersTask
{
    private static Logger logger = Logger.getLogger( ListBmcUsersTask.class );

    private final String NULL_CONNECTOR_EXCEPTION_MSG =
        "Error in List Bmc Users Task because Node is [ %s ], node is %san instance of ServerNode, and IPMI Connector is [ %s ]";

    public ServiceServerNode node;

    IpmiTaskConnector connector = null;

    private final String INVALID_USER_ID_ERROR_MESSAGE = "Invalid data field in Request.";

    public ListBmcUsersTask( ServiceHmsNode node, IpmiTaskConnector connector )
    {
        this( node );
        this.connector = connector;
    }

    public ListBmcUsersTask( ServiceHmsNode node )
    {
        this.node = (ServiceServerNode) node;
    }

    public List<BmcUser> executeTask()
        throws Exception, IpmiServiceResponseException
    {
        if ( node instanceof ServiceServerNode && connector != null )
        {
            logger.debug( "Received request to execute ipmi ListBmcUsers task for Node " + node.getNodeID() );
            try
            {
                connector.getConnector().sendMessage( connector.getHandle(),
                                                      new SetSessionPrivilegeLevel( IpmiVersion.V20,
                                                                                    connector.getCipherSuite(),
                                                                                    AuthenticationType.RMCPPlus,
                                                                                    PrivilegeLevel.Administrator ) );
                int channelNumber = -1;
                byte[] getBmcUserParams = IpmiCommandParameters.GET_USER_ACCESS_COMMAND_FOR_USER_1_PARAM;
                // First send the Get Channel Info command to get the number of the channel we are using for IPMI over
                // LAN
                GetChannelInfoCommandResponseData cn =
                    (GetChannelInfoCommandResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                              new GetChannelInfoCommand( IpmiVersion.V20,
                                                                                                                         connector.getCipherSuite(),
                                                                                                                         AuthenticationType.RMCPPlus,
                                                                                                                         IpmiCommandParameters.GET_CHANNEL_INFO_CURRENT_CHANNEL_PARAMETER ) );
                channelNumber = cn.getChannelNumber();
                // Specify the channel number as aparameter to the Get MAC Address command
                getBmcUserParams[0] = TypeConverter.intToByte( channelNumber );
                List<BmcUser> userList = new ArrayList<BmcUser>();
                // Querying for Max number of Users , Enabled user's count
                GetUserAccessCommandResponseData userAccessResponseData =
                    (GetUserAccessCommandResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                             new GetUserAccessCommand( IpmiVersion.V20,
                                                                                                                       connector.getCipherSuite(),
                                                                                                                       AuthenticationType.RMCPPlus,
                                                                                                                       getBmcUserParams ) );
                // gets the enabled user count
                int enabledUsersCount = userAccessResponseData.getCurrentlyEnabledUsersCount();
                int maxUsersCount = userAccessResponseData.getMaxUserId();
                // BMC also includes USER-1 in enabled User Count, but actually USER-1(User ID as 1) is reserved
                // and has no name, So subtracting 1 from enabled users count to exclude USER-1
                // int enabledUsersCountWithoutUser1 = enabledUsersCount - 1;
                int foundUsersCount = 0;
                GetUserNameViaOobResponseData rd = null;
                // first user is reserved according to specification, so started loop from second ID
                // also, the foundUsersCount should check for EnabledUsers Count without User 1
                try
                {
                    for ( int i = 2; i <= maxUsersCount && foundUsersCount != enabledUsersCount; i++ )
                    {
                        try
                        {
                            byte[] userId = { (byte) i };
                            rd = (GetUserNameViaOobResponseData) connector.getConnector().sendMessage( connector.getHandle(),
                                                                                                       new GetUserNameViaOob( IpmiVersion.V20,
                                                                                                                              connector.getCipherSuite(),
                                                                                                                              AuthenticationType.RMCPPlus,
                                                                                                                              userId ) );
                            if ( rd != null && !"".equals( rd.getUserName() ) )
                            {
                                BmcUser bmcUser = new BmcUser();
                                bmcUser.setUserId( i );
                                bmcUser.setUserName( rd.getUserName() );
                                userList.add( bmcUser );
                            }
                        }
                        catch ( Exception e )
                        {
                            if ( e instanceof IPMIException
                                && e.getMessage().contains( INVALID_USER_ID_ERROR_MESSAGE ) )
                            {
                                logger.debug( "User with userId [ " + i + " ] not present for node [ "
                                    + node.getNodeID() + " ]", e );
                            }
                            else
                            {
                                logger.error( "Exception while searching BMC user with user id [ " + i
                                    + " ] for Node [ " + node.getNodeID() + " ]", e );
                            }
                        }
                        finally
                        {
                            if ( rd != null && !"".equals( rd.getUserName() ) )
                            {
                                foundUsersCount++;
                            }
                        }
                    }
                    // node.setBmcUserList(userList);
                }
                catch ( Exception e )
                {
                    String err = "Exception while searching for BMC users on node [ " + node.getNodeID() + " ]";
                    logger.error( err, e );
                    throw new HmsException( err, e );
                }
                return userList;
            }
            catch ( IPMIException e )
            {
                logger.error( "Exception while executing the task ListBmcUsers: " + e.getCompletionCode() + ":"
                    + e.getMessage() );
                logger.debug( e.getCompletionCode() + ":" + e.getMessage() );
                throw new IpmiServiceResponseException( e.getCompletionCode() );
            }
        }
        else
        {
            String err = String.format( NULL_CONNECTOR_EXCEPTION_MSG, node,
                                        ( node instanceof ServiceServerNode ) ? "" : "NOT ", connector );
            logger.error( err );
            throw new IllegalArgumentException( err );
        }
    }
}
