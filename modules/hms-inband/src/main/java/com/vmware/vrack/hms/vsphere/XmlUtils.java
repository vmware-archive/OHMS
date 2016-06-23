/* ********************************************************************************
 * XmlUtils.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere;

import com.thoughtworks.xstream.XStream;

/**
 * Author: Tao Ma Date: 3/3/14
 */
public class XmlUtils
{
    /**
     * Serialize an object to XML String.
     *
     * @param obj
     * @return XML String.
     */
    public static String toXml( Object obj )
    {
        XStream xstream = new XStream();
        return xstream.toXML( obj );
    }

    /**
     * Deserialize an object back from XML String.
     *
     * @param xml XML String
     * @return
     */
    public static Object fromXml( String xml )
    {
        XStream xstream = new XStream();
        return xstream.fromXML( xml );
    }

    /**
     * Serialize an object to XML String.
     *
     * @param obj
     * @return XML String.
     */
    public static <T> String toXml( Class<T> clazz, T obj )
    {
        XStream xstream = new XStream();
        xstream.processAnnotations( clazz );
        return xstream.toXML( obj );
    }

    /**
     * Deserialize an object back from XML String.
     *
     * @param xml XML String
     * @return
     */
    public static <T> T fromXml( Class<T> clazz, String xml )
    {
        XStream xstream = new XStream();
        xstream.processAnnotations( clazz );
        return (T) xstream.fromXML( xml );
    }
}
