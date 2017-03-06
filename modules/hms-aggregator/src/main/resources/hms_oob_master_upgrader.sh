#!/bin/bash

declare HMS_TOKEN;
declare HMS_OOB_CHECKSUM_FILENAME;
declare HMS_OOB_BINARY_LOCATION;
declare HMS_OOB_BINARY_FILENAME;
declare HMS_OOB_BINARY_FULLPATH;
declare HMS_OOB_BINARY_CHECKSUM_FILENAME;
declare HMS_OOB_BINARY_CHECKSUM_FULLPATH;

declare HMS_SCRIPT_DIR;
declare HMS_OOB_BACKUP_SCRIPT_FULLPATH;
declare HMS_OOB_SERVICE_SCRIPT_FULLPATH;
declare HMS_OOB_UPGRADE_SCRIPT_FULLPATH;
declare HMS_OOB_RECOVER_SCRIPT_FULLPATH;
declare HMS_OOB_VALIDATE_SCRIPT_FULLPATH;
declare LIGHTTPD_RESTART_SCRIPT_FULLPATH;

# Remote upgrade directory
declare HMS_OOB_UPGRADE_DIR_NAME;

# Full path to upgraded Hms Directory
declare HMS_OOB_UPGRADE_DIR_FULLPATH;

# Remote Hms Backup Directory
declare HMS_OOB_BACKUP_DIR_NAME;

# Full Path to remote Hms Backup Directory
declare HMS_OOB_BACKUP_DIR_FULLPATH;

# HMS OOB UpgradeStatus JSON file absoulte path
declare HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH;

# Full Path to Lighttpd install directory
declare LIGHTTPD_DIR_FULLPATH;

################CHANGE HERE FOR ENVIRONMENT SETUP#####################
HMS_QUERY_WAIT=15;
HMS_WAIT_BEFORE_UPGRADE=30;
HMS_DIR_NAME="hms"
HMS_PARENT_DIR_FULLPATH="/opt/vrack"
HMS_UPGRADE_DIR_FULLPATH="/opt/vrack/upgrade"
HMS_DIR_FULLPATH="$HMS_PARENT_DIR_FULLPATH/$HMS_DIR_NAME"
LIGHTTPD_DIR_FULLPATH="/etc/lighttpd"

########################### Vars related to scripts and validation ###############
HMS_OOB_HOST="localhost"

# Hms oob Validate script file
HMS_OOB_VALIDATE_SCRIPT="hms_oob_validate.sh";

# Hms oob Backup script file
HMS_OOB_BACKUP_SCRIPT="hms_oob_backup.sh"

#Hms oob Service Script file that will start stop and query running HMS OOB
HMS_OOB_SERVICE_SCRIPT="hms_oob_service.sh"

#Hms oob Upgrade Script
HMS_OOB_UPGRADE_SCRIPT="hms_oob_upgrade.sh"

#Hms oob recover script that will recover from the previous running build.
HMS_OOB_RECOVER_SCRIPT="hms_oob_recover.sh"

#Hms oob Upgrade Script
LIGHTTPD_RESTART_SCRIPT="hms_oob_restart_lighttpd.sh"

# Valid Tokens for services
STOP_TOKEN="stop"
START_TOKEN="start"
QUERY_TOKEN="status"

CURL_OPTIONS=' -k '

