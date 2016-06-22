/* ********************************************************************************
 * CipherBasedDecryptionDeserializer.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CipherBasedDecryptionDeserializer
    extends JsonDeserializer<Object>
{
    @SuppressWarnings( "unused" )
    private Logger logger = Logger.getLogger( CipherBasedDecryptionDeserializer.class );

    @Override
    public Object deserialize( JsonParser jparser, DeserializationContext deserCtx )
        throws IOException, JsonProcessingException
    {
        String encryptedText = jparser.getText();
        String plaintext = CipherUtil.decrypt( encryptedText );
        // logger.debug("Translated encrypted text = " + encryptedText + " to plain text = " + plaintext);
        return plaintext;
    }
}
