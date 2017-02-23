#!/bin/bash
JAVA_HOME=/opt/jre1.7.0

############### For future reference #################

# Full Path to hms latest Build
declare HMS_LATEST_BUILD_PATH;
# Full Path to the Hms Directory
declare HMS_DIR_FULLPATH;
# Get the current TimeStamp(Used to rename directories)
declare TOKEN;
# Upgrade Directory
declare UPGRADE_DIR;
# Full path to upgraded Hms Directory
declare UPGRADE_DIR_FULLPATH;
# Full path to upgraded hms directory
declare UPGRADE_CONFIG_DIR;
# Full Path to Hms Backup Directory
declare BACKUP_DIR_FULLPATH;


# Function loginfo to log info message
function loginfo()
{
    dtstamp=`date "+%Y-%m-%d %H:%M:%S"`

    if [ -z $2 ]; then
        echo "$dtstamp $1"
    else
        echo "$dtstamp $1 [$2]"
    fi
}

# Function to check and set if any hmsbuild token has been passed while creating it
function check_arguments()
{
    if [ $# -ge 5 ]; then

        TOKEN="$1";

        # required to copy working config directory
        BACKUP_DIR_FULLPATH="$2"
        # latest build path
        HMS_LATEST_BUILD_PATH="$3"
        HMS_DIR_FULLPATH="$4"

        UPGRADE_DIR_FULLPATH="$5"
        UPGRADE_CONFIG_DIR="$UPGRADE_DIR_FULLPATH/config/"
    else
        loginfo "FAILED: Required number of arguments must be passed before continuing.";
        return 29;
    fi
}

# function to create the Upgrade directory to untar latest build
function makeUpgradeDir()
{
    mkdir $UPGRADE_DIR_FULLPATH || { echo "Failed to create upgrade dir [ $UPGRADE_DIR_FULLPATH ]"; return 155; }
    cd $UPGRADE_DIR_FULLPATH
    loginfo "Created upgrade directory [ $UPGRADE_DIR_FULLPATH ]"
}

# Untar latest build into the newly created upgrade dir
function untarLatestBuild()
{
    loginfo "Preparing to untar the latest Build [ $HMS_LATEST_BUILD_PATH ]"
    cd "$UPGRADE_DIR_FULLPATH"
    tar -zxvf "$HMS_LATEST_BUILD_PATH" -C "$UPGRADE_DIR_FULLPATH" || { echo "Failed to untar [ $HMS_LATEST_BUILD_PATH ]" ; return 156; }
    loginfo "Latest Build Untared Successfully"
}

# Copy Working configuration from Old Directory to new directory
function copyWorkingConfigDir()
{
    loginfo "Copying existing configurations and logs to new dir"
    cp -r -f "$BACKUP_DIR_FULLPATH/config/" $UPGRADE_DIR_FULLPATH || {
        echo "Unable to copy exiting configurations!!!";
        return 157;
    }
    loginfo "Copying existing logs to $UPGRADE_DIR_FULLPATH"
    mkdir -p $UPGRADE_DIR_FULLPATH/logs
    mv -f "$BACKUP_DIR_FULLPATH/logs/" $UPGRADE_DIR_FULLPATH
    if [ $? -eq 0 ]; then
        loginfo "Copied existing logs to $UPGRADE_DIR_FULLPATH"
    else
        loginfo "Failed to copy existing logs to $UPGRADE_DIR_FULLPATH"
    fi
}

# Apply delta changes on hms config on oob agent
function applyDeltaChangesOnConfig()
{
    loginfo "applying hms-config-delta.properties onto hms-config.properties"

    # Adds new line
    sed -i -e '$a\' $BACKUP_DIR_FULLPATH/config/hms-config.properties
    cat $UPGRADE_DIR_FULLPATH/upgrade-config/hms-config-delta.properties >> $BACKUP_DIR_FULLPATH/config/hms-config.properties || {
        echo "Unable to apply delta on hms-config.properties!!!";
        return 303;
    }
    loginfo "applied hms-config-delta.properties onto hms-config.properties"

    loginfo "applying connection-delta.properties onto connection.properties"

    # Adds new line
    sed -i -e '$a\' $BACKUP_DIR_FULLPATH/config/connection.properties
    cat $UPGRADE_DIR_FULLPATH/upgrade-config/connection-delta.properties >> $BACKUP_DIR_FULLPATH/config/connection.properties || {
        echo "Unable to apply delta on connection.properties!!!";
        return 304;
    }
    loginfo "applied connection-delta.properties onto connection.properties"
}

# Swap the upgrade dir with the current hms dir
function swapHmsWithLatestBuild()
{
    loginfo "Making new upgrade directory as Hms directory"
    mv -f "$UPGRADE_DIR_FULLPATH" "$HMS_DIR_FULLPATH" || {
        echo "Unable to Swap Upgrade Dir with Current Hms!!!";
        return 158;
    }
    loginfo "Upgrade directory was made hms directory"
}

# remove inventory file from the current hms dir
function removeInventoryFile()
{
    loginfo "remove the inventory file - hms-inventory.json from upgrade directory"
    rm -f $UPGRADE_DIR_FULLPATH/config/hms-inventory.json || {
        echo "Unable to delete the inventory file hms-inventory.json!!!";
        return 160;
    }
    loginfo "removed the inventory file - hms-inventory.json from upgrade directory"
}

# Log & Update HMS upgrade status in db and exit with specified code
function fatal_exit()
{
    loginfo "$1 Exit Code: $2"

    echo
    echo "***************************************************************"
    echo "HMS OOB Upgrade: Upgrade Phase failed @ `date`"
    echo "***************************************************************"
    exit $2
}

## ENTRY POINT

echo
echo "****************************************************"
echo "HMS OOB Upgrade: Upgrade Phase Started @ `date`"
echo "****************************************************"

# 0. Check Arguments
check_arguments "$@" || {
    fatal_exit "HMS upgrade upgrade phase failed: Arguments Check: Failed" $?;
}


# 1. Create Temp Upgrade Dir
makeUpgradeDir || {
    fatal_exit "Unable to create upgrade dir" $?;
}

# 2. Untar HMS from File
untarLatestBuild || {
    fatal_exit "Unable to untar latest HMS OOB build" $?;
}

# 3. apply delta changes to backed-up config
applyDeltaChangesOnConfig || {
    fatal_exit "Unable to apply delta on hms configurations" $?;
}

# 4. Copy working Config Files
copyWorkingConfigDir || {
    fatal_exit "Unable to copy working hms configurations" $?;
}

# 5. Remove inventory file
removeInventoryFile || {
    fatal_exit "Unable to remove inventory file" $?;
}

# 6. Replace Hms Dir with Upgrade dir
swapHmsWithLatestBuild || {
    fatal_exit "Unable to make upgrade dir as hms dir" $?;
}

echo
echo "***************************************************************"
echo "HMS OOB Upgrade: Upgrade Phase completed Successfully @ `date`"
echo "***************************************************************"

exit 0
