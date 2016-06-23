/* ********************************************************************************
 * TransactionContext.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.vsphere;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Tao Ma Date: 3/13/14
 */
public class TransactionContext
{
    private ThreadLocal<Map<String, String>> context = new ThreadLocal<Map<String, String>>()
    {
        @Override
        protected Map<String, String> initialValue()
        {
            return new HashMap<String, String>();
        }
    };

    private TransactionContext()
    {
    }

    private static class TransactionContextHolder
    {
        private static TransactionContext transactionContext = new TransactionContext();
    }

    public static TransactionContext getInstance()
    {
        return TransactionContextHolder.transactionContext;
    }

    public String get( String key )
    {
        return context.get().get( key );
    }

    public String set( String key, String value )
    {
        return context.get().put( key, value );
    }

    public void remove( String key )
    {
        context.get().remove( key );
    }

    @Override
    public String toString()
    {
        return String.valueOf( context.get() );
    }
}