######################Vars for remote HMS OOB  ##########################

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
    if [ "$#" -ge 5 ]
    then
        loginfo "### Required number of arguments passed.";
        HMS_TOKEN="$1";
        HMS_SCRIPT_DIR="$2";
        HMS_OOB_BINARY_LOCATION="$3";
        HMS_OOB_BINARY_FILENAME="$4";
        HMS_OOB_BINARY_CHECKSUM_FILENAME="$5";

        HMS_OOB_BINARY_FULLPATH="${HMS_OOB_BINARY_LOCATION}/${HMS_OOB_BINARY_FILENAME}";
        HMS_OOB_BINARY_CHECKSUM_FULLPATH="$HMS_OOB_BINARY_LOCATION/$HMS_OOB_BINARY_CHECKSUM_FILENAME";

        loginfo "HMS_SCRIPT_DIR                     : $HMS_SCRIPT_DIR"
        loginfo "HMS_TOKEN                          : $HMS_TOKEN"
        loginfo "HMS_OOB_BINARY_LOCATION            : $HMS_OOB_BINARY_LOCATION"
        loginfo "HMS_OOB_BINARY_FILENAME            : $HMS_OOB_BINARY_FILENAME"
        loginfo "HMS_OOB_BINARY_CHECKSUM_FILENAME   : $HMS_OOB_BINARY_CHECKSUM_FILENAME"

        HMS_OOB_UPGRADE_DIR_NAME="hms_upgrade_$HMS_TOKEN";
        HMS_OOB_UPGRADE_DIR_FULLPATH="$HMS_PARENT_DIR_FULLPATH/$HMS_OOB_UPGRADE_DIR_NAME";

        HMS_OOB_BACKUP_DIR_NAME="hms_backup_$HMS_TOKEN"
        HMS_OOB_BACKUP_DIR_FULLPATH="$HMS_PARENT_DIR_FULLPATH/$HMS_OOB_BACKUP_DIR_NAME"

        # HMS OOB absolute path to Backup Script
        HMS_OOB_VALIDATE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_OOB_VALIDATE_SCRIPT}";

        # HMS OOB absolute path to Backup Script
        HMS_OOB_BACKUP_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_OOB_BACKUP_SCRIPT}"

        # HMS OOB absolute path to Service Script
        HMS_OOB_SERVICE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_OOB_SERVICE_SCRIPT}"

        # HMS OOB absolute path to Upgrade Script
        HMS_OOB_UPGRADE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_OOB_UPGRADE_SCRIPT}"

        # HMS OOB absolute path to Recover Script
        HMS_OOB_RECOVER_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_OOB_RECOVER_SCRIPT}"

        # HMS OOB UpgradeStatus JSON file abs path
        HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH="$HMS_UPGRADE_DIR_FULLPATH/$HMS_TOKEN.json"

        # LIGHTTPD RESTART script file abs path
        LIGHTTPD_RESTART_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${LIGHTTPD_RESTART_SCRIPT}"
    else

        loginfo "FAILED: Required number of arguments must be passed before continuing.";
        return 9;

    fi
}

# Checks if all of the pre-requisites
function validate()
{
    #Check if the Hms OOB validation file exist
    local oobscript="$HMS_OOB_VALIDATE_SCRIPT_FULLPATH"
    if [[ -f "$oobscript" ]]; then

        loginfo "HMS OOB validation file $HMS_OOB_VALIDATE_SCRIPT_FULLPATH exists."
        #Run HMS OOB Validate script and Provide necessary details to OOB script
        ($HMS_OOB_VALIDATE_SCRIPT_FULLPATH $HMS_SCRIPT_DIR $HMS_OOB_BACKUP_SCRIPT $HMS_OOB_SERVICE_SCRIPT $HMS_OOB_UPGRADE_SCRIPT $HMS_OOB_RECOVER_SCRIPT $HMS_OOB_BINARY_FILENAME $HMS_OOB_BINARY_LOCATION $HMS_OOB_BINARY_CHECKSUM_FILENAME) || {
            echo "Error in validating HMS OOB scripts and files";
            return 93;
        }
    else
        return 90
    fi
}

# starts hms oob agent
function startHmsOOB()
{
    ($HMS_OOB_SERVICE_SCRIPT_FULLPATH $START_TOKEN $HMS_DIR_FULLPATH) || {
    return 111;
    }
}

# stops hms oob agent
function stopHmsOOB()
{
    ($HMS_OOB_SERVICE_SCRIPT_FULLPATH $STOP_TOKEN $HMS_DIR_FULLPATH) || {
    return 112;
    }
}

# Restarts lighttpd
function restart_lighttpd()
{
    #Check if the restart lighttpd script file exist
    local restartlighttpdscript="$LIGHTTPD_RESTART_SCRIPT_FULLPATH"
    if [[ -f "$restartlighttpdscript" ]]; then
        loginfo "restart lighttpd script exists."

        #execute restart lighttpd script
        ($LIGHTTPD_RESTART_SCRIPT_FULLPATH) || {
            echo "Error in restarting lighttpd";
            return 194;
        }
    else
        return 195
    fi
}

