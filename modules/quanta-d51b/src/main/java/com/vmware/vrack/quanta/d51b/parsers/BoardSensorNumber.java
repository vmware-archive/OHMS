/* Copyright 2015 VMware, Inc. All rights reserved. */

package com.vmware.vrack.quanta.d51b.parsers;

/**
 * The Sensor Number for the quanta ODM server board D51B (D51B-1U)
 * @author Ashvin Moro
 *
 */
public enum BoardSensorNumber
{
	//Sensor Number for Processor Sensor
	Temp_CPU0(BoardSensorNumber.TEMP_CPU0),
	Temp_CPU1(BoardSensorNumber.TEMP_CPU1),
	Temp_VR_CPU0(BoardSensorNumber.TEMP_VR_CPU0),
	Temp_VR_CPU1(BoardSensorNumber.TEMP_VR_CPU1),

	Cpu_0(BoardSensorNumber.CPU_0),
	Cpu_1(BoardSensorNumber.CPU_1),
	CatErr(BoardSensorNumber.CATERR),
	CPU_DIMM_VRHot(BoardSensorNumber.CPU_DIMM_VRHOT),
	Temp_PCH(BoardSensorNumber.TEMP_PCH),

	//Sensor Number for MemoryDevice Sensor
	Temp_DIMM_AB(BoardSensorNumber.TEMP_DIMM_AB),
	Temp_DIMM_CD(BoardSensorNumber.TEMP_DIMM_CD),
	Temp_DIMM_EF(BoardSensorNumber.TEMP_DIMM_EF),
	Temp_DIMM_GH(BoardSensorNumber.TEMP_DIMM_GH),
	Temp_VR_DIMM_AB(BoardSensorNumber.TEMP_VR_DIMM_AB),
	Temp_VR_DIMM_CD(BoardSensorNumber.TEMP_VR_DIMM_CD),
	Temp_VR_DIMM_EF(BoardSensorNumber.TEMP_VR_DIMM_EF),
	Temp_VR_DIMM_GH(BoardSensorNumber.TEMP_VR_DIMM_GH),
	Memory_Error(BoardSensorNumber.MEMORY_ERROR),
	Memory_Event(BoardSensorNumber.MEMORY_EVENT),
	QPI_Error (BoardSensorNumber.QPI_ERROR),
	Other_IIO_Error(BoardSensorNumber.OTHER_IIO_ERROR),
	DIMM_Hot(BoardSensorNumber.DIMM_HOT),

	//Sensor Number for FAN Sensor
	Fan_Sys0_1(BoardSensorNumber.FAN_SYS0_1),
	Fan_Sys0_2(BoardSensorNumber.FAN_SYS0_2),
	Fan_Sys1_1(BoardSensorNumber.FAN_SYS1_1),
	Fan_Sys1_2(BoardSensorNumber.FAN_SYS1_2),
	Fan_Sys2_1(BoardSensorNumber.FAN_SYS2_1),
	Fan_Sys2_2(BoardSensorNumber.FAN_SYS2_2),
	Fan_Sys3_1(BoardSensorNumber.FAN_SYS3_1),
	Fan_Sys3_2(BoardSensorNumber.FAN_SYS3_2),
	Fan_Sys4_1(BoardSensorNumber.FAN_SYS4_1),
	Fan_Sys4_2(BoardSensorNumber.FAN_SYS4_2),
	Fan_Sys5_1(BoardSensorNumber.FAN_SYS5_1),
	Fan_Sys5_2(BoardSensorNumber.FAN_SYS5_2),


	//Sensor Number for HDD sensor
	HDD_0(BoardSensorNumber.HDD0),
	HDD_1(BoardSensorNumber.HDD1),
	HDD_2(BoardSensorNumber.HDD2),
	HDD_3(BoardSensorNumber.HDD3),
	HDD_4(BoardSensorNumber.HDD4),
	HDD_5(BoardSensorNumber.HDD5),
	HDD_6(BoardSensorNumber.HDD6),
	HDD_7(BoardSensorNumber.HDD7),
	HDD_8(BoardSensorNumber.HDD8),
	HDD_9(BoardSensorNumber.HDD9),

	//Sensor Number for Power Unit Sensor
	Power_Unit(BoardSensorNumber.POWER_UNIT),

	//Sensor Number for Power Supply
	PSU1_Status(BoardSensorNumber.PSU1_STATUS),
	PSU2_Status(BoardSensorNumber.PSU2_STATUS),

	//Sensor Number CriticalInterrupt
	PCIE_Error(BoardSensorNumber.PCIE_ERROR),

	//Sensor Number BIOS (System Firmwares)
	POST_Error(BoardSensorNumber.POST_ERROR),

