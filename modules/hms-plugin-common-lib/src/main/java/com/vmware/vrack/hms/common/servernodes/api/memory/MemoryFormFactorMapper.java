/* ********************************************************************************
 * MemoryFormFactorMapper.java
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

package com.vmware.vrack.hms.common.servernodes.api.memory;

/**
 * Mapper to map Form factor code to respective Memory Form Factor Name
 * 
 * @author VMware, Inc.
 */
public class MemoryFormFactorMapper
{
    private static final String UNKNOWN = "Unknown";

    private static final String OTHER = "Other";

    private static final String SIP = "SIP";

    private static final String DIP = "DIP";

    private static final String ZIP = "ZIP";

    private static final String SOJ = "SOJ";

    private static final String PROPRIETARY = "Proprietary";

    private static final String SIMM = "SIMM";

    private static final String DIMM = "DIMM";

    private static final String TSOP = "TSOP";

    private static final String PGA = "PGA";

    private static final String RIMM = "RIMM";

    private static final String SODIMM = "SODIMM";

    private static final String SRIMM = "SRIMM";

    private static final String SMD = "SMD";

    private static final String SSMP = "SSMP";

    private static final String QFP = "QFP";

    private static final String TQFP = "TQFP";

    private static final String SOIC = "SOIC";

    private static final String LCC = "LCC";

    private static final String PLCC = "PLCC";

    private static final String BGA = "BGA";

    private static final String FPBGA = "FPBGA";

    private static final String LGA = "LGA";

    public static String getFormFactor( int formFactorCode )
    {
        if ( formFactorCode == 0 )
            return UNKNOWN;
        if ( formFactorCode == 1 )
            return OTHER;
        if ( formFactorCode == 2 )
            return SIP;
        if ( formFactorCode == 3 )
            return DIP;
        if ( formFactorCode == 4 )
            return ZIP;
        if ( formFactorCode == 5 )
            return SOJ;
        if ( formFactorCode == 6 )
            return PROPRIETARY;
        if ( formFactorCode == 7 )
            return SIMM;
        if ( formFactorCode == 8 )
            return DIMM;
        if ( formFactorCode == 9 )
            return TSOP;
        if ( formFactorCode == 10 )
            return PGA;
        if ( formFactorCode == 11 )
            return RIMM;
        if ( formFactorCode == 12 )
            return SODIMM;
        if ( formFactorCode == 13 )
            return SRIMM;
        if ( formFactorCode == 14 )
            return SMD;
        if ( formFactorCode == 15 )
            return SSMP;
        if ( formFactorCode == 16 )
            return QFP;
        if ( formFactorCode == 17 )
            return TQFP;
        if ( formFactorCode == 18 )
            return SOIC;
        if ( formFactorCode == 19 )
            return LCC;
        ;
        if ( formFactorCode == 20 )
            return PLCC;
        if ( formFactorCode == 21 )
            return BGA;
        if ( formFactorCode == 22 )
            return FPBGA;
        if ( formFactorCode == 23 )
            return LGA;

        return null;

    }

}
