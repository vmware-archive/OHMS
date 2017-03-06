#!/bin/bash

HMS_UPGRADE_LOG_DIR="/opt/vrack/upgrade"
HMS_UPGRADE_LOG="$HMS_UPGRADE_LOG_DIR/oob_upgrade.log"
CURR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "From hms_oob_restart_lighttpd_wrapper : $CURR_DIR"

echo "*****************************************"
echo "From hms_oob_restart_lighttpd_wrapper : No. of params that will be passed to hms_oob_restart_lighttpd_wrapper : $#"
echo "From hms_oob_restart_lighttpd_wrapper : Params:"
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
nohup bash $CURR_DIR/hms_oob_restart_lighttpd.sh "$@" >> $HMS_UPGRADE_LOG 2>&1 &
echo "hms_oob_restart_lighttpd script triggered successfully."
