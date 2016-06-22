#!/bin/bash

# ********************************************************************************
#                   ***** VARIABLES DECLARATION *****
# ********************************************************************************
declare HMS_TOKEN;
declare HMS_SCRIPT_DIR;
declare HMS_IB_BACKUP_DIR;
declare HMS_IB_UPGRADE_DIR;
declare HMS_IB_UPGRADE_WAR_NAME;
declare HMS_OOB_HOST;
declare REMOTE_HMS_OOB_SCRIPT_DIR_FULLPATH;

# for scripts absolute path
declare HMS_IB_BACKUP_DIR_NAME;
declare HMS_IB_BACKUP_DIR_FULLPATH;

declare HMS_IB_UPGRADE_WAR_FULLPATH;
declare HMS_IB_VALIDATE_SCRIPT_FULLPATH;
declare HMS_IB_BACKUP_SCRIPT_FULLPATH;
declare HMS_IB_SERVICE_SCRIPT_FULLPATH;
declare HMS_IB_UPGRADE_SCRIPT_FULLPATH;
declare HMS_IB_RECOVER_SCRIPT_FULLPATH;
declare HMS_IB_UPGRADE_WAR_CHECKSUM_FILENAME;

# payload for invoking oob agent /rollback api
declare HMS_OOB_ROLLBACK_API_PAYLOAD;

#declare HMS_IB_CHECKSUM_METHOD;

# HMS IB UpgradeStatus JSON file absoulte path
declare HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH;

# ********************************************************************************
#                   ***** VARIABLES INITIALIZATION *****
# ********************************************************************************
HMS_QUERY_WAIT=15;
HMS_WAIT_BEFORE_UPGRADE=30;

# --------------------------------------------------------------------------------
#               ***** VARIABLES FOR FOR ENVIRONMENT SETUP *****
# --------------------------------------------------------------------------------
HMS_SCRIPT_DIR="/home/vrack/vrm/webapps/hms-local/WEB-INF/classes/"
CATALINA_BASE="/home/vrack/vrm"
HMS_UPGRADE_DIR="/home/vrack/upgrade"

# --------------------------------------------------------------------------------
#           ***** VARIABLES RELATED TO SCRIPTS AND VALIDATION *****
# --------------------------------------------------------------------------------
HMS_IB_VALIDATE_SCRIPT="hms_ib_validate.sh";
HMS_IB_BACKUP_SCRIPT="hms_ib_backup.sh"
HMS_IB_SERVICE_SCRIPT="hms_ib_service.sh"
HMS_IB_UPGRADE_SCRIPT="hms_ib_upgrade.sh"
HMS_IB_RECOVER_SCRIPT="hms_ib_recover.sh"

# --------------------------------------------------------------------------------
#           ***** VARIABLES FOR REMOTE HMS IB *****
# --------------------------------------------------------------------------------

WEBAPP_NAME="hms-local"
WEBAPP_WAR_NAME="$WEBAPP_NAME.war"
WEBAPP_DIR_FULLPATH="$CATALINA_BASE/webapps/$WEBAPP_NAME"
WEBAPP_WAR_FULLPATH="$CATALINA_BASE/webapps/$WEBAPP_WAR_NAME"
JARS_DIR="$CATALINA_BASE/webapps/$WEBAPP_NAME/WEB-INF/lib"
HMS_PROP_FILE="$CATALINA_BASE/webapps/$WEBAPP_NAME/WEB-INF/classes/hms.properties"

# Valid Tokens for services
# --------------------------------------------------------------------------------
#           ***** VARIABLES FOR SERVICE START/STOP/STATUS *****
# --------------------------------------------------------------------------------
STOP_TOKEN="stop"
START_TOKEN="start"
QUERY_TOKEN="status"

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

