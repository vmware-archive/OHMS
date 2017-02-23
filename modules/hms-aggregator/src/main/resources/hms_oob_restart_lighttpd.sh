#!/bin/bash

# *********************************************************************************
#                           variable declarations
# *********************************************************************************
declare LIGHTTPD_SERVER;

# *********************************************************************************
#                           variable initializations
# *********************************************************************************
# Full Path to Lighttpd startup script
LIGHTTPD_SERVER=/etc/init.d/lighttpd

# Function loginfo to log info message
loginfo()
{
	dtstamp=`date "+%Y-%m-%d %H:%M:%S"`

	if [ -z $2 ]
	then
		echo "$dtstamp $1"
	else
		echo "$dtstamp $1 [$2]"
	fi
}


get_lighttpd_pid() {
    echo `ps aux | grep /usr/sbin/lighttpd | grep -v grep | awk '{ print $2 }'`
}

function stop_lighttpd()
{
    pid=$(get_lighttpd_pid)

    if [ -z $pid ]
    then
        loginfo "Lighttpd not running. nothing to stop."
    else
        loginfo "Lighttpd currently running. Killing with process Id $pid"
        kill -9 $pid || {
            loginfo "Unable to kill Lighttpd with pid $pid";
            return 196;
        }
        loginfo "Successfully stopped Lighttpd with pid $pid"
    fi
}

function start_lighttpd()
{
    loginfo "Lighttpd restarting ..."
    $LIGHTTPD_SERVER start || {
        echo "Error in starting Lighttpd.";
        return 197;
    }
    loginfo "Lighttpd restarted successfully"
    return 0;
}

function restart_lighttpd()
{
    sleep 5s
    stop_lighttpd || {
        return 198;
    }
    start_lighttpd || {
        return 199;
    }
    return 0;
}

# Log & Update HMS Validation Status
function fatal_exit()
{
    loginfo "$1 Exit Code: $2"

    echo
    echo "***************************************************************"
    echo "Restart Lighttpd: Failed @ `date`"
    echo "***************************************************************"
    exit $2
}

# MAIN ENTRY POINT

restart_lighttpd || {
    fatal_exit "Lighttpd restart failed" $?;
}

echo
echo "***********************************************************************"
echo "Lighttpd restart completed successfully. @ `date`"
echo "***********************************************************************"

exit 0;
