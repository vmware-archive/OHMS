#!/bin/bash

declare HMS_OOB_HOST;
declare HMS_OOB_USERNAME;

#absolute path of the directory in which logs archive should be kept
declare HMS_LOG_FOLDER_ABSOLUTE_PATH;
#log archive name (without timestamp)
declare HMS_LOG_ARCHIVE_NAME;

SSH_COMMAND="ssh";
SCP_COMMAND="scp";

#No. of lines to be read from last
NO_OF_LINES="10000";

declare TIMESTAMP;

# Generated HMS OOB log filename
declare HMS_OOB_GENERATED_LOG;
# Generated HMS IB log filename
declare HMS_IB_GENERATED_LOG;

#absolute path to the already generated events log
declare HMS_EVENT_LOG_FULLPATH;


################CHANGE HERE FOR ENVIRONMENT SETUP#####################
HMS_SCRIPT_DIR="/home/vrack/vrm/webapps/hms-local/WEB-INF/classes/"

HMS_OOB_REMOTE_LOG_DIR_FULLPATH="/opt/vrack/hms/logs"
HMS_IB_LOG_DIR_FULLPATH="/home/vrack/vrm/logs";

####################################################

HMS_OOB_REMOTE_LOG_FILENAME="hms.log";
HMS_OOB_REMOTE_LOG_FULLPATH="$HMS_OOB_REMOTE_LOG_DIR_FULLPATH/$HMS_OOB_REMOTE_LOG_FILENAME";

HMS_IB_LOG_FILENAME="hms.log";
HMS_IB_LOG_FULLPATH="$HMS_IB_LOG_DIR_FULLPATH/$HMS_IB_LOG_FILENAME";


# Function loginfo to log info message
loginfo()
{
	dtstamp=`date "+%Y-%m-%d %H:%M:%S"`

	if [ -z $2 ]
	then
		echo "$dtstamp $1"
	else
		echo "$dtstamp $1 [$2]"
	fi
}

# check arguments
function check_arguments()
{
	if [ "$#" -ge 9 ] 
	then
		loginfo "###Got OOB Host, UserName, Path and archive_name";
		HMS_OOB_HOST="$1";
		HMS_OOB_USERNAME="$2";
		HMS_LOG_FOLDER_ABSOLUTE_PATH="$3";
		HMS_LOG_ARCHIVE_NAME="$4";
		TIMESTAMP="$5";
		HMS_OOB_REMOTE_LOG_FULLPATH="$6";
		HMS_IB_LOG_FULLPATH="$7";
		HMS_EVENT_LOG_FULLPATH="$8"
		NO_OF_LINES="$9"
		
		HMS_OOB_GENERATED_LOG="hms_oob_${TIMESTAMP}.log";
		HMS_IB_GENERATED_LOG="hms_ib_${TIMESTAMP}.log";
	else
		loginfo "FAILED: Required number of arguments must be passed before continuing.";
		return 9;
	fi
}

#Create Log directory
function create_log_dir()
{
		test -d "$HMS_LOG_FOLDER_ABSOLUTE_PATH" || {
		mkdir -p "$HMS_LOG_FOLDER_ABSOLUTE_PATH" || {
		loginfo "Unable to create folder at [ $HMS_LOG_FOLDER_ABSOLUTE_PATH ]";
		return 19;
		}
		}
}

