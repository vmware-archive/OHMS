/* ********************************************************************************
 * VsphereOperationException.java
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

/**
 * Author: Tao Ma Date: 3/6/14
 */
public class VsphereOperationException
    extends VsphereException
{
    public VsphereOperationException()
    {
        super();
    }

    public VsphereOperationException( String message )
    {
        super( message );
    }

    public VsphereOperationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public VsphereOperationException( Throwable cause )
    {
        super( cause );
    }

    protected VsphereOperationException( String message, Throwable cause, boolean enableSuppression,
                                         boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
