/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b.parsers;

import java.util.ArrayList;
import java.util.List;

/**
 * Failure or Error code to trigger the events by sensor for quanta ODM server board D51B (D51B-1U)
 * @author VMware Inc.
 *
 */
public enum FailureCodeEventTrigger {

	LowerNonCriticalGoingLow(FailureCodeEventTrigger.LOWERNONCRITICALGOINGLOW),
	LowerNonCriticalGoingHigh(FailureCodeEventTrigger.LOWERNONCRITICALGOINGHIGH),
	LowerCriticalGoingLow(FailureCodeEventTrigger.LOWERCRITICALGOINGLOW),
	LowerCriticalGoingHigh(FailureCodeEventTrigger.LOWERCRITICALGOINGHIGH),
	LowerNonRecoverableGoingLow(FailureCodeEventTrigger.LOWERNONRECOVERABLEGOINGLOW),
	LowerNonRecoverableGoingHigh(FailureCodeEventTrigger.LOWERNONRECOVERABLEGOINGHIGH),
	UpperNonCriticalGoingLow(FailureCodeEventTrigger.UPPERNONCRITICALGOINGLOW),
	UpperNonCriticalGoingHigh(FailureCodeEventTrigger.UPPERNONCRITICALGOINGHIGH),
	UpperCriticalGoingLow(FailureCodeEventTrigger.UPPERCRITICALGOINGLOW),
	UpperCriticalGoingHigh(FailureCodeEventTrigger.UPPERCRITICALGOINGHIGH),
	UpperNonRecoverableGoingLow(FailureCodeEventTrigger.UPPERNONRECOVERABLEGOINGLOW),
	UpperNonRecoverableGoingHigh(FailureCodeEventTrigger.UPPERNONRECOVERABLEGOINGHIGH),

	BelowLowerNonRecoverable(FailureCodeEventTrigger.BELOWLOWERNONRECOVERABLE),
	AboveUpperNonCritical(FailureCodeEventTrigger.ABOVEUPPERNONCRITICAL),
	AboveUpperNonRecoverable(FailureCodeEventTrigger.ABOVEUPPERNONRECOVERABLE),
	BelowLowerNonCritical(FailureCodeEventTrigger.BELOWLOWERNONCRITICAL),
	BelowLowerCritical(FailureCodeEventTrigger.BELOWLOWERCRITICAL),
	AboveUpperCritical(FailureCodeEventTrigger.ABOVEUPPERCRITICAL),

	DriveFault(FailureCodeEventTrigger.DRIVEFAULT),
	PredictiveFailure(FailureCodeEventTrigger.PREDICTIVEFAILURE),
	HotSpare(FailureCodeEventTrigger.HOTSPARE),
	InCriticalArray(FailureCodeEventTrigger.INCRITICALARRAY),
	InFailedArray(FailureCodeEventTrigger.INFAILEDARRAY),
	RebuildRemapAborted(FailureCodeEventTrigger.REBUILDREMAPABORTED),

	PowerSupplyFailureDetected(FailureCodeEventTrigger.POWERSUPPLYFAILUREDETECTED),
	PowerSupplyPredictiveFailure(FailureCodeEventTrigger.POWERSUPPLYPREDICTIVEFAILURE),
	PowerSupplyInputLost(FailureCodeEventTrigger.POWERSUPPLYINPUTLOST),
	PowerSupplyInputLostOrOutOfRange(FailureCodeEventTrigger.POWERSUPPLYINPUTLOSTOROUTOFRANGE),
	PowerSupplyInputOutOfRange(FailureCodeEventTrigger.POWERSUPPLYINPUTOUTOFRANGE),

	BusFatalError(FailureCodeEventTrigger.BUSFATALERROR),
	BusTimeout(FailureCodeEventTrigger.BUSTIMEOUT),
	BusCorrectableError(FailureCodeEventTrigger.BUSCORRECTABLEERROR),
	BusUncorrectableError(FailureCodeEventTrigger.BUSUNCORRECTABLEERROR),

	ProcessorThermalTrip(FailureCodeEventTrigger.PROCESSORTHERMALTRIP),
	ChipsetThermalTrip(FailureCodeEventTrigger.CHIPSETTHERMALTRIP),
	Ierr(FailureCodeEventTrigger.IERR),

	StateDeasserted(FailureCodeEventTrigger.STATEDEASSERTED),
	StateAsserted(FailureCodeEventTrigger.STATEASSERTED),

	Invalid(FailureCodeEventTrigger.INVALID),
	Unknown(FailureCodeEventTrigger.UNKNOWN);

	private static final int LOWERNONCRITICALGOINGLOW = 100;
    private static final int LOWERNONCRITICALGOINGHIGH = 101;
    private static final int LOWERCRITICALGOINGLOW = 102;
    private static final int LOWERCRITICALGOINGHIGH = 103;
    private static final int LOWERNONRECOVERABLEGOINGLOW = 104;
    private static final int LOWERNONRECOVERABLEGOINGHIGH = 105;
    private static final int UPPERNONCRITICALGOINGLOW = 106;
    private static final int UPPERNONCRITICALGOINGHIGH = 107;
    private static final int UPPERCRITICALGOINGLOW = 108;
    private static final int UPPERCRITICALGOINGHIGH = 109;
    private static final int UPPERNONRECOVERABLEGOINGLOW = 110;
    private static final int UPPERNONRECOVERABLEGOINGHIGH = 111;

	private static final int BELOWLOWERNONRECOVERABLE = 700;
	private static final int ABOVEUPPERNONCRITICAL = 701;
	private static final int ABOVEUPPERNONRECOVERABLE = 702;
	private static final int BELOWLOWERNONCRITICAL = 703;
	private static final int BELOWLOWERCRITICAL = 704;
	private static final int ABOVEUPPERCRITICAL = 705;