# takes backup of oob agent
function hms_oob_backup()
{
    #Stopping service before creating backup
    stopHmsOOB || {
        echo "Unable to Stop HMS OOB Service.";
        return 115;
    }

    #Creating OOB Backup
    ($HMS_OOB_BACKUP_SCRIPT_FULLPATH $HMS_TOKEN $HMS_DIR_FULLPATH $HMS_OOB_BACKUP_DIR_FULLPATH) || {

        echo "Unable to Create HMS OOB Backup.";
        #Start HMS OOB
        startHmsOOB || {
            echo "Unable to Start HMS OOB Service after backup failed.";
            return 116;
        }
        return 117;
    }
}

# upgrades oob agent
function hms_oob_upgrade()
{
    #Call HMS OOB upgrade script
    ($HMS_OOB_UPGRADE_SCRIPT_FULLPATH $HMS_TOKEN $HMS_OOB_BACKUP_DIR_FULLPATH $HMS_OOB_BINARY_FULLPATH $HMS_DIR_FULLPATH $HMS_OOB_UPGRADE_DIR_FULLPATH) || {
        echo "Unable to upgrade HMS OOB.";
        return 119;
    }

    #Start HMS OOB
    startHmsOOB || {
        echo "Unable to Start HMS OOB Service.";
        return 120;
    }
}

# takes backup of Lighttpd config
function lighttpd_backup()
{
    mkdir -p $LIGHTTPD_DIR_FULLPATH/backup || { echo "Failed to create Lighttpd backup dir [ $LIGHTTPD_DIR_FULLPATH/backup ]"; return 190; }
    cp $LIGHTTPD_DIR_FULLPATH/lighttpd.conf $LIGHTTPD_DIR_FULLPATH/backup || { echo "unable to take the backup of lighttpd.conf file"; return 191; }

    loginfo "lighttpd.conf backup is created under $LIGHTTPD_DIR_FULLPATH/backup"
}

# upgrades Lighttpd
function lighttpd_upgrade()
{
    loginfo "applying lighttpd upgrade configs"

    cp $HMS_DIR_FULLPATH/upgrade-config/lighttpd.conf $LIGHTTPD_DIR_FULLPATH || {
        echo "Unable to copy lighttpd.conf!!!";
        return 192;
    }

    cp $HMS_DIR_FULLPATH/upgrade-config/lighttpd-misc.conf $LIGHTTPD_DIR_FULLPATH || {
        echo "Unable to copy lighttpd-misc.conf!!!";
        return 193;
    }

    loginfo "lighttpd configuration has been upgraded"
}

# rollbacks oob agent upgrade
function hms_oob_rollback()
{
    loginfo "##############################";
    loginfo "### HMS OOB RollBack Phase ###";
    loginfo "##############################";

    #Stopping service before rollback(if running)
    stopHmsOOB || {
        echo "Unable to Stop HMS OOB Service.";
        return 125;
    }

    #Call Rollback Script
    ($HMS_OOB_RECOVER_SCRIPT_FULLPATH $HMS_TOKEN $HMS_SCRIPT_DIR $HMS_OOB_BACKUP_DIR_FULLPATH $HMS_DIR_FULLPATH) || {

        save_upgrade_status "$HMS_TOKEN" "HMS_OOB_ROLLBACK_FAILED"|| {
            loginfo "Unable to save upgrade status to - $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
        }
        echo "Unable to rollback HMS OOB"
        return 126;
    }

    #Start HMS OOB
    startHmsOOB || {
        echo "Unable to Start HMS OOB Service after Rollback.";
        return 127;
    }

    save_upgrade_status "$HMS_TOKEN" "HMS_OOB_ROLLBACK_SUCCESS"|| {
        loginfo "Unable to save upgrade status to - $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    loginfo "HMS OOB Rollback successful"
}

# Log & Update HMS upgrade status in db and exit with specified code
function fatal_exit()
{
    case "$3" in
    RESTORE_OOB)
        hms_oob_rollback || {
            loginfo "HMS OOB RollBack : Failed: " $?;
        }
        ;;
    esac

    # delete upgrade files
    delete_upgrade_files

    loginfo "$1 Exit Code: $2"
    loginfo "***************************************************************"
    loginfo "HMS OOB Upgrade: Failed - `date`"
    loginfo "***************************************************************"
    exit $2
}

