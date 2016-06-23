#!/bin/bash

############### For future reference #################

# Full Path to the Hms Directory
declare HMS_DIR_FULLPATH;
# Get the current TimeStamp(Used to rename directories)
declare TOKEN;
# Full Path to Hms Backup Directory
declare BACKUP_DIR_FULLPATH;


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

# Function to check and set if any hmsbuild token has been passed while creating it
function check_arguments()
{
	if [ $# -eq 3 ]
	then
		TOKEN="$1";
		HMS_DIR_FULLPATH="$2"
		BACKUP_DIR_FULLPATH="$3"
	else
		loginfo "FAILED: Required number of arguments must be passed before continuing.";
		return 19;
	fi
}

# Create Backup of the Old Build Before moving on,
# In case something goes wrong, it can be used as backup
function createBackUp()
{
	loginfo "Creating backup of working Hms oob build"

	mv -f "$HMS_DIR_FULLPATH" "$BACKUP_DIR_FULLPATH" || {
        echo "Unable to backup current HMS build to [ $BACKUP_DIR_FULLPATH ]";
		return 150;
    }
	loginfo "Hms OOB backup success. Backup Dir [ $BACKUP_DIR_FULLPATH ]";
}


# Log & Update HMS upgrade status in db and exit with specified code
function fatal_exit()
{
	loginfo "$1 Exit Code: $2"

	echo
	echo "***************************************************************"
	echo "HMS OOB Upgrade : Backup Phase Failed @ `date`"
	echo "***************************************************************"
	exit $2
}

## ENTRY POINT

echo
echo "****************************************************"
echo "HMS OOB Upgrade: Backup Phase started @ `date`"
echo "****************************************************"

# 0. Check Arguments
check_arguments "$@" || {
    fatal_exit "HMS upgrade backup phase failed : Arguments check: Failed" $?;
}

createBackUp  || {
fatal_exit "Backup creation failed for HMS OOB" $?;
}

echo
echo "***************************************************************"
echo "HMS OOB Upgrade: Backup Phase completed successfully @ `date`"
echo "***************************************************************"

exit 0
