/* ********************************************************************************
 * XmlUtils.java
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
