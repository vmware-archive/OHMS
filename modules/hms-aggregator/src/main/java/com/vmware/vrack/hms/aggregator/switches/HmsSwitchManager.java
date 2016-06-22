/* ********************************************************************************
 * HmsSwitchManager.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator.switches;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.resource.SwitchList;
import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchBgpConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchLagConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchMcLagConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchNodeIPs;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchOspfv2Config;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchPortConfig;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchPortInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchSensorInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchVlanConfig;
import com.vmware.vrack.hms.common.rest.model.switches.bulk.NBSwitchBulkConfig;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.switches.adapters.SwitchBgpConfigDisassemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchBulkConfigDisassemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchInfoAssemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchLagConfigAssemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchLagConfigDisassemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchMcLagConfigDisassemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchOspfv2ConfigAssemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchOspfv2ConfigDisassemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchPortConfigDisassemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchPortInfoAssemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchVlanConfigAssemblers;
import com.vmware.vrack.hms.common.switches.adapters.SwitchVlanConfigDisassemblers;
import com.vmware.vrack.hms.common.switches.api.SwitchBgpConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchLacpGroup;
import com.vmware.vrack.hms.common.switches.api.SwitchMclagInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchOspfConfig;
import com.vmware.vrack.hms.common.switches.api.SwitchPort;
import com.vmware.vrack.hms.common.switches.api.SwitchVlan;
import com.vmware.vrack.hms.inventory.SwitchDataChangeMessage;
import com.vmware.vrack.hms.inventory.SwitchPortsConfigChangeMessage;

@Component( "com.vmware.vrack.hms.aggregator.switches.HmsSwitchManager" )
public class HmsSwitchManager
{
    private static Logger logger = Logger.getLogger( HmsSwitchManager.class );

    @Autowired
    private HmsSwitchOobManager hmsSwitchOobManager;

    @Autowired
    private ApplicationContext context;

    private static ExecutorService service = Executors.newFixedThreadPool( 6 );

    public NBSwitchInfo getSwitchInfo( String switchId )
        throws HMSRestException
    {
        NBSwitchInfo lInfo = null;
        NBSwitchConfig lConfig = new NBSwitchConfig();
        SwitchInfo info = hmsSwitchOobManager.parseGetResponse( new TypeReference<SwitchInfo>()
        {
        }, String.format( "/%s", switchId ) );
        /* get first level of values */
        if ( info.isPowered() )
        {
            lInfo = SwitchInfoAssemblers.toSwitchInfo( info );
            lInfo.setPorts( getSwitchAllPortInfos( switchId ) );
            lInfo.setSensors( getSwitchSensorInfo( switchId ) );
            lConfig.setOspf( getSwitchOspfv2Config( switchId ) );
            lConfig.setBonds( getSwitchAllLagsConfigs( switchId ) );
            lConfig.setVlans( getSwitchAllVlansConfigs( switchId ) );
            lConfig.setBgp( getSwitchBgpConfig( switchId ) );
            lInfo.setConfig( lConfig );
        }
        else
        {
            lInfo = new NBSwitchInfo();
            lInfo.setAdminStatus( NodeAdminStatus.OPERATIONAL );
            lInfo.setOperationalStatus( FruOperationalStatus.NonOperational );
            lInfo.setSwitchId( switchId );
        }
        return lInfo;
    }

    private NBSwitchSensorInfo getSwitchSensorInfo( String switchId )
        throws HMSRestException
    {
        return null; // TODO: This is not implemented in switch plugin
        // controller
    }

    public NBSwitchBgpConfig getSwitchBgpConfig( String switchId )
        throws HMSRestException
    {
        return null; // TODO: This is not implemented in switch plugin
        // controller
    }

    public BaseResponse createOrUpdateSwitchBgpConfig( String switchId, NBSwitchBgpConfig config )
        throws HMSRestException
    {
        if ( config == null )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Syntax Error",
                                        "Cannot perform operation with NULL object" );
        }
        return hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, SwitchBgpConfigDisassemblers.fromSwitchBgpConfig( config ), String.format( "/%s/bgp", switchId ) );
    }

    public BaseResponse deleteSwitchBgpConfig( String switchId )
        throws HMSRestException
    {
        SwitchBgpConfig config = new SwitchBgpConfig();
        BaseResponse response = null;
        /* We do not support delete on BGP on OOB agent hence the hack */
        config.setEnabled( false );
        response = hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, config, String.format( "/%s/bgp", switchId ) );
        if ( response.getStatusCode() == HttpStatus.OK.value() )
        {
            response.setStatusMessage( String.format( "Deleted BGP Configuration on switch %s successfully",
                                                      switchId ) );
        }
        else
        {
            response.setErrorMessage( String.format( "Failed to delete BGP Configuration on switch %s", switchId ) );
        }
        return response;
    }

    public NBSwitchOspfv2Config getSwitchOspfv2Config( String switchId )
        throws HMSRestException
    {
        SwitchOspfConfig ospf = hmsSwitchOobManager.parseGetResponse( new TypeReference<SwitchOspfConfig>()
        {
        }, String.format( "/%s/ospf", switchId ) );
        return SwitchOspfv2ConfigAssemblers.toSwitchOspfv2Config( ospf );
    }

    public BaseResponse createOrUpdateSwitchOspfv2Config( String switchId, NBSwitchOspfv2Config config )
        throws HMSRestException
    {
        if ( config == null )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Syntax Error",
                                        "Cannot perform operation with NULL object" );
        }
        return hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, SwitchOspfv2ConfigDisassemblers.fromSwitchOspfv2Config( config ), String.format( "/%s/ospf", switchId ) );
    }

    public BaseResponse deleteSwitchOspfv2Config( String switchId )
        throws HMSRestException
    {
        SwitchOspfConfig config = new SwitchOspfConfig();
        BaseResponse response = null;
        /* We do not support delete on OSPFv2 on OOB agent hence the hack */
        config.setEnabled( false );
        response = hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, config, String.format( "/%s/ospf", switchId ) );
        if ( response.getStatusCode() == HttpStatus.OK.value() )
        {
            response.setStatusMessage( String.format( "Deleted OSPFv2 Configuration on switch %s successfully",
                                                      switchId ) );
        }
        else
        {
            response.setErrorMessage( String.format( "Failed to delete OSPFv2 Configuration on switch %s", switchId ) );
        }
        return response;
    }

    public NBSwitchMcLagConfig getSwitchMcLagConfig( String switchId )
        throws HMSRestException
    {
        throw new NotImplementedException( "ISwitchService does not declare any method to get MC-LAG configuration" );
    }

    public BaseResponse createOrUpdateSwitchMcLagConfig( String switchId, NBSwitchMcLagConfig config )
        throws HMSRestException
    {
        if ( config == null )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Syntax Error",
                                        "Cannot perform operation with NULL object" );
        }
        return hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, SwitchMcLagConfigDisassemblers.fromSwitchMcLagConfig( config ), String.format( "/%s/mclag", switchId ) );
    }

    public BaseResponse deleteSwitchMcLagConfig( String switchId )
        throws HMSRestException
    {
        BaseResponse response = null;
        SwitchMclagInfo config = new SwitchMclagInfo();
        /* We do not support delete on MC-LAG on OOB agent hence the hack */
        config.setEnabled( false );
        response = hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, config, String.format( "/%s/mclag", switchId ) );
        if ( response.getStatusCode() == HttpStatus.OK.value() )
        {
            response.setStatusMessage( String.format( "Deleted MC-LAG Configuration on switch %s successfully",
                                                      switchId ) );
        }
        else
        {
            response.setErrorMessage( String.format( "Failed to delete MC-LAG Configuration on switch %s", switchId ) );
        }
        return response;
    }

    public NBSwitchLagConfig getSwitchLagConfig( String switchId, String lagId )
        throws HMSRestException
    {
        SwitchLacpGroup lag = hmsSwitchOobManager.parseGetResponse( new TypeReference<SwitchLacpGroup>()
        {
        }, String.format( "/%s/lacpgroups/%s", switchId, lagId ) );
        return SwitchLagConfigAssemblers.toSwitchLagConfig( lag );
    }

    public BaseResponse createOrUpdateSwitchLagConfig( String switchId, NBSwitchLagConfig config )
        throws HMSRestException
    {
        if ( config == null )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Syntax Error",
                                        "Cannot perform operation with NULL object" );
        }
        return hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, SwitchLagConfigDisassemblers.fromSwitchLagConfig( config ), String.format( "/%s/lacpgroups", switchId ) );
    }

    public BaseResponse deleteSwitchLagConfig( String switchId, String lagId )
        throws HMSRestException
    {
        return hmsSwitchOobManager.parseDeleteResponse( new TypeReference<BaseResponse>()
        {
        }, String.format( "/%s/lacpgroups/%s", switchId, lagId ) );
    }

    public List<NBSwitchLagConfig> getSwitchAllLagsConfigs( String switchId )
        throws HMSRestException
    {
        List<String> lagIds = null;
        List<SwitchLacpGroup> lags = new ArrayList<SwitchLacpGroup>();
        /* Retrieve multiple element array responses */
        lagIds = hmsSwitchOobManager.parseGetListResponse( String.format( "/%s/lacpgroups", switchId ), String.class );
        /* Get all LAG details for all LAG IDs retrieved earlier */
        if ( lagIds != null )
        {
            for ( String lagId : lagIds )
            {
                SwitchLacpGroup lag = hmsSwitchOobManager.parseGetResponse( new TypeReference<SwitchLacpGroup>()
                {
                }, String.format( "/%s/lacpgroups/%s", switchId, lagId ) );
                lags.add( lag );
            }
        }
        return SwitchLagConfigAssemblers.toSwitchLagConfigs( lags );
    }

    public NBSwitchVlanConfig getSwitchVlanConfig( String switchId, String vlanId )
        throws HMSRestException
    {
        SwitchVlan vlan = hmsSwitchOobManager.parseGetResponse( new TypeReference<SwitchVlan>()
        {
        }, String.format( "/%s/vlans/%s", switchId, vlanId ) );
        return SwitchVlanConfigAssemblers.toSwitchVlanConfig( vlan );
    }

    public BaseResponse createOrUpdateSwitchVlanConfig( String switchId, NBSwitchVlanConfig config )
        throws HMSRestException
    {
        if ( config == null )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Syntax Error",
                                        "Cannot perform operation with NULL object" );
        }
        return hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, SwitchVlanConfigDisassemblers.fromSwitchVlanConfig( config ), String.format( "/%s/vlans", switchId ) );
    }

    public BaseResponse deleteSwitchVlanConfig( String switchId, String vlanId )
        throws HMSRestException
    {
        return hmsSwitchOobManager.parseDeleteResponse( new TypeReference<BaseResponse>()
        {
        }, String.format( "/%s/vlans/%s", switchId, vlanId ) );
    }

    public List<NBSwitchVlanConfig> getSwitchAllVlansConfigs( String switchId )
        throws HMSRestException
    {
        return SwitchVlanConfigAssemblers.toSwitchVlanConfigs( hmsSwitchOobManager.parseGetListResponse( String.format( "/%s/vlansbulk",
                                                                                                                        switchId ),
                                                                                                         SwitchVlan.class ) );
    }

    public BaseResponse configureIpv4DefaultRoute( String switchId, String gateway, String port )
        throws HMSRestException
    {
        return hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, null, String.format( "/%s/ipv4defaultroute?gateway=%s&port=%s", switchId, gateway, port ) );
    }

    public BaseResponse deleteIpv4DefaultRoute( String switchId )
        throws HMSRestException
    {
        return hmsSwitchOobManager.parseDeleteResponse( new TypeReference<BaseResponse>()
        {
        }, String.format( "/%s/ipv4defaultroute", switchId ) );
    }

    public NBSwitchPortInfo getSwitchPortInfo( String switchId, String portId )
        throws HMSRestException
    {
        SwitchPort port = hmsSwitchOobManager.parseGetResponse( new TypeReference<SwitchPort>()
        {
        }, String.format( "/%s/ports/%s", switchId, portId ) );
        return SwitchPortInfoAssemblers.toSwitchPortInfo( port );
    }

    public BaseResponse updateSwitchPortConfig( String switchId, String portname, NBSwitchPortConfig config )
        throws HMSRestException
    {
        if ( config == null || portname == null )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Syntax Error",
                                        "Cannot perform operation with NULL object/port name" );
        }
        return hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, SwitchPortConfigDisassemblers.fromSwitchPortConfig( config ),
                                                     String.format( "/%s/ports/%s", switchId, portname ) );
    }

    public BaseResponse applyBulkConfigs( String switchId, List<NBSwitchBulkConfig> configs )
        throws HMSRestException
    {
        if ( configs == null )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Syntax Error",
                                        "Cannot perform operation with NULL object/port name" );
        }
        return hmsSwitchOobManager.parsePutResponse( new TypeReference<BaseResponse>()
        {
        }, SwitchBulkConfigDisassemblers.toSwitchBulkConfigs( configs ),
                                                     String.format( "/%s/bulkconfigure", switchId ) );
    }

    public List<NBSwitchPortInfo> getSwitchAllPortInfos( String switchId )
        throws HMSRestException
    {
        return SwitchPortInfoAssemblers.toSwitchPortInfos( hmsSwitchOobManager.parseGetListResponse( String.format( "/%s/portsbulk",
                                                                                                                    switchId ),
                                                                                                     SwitchPort.class ) );
    }

    /**
     * Fetch port details on port data change event to update the switch cache
     *
     * @param switchId
     * @return List<NBSwitchPortInfo>
     * @throws HMSRestException
     */
    public List<NBSwitchPortInfo> getSwitchAllPortInfoList( String switchId )
        throws HMSRestException
    {
        List<NBSwitchPortInfo> NBSwitchPortInfoList =
            SwitchPortInfoAssemblers.toSwitchPortInfos( hmsSwitchOobManager.parseGetListResponse( String.format( "/%s/portsbulk",
                                                                                                                 switchId ),
                                                                                                  SwitchPort.class ) );
        context.publishEvent( new SwitchPortsConfigChangeMessage( NBSwitchPortInfoList, switchId ) );
        return NBSwitchPortInfoList;
    }

    public List<NBSwitchInfo> getAllSwitchInfos()
        throws HMSRestException
    {
        List<NBSwitchInfo> switches = new ArrayList<NBSwitchInfo>();
        List<String> switchIds = getAllSwitchIds();
        for ( String switchId : switchIds )
        {
            switches.add( getSwitchInfo( switchId ) );
        }
        return switches;
    }

    /**
     * Function to get all switch ids which are served by the HMS OOB Agent on this rack
     *
     * @return
     */
    public List<String> getAllSwitchIds()
    {
        List<String> ids = new ArrayList<String>();
        NBSwitchNodeIPs switches;
        try
        {
            switches = hmsSwitchOobManager.parseGetResponse( new TypeReference<NBSwitchNodeIPs>()
            {
            }, "/" );
        }
        catch ( HMSRestException e )
        {
            return null;
        }
        for ( SwitchList switchList : switches.getSwitchList() )
        {
            ids.add( switchList.getName() );
        }
        return ids;
    }

    /**
     * Send Event to update the switch cache on switch config changes
     *
     * @param baseResponse
     * @param switchId
     */
    public void sendEventToUpdateSwitchCache( BaseResponse baseResponse, final String switchId )
    {
        if ( baseResponse != null && ( baseResponse.getStatusCode() == HttpStatus.OK.value()
            || baseResponse.getStatusCode() == HttpStatus.ACCEPTED.value() ) )
        {
            // Perform update Switch Cache in a separate thread
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        NBSwitchInfo nbSwitchInfo = getSwitchInfo( switchId );
                        context.publishEvent( new SwitchDataChangeMessage( nbSwitchInfo, SwitchComponentEnum.SWITCH ) );
                    }
                    catch ( HMSRestException e )
                    {
                        logger.error( "Exception while sending an event to update the switch cache for switch Node: "
                            + switchId, e );
                    }
                }
            };
            service.submit( run );
        }
    }
}
