/* ********************************************************************************
 * InBandServiceFactory.java
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
package com.vmware.vrack.hms.common.boardvendorservice.api.ib;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.annotation.HmsComponentUpgradePlugin;
import com.vmware.vrack.hms.common.annotation.SupportsUpgrade;
import com.vmware.vrack.hms.common.componentscan.ClassScanner;

/**
 * Singleton class At the time of HMS Local bootup, this will scan all classes under inBandServiceBasePackages and looks
 * for all classes which have got annotated as @InBandServiceImplementation. List of .class object for all such classes
 * will be held here in boardServiceImplementationClasses
 */
public class InBandServiceFactory
{
    private static Logger logger = Logger.getLogger( InBandServiceFactory.class );

    private volatile static InBandServiceFactory boardServicefactory = null;

    public static final String IN_BAND_SERVICE_BASE_PACKAGES_PROP = "IN_BAND_SERVICE_BASE_PACKAGES";

    /**
     * Comma separated list of base package names to be scanned for finding Classes which have got required Annotation
     */
    public static String boardServiceBasePackages;

    /**
     * List of Annotation classes which needs to be considered for filtring Board Service Impl classes
     */
    public List<Class<? extends Annotation>> boardServiceImplementationAnnotations = null;

    /**
     * List of all Classes which have got @BoardServiceImplementation Annotation
     */
    public List<Class<?>> boardServiceImplementationClasses = null;

    /**
     * Map that will contain all BoardService
     */
    public Map<String, Class> boardServiceMap = new HashMap<String, Class>();

    /**
     * List of Annotation classes which needs to be considered for filtering IComponentLifeCycleManager Impl classes
     */
    public List<Class<? extends Annotation>> hmsComponentUpgradePluginAnnotations = null;

    /**
     * List of all Classes which have got @HmsComponentUpgradePlugin Annotation
     */
    public List<Class<?>> hmsComponentUpgradePluginAnnotatedClasses = new ArrayList<Class<?>>();

    /**
     * Map that will contain all hmsComponentUpgradeClasses
     */
    public Map<String, Class> hmsComponentUpgradePluginClassesMap = new HashMap<String, Class>();

    private InBandServiceFactory()
    {
        // TODO: Get annotation list as well from the Spring/config in future.
        boardServiceImplementationAnnotations = new ArrayList<Class<? extends Annotation>>();
        boardServiceImplementationAnnotations.add( InBandServiceImplementation.class );
        hmsComponentUpgradePluginAnnotations = new ArrayList<Class<? extends Annotation>>();
        hmsComponentUpgradePluginAnnotations.add( HmsComponentUpgradePlugin.class );
    }

    /**
     * Returns singleton instance of the BoardServiceProvider
     */
    public static InBandServiceFactory getBoardServiceFactory()
    {
        if ( boardServicefactory == null )
        {
            boardServicefactory = new InBandServiceFactory();
        }
        return boardServicefactory;
    }

    /**
     * Initializes BoardServiceFactory and prepares list of all classes in the class path which are in the
     * package/subpackage as mentioned in serverBoardBasePackage and having annotation as mentioned in the
     * boardServiceImplementationAnnotations
     */
    public static void initialize()
    {
        InBandServiceFactory boardServiceFactory = getBoardServiceFactory();
        // TODO Suppose that the properties file has been read.
        String serverBoardBasePackage = HmsConfigHolder.getHMSConfigProperty( IN_BAND_SERVICE_BASE_PACKAGES_PROP );
        if ( serverBoardBasePackage == null || "".equals( serverBoardBasePackage.trim() ) )
        {
            serverBoardBasePackage = "com.vmware";
        }
        boardServiceFactory.prepareBoardServiceImplementationClassesList( serverBoardBasePackage );
        // Prepare Map for BoardServiceProvider with key=[Vendor]-[BoardProductName] and value=[Board Class].
        boardServiceFactory.prepareBoardServiceMap();
        /*
         * boardServiceFactory.prepareHmsComponentUpgradePluginAnnotationClassesList(serverBoardBasePackage);
         * boardServiceFactory.prepareHmsComponentUpgradeClasssMap();
         */
        // Initilaize
    }

    /**
     * Prepares list of all classes in the class path which are in the package/subpackage as mentioned in
     * serverBoardBasePackage and having annotation as mentioned in the hmsComponentUpgradePluginAnnotations and stores
     * it in hmsComponentUpgradePluginAnnotationClasses
     *
     * @param basePackage
     */
    public void prepareHmsComponentUpgradePluginAnnotationClassesList( String basePackage )
    {
        final ClassScanner classScanner = new ClassScanner( basePackage );
        for ( Class<? extends Annotation> annotation : hmsComponentUpgradePluginAnnotations )
        {
            classScanner.withAnnotationFilter( annotation );
        }
        hmsComponentUpgradePluginAnnotatedClasses = classScanner.findClasses();
        logger.info( "Available Hms Component Upgrade classes:" + hmsComponentUpgradePluginAnnotatedClasses );
    }