# Function to check and set if any hmsbuild token has been passed while creating it
function check_arguments()
{
	if [ "$#" -ge 7 ]
	then

		loginfo "### Got HMS token, upgrade directory, upgrade war file, OOB Host and Scripts directory of the OOB.";

		# unique token
		HMS_TOKEN="$1";

		# directory where IB upgrade scripts have been copied to.
		HMS_SCRIPT_DIR="$2";

		# backup directory to which running IB agent will be backup'ed to.
		HMS_IB_BACKUP_DIR="$3"
		HMS_IB_BACKUP_DIR_NAME="hms_backup_$HMS_TOKEN"
        HMS_IB_BACKUP_DIR_FULLPATH="$HMS_IB_BACKUP_DIR/$HMS_IB_BACKUP_DIR_NAME"

		# directory where upgrade binary is copied to
		HMS_IB_UPGRADE_DIR="$4";

		# upgrade binary name
		HMS_IB_UPGRADE_WAR_NAME="$5";
		HMS_IB_UPGRADE_WAR_FULLPATH="$HMS_IB_UPGRADE_DIR/$HMS_IB_UPGRADE_WAR_NAME";

		# for rollback of OOB agent upgrade, if Aggregator upgrade fails.
		HMS_OOB_HOST="$6";
		REMOTE_HMS_OOB_SCRIPT_DIR_FULLPATH="$7";

        HMS_IB_VALIDATE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_VALIDATE_SCRIPT}";
        HMS_IB_BACKUP_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_BACKUP_SCRIPT}";
        HMS_IB_SERVICE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_SERVICE_SCRIPT}";
        HMS_IB_UPGRADE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_UPGRADE_SCRIPT}";
        HMS_IB_RECOVER_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_RECOVER_SCRIPT}";

        HMS_IB_UPGRADE_WAR_CHECKSUM_FILENAME="${HMS_IB_UPGRADE_WAR_NAME}.md5"
        HMS_OOB_ROLLBACK_API_PAYLOAD="{\"id\" : \"$HMS_TOKEN\",\"scriptsLocation\" : \"$REMOTE_HMS_OOB_SCRIPT_DIR_FULLPATH\"}"

        # HMS IB UpgradeStatus JSON file abs path
        HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH="$HMS_UPGRADE_DIR/$HMS_TOKEN.json"

        loginfo "HMS_TOKEN                          : $HMS_TOKEN"
        loginfo "HMS_SCRIPT_DIR                     : $HMS_SCRIPT_DIR"
        loginfo "HMS_IB_BACKUP_DIR                  : $HMS_IB_BACKUP_DIR"
        loginfo "HMS_IB_UPGRADE_DIR                 : $HMS_IB_UPGRADE_DIR"
        loginfo "HMS_IB_UPGRADE_WAR_NAME            : $HMS_IB_UPGRADE_WAR_NAME"
        loginfo "HMS_OOB_HOST                       : $HMS_OOB_HOST"
        loginfo "REMOTE_HMS_OOB_SCRIPT_DIR_FULLPATH : $REMOTE_HMS_OOB_SCRIPT_DIR_FULLPATH"

	else

		loginfo "FAILED: Required number of arguments must be passed before continuing.";
		return 9;
	fi
}

# Checks if all of the pre-requisites
function validate()
{
	#Check if the Hms OOB validation file exist
	local ibscript="$HMS_IB_VALIDATE_SCRIPT_FULLPATH"
	[[ -f "$ibscript" ]] || return 91

	#Grant Execution rights to all Scripts
	cd $HMS_SCRIPT_DIR/ && chmod +x *.sh || {

	   echo "Unable to provide execute rights to scripts at [ $HMS_SCRIPT_DIR ]";
	   return 92;
	}

	#Run HMS IB Validate script and Provide necessary details to IB script
	($HMS_IB_VALIDATE_SCRIPT_FULLPATH \
	       $HMS_SCRIPT_DIR \
	       $HMS_IB_BACKUP_SCRIPT \
	       $HMS_IB_SERVICE_SCRIPT \
	       $HMS_IB_UPGRADE_SCRIPT \
	       $HMS_IB_RECOVER_SCRIPT \
	       $WEBAPP_WAR_NAME \
	       $HMS_IB_UPGRADE_DIR \
	       $HMS_IB_UPGRADE_WAR_CHECKSUM_FILENAME) || {

	   echo "Error in validating HMS IB scripts and files";
	   return 94;
	}
}

function startHmsIB()
{
	($HMS_IB_SERVICE_SCRIPT_FULLPATH $START_TOKEN $CATALINA_BASE) || {

	   return 113;
	}
}

function stopHmsIB()
{
	($HMS_IB_SERVICE_SCRIPT_FULLPATH $STOP_TOKEN $CATALINA_BASE) || {

	   return 114;
	}
}

function restartHmsIB()
{
    loginfo "Restarting HMS Aggregator."
	#Stopping TC Server
	stopHmsIB || {
	   echo "Unable to Stop TC Service on HMS IB"
	   return 117;
	}

    #Start HMS IB
	startHmsIB || {
	   echo "Unable to Start HMS IB Service";
	   return 124;
	}
    loginfo "HMS Aggregator restarted successfully."
    return 0;
}