    private static final int DRIVEFAULT = 200;
    private static final int PREDICTIVEFAILURE = 201;
    private static final int HOTSPARE = 202;
	private static final int INCRITICALARRAY = 203;
    private static final int INFAILEDARRAY = 204;
	private static final int REBUILDREMAPABORTED = 205;

    private static final int POWERSUPPLYFAILUREDETECTED = 300;
    private static final int POWERSUPPLYPREDICTIVEFAILURE = 301;
    private static final int POWERSUPPLYINPUTLOST = 302;
    private static final int POWERSUPPLYINPUTLOSTOROUTOFRANGE = 303;
    private static final int POWERSUPPLYINPUTOUTOFRANGE = 304;

    private static final int BUSFATALERROR = 400;
    private static final int BUSCORRECTABLEERROR = 401;
    private static final int BUSUNCORRECTABLEERROR = 402;
	private static final int BUSTIMEOUT = 403;

    private static final int PROCESSORTHERMALTRIP = 500;
    private static final int CHIPSETTHERMALTRIP = 501;
    //IERR - Internal Error
    private static final int IERR = 502;

    private static final int STATEASSERTED = 600;
    private static final int STATEDEASSERTED = 601;

    private static final int UNKNOWN = 0;
    private static final int INVALID = -1;

	private int code;

	FailureCodeEventTrigger (int code)
	{
		this.code = code;
	}

	public int getCode()
	{
		return code;
	}

    public static List<String> getFailureStates ()
    {
    	List<Integer> listCode = new ArrayList<Integer>();
    	List<String> failureStates = new ArrayList<String>();

    	for (FailureCodeEventTrigger failureCodeEventTrigger: FailureCodeEventTrigger.values())
    	{
    			listCode.add(failureCodeEventTrigger.getCode());
    	}

        for (int i=0; i < listCode.size(); i++)
        {
        	failureStates.add(getFailureCode(listCode.get(i)).toString());
        }

		return failureStates;
    }

    public static FailureCodeEventTrigger getFailureCode (int code)
    {
		switch(code)
		{
			case LOWERNONCRITICALGOINGLOW:
				return LowerNonCriticalGoingLow;
			case LOWERNONCRITICALGOINGHIGH:
				return LowerNonCriticalGoingHigh;
			case LOWERCRITICALGOINGLOW:
				return LowerCriticalGoingLow;
			case LOWERCRITICALGOINGHIGH:
				return LowerCriticalGoingHigh;
			case LOWERNONRECOVERABLEGOINGLOW:
				return LowerNonRecoverableGoingLow;
			case LOWERNONRECOVERABLEGOINGHIGH:
				return LowerNonRecoverableGoingHigh;
			case UPPERNONCRITICALGOINGLOW:
				return UpperNonCriticalGoingLow;
			case UPPERNONCRITICALGOINGHIGH:
				return UpperNonCriticalGoingHigh;
			case UPPERCRITICALGOINGLOW:
				return UpperCriticalGoingLow;
			case UPPERCRITICALGOINGHIGH:
				return UpperCriticalGoingHigh;
			case UPPERNONRECOVERABLEGOINGLOW:
				return UpperNonRecoverableGoingLow;
			case UPPERNONRECOVERABLEGOINGHIGH:
				return UpperNonRecoverableGoingHigh;

			case BELOWLOWERNONRECOVERABLE:
				return BelowLowerNonRecoverable;
			case ABOVEUPPERNONCRITICAL:
				return AboveUpperNonCritical;
			case ABOVEUPPERNONRECOVERABLE:
				return AboveUpperNonRecoverable;
			case BELOWLOWERNONCRITICAL:
				return BelowLowerNonCritical;
			case BELOWLOWERCRITICAL:
				return BelowLowerCritical;
			case ABOVEUPPERCRITICAL:
				return AboveUpperCritical;

			case DRIVEFAULT:
				return DriveFault;
			case PREDICTIVEFAILURE:
				return PredictiveFailure;
			case HOTSPARE:
				return HotSpare;
			case INCRITICALARRAY:
				return InCriticalArray;
			case INFAILEDARRAY:
				return InFailedArray;
			case REBUILDREMAPABORTED:
				return RebuildRemapAborted;

			case POWERSUPPLYFAILUREDETECTED:
				return PowerSupplyFailureDetected;
			case POWERSUPPLYPREDICTIVEFAILURE:
				return PowerSupplyPredictiveFailure;
			case POWERSUPPLYINPUTLOST:
				return PowerSupplyInputLost;
			case POWERSUPPLYINPUTLOSTOROUTOFRANGE:
				return PowerSupplyInputLostOrOutOfRange;
			case POWERSUPPLYINPUTOUTOFRANGE:
				return PowerSupplyInputOutOfRange;

			case BUSFATALERROR:
				return BusFatalError;
			case BUSCORRECTABLEERROR:
				return BusCorrectableError;
			case BUSUNCORRECTABLEERROR:
				return BusUncorrectableError;
			case BUSTIMEOUT:
				return BusTimeout;

			case PROCESSORTHERMALTRIP:
				return ProcessorThermalTrip;
			case CHIPSETTHERMALTRIP:
				return ChipsetThermalTrip;
			case IERR:
				return Ierr;

			case STATEASSERTED:
				return StateAsserted;
			case INVALID:
				return Invalid;

			default:
				return Unknown;
		}
    }

    public static String[] getStates (String state)
    {
    	String delims = "[ ]";

    	String[] liststate = state.split(delims);

		return liststate;
    }
}