	Invalid(BoardSensorNumber.INVALID);

	//Sensor Type Temperature and Entity Processor
	private static final int TEMP_CPU0 = 170;
	private static final int TEMP_CPU1 = 171;
	private static final int TEMP_VR_CPU0 = 177;
	private static final int TEMP_VR_CPU1 = 178;
	private static final int CPU_DIMM_VRHOT = 183;
	private static final int TEMP_PCH = 190;

	//Sensor Type Temperature and Entity MemoryDevice
	private static final int TEMP_DIMM_AB = 172;
	private static final int TEMP_DIMM_CD = 173;
	private static final int TEMP_DIMM_EF = 174;
	private static final int TEMP_DIMM_GH = 175;
	private static final int TEMP_VR_DIMM_AB = 179;
	private static final int TEMP_VR_DIMM_CD = 180;
	private static final int TEMP_VR_DIMM_EF = 181;
	private static final int TEMP_VR_DIMM_GH = 182;
	private static final int DIMM_HOT = 184;

	//Sensor Type Processor
	private static final int CATERR = 235;
	private static final int CPU_0 = 168;
	private static final int CPU_1 = 169;

	//Sensor Type Memory
	private static final int MEMORY_ERROR = 135;
	private static final int MEMORY_EVENT = 166;
	private static final int QPI_ERROR = 157;
	private static final int OTHER_IIO_ERROR = 167;

	//Sensor Type Fan
	private static final int FAN_SYS0_1 = 192;
	private static final int FAN_SYS0_2 = 193;
	private static final int FAN_SYS1_1 = 194;
	private static final int FAN_SYS1_2 = 195;
	private static final int FAN_SYS2_1 = 196;
	private static final int FAN_SYS2_2 = 197;
	private static final int FAN_SYS3_1 = 198;
	private static final int FAN_SYS3_2 = 199;
	private static final int FAN_SYS4_1 = 200;
	private static final int FAN_SYS4_2 = 201;
	private static final int FAN_SYS5_1 = 202;
	private static final int FAN_SYS5_2 = 203;

	//Sensor Type Power Unit
	private static final int POWER_UNIT = 232;

	//Sensor Type CriticalInterrupt
	private static final int PCIE_ERROR = 161;

	//Sensor Type System Firmwares
	private static final int POST_ERROR = 158;

	//Sensor Type "Drive Slot / Bay"
	private static final int HDD0 = 69;
	private static final int HDD1 = 70;
	private static final int HDD2 = 71;
	private static final int HDD3 = 72;
	private static final int HDD4 = 73;
	private static final int HDD5 = 74;
	private static final int HDD6 = 75;
	private static final int HDD7 = 76;
	private static final int HDD8 = 77;
	private static final int HDD9 = 78;

	//Sensor Type Power Supply
	private static final int PSU1_STATUS = 224;
	private static final int PSU2_STATUS = 225;

	private static final int INVALID = 0;

	private int code;

	BoardSensorNumber (int code)
	{
		this.code = code;
	}

	public int getCode()
	{
		return code;
	}

	public static BoardSensorNumber sensorTypeProcessor (int sensorNumber)
	{
		switch(sensorNumber)
		{
			case CPU_0:
				return Cpu_0;
			case CPU_1:
				return Cpu_1;
			case CATERR:
				return CatErr;

			default:
				return Invalid;
		}
	}

	public static BoardSensorNumber processorSensor (int sensorNumber)
	{
		switch(sensorNumber)
		{
			case CPU_0:
				return Cpu_0;
			case CPU_1:
				return Cpu_1;
			case TEMP_CPU0:
				return Temp_CPU0;
			case TEMP_CPU1:
				return Temp_CPU1;
			case TEMP_VR_CPU0:
				return Temp_VR_CPU0;
			case TEMP_VR_CPU1:
				return Temp_VR_CPU1;
			case CPU_DIMM_VRHOT:
				return CPU_DIMM_VRHot;
			case TEMP_PCH:
				return Temp_PCH;

			default:
				return Invalid;
		}
	}

