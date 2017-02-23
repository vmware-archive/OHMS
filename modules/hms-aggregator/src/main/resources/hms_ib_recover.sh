#!/bin/bash

# Directory of the Webapp in Tomcat Dir
declare WEBAPP_DIR_FULLPATH;
# Absolute path to backed up webapp dir, before upgrading
declare BACKUP_WEBAPP_FULLPATH;
# Directory of the war file in Tomcat Dir
declare WEBAPP_WAR_FULLPATH;
# Absolute path to backed up war file, before upgrading
declare BACKUP_WAR_FULLPATH;
# Absolute path to upgrade directory (including the HMS TOKEN)
declare HMS_UPGRADE_DIR;
# declare Hms inventory file location
declare HMS_INVENTORY_CONFIG_DIR;

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
    if [ $# -ge 5 ]
    then
        WEBAPP_DIR_FULLPATH="$1";
        BACKUP_WEBAPP_FULLPATH="$2";
        WEBAPP_WAR_FULLPATH="$3";
        BACKUP_WAR_FULLPATH="$4";
        HMS_IB_BACKUP_DIR_FULLPATH=$5;
    else
        log "FAILED: Required number of arguments must be passed before continuing.";
        return 89;
    fi
}

# Copy the backedup inventory file to HMS_INVENTORY_CONFIG_DIR. TODO - remove this later RTP3
function copyBackedupInventoryFile() {
    HMS_INVENTORY_CONFIG_DIR="/home/vrack/VMware/vRack"
    HMS_BACKEDUP_INVENTORY_FILE_LOCATION="$HMS_IB_BACKUP_DIR_FULLPATH/hms_ib_inventory.json"
    if [ -f $HMS_BACKEDUP_INVENTORY_FILE_LOCATION ]; then
        cp $HMS_BACKEDUP_INVENTORY_FILE_LOCATION $HMS_INVENTORY_CONFIG_DIR
        echo "$HMS_BACKEDUP_INVENTORY_FILE_LOCATION file copied to $HMS_INVENTORY_CONFIG_DIR"
        return 0;
    else
        loginfo "$HMS_BACKEDUP_INVENTORY_FILE_LOCATION file does not exist"
        return 311;
    fi
}

# Upgrade hms webapp
function rollback()
{
    log "Starting to recover old working web app war from backup [ $BACKUP_WEBAPP_FULLPATH/ ]"

    #copy backedup inventory file
    copyBackedupInventoryFile || {
        echo "Unable to copy the hms_ib_inventory.json file!";
        return 312;
    }

    #remove deployed webapp folder
    rm -rf $WEBAPP_DIR_FULLPATH || {
        echo "Unable to remove new webapp directory!";
        return 183;
    }

    #Overwrite existing war file
    mv $BACKUP_WAR_FULLPATH $WEBAPP_WAR_FULLPATH || {

        echo "Unable to restore webapp WAR from backup [ $BACKUP_WAR_FULLPATH ]";
        return 184;
    }

    mv $BACKUP_WEBAPP_FULLPATH $WEBAPP_DIR_FULLPATH || {

        echo "Unable to restore webapp directory from backup [ $BACKUP_WEBAPP_FULLPATH ]";
        return 185;
    }

    log "Restore HMS IB from backup [ $BACKUP_WAR_FULLPATH ] successful."
}

# Log & Update HMS upgrade status in db and exit with specified code
function fatal_exit()
{
    log "$1" "$2"

    echo
    echo "***************************************************************"
    echo "HMS-IB Upgrade: Restore Phase Failed @ `date`"
    echo "***************************************************************"
    exit $2
}


echo
echo "****************************************************"
echo "HMS-IB Upgrade: Restore Phase Started @ `date`"
echo "****************************************************"

# 0. Check_arguments
check_arguments "$@" || {
    fatal_exit "HMS-IB Upgrade: Arguments check: Failed" $?;
}

# 1. Rollback
rollback || {
    fatal_exit "HMS-IB Webapp Upgrade: Failed" $?;
}

echo
echo "***************************************************************"
echo "HMS-IB Upgrade: Completed Successfully @ `date`"
echo "***************************************************************"
