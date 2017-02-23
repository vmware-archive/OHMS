#!/bin/bash

HMS_UPGRADE_LOG_DIR="/home/vrack/lcm/upgrades/hms"
HMS_UPGRADE_LOG="$HMS_UPGRADE_LOG_DIR/hms_upgrade.log"
CURR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "From hms_ib_upgrade_wrapper : $CURR_DIR"

echo "*****************************************"
echo "From hms_ib_upgrade_wrapper : No. of params that will be passed to hms_master_script : $#"
echo "From hms_ib_upgrade_wrapper : Params:"
echo
echo "$@"
echo
echo "*****************************************"
# create HMS upgrade log directory, if does not exist
if [ ! -d $HMS_UPGRADE_LOG_DIR ]; then
    mkdir -p $HMS_UPGRADE_LOG_DIR
fi

# create HMS upgrade log, if does not exist
if [ ! -f $HMS_UPGRADE_LOG ]; then
    touch $HMS_UPGRADE_LOG
fi
nohup bash $CURR_DIR/hms_ib_master_upgrader.sh "$@" >> $HMS_UPGRADE_LOG 2>&1 &
echo "hms_ib_master_upgrader script triggered successfully."
