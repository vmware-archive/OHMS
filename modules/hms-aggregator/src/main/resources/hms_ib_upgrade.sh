#!/bin/bash

declare HMS_IB_UPGRADE_WAR_FULLPATH;
declare WEBAPP_WAR_FULLPATH;

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
	if [ $# -ge 2 ]
	then
		HMS_IB_UPGRADE_WAR_FULLPATH="$1";
		WEBAPP_WAR_FULLPATH="$2";
	else
		log "FAILED: Required number of arguments must be passed before continuing.";
		return 69;
	fi
}

# Upgrade hms webapp
function upgrade_hms_ib_webapp()
{
    #replace new war
    cp $HMS_IB_UPGRADE_WAR_FULLPATH $WEBAPP_WAR_FULLPATH || {

        echo "Unable to copy $HMS_IB_UPGRADE_WAR_FULLPATH as $WEBAPP_WAR_FULLPATH !!!";
        return 173;
    }
}

# Log & Update HMS upgrade status in db and exit with specified code
function fatal_exit()
{
    log "$1" "$2"

	echo
    echo "***************************************************************"
    echo "HMS-IB Upgrade: Upgrade Phase Failed @ `date`"
    echo "***************************************************************"
    exit $2
}

echo
echo "****************************************************"
echo "HMS-IB Upgrade: Upgrade Phase Started @ `date`"
echo "****************************************************"

# 0. Check_arguments
check_arguments "$@" || {

	fatal_exit "HMS-IB Upgrade: Upgrade Phase Arguments check: Failed" $?;
}

# 1 Upgrade HMS IB webapp
upgrade_hms_ib_webapp || {

	fatal_exit "HMS-IB Webapp Upgrade: Upgrade Phase Failed" $?;
}

echo "***************************************************************"
echo "HMS-IB Upgrade: Upgrade Phase Completed Successfully @ `date`"
echo "***************************************************************"