# Gets Last Upgrade Status of HMS OOB.
# This assumes that an empty file with the passed HMS Token should be present in remote hms directory, if it was successfully upgraded.
function getLastUpgradeStatus_HmsOOB()
{
    # Wait before querying if the HMS OOB is up and responsive.
    loginfo "Waiting for $HMS_QUERY_WAIT seconds, before querying HMS OOB response"
    sleep $HMS_QUERY_WAIT
    about_http_url="https://${HMS_OOB_HOST}:8450/api/1.0/hms/about"
    about_response=`curl ${CURL_OPTIONS} ${about_http_url}`

    loginfo "Checking for HMS OOB Response";
    # Checking if the HMS OOB is responsive, by hitting its /about endpoint.
    # If it is responsive, the following will execute normally, otherwise, it will exit with returncode
    echo ${about_response} | grep -iq "buildVersion" || {
        return 134;
    }
    loginfo "Got valid response from HMS OOB.";
    return 0;
}

# Function saves the upgrade status as upgrade_status.json
# It expects below arguments.
#   1. HMS_TOKEN
#   2. UpgradeStatusCode
#           HMS_OOB_UPGRADE_SCRIPT_INVALIDARGUMENTS
#           HMS_OOB_UPGRADE_SCRIPT_VALIDATION_FAILED
#           HMS_OOB_UPGRADE_SUCCESS
#           HMS_OOB_UPGRADE_FAILED
#           HMS_OOB_BACKUP_FAILED
#           HMS_OOB_ROLLBACK_FAILED
#           HMS_OOB_ROLLBACK_SUCCESS
#
function save_upgrade_status()
{
    id=$1
    status_code=$2
    upgrade_status_json="{\"id\":\"$id\",\"statusCode\":\"$status_code\"}"
    echo $upgrade_status_json > $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH
}