function hms_ib_backup()
{
	#After triggering upgrade from endpoint, This will be the last point for logs. after that no logs will be logged
	echo
	echo "*****************************End of Tomcat Log, because tomcat will be stopped NOW for upgrade.***********************"
	echo

	#Stopping TC Server
	stopHmsIB || {

	   echo "Unable to Stop TC Service on HMS IB"
	   return 117;
	}

	#Create HMS IB Backup
    ($HMS_IB_BACKUP_SCRIPT_FULLPATH $HMS_TOKEN $HMS_IB_BACKUP_DIR_FULLPATH $WEBAPP_WAR_NAME $WEBAPP_DIR_FULLPATH) || {

	   echo "Unable to create HMS IB Backup"
	   return 118;
	}
}

function hms_ib_upgrade()
{
	#call Hms IB Upgrade Script
	($HMS_IB_UPGRADE_SCRIPT_FULLPATH $HMS_IB_UPGRADE_WAR_FULLPATH $WEBAPP_WAR_FULLPATH) || {

	   echo "Unable to upgrade HMS IB";
	   return 123;
	}

	#Start HMS IB
	startHmsIB || {

	   echo "Unable to Start HMS IB Service";
	   return 124;
	}
}

function hms_oob_rollback()
{
	loginfo "##############################";
	loginfo "### HMS OOB RollBack Phase ###";
	loginfo "##############################";

    rollback_http_url="http://${HMS_OOB_HOST}:8448/api/1.0/hms/upgrade/rollback"
    rollback_response=`echo $HMS_OOB_ROLLBACK_API_PAYLOAD | curl -s -i --request POST --data @- --header "Content-Type:application/json" ${rollback_http_url}`
    rollback_http_code=`echo $rollback_response | head -n 1 | cut -d$' ' -f2`
    loginfo "\n$rollback_response"

    # check that oob agent has accepted the request for rollback of upgrade
    if [[ ! -z ${rollback_http_code+x} ]] && [[ $rollback_http_code -eq 202 ]]; then

        loginfo "Initiated rollback of OOB agent upgrade !!!"

    else

        loginfo "Unable to initiate rollback of OOB agent upgrade !!!"
        return 126;
    fi

	loginfo "Initiating HMS OOB Rollback successful."
}

function hms_ib_rollback()
{
	loginfo "##############################";
	loginfo "### HMS IB RollBack Phase ###";
	loginfo "##############################";

	stopHmsIB || {

	   echo "Unable to Stop TC Service on HMS IB"
	   return 131;
	}

	($HMS_IB_RECOVER_SCRIPT_FULLPATH \
	   $WEBAPP_DIR_FULLPATH \
	   "$HMS_IB_BACKUP_DIR_FULLPATH/$WEBAPP_NAME" \
	   $WEBAPP_WAR_FULLPATH \
	   "$HMS_IB_BACKUP_DIR_FULLPATH/$WEBAPP_WAR_NAME") || {

	   echo "Unable to rollback HMS IB from backup: $HMS_IB_BACKUP_DIR_FULLPATH";
	   return 132;
	}

	#Start HMS IB
	startHmsIB || {

	   echo "Unable to Start HMS IB Service after rollback !!!";
	   return 133;
	}
	loginfo "Hms IB Rollback successful"
}

# Log & Update HMS upgrade status in db and exit with specified code
function fatal_exit()
{
	case "$3" in
	RESTORE_OOB)
        hms_oob_rollback || {
            save_upgrade_status "$HMS_TOKEN" "HMS_ROLLBACK_FAILED"|| {
                loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH after OOB Rollback failed."
            }
		    loginfo "HMS OOB RollBack : Failed" $?;
		}
        save_upgrade_status "$HMS_TOKEN" "HMS_ROLLBACK_SUCCESS"|| {
            loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH after OOB Rollback succeeded."
        }
        # Delete HMS Upgrade Bundle and Scripts Directory
        delete_ib_upgrade_directory
        #NOTE:
        # 1. IB backup is not done yet. Hence nothing to delete.
        # 2. OOB backup does not exist as OOB upgrade is rollbacked.
		;;
	RESTORE_IB_OOB)
		hms_oob_rollback || {
            save_upgrade_status "$HMS_TOKEN" "HMS_ROLLBACK_FAILED"|| {
                loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH after OOB Rollback failed."
            }
		    loginfo "HMS OOB RollBack : Failed" $?;
		}
		hms_ib_rollback || {
            save_upgrade_status "$HMS_TOKEN" "HMS_ROLLBACK_FAILED"|| {
                loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH after IB Rollback failed."
            }
		    loginfo "HMS IB RollBack : Failed" $?;
		}
        save_upgrade_status "$HMS_TOKEN" "HMS_ROLLBACK_SUCCESS"|| {
            loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH after IB and OOB Rollback succeeded."
        }
        # Delete HMS Upgrade Bundle and Scripts Directory
        delete_ib_upgrade_directory

        #NOTE:
        # 1. IB and OOB backups have been rollbacked.
		;;
	esac
	loginfo "$1 Exit Code: $2"

	echo "***************************************************************"
	echo "HMS Upgrade: Failed @ `date`"
	echo "***************************************************************"
	exit $2
}