	public static BoardSensorNumber sensorTypeTemperature (int sensorNumber)
	{
		switch(sensorNumber)
		{
			case TEMP_CPU0:
				return Temp_CPU0;
			case TEMP_CPU1:
				return Temp_CPU1;
			case TEMP_VR_CPU0:
				return Temp_VR_CPU0;
			case TEMP_VR_CPU1:
				return Temp_VR_CPU1;
			case CPU_DIMM_VRHOT:
				return CPU_DIMM_VRHot;
			case TEMP_PCH:
				return Temp_PCH;
			case TEMP_DIMM_AB:
				return Temp_DIMM_AB;
			case TEMP_DIMM_CD:
				return Temp_DIMM_CD;
			case TEMP_DIMM_EF:
				return Temp_DIMM_EF;
			case TEMP_DIMM_GH:
				return Temp_DIMM_GH;
			case TEMP_VR_DIMM_AB:
				return Temp_VR_DIMM_AB;
			case TEMP_VR_DIMM_CD:
				return Temp_VR_DIMM_CD;
			case TEMP_VR_DIMM_EF:
				return Temp_VR_DIMM_EF;
			case TEMP_VR_DIMM_GH:
				return Temp_VR_DIMM_GH;
			case DIMM_HOT:
				return DIMM_Hot;

			default:
				return Invalid;
		}
	}

	public static BoardSensorNumber sensorTypeMemory (int sensorNumber)
	{
		switch(sensorNumber)
		{
			case MEMORY_ERROR:
				return Memory_Error;
			case MEMORY_EVENT:
				return Memory_Event;
			case QPI_ERROR:
				return QPI_Error;
			case OTHER_IIO_ERROR:
				return Other_IIO_Error;

			default:
				return Invalid;
		}
	}

	public static BoardSensorNumber memorySensor (int sensorNumber)
	{
		switch(sensorNumber)
		{

			case TEMP_DIMM_AB:
				return Temp_DIMM_AB;
			case TEMP_DIMM_CD:
				return Temp_DIMM_CD;
			case TEMP_DIMM_EF:
				return Temp_DIMM_EF;
			case TEMP_DIMM_GH:
				return Temp_DIMM_GH;
			case TEMP_VR_DIMM_AB:
				return Temp_VR_DIMM_AB;
			case TEMP_VR_DIMM_CD:
				return Temp_VR_DIMM_CD;
			case TEMP_VR_DIMM_EF:
				return Temp_VR_DIMM_EF;
			case TEMP_VR_DIMM_GH:
				return Temp_VR_DIMM_GH;
			case DIMM_HOT:
				return DIMM_Hot;
			case MEMORY_ERROR:
				return Memory_Error;

			default:
				return Invalid;
		}
	}

	public static BoardSensorNumber sensorTypeFan(int sensorNumber)
	{
		switch(sensorNumber)
		{
			case FAN_SYS0_1:
				return Fan_Sys0_1;
			case FAN_SYS0_2:
				return Fan_Sys0_2;
			case FAN_SYS1_1:
				return Fan_Sys1_1;
			case FAN_SYS1_2:
				return Fan_Sys1_2;
			case FAN_SYS2_1:
				return Fan_Sys2_1;
			case FAN_SYS2_2:
				return Fan_Sys2_2;
			case FAN_SYS3_1:
				return Fan_Sys3_1;
			case FAN_SYS3_2:
				return Fan_Sys3_2;
			case FAN_SYS4_1:
				return Fan_Sys4_1;
			case FAN_SYS4_2:
				return Fan_Sys4_2;
			case FAN_SYS5_1:
				return Fan_Sys5_1;
			case FAN_SYS5_2:
				return Fan_Sys5_2;

			default:
				return Invalid;
		}
	}

	public static BoardSensorNumber sensorTypePowerUnit (int sensorNumber)
	{
		switch(sensorNumber)
		{
			case POWER_UNIT:
				return Power_Unit;

			default:
				return Invalid;
		}
	}

	public static BoardSensorNumber powerSensor(int sensorNumber)
	{
		switch(sensorNumber)
		{
			case POWER_UNIT:
				return Power_Unit;
			case PSU1_STATUS:
				return PSU1_Status;
			case PSU2_STATUS:
				return PSU2_Status;

			default:
				return Invalid;
		}
	}

	public static BoardSensorNumber sensorTypeSystem(int sensorNumber)
	{
		switch(sensorNumber)
		{
			case PCIE_ERROR:
				return PCIE_Error;
			case POST_ERROR:
				return POST_Error;

			default:
				return Invalid;
		}
	}

	public static BoardSensorNumber sensorTypeDriveBay(int sensorNumber)
	{
		switch(sensorNumber)
		{
			case HDD0:
				return HDD_0;
			case HDD1:
				return HDD_1;
			case HDD2:
				return HDD_2;
			case HDD3:
				return HDD_3;
			case HDD4:
				return HDD_4;
			case HDD5:
				return HDD_5;
			case HDD6:
				return HDD_6;
			case HDD7:
				return HDD_7;
			case HDD8:
				return HDD_8;
			case HDD9:
				return HDD_9;

			default:
				return Invalid;
		}
	}

}