# Deletes Upgrade Scripts and Upgrade Bundle
function delete_upgrade_files()
{
    # delete upgrade bundle
    if [ ! -z $HMS_OOB_BINARY_FULLPATH -a -f $HMS_OOB_BINARY_FULLPATH ]; then
        rm -f $HMS_OOB_BINARY_FULLPATH
        if [ $? -eq 0 ]; then
            loginfo "HMS OOB Upgrade Binary $HMS_OOB_BINARY_FULLPATH deleted successfully."
        else
            loginfo "Failed to delete HMS OOB Upgrade Binary $HMS_OOB_BINARY_FULLPATH ."
        fi
    else
        if [ ! -z $HMS_OOB_BINARY_FULLPATH ]; then
            loginfo "HMS OOB Upgrade Binary - $HMS_OOB_BINARY_FULLPATH either does not exist or not a file."
        else
            loginfo "HMS_OOB_BINARY_FULLPATH is not a set."
        fi
    fi

    # delete upgrade scripts
    upgrade_scripts=(`ls $HMS_SCRIPT_DIR/*.sh 2>/dev/null`)
    if [ ${#upgrade_scripts[@]} -gt 0 ]; then
        if [ "$HMS_SCRIPT_DIR" != "$HMS_DIR_FULLPATH" ]; then
            rm -rf $HMS_SCRIPT_DIR
            if [ $? -eq 0 ]; then
                loginfo "HMS upgrade scripts directory $HMS_SCRIPT_DIR deleted."
            else
                loginfo "Failed to delete HMS upgrade scripts directory $HMS_SCRIPT_DIR"
            fi
	    else
	        loginfo "HMS_SCRIPT_DIR is HMS_DIR_FULLPATH - $HMS_DIR_FULLPATH. not deleting"
        fi
    else
        loginfo "No upgrade scripts found at - $HMS_SCRIPT_DIR"
    fi

    # delete upgrade bundle location
    if [ ! -z $HMS_OOB_BINARY_LOCATION -a -d $HMS_OOB_BINARY_LOCATION ]; then
        if [ "$HMS_OOB_BINARY_LOCATION" != "$HMS_DIR_FULLPATH" ]; then
            rm -rf $HMS_OOB_BINARY_LOCATION
            if [ $? -eq 0 ]; then
                loginfo "HMS Upgrade Binary Location - $HMS_OOB_BINARY_LOCATION deleted successfully."
            else
                loginfo "Failed to delete HMS Upgrade Binary Location - $HMS_OOB_BINARY_LOCATION."
            fi
        fi
    fi
}

##ENTRY POINT
loginfo "***********************************************************************"
loginfo "HMS OOB Upgrade: Starting HMS OOB Upgrade - `date`"
loginfo "***********************************************************************"

# log PID
loginfo "PID: $$"

# 0. Check Arguments first to see if any HMS Token has been provided.
check_arguments "$@" || {

    # when check_arguments fails, declared variables are not initialized.
    if [[ ! -z "$1" ]]; then

        # Get current script directory
        SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
        HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH="$HMS_UPGRADE_DIR_FULLPATH/$1.json"
        save_upgrade_status "$1" "HMS_OOB_UPGRADE_SCRIPT_INVALIDARGUMENTS" || {
            loginfo "Unable to save upgrade status to - $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
        }
    fi
    fatal_exit "HMS Arguments Check: Failed" $? NO_UNDO_ACTION;
}

# 1. Validate HMS OOB and HMS IB
validate || {
    save_upgrade_status "$HMS_TOKEN" "HMS_OOB_UPGRADE_SCRIPT_VALIDATION_FAILED" || {
        loginfo "Unable to save upgrade status to - $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    fatal_exit "HMS Validation Check: Failed" $? NO_UNDO_ACTION;
}

# Wait for HMS_WAIT_BEFORE_UPGRADE seconds. This is to ensure that running HMS App to respond to the caller
# that upgrade has been initiated. (Before it gets killed during upgrade process.)
loginfo "Waiting for $HMS_WAIT_BEFORE_UPGRADE seconds before starting upgrade."
sleep $HMS_WAIT_BEFORE_UPGRADE

# 2. HMS OOB Backup Phase
hms_oob_backup || {
    save_upgrade_status "$HMS_TOKEN" "HMS_OOB_BACKUP_FAILED" || {
        loginfo "Unable to save upgrade status to - $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    fatal_exit "HMS OOB Backup Phase: Failed" $? RESTORE_OOB;
}

# 3. Lighttpd Backup Phase
lighttpd_backup || {
    save_upgrade_status "$HMS_TOKEN" "LIGHTTPD_BACKUP_FAILED" || {
        loginfo "Unable to save upgrade status to - $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    fatal_exit "Lighttpd Backup Phase: Failed" $? RESTORE_OOB;
}

# 4. HMS OOB Upgrade Phase
hms_oob_upgrade || {
    save_upgrade_status "$HMS_TOKEN" "HMS_OOB_UPGRADE_FAILED"|| {
        loginfo "Unable to save upgrade status to - $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    fatal_exit "HMS OOB Upgrade Phase: Failed" $? RESTORE_OOB;
}

# 5. Lighttpd Upgrade Phase (e.g. Lighttpd)
lighttpd_upgrade || {
    save_upgrade_status "$HMS_TOKEN" "LIGHTTPD_UPGRADE_FAILED"|| {
        loginfo "Unable to save upgrade status to - $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    fatal_exit "Lighttpd Upgrade Phase: Failed" $? RESTORE_OOB;
}

# 6. Restarts Lighttpd after config upgrade
#restart_lighttpd || {
#    save_upgrade_status "$HMS_TOKEN" "LIGHTTPD_RESTART_FAILED"|| {
#        loginfo "Unable to save upgrade status to - $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
#    }
#    fatal_exit "lighttpd Restart Phase: Failed" $? RESTORE_OOB;
#}

# 7. Check if HMS OOB started afer upgrade
# This check is not done because, on the out-of-band agent curl command is not available.
#getLastUpgradeStatus_HmsOOB || {
#fatal_exit "HMS OOB upgrade status: Failed" $? RESTORE_OOB;
#}

save_upgrade_status "$HMS_TOKEN" "HMS_OOB_UPGRADE_SUCCESS"|| {
    loginfo "Unable to save upgrade status to - $HMS_OOB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
}

# delete upgrade files
# delete_upgrade_files

loginfo
loginfo "***********************************************************************"
loginfo "HMS OOB Upgrade: HMS-OOB upgraded successfully - `date`"
loginfo "***********************************************************************"

exit 0;
