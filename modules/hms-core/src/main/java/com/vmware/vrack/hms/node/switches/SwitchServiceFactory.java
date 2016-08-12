/* ********************************************************************************
 * SwitchServiceFactory.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.node.switches;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.componentscan.ClassScanner;
import com.vmware.vrack.hms.common.switches.api.SwitchServiceImplementation;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;

/**
 * Singleton class At the time of System bootup, this will scan all classes under switchServiceBasePackages and looks
 * for all classes which have got annotated as @TorSwitchImplementation. List of .class object for all such classes will
 * be held here in switchServiceImplementationClasses
 */
public class SwitchServiceFactory
{
    private Logger logger = Logger.getLogger( SwitchServiceFactory.class );

    private volatile static SwitchServiceFactory switchServicefactory = null;

    public static final String SWITCH_SERVICE_BASE_PACKAGES_PROP = "hms.switch.service.base.package";

    /**
     * List of Annotation classes which needs to be considered for filtering Board Service Impl classes
     */
    private List<Class<? extends Annotation>> switchServiceImplementationAnnotations = null;

    /**
     * List of all Classes which have got @TorSwitchImplementation Annotation
     */
    private List<Class<?>> switchServiceImplementationClasses = null;

    /**
     * List of all switch services
     */
    private List<ISwitchService> switchServiceList = null;

    private SwitchServiceFactory()
    {
        switchServiceImplementationAnnotations = new ArrayList<Class<? extends Annotation>>();
        switchServiceImplementationAnnotations.add( SwitchServiceImplementation.class );
    }

    /**
     * Returns singleton instance of the SwitchServiceProvider
     */
    public static SwitchServiceFactory getSwitchServiceFactory()
    {
        if ( switchServicefactory == null )
        {
            switchServicefactory = new SwitchServiceFactory();
        }
        return switchServicefactory;
    }

    /**
     * Initializes SwitchServiceFactory and prepares list of all classes in the class path which are in the
     * package/subpackage as mentioned in serverBoardBasePackage and having annotation as mentioned in the
     * switchServiceImplementationAnnotations
     */
    public static void initialize()
    {
        SwitchServiceFactory switchServiceFactory = getSwitchServiceFactory();
        // TODO Suppose that the properties file has been read.
        String basePackage = HmsConfigHolder.getHMSConfigProperty( SWITCH_SERVICE_BASE_PACKAGES_PROP );
        if ( basePackage == null || "".equals( basePackage.trim() ) )
        {
            basePackage = "com.vmware";
        }
        switchServiceFactory.prepareSwitchServiceImplementationClassesList( basePackage );
        switchServiceFactory.prepareSwitchServiceList();
    }

    /**
     * Prepares list of all classes in the class path which are in the package/subpackage as mentioned in
     * serverBoardBasePackage and having annotation as mentioned in the switchServiceImplementationAnnotations and
     * stores it in switchServiceImplementationClasses
     *
     * @param basePackage
     */
    public void prepareSwitchServiceImplementationClassesList( String basePackage )
    {
        final ClassScanner classScanner = new ClassScanner( basePackage );
        for ( Class<? extends Annotation> annotation : switchServiceImplementationAnnotations )
        {
            classScanner.withAnnotationFilter( annotation );
        }
        switchServiceImplementationClasses = classScanner.findClasses();
    }

    public List<Class<?>> getSwitchServiceImplementationClasses()
    {
        return switchServiceImplementationClasses;
    }

    private void prepareSwitchServiceList()
    {
        logger.debug( "Initializing ToR switch implementations." );
        if ( switchServiceImplementationClasses != null && switchServiceImplementationClasses.size() > 0 )
        {
            switchServiceList = new ArrayList<ISwitchService>();
            for ( Class<?> c : switchServiceImplementationClasses )
            {
                try
                {
                    logger.info( "Loading ToR switch implementation service " + c.getName() );
                    ISwitchService t = (ISwitchService) c.newInstance();
                    switchServiceList.add( t );
                }
                catch ( InstantiationException | IllegalAccessException e )
                {
                    logger.warn( "Exception received while loading ToR switch implementation", e );
                }
            }
        }
        else
        {
            logger.warn( "No ToR switch implementations found." );
        }
    }

    public List<ISwitchService> getSwitchServiceList()
    {
        return switchServiceList;
    }
}