# get remote hms oob log and write it on specific location on hms ib
function get_oob_logs()
{
	if $SSH_COMMAND ${HMS_OOB_USERNAME}@${HMS_OOB_HOST} test -f $HMS_OOB_REMOTE_LOG_FULLPATH
	then
		($SSH_COMMAND ${HMS_OOB_USERNAME}@${HMS_OOB_HOST} tail -n $NO_OF_LINES $HMS_OOB_REMOTE_LOG_FULLPATH) > "$HMS_LOG_FOLDER_ABSOLUTE_PATH/$HMS_OOB_GENERATED_LOG" || {
		hms_oob_remote_log_read_error="Error in reading remote HMS OOB [ $HMS_OOB_HOST ] log at [ $HMS_OOB_REMOTE_LOG_FULLPATH ]";
		loginfo "$hms_oob_remote_log_read_error";
		echo "$hms_oob_remote_log_read_error" > "$HMS_LOG_FOLDER_ABSOLUTE_PATH/$HMS_OOB_GENERATED_LOG";
		}
	else
	{
		no_hms_oob_log_present="No HMS OOB log file present at [ $HMS_OOB_REMOTE_LOG_FULLPATH ] on remote HMS OOB [ $HMS_OOB_HOST ]";
		loginfo "$no_hms_oob_log_present";
		echo "$no_hms_oob_log_present" > "$HMS_LOG_FOLDER_ABSOLUTE_PATH/$HMS_OOB_GENERATED_LOG";
	}
	fi
}

# get hms ib log and write it on specific location on hms ib
function get_ib_logs()
{
	if test -f $HMS_IB_LOG_FULLPATH
	then
		(tail -n $NO_OF_LINES $HMS_IB_LOG_FULLPATH) > "$HMS_LOG_FOLDER_ABSOLUTE_PATH/$HMS_IB_GENERATED_LOG" || {
		ib_log_read_error="Error in reading HMS IB log at [ $HMS_OOB_REMOTE_LOG_FULLPATH ]";
		loginfo "$ib_log_read_error";
		echo "$ib_log_read_error" > "$HMS_LOG_FOLDER_ABSOLUTE_PATH/$HMS_IB_GENERATED_LOG";
		}
	else
	{
		hms_ib_log_not_present="No HMS IB log file found at [ $HMS_IB_LOG_FULLPATH ]";
		loginfo "$hms_ib_log_not_present";
		echo "$hms_ib_log_not_present" > "$HMS_LOG_FOLDER_ABSOLUTE_PATH/$HMS_IB_GENERATED_LOG";
	}
	fi
}

# creates a zip archive of Hms oob and hms ib logs
function create_archive()
{
	#TODO: CHeck the availablility of event log file, if not present, skip that
	HMS_EVENT_LOG_FILENAME="${HMS_EVENT_LOG_FULLPATH##*/}";
	
	cd $HMS_LOG_FOLDER_ABSOLUTE_PATH;
	if ( ! test -f $HMS_EVENT_LOG_FILENAME )
	then
		echo "Events data not found" > $HMS_EVENT_LOG_FULLPATH;
	fi
	
	cd $HMS_LOG_FOLDER_ABSOLUTE_PATH && zip -m $HMS_LOG_ARCHIVE_NAME $HMS_IB_GENERATED_LOG $HMS_OOB_GENERATED_LOG $HMS_EVENT_LOG_FILENAME || {
	loginfo "Error in creating archive with name [ $HMS_LOG_ARCHIVE_NAME ] in directory [ $HMS_LOG_FOLDER_ABSOLUTE_PATH ]";
	return 19;
	}
	loginfo "Log archive successfully created at [ ${HMS_LOG_FOLDER_ABSOLUTE_PATH}/${HMS_LOG_ARCHIVE_NAME} ]"
}


function fatal_exit()
{
	loginfo "$1 Exit Code: $2"

	echo "***************************************************************"
	echo "HMS Log archiving failed at `date`"
	echo "***************************************************************"
	exit $2
}

##ENTRY POINT

check_arguments "$@" || {
fatal_exit "HMS Arguments Check: Failed" $?;
}

create_log_dir || {
fatal_exit "HMS log directory creation: Failed" $?;
}

get_oob_logs || {
fatal_exit "Failed to get OOB logs: Failed" $?;
}

get_ib_logs || {
fatal_exit "failed to get IB logs: Failed" $?;
}

create_archive || {
fatal_exit "HMS OOB Upgrade Phase: Failed" $?;
}

echo 
echo "***********************************************************************"
echo "HMS Log Archiver successfully archived both HMS OOB and HMS IB logs at @ `date`"
echo "***********************************************************************"

exit 0;