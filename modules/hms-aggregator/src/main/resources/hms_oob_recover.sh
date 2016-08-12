#!/bin/bash

# *********************************************************************************
#                           variable declarations
# *********************************************************************************

# Get the current TimeStamp(Used to rename directories)
declare TOKEN;

# Full Path to Hms Backup Directory
declare BACKUP_DIR_FULLPATH;

# hms direcotry full path
declare HMS_DIR_FULLPATH;

# hms scripts dir
declare HMS_SCRIPT_DIR;

# HMS OOB absolute path to Service Script
declare HMS_OOB_SERVICE_SCRIPT_FULLPATH;

# *********************************************************************************
#                           variable initializations
# *********************************************************************************

# hms parent directory
HMS_PARENT_DIR_FULLPATH="/opt/vrack"

#Hms oob Service Script file that will start stop and query running HMS OOB
HMS_OOB_SERVICE_SCRIPT="hms_oob_service.sh"

# Valid Tokens for services
STOP_TOKEN="stop"
START_TOKEN="start"
QUERY_TOKEN="status"

# Function loginfo to log info message
function loginfo()
{
	dtstamp=`date "+%Y-%m-%d %H:%M:%S"`

	if [ -z $2 ]
	then
		echo "$dtstamp $1"
	else
		echo "$dtstamp $1 [$2]"
	fi
}

function stopHmsOOB()
{
    ($HMS_OOB_SERVICE_SCRIPT_FULLPATH $STOP_TOKEN $HMS_DIR_FULLPATH) || {
    return 112;
    }
}

function startHmsOOB()
{
    ($HMS_OOB_SERVICE_SCRIPT_FULLPATH $START_TOKEN $HMS_DIR_FULLPATH) || {
    return 111;
    }
}

# Function to check and set if any hmsbuild token has been passed while creating it
function check_arguments()
{
	if [ "$#" -ge 4 ]
	then
		TOKEN="$1";
        HMS_SCRIPT_DIR="$2"
		# required to copy working config directory
		BACKUP_DIR_FULLPATH="$3"
		HMS_DIR_FULLPATH="$4"

        HMS_OOB_SERVICE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_OOB_SERVICE_SCRIPT}"

	else
		loginfo "FAILED: Required number of arguments must be passed before continuing.";
		return 39;
	fi
}

# Rollback if the new version fails to start
function rollback()
{
	# Stop Existing Running Service
	loginfo "Starting Roll Back for HMS OOB."

    #Stopping service before rollback(if running)
    stopHmsOOB || {
    echo "Unable to Stop HMS OOB Service remotely";
    return 125;
    }

	loginfo "Removing failed HMS OOB build."
	rm -r -f $HMS_DIR_FULLPATH || {
	echo "Unable to remove failed HMS OOB Directory [ $HMS_DIR_FULLPATH ]";
	return 160;
	}

	loginfo "Restoring HMS OOB from backup [ $BACKUP_DIR_FULLPATH ]"
	mv $BACKUP_DIR_FULLPATH $HMS_DIR_FULLPATH || {
	echo "Unable to restore from Backup. Backup HMS OOB dir [ $BACKUP_DIR_FULLPATH ]";
	return 161;
	}

    #Start HMS OOB
    startHmsOOB || {
    echo "Unable to Start HMS OOB Service after Rollback.";
    return 127;
    }

	loginfo "Rolled back to working HMS OOB state"
}

# Log & Update HMS upgrade status in db and exit with specified code
function fatal_exit()
{
	loginfo "$1 Exit Code: $2"
	echo
	echo "***************************************************************"
	echo "HMS OOB Upgrade: RollBack Phase Failed @ `date`"
	echo "***************************************************************"
	exit $2
}

## ENTRY POINT
echo
echo "****************************************************"
echo "HMS OOB Upgrade: RollBack Phase Started @ `date`"
echo "****************************************************"

# 0. Check Arguments
check_arguments "$@" || {
fatal_exit "HMS Upgrade upgrade phase failed: Arguments Check: Failed" $?;
}

rollback || {
fatal_exit "HMS Upgrade upgrade phase failed: RollBack Failed" $?;
}

echo
echo "***************************************************************"
echo "HMS OOB Upgrade: Rollback Phase completed Successfully @ `date`"
echo "***************************************************************"


exit 0
