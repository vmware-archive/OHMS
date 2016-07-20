#!/bin/bash

############### For future reference #################

# Token Provided
declare TOKEN;

# IB upgrade dir where upgrade war files reside
declare HMS_IB_BACKUP_DIR;

# IB Upgrade war name
declare HMS_IB_WAR_NAME;

# Absolute path to HMS IB war file
declare HMS_IB_UPGRADE_WAR_FULLPATH;

# Absolute path to HMS IB Webapp dir running in Tomcat
declare WEBAPP_DIR_FULLPATH;

# Absolute path to .war file in HMS IB webapp dir
declare WEBAPP_WAR_FULLPATH;

# Absolute path to the backup dir inside upgrade dir, which will hold backup of HMS IB webapp and .war file from Catalina dir
declare TMP_DIR;


# Log specified message with an optional exit code
function log()
{
    if [ -z $2 ]
    then
        echo "HMS-IB Upgrade: $1";
    else
        echo "HMS-IB Upgrade: $1 [$2]";
    fi
}

# Check for passed Arguments. Significant argument is HMS_TOKEN
function check_arguments()
{
	if [ "$#" -ge 4 ]
	then

		TOKEN="$1";
		HMS_IB_BACKUP_DIR="$2"
		HMS_IB_WAR_NAME="$3"
		WEBAPP_DIR_FULLPATH="$4"
		WEBAPP_WAR_FULLPATH="${WEBAPP_DIR_FULLPATH}.war"

	else

		log "FAILED: Required number of arguments must be passed before continuing.";
		return 59;
	fi
}

#Create HMS IB Backup
function createBackup()
{
    # delete if the backup directory already exists
    [[ -d $HMS_IB_BACKUP_DIR ]] && rm -rf $HMS_IB_BACKUP_DIR

    mkdir -p $HMS_IB_BACKUP_DIR || { echo "Failed to create dir: $HMS_IB_BACKUP_DIR"; return 200; }

	# move deployed webapp folder to backup folder
    mv -f $WEBAPP_DIR_FULLPATH $HMS_IB_BACKUP_DIR/ || {

        echo "Unable to backup existing webapp directory!";
        return 170;
    }

    # backup existing war to backup folder
    mv -f $WEBAPP_WAR_FULLPATH $HMS_IB_BACKUP_DIR || {

        echo "Unable to backup existing webapp WAR!";
        return 171;
    }
}

# Log & Update HMS upgrade status in db and exit with specified code
function fatal_exit()
{
    log "$1" "$2"

	echo
    echo "***************************************************************"
    echo "HMS-IB Upgrade: Backup Phase Failed @ `date`"
    echo "***************************************************************"
    exit $2
}

echo
echo "****************************************************"
echo "HMS-IB Upgrade: Backup Phase Started @ `date`"
echo "****************************************************"

# 0. Check_arguments
check_arguments "$@" || {

	fatal_exit "HMS-IB Upgrade: Backup Phase arguments check: Failed" $?;
}

# 1. Create Backup
createBackup || {

	fatal_exit "HMS-IB Webapp Upgrade: Backup Phase Failed" $?;
}

echo
echo "***************************************************************"
echo "HMS-IB Upgrade: Backup Phase Completed Successfully @ `date`"
echo "***************************************************************"
