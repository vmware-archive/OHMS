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

# HMS upgrade binary uploaded location
declare HMS_OOB_BINARY_LOCATION;

# Full Path to Lighttpd install directory
declare LIGHTTPD_DIR_FULLPATH;

# Lighttpd restart script
declare LIGHTTPD_RESTART_SCRIPT;

# Lighttpd restart script abs path
declare LIGHTTPD_RESTART_SCRIPT_FULLPATH;
# *********************************************************************************
#                           variable initializations
# *********************************************************************************

# hms parent directory
HMS_PARENT_DIR_FULLPATH="/opt/vrack"

#Hms oob Service Script file that will start stop and query running HMS OOB
HMS_OOB_SERVICE_SCRIPT="hms_oob_service.sh"

# Full Path to Lighttpd install directory
LIGHTTPD_DIR_FULLPATH="/etc/lighttpd"

# Lighttpd startup script
LIGHTTPD_RESTART_SCRIPT="hms_oob_restart_lighttpd.sh"

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

function restart_lighttpd()
{
    ($LIGHTTPD_RESTART_SCRIPT_FULLPATH) || {
    return 201;
    }
}

# Function to check and set if any hmsbuild token has been passed while creating it
function check_arguments()
{
	if [ "$#" -ge 4 ]
	then
		TOKEN="$1";
        HMS_SCRIPT_DIR="$2"
		BACKUP_DIR_FULLPATH="$3"
		HMS_DIR_FULLPATH="$4"

        loginfo "TOKEN: $TOKEN"
        loginfo "HMS_SCRIPT_DIR: $HMS_SCRIPT_DIR"
        loginfo "BACKUP_DIR_FULLPATH: $BACKUP_DIR_FULLPATH"
        loginfo "HMS_DIR_FULLPATH: $HMS_DIR_FULLPATH"

        HMS_OOB_BINARY_LOCATION="${HMS_SCRIPT_DIR}"
        HMS_OOB_SERVICE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_OOB_SERVICE_SCRIPT}"
        LIGHTTPD_RESTART_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${LIGHTTPD_RESTART_SCRIPT}"

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

    loginfo "Restoring existing logs to $BACKUP_DIR_FULLPATH"
    mkdir -p $BACKUP_DIR_FULLPATH/logs/
    mv -f "$HMS_DIR_FULLPATH/logs/" $BACKUP_DIR_FULLPATH
    if [ $? -eq 0 ]; then
        loginfo "Restored existing logs to $BACKUP_DIR_FULLPATH"
    else
        loginfo "Failed to restore existing logs to $BACKUP_DIR_FULLPATH"
    fi

	loginfo "Removing failed HMS OOB build."
	rm -r -f $HMS_DIR_FULLPATH/* || {
	   echo "Unable to remove failed HMS OOB Directory [ $HMS_DIR_FULLPATH ]";
	   return 160;
	}

	loginfo "Restoring HMS OOB from backup [ $BACKUP_DIR_FULLPATH ]"
	mv $BACKUP_DIR_FULLPATH/* $HMS_DIR_FULLPATH || {
	   echo "Unable to restore from Backup. Backup HMS OOB dir [ $BACKUP_DIR_FULLPATH ]";
	   return 161;
	}

    rm -Rf $BACKUP_DIR_FULLPATH || {
        echo "unable to remove the backup directory [ $BACKUP_DIR_FULLPATH] ";
        return 313;
    }

    loginfo "restoring Lighttpd config from [ $LIGHTTPD_DIR_FULLPATH/backup ]"
    if [[ -f "$LIGHTTPD_DIR_FULLPATH/backup/lighttpd.conf" ]]; then
        cp $LIGHTTPD_DIR_FULLPATH/backup/lighttpd.conf $LIGHTTPD_DIR_FULLPATH
        echo "lighttpd config restored from Backup. Lighttpd backup dir [ $LIGHTTPD_DIR_FULLPATH/backup ]";
    else
        echo "Unable to restore from Backup. Lighttpd backup dir [ $LIGHTTPD_DIR_FULLPATH/backup ]";
    fi

    loginfo "remove Lighttpd backup directory from [ $LIGHTTPD_DIR_FULLPATH ]"
    rm -Rf $LIGHTTPD_DIR_FULLPATH/backup || {
        echo "Unable to remove the backup directory from [ $LIGHTTPD_DIR_FULLPATH ]";
        return 300;
    }

    #loginfo "remove Lighttpd config from [ $LIGHTTPD_DIR_FULLPATH ]"
    #rm -f $LIGHTTPD_DIR_FULLPATH/lighttpd-misc.conf || {
    #    echo "Unable to remove the lighttpd-misc.conf from [ $LIGHTTPD_DIR_FULLPATH ]";
    #    return 314;
    #}

    #Start HMS OOB
    startHmsOOB || {
    echo "Unable to Start HMS OOB Service after Rollback.";
    return 127;
    }

    # Restarts Lighttpd after config restore
    restart_lighttpd || {
    echo "Unable to restart lighttpd after Rollback.";
    return 302;
    }

	loginfo "Rolled back to working HMS OOB state"
}

# Log & Update HMS upgrade status in db and exit with specified code
function fatal_exit()
{
    # delete upgrade bundle and upgrade scripts
    delete_upgrade_files

    loginfo "$1 Exit Code: $2"
	echo
	echo "***************************************************************"
	echo "HMS OOB Upgrade: RollBack Phase Failed @ `date`"
	echo "***************************************************************"
	exit $2
}

# Deletes Upgrade Scripts and Upgrade Bundle
function delete_upgrade_files()
{
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

# delete upgrade bundle and upgrade scripts
# delete_upgrade_files

echo
echo "***************************************************************"
echo "HMS OOB Upgrade: Rollback Phase completed Successfully @ `date`"
echo "***************************************************************"


exit 0
