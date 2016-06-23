#!/bin/bash

HMS_PARENT_DIR_FULLPATH="/opt/vrack"
CURR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "From hms_oob_recover_wrapper : $CURR_DIR"

echo "*****************************************"
echo "From hms_oob_recover_wrapper : No. of params that will be passed to hms_oob_recover : $#"
echo "From hms_oob_recover_wrapper : Params:"
echo
echo "$@"
echo
echo "*****************************************"
nohup bash $CURR_DIR/hms_oob_recover.sh "$@" >> $HMS_PARENT_DIR_FULLPATH/oob_upgrade.log 2>&1 &
echo "hms_oob_recover script triggered successfully."
