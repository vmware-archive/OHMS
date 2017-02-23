#!/bin/bash

#Query Timeout for TC Service Status in seconds
SERVICE_STATUS_QUERY_TIMEOUT=30

################ Service / Tomcat Scripts Toggle #########################

#Setting this to True will use Service, otherwise it will call startup scripts from tomcat bin dir
USE_SERVICE=true
TOMCAT_USER=
declare TOMCAT_HOME;
declare VRM_ROOT_PASSWORD;
#########################################

# Valid Tokens that will be passed to this script
STOP_TOKEN="stop"
START_TOKEN="start"
QUERY_TOKEN="status"
VRM_TCSERVER="/home/vrack/vrm/bin/tcruntime-ctl.sh"
VRM_WATCHDOGSERVER="/home/vrack/vrm/bin/vrm-watchdogserver.sh"

# Log specified message with an optional exit code
function log()
{
    if [ -z $2 ]; then
        echo "HMS-IB Upgrade: $1";
    else
        echo "HMS-IB Upgrade: $1 [$2]";
    fi
}

# Function to check and perform action on the basis of token passed
function perform_action_using_service()
{
    if [ $# -ge 1 ]; then
        case $1 in
            "$STOP_TOKEN")
                stop_tc_server  || {
                    log "Unable to stop TC Server";
                    exit 175;
                }
                exit 0;
                ;;

            "$START_TOKEN")
                start_tc_server || {
                    log "Unable to start TC Server";
                }

                # Sleep before chceking TC status
                log "Waiting for $SERVICE_STATUS_QUERY_TIMEOUT seconds for 'vrm-watchdogserver' to start TC Server."
                sleep $SERVICE_STATUS_QUERY_TIMEOUT
                is_tc_server_running || {
                    log "TC Server not running. Unable to start TC Server";
                    exit 177;
                }

                log "TC Server started successfully.";
                exit 0;
                ;;

            "$QUERY_TOKEN")
                if (is_tc_server_running); then
                    log "TC Server is running";
                    exit 0;
                else
                    log "TC Server is NOT running"
                    exit 178;
                fi
                ;;
            *)
                log "Not a valid option. Specify [start, stop, status]"
                exit 179;
        esac
    else
        log "FAILED: Required number of arguments must be passed before continuing.";
        return 79;
    fi
}

function is_tc_server_running()
{
    status=`sudo $VRM_TCSERVER status`
    echo "Status: $status"

    if [[ $status == *"RUNNING as PID="* ]]; then
        return 0;
    else
        return 1;
    fi
}

# Start tc server
function start_tc_server()
{
    log "TC server: Starting ..."
    sudo $VRM_WATCHDOGSERVER start || {
        echo "Error in starting watchdogserver.";
        return 180;
    }
    log "TC server: Started Successfully"
}

# Stop tc server
function stop_tc_server()
{
    log "TC server: Stopping ..."

    watchdog_status=`sudo $VRM_WATCHDOGSERVER status`
    if [ "$watchdog_status" == "Stopped" ]; then
        echo "vrm-watchdogserver is already stopped";
    else
        sudo $VRM_WATCHDOGSERVER stop || {
            echo "Error in stopping vrm-watchdogserver.";
            return 181;
        }
    fi

    tcserver_status=`$VRM_TCSERVER status | grep "Status" | cut -d ":" -f2- | sed -e 's/^[[:space:]]*//'`
    if [ "$tcserver_status" == "NOT RUNNING" ]; then
        echo "vrm-tcserver is already stopped";
    else
        sudo $VRM_TCSERVER stop || {
            echo "Error in stopping vrm-tcserver.";
            return 182;
        }
        log "TC server: Stopped Successfully"
    fi
}

###################### for system Testing ##########################

function perform_action_using_tomcat()
{
    if [ $# -ge 2 ]; then
        # Set tomcat directory
        TOMCAT_HOME="$2"
        case $1 in
            "$STOP_TOKEN")
                stopTomcat  || {
                log "Unable to stop TC Server";
                exit 175;
                }
                exit 0;
                ;;

            "$START_TOKEN")
                startTomcat || {
                    log "Unable to start TC Server";
                }

                #Sleep before chceking TC status
                sleep $SERVICE_STATUS_QUERY_TIMEOUT
                is_running || {
                    log "TC Server not running. Unable to start TC Server";
                    exit 177;
                }

                log "TC Server started successfully.";
                exit 0;
                ;;

            "$QUERY_TOKEN")
                if (is_running)
                then
                    log "TC Server is running";
                    exit 0;
                else
                    log "TC Server is NOT running"
                    exit 178;
                fi
                ;;
            *)
                log "Not a valid option. Specify [start, stop, status]"
                exit 179;
        esac
    else
        log "FAILED: Required number of arguments must be passed before continuing.";
        return 79;
    fi
}

tomcat_pid() {
    echo `ps aux | grep org.apache.catalina.startup.Bootstrap | grep -v grep | awk '{ print $2 }'`
}

# Checks if hms-service is running
function is_running()
{
    if [ -z $(tomcat_pid) ]; then
        return 1
    else
        return 0
    fi
}

# Start Tomcat on Machine
function startTomcat()
{
    log ""
    log "Tomcat: Starting ...Using local system Tomcat config"

    # First check if the Tomcat is already running and we tried to start it twice
    curr_pid=$(tomcat_pid)
    if [ ! -z $curr_pid ]; then
        log "Tomcat already running with pid $curr_pid";
        exit 0;
    fi

    echo "Starting tomcat"
    cd $TOMCAT_HOME/bin && $TOMCAT_HOME/bin/startup.sh $TOMCAT_USER

    pid=$(tomcat_pid)

    if [ -z $pid ]; then
        log "Something went wrong. Tomcat failed to start. Check directory path again [ $TOMCAT_HOME ]"
    else
        log "Started Tomcat with process ID $pid"
    fi
}

# Stop the Tomcat on the machine
function stopTomcat()
{
    log ""
    log "Stopping HMS IB."

    pid=$(tomcat_pid)

    if [ -z $pid ]
    then
        log "Tomcat not running. nothing to stop."
    else
        log "Tomcat currently running. Killing Hms with process ID $pid"
        kill -9 $pid || {
            log "Unable to kill Tomcat with pid $pid";
            return 207;
        }
        log "Successfully stopped Tomcat with pid $pid"
    fi
}

####################################################################

# 0. Check_arguments
if [ "$USE_SERVICE" = true ]; then
    perform_action_using_service "$@" || { exit $?; }
else
    perform_action_using_tomcat "$@" || { exit $?; }
fi

exit 0
