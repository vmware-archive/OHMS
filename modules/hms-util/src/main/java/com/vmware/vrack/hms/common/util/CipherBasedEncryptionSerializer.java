/* ********************************************************************************
 * CipherBasedEncryptionSerializer.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CipherBasedEncryptionSerializer
    extends JsonSerializer<Object>
{
    @Override
    public void serialize( Object value, JsonGenerator jgen, SerializerProvider provider )
        throws IOException, JsonProcessingException
    {
        if ( value instanceof String )
        {
            String encryptedText = CipherUtil.encrypt( (String) value );
            jgen.writeString( encryptedText );
        }
    }
}
