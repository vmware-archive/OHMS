package com.veraxsystems.vxipmi.coding.commands.session;

public class SessionCustomPayload
{
    private byte authCode;

    private byte integrityCode;

    private byte confidentialityCode;

    public SessionCustomPayload( byte authCode, byte integrityCode, byte confidentialityCode )
    {
        super();
        this.authCode = authCode;
        this.integrityCode = integrityCode;
        this.confidentialityCode = confidentialityCode;
    }

    public byte getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode( byte authCode )
    {
        this.authCode = authCode;
    }

    public byte getIntegrityCode()
    {
        return integrityCode;
    }

    public void setIntegrityCode( byte integrityCode )
    {
        this.integrityCode = integrityCode;
    }

    public byte getConfidentialityCode()
    {
        return confidentialityCode;
    }

    public void setConfidentialityCode( byte confidentialityCode )
    {
        this.confidentialityCode = confidentialityCode;
    }
}
