/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b.parsers;

import java.util.ArrayList;
import java.util.List;

/**
 * Normal or success code sensor state for quanta ODM server board D51B (D51B-1U)
 * @author VMware Inc.
 *
 */
public enum SuccessCodeSensorState {

	DrivePresence(SuccessCodeSensorState.DRIVEPRESENCE),
	ConsistencyOrParityCheckInProgress(SuccessCodeSensorState.CONSISTENCYORPARITYCHECKINPROGRESS),
	RebuildRemapInProgress(SuccessCodeSensorState.REBUILDREMAPINPROGRESS),

	PowerSupplyPresenceDetected(SuccessCodeSensorState.POWERSUPPLYPRESENCEDETECTED),
	PowerOffOrDown(SuccessCodeSensorState.POWEROFFORDOWN),

	ProcessorPresenceDetected(SuccessCodeSensorState.PROCESSORPRESENCEDETECTED),
	TerminatorPresenceDetected(SuccessCodeSensorState.TERMINATORPRESENCEDETECTED),

	StateDeasserted(SuccessCodeSensorState.STATEDEASSERTED),
	Ok(SuccessCodeSensorState.OK),

	Invalid(SuccessCodeSensorState.INVALID);


	private static final int STATEDEASSERTED = 101;
	private static final int OK = 102;

	private static final int DRIVEPRESENCE = 200;
	private static final int CONSISTENCYORPARITYCHECKINPROGRESS = 201;
	private static final int REBUILDREMAPINPROGRESS = 202;

	private static final int POWERSUPPLYPRESENCEDETECTED = 300;
	private static final int POWEROFFORDOWN = 301;

	private static final int PROCESSORPRESENCEDETECTED = 400;
	private static final int TERMINATORPRESENCEDETECTED = 401;

    private static final int INVALID = 0;

	private int code;

	SuccessCodeSensorState (int code)
	{
		this.code = code;
	}

	public int getCode()
	{
		return code;
	}

    public static List<String> getSuccessStates ()
    {
    	List<Integer> listCode = new ArrayList<Integer>();
    	List<String> successStates = new ArrayList<String>();

    	for (SuccessCodeSensorState successCodeSensorState: SuccessCodeSensorState.values())
    	{
    			listCode.add(successCodeSensorState.getCode());
    	}

        for (int i=0; i < listCode.size(); i++)
        {
        	successStates.add(getSuccessCode(listCode.get(i)).toString());
        }

		return successStates;
    }

    public static SuccessCodeSensorState getSuccessCode (int code)
    {
		switch(code)
		{
		case DRIVEPRESENCE:
			return DrivePresence;
		case CONSISTENCYORPARITYCHECKINPROGRESS:
            return ConsistencyOrParityCheckInProgress;
		case REBUILDREMAPINPROGRESS:
            return RebuildRemapInProgress;

		case POWERSUPPLYPRESENCEDETECTED:
            return PowerSupplyPresenceDetected;
		case POWEROFFORDOWN:
            return PowerOffOrDown;

		case STATEDEASSERTED:
		    return StateDeasserted;
		case OK:
			return Ok;

		default:
			return Invalid;
		}
    }
}