# Gets Last Upgrade Status of HMS OOB.
# This assumes that an empty file with the passed HMS Token should be present in remote hms directory, if it was successfully upgraded.
function getLastUpgradeStatus_HmsOOB()
{
	# Wait before querying if the HMS OOB is up and responsive.
	loginfo "Waiting for $HMS_QUERY_WAIT seconds, before querying HMS OOB response"
	sleep $HMS_QUERY_WAIT
	about_http_url="http://${HMS_OOB_HOST}:8448/api/1.0/hms/about"
	about_response=`curl ${about_http_url}`

	loginfo "Checking for HMS OOB Response";
	# Checking if the HMS OOB is responsive, by hitting its /about endpoint.
	# If it is responsive, the following will execute normally, otherwise, it will exit with returncode
	echo ${about_response} | grep -iq "buildVersion" || {
        return 134;
	}
	return 0;
}

# Gets Last updated status of HMS IB
function getLastUpgradeStatus_HmsIB()
{
	# Wait before querying if the HMS IB is up and responsive.
	loginfo "Waiting for $HMS_QUERY_WAIT seconds, before querying HMS IB response"
	sleep $HMS_QUERY_WAIT
	about_http_url="http://127.0.0.1:8080/hms-local/api/1.0/hms/about"
	about_response=`curl -s ${about_http_url}`

	loginfo "Checking for HMS IB Response";
	# Checking if the HMS IB is responsive, by hitting its /about endpoint.
	# If it is responsive, the following will execute normally, otherwise, it will exit with returncode
	echo ${about_response} | grep -iq "buildVersion" || {
        return 135;
    }
	return 0;
}

# Function saves the upgrade status as upgrade_status.json
# It expects below arguments.
#   1. HMS_TOKEN
#   2. UpgradeStatusCode
#           HMS_UPGRADE_INVALID_REQUEST
#           HMS_UPGRADE_FORBIDDEN
#           HMS_UPGRADE_INTERNAL_ERROR
#           HMS_UPGRADE_INITIATED
#           HMS_UPGRADE_SCRIPT_INVALIDARGUMENTS
#           HMS_UPGRADE_SCRIPT_VALIDATION_FAILED
#           HMS_UPGRADE_SUCCESS
#           HMS_UPGRADE_FAILED
#           HMS_BACKUP_FAILED
#           HMS_ROLLBACK_FAILED
#           HMS_ROLLBACK_SUCCESS
function save_upgrade_status()
{
    id=$1
    status_code=$2
    upgrade_status_json="{\"id\":\"$id\",\"statusCode\":\"$status_code\"}"
    echo $upgrade_status_json > $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH
}

function delete_directory()
{
    dir=$1
    dir_type=$2
    if [ -d $dir ]; then
        rm -rf $dir
        if [ $? -eq 0 ]; then
            loginfo "HMS $dir_type directory - $dir deleted."
        else
            loginfo "Failed to delete HMS $dir_type directory - $dir"
        fi
    else
        loginfo "Failed to delete HMS $dir_type directory - $dir"
    fi
}

function delete_ib_upgrade_directory()
{
    if [ ! -z $HMS_IB_UPGRADE_DIR -a -d $HMS_IB_UPGRADE_DIR ]; then
        delete_directory $HMS_IB_UPGRADE_DIR upgrade
    else
        loginfo "HMS_IB_UPGRADE_DIR is either not set or not a directory."
    fi
    if [ ! -z $HMS_SCRIPT_DIR -a -d $HMS_SCRIPT_DIR ]; then
        delete_directory $HMS_SCRIPT_DIR script
    else
        loginfo "HMS_SCRIPT_DIR is either not set or not a directory."
    fi
}

