#!/bin/bash

HMS_UPGRADE_DIR="/home/vrack/upgrade"
CURR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "From hms_ib_upgrade_wrapper : $CURR_DIR"

echo "*****************************************"
echo "From hms_ib_upgrade_wrapper : No. of params that will be passed to hms_master_script : $#"
echo "From hms_ib_upgrade_wrapper : Params:"
echo
echo "$@"
echo
echo "*****************************************"
nohup bash $CURR_DIR/hms_ib_master_upgrader.sh "$@" >> $HMS_UPGRADE_DIR/hms_upgrade.log 2>&1 &
echo "hms_ib_master_upgrader script triggered successfully."
