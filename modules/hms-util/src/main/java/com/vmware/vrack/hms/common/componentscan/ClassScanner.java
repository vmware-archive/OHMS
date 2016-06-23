/* ********************************************************************************
 * ClassScanner.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.componentscan;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

/**
 * Utility class to scan for all classes under the packages, having Annotations as passed in the initialization
 *
 * @author Yagnesh Chawda
 */
public class ClassScanner
{
    /**
     * Comma separated package names
     */
    private final String basePackages;

    private final ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider( false );

    /**
     * If no basePackages is provided, system will default to scan it from package "com.vmware.vrack.hms"
     * 
     * @param basePackages
     */
    public ClassScanner( String basePackages )
    {
        if ( basePackages != null && !"".equals( basePackages.trim() ) )
        {
            this.basePackages = basePackages;
        }
        else
        {
            this.basePackages = "com.vmware.vrack.hms";
        }
    }

    /**
     * Iterates through all Classes under basePackages and returns all classes which are annotated with given filter
     *
     * @return
     */
    public final List<Class<?>> findClasses()
    {
        final List<Class<?>> classes = new ArrayList<Class<?>>();
        for ( String basePackage : basePackages.split( "," ) )
        {
            basePackage = basePackage.replaceAll( "\\ ", "" );
            for ( final BeanDefinition candidate : scanner.findCandidateComponents( basePackage ) )
            {
                classes.add( ClassUtils.resolveClassName( candidate.getBeanClassName(),
                                                          ClassUtils.getDefaultClassLoader() ) );
            }
        }
        return classes;
    }

    /**
     * Adds a filter to the scanner
     *
     * @param filter
     * @return
     */
    public ClassScanner withIncludeFilter( final TypeFilter filter )
    {
        scanner.addIncludeFilter( filter );
        return this;
    }

    /**
     * Adds Annotation filter to scanner.
     *
     * @param annotationClass
     * @return
     */
    public ClassScanner withAnnotationFilter( final Class<? extends Annotation> annotationClass )
    {
        return withIncludeFilter( new AnnotationTypeFilter( annotationClass ) );
    }
}
