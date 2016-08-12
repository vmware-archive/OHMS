#!/bin/bash
# ********************************************************************************
#                           ***** VARIABLES *****
# ********************************************************************************
declare JAVA;

SUCCESS=0
FAILURE=1
EXIT_FAILURE=$FAILURE
EXIT_SUCCESS=$SUCCESS
TOKENS=("start" "stop" "restart" "status")
JAVA_VERSION="1.7"

HMS_USER="cumulus"
VRACK_DIR="/opt/vrack"
HMS_DIR="$VRACK_DIR/hms"
LOG_DIR="$HMS_DIR/logs"
LOG_FILE="$LOG_DIR/hms.log"
HMS_CONFIG="$HMS_DIR/config/hms-config.properties"
CLASSPATH="$HMS_DIR/lib/*:$HMS_DIR/plugin-jars/*"

# Jetty logging options
JETTY_LOGGING_OPT="-Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.Slf4jLog"

# ********************************************************************************
#                           ***** FUNCTIONS *****
# ********************************************************************************
log_message()
{
    dtstamp=`date "+%Y-%m-%d %H:%M:%S"`
    echo "$dtstamp INFO  [hms.sh] $1"
}

find_java()
{
    local java_path
    java_path=`type -p java`
    if [ ! -z $java_path ]; then
        java_path="$(readlink -f "$java_path")"
    elif [ ! -z "$JAVA_HOME" ] && [ -x $JAVA_HOME/bin/java ]; then
        java_path="$(readlink -f "$JAVA_HOME/bin/java")"
    fi

    if [ ! -z $java_path ]; then
        JAVA=$java_path
        return $SUCCESS
    fi
    return $FAILURE
}

get_java_version()
{
    local java_version
    if [ ! -z $JAVA ]; then
        java_version=`$JAVA -version 2>&1 | grep -i version | cut -d'"' -f2 | cut -d'.' -f1-2`
    fi

    if [ ! -z $java_version ]; then
        echo "$java_version"
    fi
}

check_java()
{
    if ! find_java; then
        log_message "Java is not in the path and JAVA_HOME is not set."
        return $FAILURE
    fi

    local java_version
    java_version=$(get_java_version)
    if [ -z $java_version ] ; then
        log_message "Failed to determine java version."
        return $FAILURE
    else
        if [ "$JAVA_VERSION" != "$java_version" ]; then
            log_message "Java: $JAVA; Version: $java_version; Java version must be $JAVA_VERSION."
            return $FAILURE
        else
            return $SUCCESS
        fi
    fi
    return $FAILURE
}

check_user()
{
    local user
    user=`whoami`
    if [[ ! -z $user && "$user" == "$HMS_USER" ]]; then
        return $SUCCESS
    else
        log_message "ERROR: HMS can't be started by '$user' user."
        return $FAILURE
    fi
}

check_args()
{
    if [[ $# -eq 1 && "${TOKENS[@]}" =~ "$1" ]]; then
        return $SUCCESS
    fi
    return $FAILURE
}

get_pid()
{
    echo `ps aux | grep "com.vmware.vrack.hms.HmsApp" | grep -v "grep" | awk '{print $2}'`
}

start()
{
    pid=$(get_pid)
    if [ ! -z $pid ]; then
        log_message "HMS is already running as PID: $pid"
        return $SUCCESS
    fi

    if ! check_user; then
        # Exit if script is not executed by HMS_USER
        return $FAILURE
    fi

    if ! check_java; then
        log_message "Java validation failed."
        return $FAILURE
    fi

    "$JAVA" -classpath "$CLASSPATH" -Dhms.config.file=$HMS_CONFIG $JETTY_LOGGING_OPT com.vmware.vrack.hms.HmsApp &
    if [ $? -eq 0 ]; then
        pid=$!
        log_message "Started HMS; PID: $pid"
        return $SUCCESS
    else
        log_message "Failed to start HMS."
        return $FAILURE
    fi
}

stop()
{
    if status; then
        pid=$(get_pid)
        kill -SIGTERM $pid
        if [ $? -eq 0 ]; then
            log_message "Successfully stopped HMS."
            return $SUCCESS
        else
            log_message "Failed to stop HMS."
            return $FAILURE
        fi
    fi
}

status()
{
    pid=$(get_pid)
    if [ ! -z $pid ]; then
        log_message "HMS running as PID: $pid"
        return $SUCCESS
    else
        log_message "HMS is not running."
        return $FAILURE
    fi
}

usage()
{
    local script
    script=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/`basename "${BASH_SOURCE[0]}"`
    echo "Usage: $script {start|stop|restart|status}"
    exit $EXIT_FAILURE
}

# ********************************************************************************
#                           ***** MAIN PROGRAM *****
# ********************************************************************************

# Redirect STDOUT and STDERR to log file
[ ! -d $LOG_DIR ] && mkdir -p $LOG_DIR
exec >  >(tee -a $LOG_FILE)
exec 2> >(tee -a $LOG_FILE >&2)

check_args "$@" || usage

declare cmd_status;
case $1 in
    start)
        start
        cmd_status=$?
        ;;
    stop)
        stop
        cmd_status=$?
        ;;
    status)
        status
        cmd_status=$?
        ;;
    restart)
        stop
        cmd_status=$?
        if [ $cmd_status -eq $SUCCESS ]; then
            # sleep for 5 seconds for hms to be stopped completely
            sleep 5
            start
            cmd_status=$?
        fi
        ;;
    *)
        usage
esac

if [ $cmd_status -eq $SUCCESS ]; then
    exit $EXIT_SUCCESS
else
    exit $EXIT_FAILURE
fi

