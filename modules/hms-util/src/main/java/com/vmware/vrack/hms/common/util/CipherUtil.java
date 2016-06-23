/* ********************************************************************************
 * CipherUtil.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

public class CipherUtil
{
    private static Logger logger = Logger.getLogger( CipherUtil.class );

    private final static String algorithmName = "PBKDF2WithHmacSHA1";

    private final static String provider = "AES";

    private final static String passphrase = "SDD3&OnE$PlaTf0rm";

    private final static byte[] salt = { 0x76, 0x4D, 0x77, 0x61, 0x52, 0x33, 0x53, 0x42, 0x42, 0x43, 0x31, 0x50, 0x6C,
        0x61, 0x74, 0x66, 0x30, 0x72, 0x6D };

    private final static int iterations = 10000;

    private static Cipher getCipher( int mode )
    {
        Cipher cipher = null;
        try
        {
            SecretKeyFactory factory = SecretKeyFactory.getInstance( algorithmName );
            SecretKey generatedKey =
                factory.generateSecret( new PBEKeySpec( passphrase.toCharArray(), salt, iterations, 128 ) );
            SecretKeySpec key = new SecretKeySpec( generatedKey.getEncoded(), provider );
            cipher = Cipher.getInstance( provider );
            cipher.init( mode, key );
        }
        catch ( Exception exp )
        {
            logger.error( "Received error while creating cipher object.", exp );
        }
        return cipher;
    }

    public static String encrypt( String text )
    {
        String encrypted = null;
        if ( text != null && text.length() > 0 )
        {
            try
            {
                Cipher cipher = getCipher( Cipher.ENCRYPT_MODE );
                byte[] ciphertext = cipher.doFinal( text.getBytes() );
                encrypted = DatatypeConverter.printBase64Binary( ciphertext );
            }
            catch ( Exception exp )
            {
                logger.error( "Received error while encrypting string.", exp );
            }
        }
        return encrypted;
    }

    public static String decrypt( String encrypted )
    {
        String original = null;
        if ( encrypted != null && encrypted.length() > 0 )
        {
            try
            {
                Cipher cipher = getCipher( Cipher.DECRYPT_MODE );
                byte[] base64data = DatatypeConverter.parseBase64Binary( encrypted );
                if ( base64data.length % cipher.getBlockSize() == 0 )
                {
                    original = new String( cipher.doFinal( base64data ) );
                }
                else
                {
                    original = encrypted;
                }
            }
            catch ( Exception exp )
            {
                logger.error( "Received error while decrypting string.", exp );
            }
        }
        return original;
    }

    public static void main( String[] args )
        throws Exception
    {
        if ( args != null && args.length >= 2 )
        {
            String action = args[0];
            if ( action.equalsIgnoreCase( "encrypt" ) )
            {
                String encrypt = CipherUtil.encrypt( args[1] );
                System.out.println( "Encrypted String: " + encrypt );
            }
            // else if (action.equalsIgnoreCase("decrypt")) {
            // String decrypt = CipherUtil.decrypt(args[1]);
            // System.out.println("Original String: " + decrypt);
            // }
        }
    }
}