function delete_ib_backup_directory()
{
    if [ ! -z $HMS_IB_BACKUP_DIR_FULLPATH -a -d $HMS_IB_BACKUP_DIR_FULLPATH ]; then
        delete_directory $HMS_IB_BACKUP_DIR_FULLPATH backup
    else
        loginfo "HMS_IB_BACKUP_DIR_FULLPATH is either not set or not a directory."
    fi
}

function delete_oob_backup()
{
	loginfo "###################################";
	loginfo "### HMS OOB Backup Delete Phase ###";
	loginfo "###################################";

    backup_deletehttp_url="http://${HMS_OOB_HOST}:8448/api/1.0/hms/upgrade/backup/$HMS_TOKEN"
    backup_deleteresponse=`curl -s -i --request DELETE ${backup_deletehttp_url}`
    backup_deletehttp_code=`echo $backup_deleteresponse | head -n 1 | cut -d$' ' -f2`
    loginfo "\n$backup_deleteresponse"

    # check that oob agent has accepted the request for rollback of upgrade
    if [[ ! -z ${backup_deletehttp_code+x} ]] && [[ $backup_deletehttp_code -eq 200 ]]; then
        loginfo "HMS OOB Backup deleted."
    else
        loginfo "Failed to delete HMS OOB Backup."
    fi
}

##ENTRY POINT
echo "***********************************************************************"
echo "HMS Upgrade: Starting HMS Aggregator update - `date`"
echo "***********************************************************************"

# log PID
loginfo "PID: $$"

# 0. Check Arguments first to see if any HMS Token has been provided.
check_arguments "$@" || {

    # when check_arguments fails, declared variables are not initialized.
    # use first argument if it is passed to save upgrade status
    if [[ ! -z "$1" ]]; then
        HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH="$HMS_UPGRADE_DIR/$1.json"
        save_upgrade_status "$1" "HMS_UPGRADE_SCRIPT_INVALIDARGUMENTS" || {
            loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
        }
    fi

    # Restart HMS Aggregator, as it is in MAINTENANCE mode.
    restartHmsIB || {
        loginfo "Failed to restart HMS Aggregator."
    }
    fatal_exit "HMS Arguments Check: Failed" $? RESTORE_OOB;
}

# 1. Validate HMS IB
validate || {

    save_upgrade_status "$HMS_TOKEN" "HMS_UPGRADE_SCRIPT_VALIDATION_FAILED" || {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }

    # Restart HMS Aggregator, as it is in MAINTENANCE mode.
    restartHmsIB || {
        loginfo "Failed to restart HMS Aggregator."
    }
    fatal_exit "HMS Validation Check: Failed" $? RESTORE_OOB;
}

# Wait for HMS_WAIT_BEFORE_UPGRADE seconds. This is to ensure that running HMS to respond to the caller
# that upgrade has been initiated. (Before it gets killed during upgrade process.)
loginfo "Waiting for $HMS_WAIT_BEFORE_UPGRADE seconds before starting upgrade."
sleep $HMS_WAIT_BEFORE_UPGRADE

# 2. HMS IB Backup Phase
hms_ib_backup || {

    save_upgrade_status "$HMS_TOKEN" "HMS_BACKUP_FAILED" || {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }

    # Restart HMS Aggregator, as it is in MAINTENANCE mode.
    restartHmsIB || {
        loginfo "Failed to restart HMS Aggregator."
    }
    fatal_exit "HMS IB Backup Phase: Failed" $? RESTORE_OOB;
}

# 3. HMS IB Upgrade Phase
hms_ib_upgrade || {

    save_upgrade_status "$HMS_TOKEN" "HMS_UPGRADE_FAILED"|| {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    fatal_exit "HMS IB Upgrade Phase: Failed" $? RESTORE_IB_OOB;
}

# 4. Check if HMS IB was able to boot up successfully.
getLastUpgradeStatus_HmsIB || {

    save_upgrade_status "$HMS_TOKEN" "HMS_UPGRADE_FAILED"|| {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    fatal_exit "HMS IB upgrade status: Failed" $? RESTORE_IB_OOB;
}

save_upgrade_status "$HMS_TOKEN" "HMS_UPGRADE_SUCCESS"|| {
    loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
}

# Delete HMS OOB Backup
delete_oob_backup

# Delete HMS Aggregator backup
delete_ib_backup_directory

# Delete HMS Upgrade Bundle and Scripts Directory
delete_ib_upgrade_directory

echo
echo "***********************************************************************"
echo "HMS Upgrade: HMS Aggregator upgraded successfully - `date`"
echo "***********************************************************************"

exit 0;
