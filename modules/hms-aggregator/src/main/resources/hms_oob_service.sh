#!/bin/bash

# Full Path to the Hms Directory
declare HMS_DIR_FULLPATH;
#Query Timeout for Hms Service Status in seconds
SERVICE_STATUS_QUERY_TIMEOUT=5

# Valid Tokens that will be passed to this script
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


# Function to check and perform action on the basis of token passed
function perform_action()
{
	if [ $# -ge 2 ]
	then
	# Set the path to the HMS dir. Setting this before checking first argument is important.
	HMS_DIR_FULLPATH="$2"

	case $1 in
	"$STOP_TOKEN")
		stopHmsService  || {
		loginfo "Unable to stop Hms Service";
		exit 201;
		}
		exit 0;
		;;

	"$START_TOKEN")
		startHmsService || {
		loginfo "Unable to start Hms OOB";
		}

		sleep $SERVICE_STATUS_QUERY_TIMEOUT
		is_hms_service_running || {
		loginfo "HMS OOB not running. Unable to start Hms Service";
		exit 202;
		}

		loginfo "HMS OOB started successfully.";
		exit 0;
		;;

	"$QUERY_TOKEN")
		if (is_hms_service_running)
		then
			pid=$(get_hms_pid)
			loginfo "HMS OOB Service is running with pid : $pid";
			exit 0;
		else
			loginfo "HMS OOB Service not running"
			exit 203;
		fi
		;;
	*)
	{
		loginfo "Not a valid option. Specify [start <path to hms>, stop <path to hms>, status <path to hms>]"
		exit 204;
	}
	esac
	else
		loginfo "FAILED: Required number of arguments must be passed before continuing.";
		exit 49;
	fi
}

# returns the process id of HmsApp
function get_hms_pid()
{
	echo `ps aux | grep "com.vmware.vrack.hms.HmsApp" | grep -v "grep" | awk '{print $2}'`
}

# Checks if hms-service is running
function is_hms_service_running()
{
	if [ -z $(get_hms_pid) ]
	then
		return 1
	else
		return 0
	fi
	#return 0
}

# Start the Hms Service on the machine
function startHmsService()
{
	loginfo ""
	loginfo "Hms Service: Starting ..."

	# First check if the HMS is already running and we tried to start it twice
	curr_pid=$(get_hms_pid)
	if [ ! -z $curr_pid ]
	then
	{
		loginfo "HMS already running with pid $curr_pid";
		exit 0;
	}
	fi

	hms_shell_script="$HMS_DIR_FULLPATH/hms.sh"

	# Check if the shell script physically exist on disk. Exit immediately with Error message
	local hmsshell=$hms_shell_script
	[[ -f "$hmsshell" ]] || {
		loginfo "No shell script found at path [ $hms_shell_script ]";
		exit 206;
	}

    cd $HMS_DIR_FULLPATH
	#Taking reference from this url because without this, it will stuck after executing the following line.
	#http://stackoverflow.com/questions/23520541/unable-to-execute-a-bash-script-in-background-from-another-bash-script
	#nohup bash $hmsshell >> ./upgrade_log.txt 2>&1 &
    nohup bash $hmsshell > /dev/null 2>&1 &
	sleep $SERVICE_STATUS_QUERY_TIMEOUT

	pid=$(get_hms_pid)

	if [ -z $pid ]
	then
	{
		loginfo "Something went wrong. HMS failed to start. Check directory path again [ $hmsshell ]"
	}
	else
	{
		loginfo "Started Hms with process ID $pid"
	}
	fi
}

# Stop the Hms Service on the machine
function stopHmsService()
{
	loginfo ""
	loginfo "Stopping HMS OOB"

	pid=$(get_hms_pid)

	if [  -z $pid ]
	then
		loginfo "HMS OOB not running. nothing to stop."
	else
		loginfo "HMS OOB currently running. Killing Hms with process ID $pid"
		kill -9 $pid || {
			loginfo "Unable to kill HMS OOB with pid $pid";
			return 207;
		}
		loginfo "Successfully stopped HMS OOB with pid $pid"
	fi
}


# 0. Check Arguments
perform_action "$@" || { exit $?; }

exit 0