    /**
     * Prepare Map for BoardServiceProvider with
     * key=[boardManufacturer]-[boardModel]-[hypervisorName]-[hypervisorProvider] and value=[IComponentLifeCycleManager
     * Class].
     */
    @SuppressWarnings( "rawtypes" )
    public void prepareHmsComponentUpgradeClasssMap()
    {
        if ( hmsComponentUpgradePluginAnnotatedClasses != null )
        {
            for ( Class hmsComponentUpgradeClass : hmsComponentUpgradePluginAnnotatedClasses )
            {
                Annotation[] annotations = hmsComponentUpgradeClass.getAnnotations();
                for ( Annotation annotation : annotations )
                {
                    if ( annotation != null
                        && HmsComponentUpgradePlugin.class.isAssignableFrom( annotation.getClass() ) )
                    {
                        SupportsUpgrade[] supportedBoardCombinations =
                            ( (HmsComponentUpgradePlugin) annotation ).supportsUpgradeFor();
                        for ( SupportsUpgrade supportedBoardCombination : supportedBoardCombinations )
                        {
                            String boardManufacturer = supportedBoardCombination.boardManufacturer();
                            String boardModel = supportedBoardCombination.boardModel();
                            String hypervisorName = supportedBoardCombination.hypervisorName();
                            String hypervisorProvider = supportedBoardCombination.hypervisorProvider();
                            String key = getSupportedBoardCombinationKey( boardManufacturer, boardModel, hypervisorName,
                                                                          hypervisorProvider );
                            hmsComponentUpgradePluginClassesMap.put( key, hmsComponentUpgradeClass );
                        }
                    }
                }
            }
        }
        else
        {
            logger.info( "No supported upgrade plugins found" );
        }
    }

    /**
     * @param boardInfo
     * @return
     */
    public static String getSupportedBoardCombinationKey( String boardManufacturer, String boardModel,
                                                          String hypervisorName, String hypervisorProvider )
    {
        String key = null;
        if ( boardManufacturer != null && !boardManufacturer.isEmpty() && boardModel != null && !boardModel.isEmpty()
            && hypervisorName != null && !hypervisorName.isEmpty() && hypervisorProvider != null
            && !hypervisorProvider.isEmpty() )
        {
            key = ( "[" + boardManufacturer + "]-[" + boardModel + "]-[" + hypervisorName + "]-[" + hypervisorProvider
                + "]" ).toLowerCase();
        }
        return key;
    }

    /**
     * @param key
     * @return
     */
    @SuppressWarnings( "rawtypes" )
    public Class getHmsComponentUpgradeClass( String key )
    {
        return hmsComponentUpgradePluginClassesMap.get( key );
    }

    /**
     * Prepares list of all classes in the class path which are in the package/subpackage as mentioned in
     * serverBoardBasePackage and having annotation as mentioned in the boardServiceImplementationAnnotations and stores
     * it in boardServiceImplementationClasses
     *
     * @param basePackage
     */
    public void prepareBoardServiceImplementationClassesList( String basePackage )
    {
        final ClassScanner classScanner = new ClassScanner( basePackage );
        for ( Class<? extends Annotation> annotation : boardServiceImplementationAnnotations )
        {
            classScanner.withAnnotationFilter( annotation );
        }
        boardServiceImplementationClasses = classScanner.findClasses();
        logger.info( "Available Board Service classes:" + boardServiceImplementationClasses );
    }

    /**
     * @return
     */
    public List<Class<?>> getBoardServiceImplementationClasses()
    {
        return boardServiceImplementationClasses;
    }

    /**
     * Prepare Map for BoardServiceProvider with key=[Vendor]-[BoardProductName] and value=[Board Class].
     */
    public void prepareBoardServiceMap()
    {
        // "[Intel-Corpo]-[S2600GZ]"
        for ( Class boardServiceClass : boardServiceImplementationClasses )
        {
            try
            {
                IInbandService boardService = (IInbandService) boardServiceClass.newInstance();
                List<HypervisorInfo> boardInfos = boardService.getSupportedHypervisorInfo();
                if ( boardInfos != null )
                {
                    for ( HypervisorInfo boardInfo : boardInfos )
                    {
                        if ( boardInfo != null )
                        {
                            String key = getBoardServiceKey( boardInfo );
                            logger.debug( "Adding Key:" + key + " for Board Class:" + boardServiceClass );
                            boardServiceMap.put( key, boardServiceClass );
                        }
                    }
                }
            }
            catch ( InstantiationException e )
            {
                logger.error( "Exception during creating new Instance of class:" + boardServiceClass, e );
            }
            catch ( IllegalAccessException e )
            {
                logger.error( "Exception during creating new Instance of class:" + boardServiceClass, e );
            }
        }
    }

    /**
     * @param boardInfo
     * @return
     */
    public static String getBoardServiceKey( HypervisorInfo boardInfo )
    {
        String key = null;
        // We can keep key as vendorName and Product Name, but for now we are keeping it as Product name only.
        if ( boardInfo != null )
        {
            // key = ("[" + boardInfo.getBoardManufacturer() + "]-[" + boardInfo.getBoardProductName() +
            // "]").toLowerCase();
            key = ( "[" + boardInfo.getName() + "]" ).toLowerCase();
        }
        return key;
    }

    /**
     * Returns the BoardService Class based on provided key
     * 
     * @param key
     * @return
     */
    public Class getBoardServiceClass( String key )
    {
        return boardServiceMap.get( key );
    }
}
