#!/bin/bash

# ********************************************************************************
#                   ***** VARIABLES DECLARATION *****
# ********************************************************************************
declare HMS_TOKEN;
declare HMS_SCRIPT_DIR;
declare HMS_IB_BACKUP_DIR;
declare HMS_IB_UPGRADE_DIR;
declare HMS_IB_UPGRADE_WAR_NAME;
declare HMS_OOB_HOST;
declare HMS_MAINIFEST_LOCATION;
declare HMS_BUILD_TO_MATCH;
declare HMS_BUILD_VERSION;

# for scripts absolute path
declare HMS_IB_BACKUP_DIR_NAME;
declare HMS_IB_BACKUP_DIR_FULLPATH;

declare HMS_IB_UPGRADE_WAR_FULLPATH;
declare HMS_IB_VALIDATE_SCRIPT_FULLPATH;
declare HMS_IB_BACKUP_SCRIPT_FULLPATH;
declare HMS_IB_SERVICE_SCRIPT_FULLPATH;
declare HMS_IB_UPGRADE_SCRIPT_FULLPATH;
declare HMS_IB_RECOVER_SCRIPT_FULLPATH;
declare HMS_IB_UPGRADE_WAR_CHECKSUM_FILENAME;

# payload for invoking oob agent /rollback api
declare HMS_OOB_ROLLBACK_API_PAYLOAD;

#declare HMS_IB_CHECKSUM_METHOD;

# HMS IB UpgradeStatus JSON file absoulte path
declare HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH;

# for building Hms ib inventory
declare HMS_INVENTORY_CONFIG_DIR;
declare HMS_LOCAL_CLASS_PATH;
declare HMS_LOCAL_EXTRACT_DIR;
declare HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION;
declare HMS_IB_INVENTORY_CONFIG_FILE_LOCATION;

# security
declare VRM_TRUSTSTORE_FILE;
declare VRM_TRUSTSTORE_PASSWORD;
declare VRM_TRUSTSTORE_PEM;
declare PSC_CA_1_ALIAS;
declare PSC_CA_2_ALIAS;
declare CURL_OPTIONS;
declare PSC_CA_1_PEM;
declare PSC_CA_2_PEM;

# ********************************************************************************
#                   ***** VARIABLES INITIALIZATION *****
# ********************************************************************************
HMS_QUERY_WAIT=15;
HMS_OOB_QUERY_WAIT_TIME=5;
HMS_OOB_ABOUT_CALL_RETRY_LIMIT=3;
HMS_WAIT_BEFORE_UPGRADE=30;
LIGHTTPD_RESTART_WAIT_TIME=15;
# --------------------------------------------------------------------------------
#               ***** VARIABLES FOR FOR ENVIRONMENT SETUP *****
# --------------------------------------------------------------------------------
HMS_SCRIPT_DIR="/home/vrack/vrm/webapps/hms-aggregator/WEB-INF/classes/"
CATALINA_BASE="/home/vrack/vrm"
HMS_UPGRADE_DIR="/home/vrack/upgrade"
JAVA_KEYSTORE_FULLPATH="/usr/java/jre-vmware/bin/keytool"
JAVA_FULLPATH="/usr/java/jre-vmware/bin/java"
HMS_INVENTORY_CONFIG_DIR="/home/vrack/VMware/vRack"
HMS_BUILD_TO_MATCH="1.1.*"

# --------------------------------------------------------------------------------
#           ***** VARIABLES RELATED TO SCRIPTS AND VALIDATION *****
# --------------------------------------------------------------------------------
HMS_IB_VALIDATE_SCRIPT="hms_ib_validate.sh";
HMS_IB_BACKUP_SCRIPT="hms_ib_backup.sh"
HMS_IB_SERVICE_SCRIPT="hms_ib_service.sh"
HMS_IB_UPGRADE_SCRIPT="hms_ib_upgrade.sh"
HMS_IB_RECOVER_SCRIPT="hms_ib_recover.sh"

# --------------------------------------------------------------------------------
#           ***** VARIABLES FOR REMOTE HMS IB *****
# --------------------------------------------------------------------------------

WEBAPP_NAME="hms-aggregator"
WEBAPP_WAR_NAME="$WEBAPP_NAME.war"
WEBAPP_DIR_FULLPATH="$CATALINA_BASE/webapps/$WEBAPP_NAME"
WEBAPP_WAR_FULLPATH="$CATALINA_BASE/webapps/$WEBAPP_WAR_NAME"
JARS_DIR="$CATALINA_BASE/webapps/$WEBAPP_NAME/WEB-INF/lib"
HMS_PROP_FILE="$CATALINA_BASE/webapps/$WEBAPP_NAME/WEB-INF/classes/hms.properties"
HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION="$HMS_INVENTORY_CONFIG_DIR/hms-inventory.json"
HMS_IB_INVENTORY_CONFIG_FILE_LOCATION="$HMS_INVENTORY_CONFIG_DIR/hms_ib_inventory.json"
# Valid Tokens for services
# --------------------------------------------------------------------------------
#           ***** VARIABLES FOR SERVICE START/STOP/STATUS *****
# --------------------------------------------------------------------------------
STOP_TOKEN="stop"
START_TOKEN="start"
QUERY_TOKEN="status"

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

# Function to check and set if any hmsbuild token has been passed while creating it
function check_arguments()
{
    if [ "$#" -ge 10 ]
    then

        loginfo "### Got HMS token, upgrade directory, upgrade war file, OOB Host and Scripts directory of the OOB.";

        # unique token
        HMS_TOKEN="$1";

        # directory where IB upgrade scripts have been copied to.
        HMS_SCRIPT_DIR="$2";

        # backup directory to which running IB agent will be backup'ed to.
        HMS_IB_BACKUP_DIR="$3"
        HMS_IB_BACKUP_DIR_NAME="hms_backup_$HMS_TOKEN"
        HMS_IB_BACKUP_DIR_FULLPATH="$HMS_IB_BACKUP_DIR/$HMS_IB_BACKUP_DIR_NAME"

        # directory where upgrade binary is copied to
        HMS_IB_UPGRADE_DIR="$4";

        # upgrade binary name
        HMS_IB_UPGRADE_WAR_NAME="$5";
        HMS_IB_UPGRADE_WAR_FULLPATH="$HMS_IB_UPGRADE_DIR/$HMS_IB_UPGRADE_WAR_NAME";

        # for rollback of OOB agent upgrade, if Aggregator upgrade fails.
        HMS_OOB_HOST="$6";

        HMS_IB_VALIDATE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_VALIDATE_SCRIPT}";
        HMS_IB_BACKUP_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_BACKUP_SCRIPT}";
        HMS_IB_SERVICE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_SERVICE_SCRIPT}";
        HMS_IB_UPGRADE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_UPGRADE_SCRIPT}";
        HMS_IB_RECOVER_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_RECOVER_SCRIPT}";

        HMS_IB_UPGRADE_WAR_CHECKSUM_FILENAME="${HMS_IB_UPGRADE_WAR_NAME}.md5"
        HMS_OOB_ROLLBACK_API_PAYLOAD="{\"id\" : \"$HMS_TOKEN\"}"

        # HMS IB UpgradeStatus JSON file abs path
        HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH="$HMS_UPGRADE_DIR/$HMS_TOKEN.json"

        # security
        VRM_TRUSTSTORE_FILE="$7"
        VRM_TRUSTSTORE_PASSWORD="$8"
        PSC_CA_1_ALIAS="$9"
        PSC_CA_2_ALIAS="${10}"
        VRM_TRUSTSTORE_PEM="$HMS_SCRIPT_DIR/truststore.pem"
        PSC_CA_1_PEM="$HMS_SCRIPT_DIR/$PSC_CA_1_ALIAS.pem"
        PSC_CA_2_PEM="$HMS_SCRIPT_DIR/$PSC_CA_2_ALIAS.pem"
        CURL_OPTIONS="--cacert $VRM_TRUSTSTORE_PEM"

        loginfo "HMS_TOKEN                          : $HMS_TOKEN"
        loginfo "HMS_SCRIPT_DIR                     : $HMS_SCRIPT_DIR"
        loginfo "HMS_IB_BACKUP_DIR                  : $HMS_IB_BACKUP_DIR"
        loginfo "HMS_IB_UPGRADE_DIR                 : $HMS_IB_UPGRADE_DIR"
        loginfo "HMS_IB_UPGRADE_WAR_NAME            : $HMS_IB_UPGRADE_WAR_NAME"
        loginfo "HMS_OOB_HOST                       : $HMS_OOB_HOST"
        loginfo "VRM_TRUSTSTORE_FILE                : $VRM_TRUSTSTORE_FILE"
        loginfo "PSC_CA_1_ALIAS                     : $PSC_CA_1_ALIAS"
        loginfo "PSC_CA_2_ALIAS                     : $PSC_CA_2_ALIAS"

    else

        loginfo "FAILED: Required number of arguments must be passed before continuing.";
        return 9;
    fi
}

# Checks if all of the pre-requisites
function validate()
{
    #Check if the Hms OOB validation file exist
    local ibscript="$HMS_IB_VALIDATE_SCRIPT_FULLPATH"
    [[ -f "$ibscript" ]] || return 91

    #Grant Execution rights to all Scripts
    cd $HMS_SCRIPT_DIR/ && chmod +x *.sh || {

       echo "Unable to provide execute rights to scripts at [ $HMS_SCRIPT_DIR ]";
       return 92;
    }

    #Run HMS IB Validate script and Provide necessary details to IB script
    ($HMS_IB_VALIDATE_SCRIPT_FULLPATH \
           $HMS_SCRIPT_DIR \
           $HMS_IB_BACKUP_SCRIPT \
           $HMS_IB_SERVICE_SCRIPT \
           $HMS_IB_UPGRADE_SCRIPT \
           $HMS_IB_RECOVER_SCRIPT \
           $WEBAPP_WAR_NAME \
           $HMS_IB_UPGRADE_DIR \
           $HMS_IB_UPGRADE_WAR_CHECKSUM_FILENAME) || {

       echo "Error in validating HMS IB scripts and files";
       return 94;
    }
}

function startHmsIB()
{
    ($HMS_IB_SERVICE_SCRIPT_FULLPATH $START_TOKEN $CATALINA_BASE) || {

       return 113;
    }
}

function stopHmsIB()
{
    ($HMS_IB_SERVICE_SCRIPT_FULLPATH $STOP_TOKEN $CATALINA_BASE) || {

       return 114;
    }
}

function restartHmsIB()
{
    loginfo "Restarting HMS Aggregator."
    #Stopping TC Server
    stopHmsIB || {
       echo "Unable to Stop TC Service on HMS IB"
       return 117;
    }

    #Start HMS IB
    startHmsIB || {
       echo "Unable to Start HMS IB Service";
       return 124;
    }
    loginfo "HMS Aggregator restarted successfully."
    return 0;
}

function hms_ib_backup()
{
    #After triggering upgrade from endpoint, This will be the last point for logs. after that no logs will be logged
    echo
    echo "*****************************End of Tomcat Log, because tomcat will be stopped NOW for upgrade.***********************"
    echo

    #Stopping TC Server
    stopHmsIB || {

       echo "Unable to Stop TC Service on HMS IB"
       return 117;
    }

    #Create HMS IB Backup
    ($HMS_IB_BACKUP_SCRIPT_FULLPATH $HMS_TOKEN $HMS_IB_BACKUP_DIR_FULLPATH $WEBAPP_WAR_NAME $WEBAPP_DIR_FULLPATH) || {

       echo "Unable to create HMS IB Backup"
       return 118;
    }
}

function hms_inventory_backup()
{
    # backup existing hms_ib_inventory.json to backup folder
    cp $HMS_IB_INVENTORY_CONFIG_FILE_LOCATION $HMS_IB_BACKUP_DIR_FULLPATH || {
        echo "Unable to backup ib inventory file";
        return 172;
    }
    echo "backup of inventory to backup folder is successful"
}

function hms_ib_upgrade()
{
    #call Hms IB Upgrade Script
    ($HMS_IB_UPGRADE_SCRIPT_FULLPATH $HMS_IB_UPGRADE_WAR_FULLPATH $WEBAPP_WAR_FULLPATH) || {

       echo "Unable to upgrade HMS IB";
       return 123;
    }

    #Start HMS IB
    startHmsIB || {

       echo "Unable to Start HMS IB Service";
       return 124;
    }
}

function hms_oob_rollback()
{
    loginfo "##############################";
    loginfo "### HMS OOB RollBack Phase ###";
    loginfo "##############################";

    rollback_http_url="https://${HMS_OOB_HOST}:8450/api/1.0/hms/upgrade/rollback"
    rollback_response=`echo $HMS_OOB_ROLLBACK_API_PAYLOAD | curl ${CURL_OPTIONS} -s -i -w "\n" --request POST --data @- --header "Content-Type:application/json" ${rollback_http_url}`
    loginfo "$rollback_response"

    rollback_http_code=`echo $rollback_response | head -n 1 | cut -d$' ' -f2`
    loginfo "OOB Rollback API returned HTTP Status: $rollback_http_code"

    # check that oob agent has accepted the request for rollback of upgrade
    if [ $rollback_http_code -eq 202 -o $rollback_http_code -eq 200 ]; then
        loginfo "Initiated rollback of OOB agent upgrade !!!"
        return 0;
    else
        loginfo "Unable to initiate rollback of OOB agent upgrade !!!"
        return 126;
    fi
}

function hms_ib_rollback()
{
    loginfo "##############################";
    loginfo "### HMS IB RollBack Phase ###";
    loginfo "##############################";

    stopHmsIB || {

       echo "Unable to Stop TC Service on HMS IB"
       return 131;
    }

    ($HMS_IB_RECOVER_SCRIPT_FULLPATH \
       $WEBAPP_DIR_FULLPATH \
       "$HMS_IB_BACKUP_DIR_FULLPATH/$WEBAPP_NAME" \
       $WEBAPP_WAR_FULLPATH \
       "$HMS_IB_BACKUP_DIR_FULLPATH/$WEBAPP_WAR_NAME" \
       $HMS_IB_BACKUP_DIR_FULLPATH) || {

       echo "Unable to rollback HMS IB from backup: $HMS_IB_BACKUP_DIR_FULLPATH";
       return 132;
    }

    #Start HMS IB
    startHmsIB || {

       echo "Unable to Start HMS IB Service after rollback !!!";
       return 133;
    }
    loginfo "Hms IB Rollback successful"
}

# Log & Update HMS upgrade status in db and exit with specified code
function fatal_exit()
{
    case "$3" in
    RESTORE_OOB)
        hms_oob_rollback || {
            loginfo "HMS OOB RollBack: Failed [$?]."
            save_upgrade_status "$HMS_TOKEN" "HMS_ROLLBACK_FAILED"|| {
                loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH after OOB Rollback failed."
            }
        }
        save_upgrade_status "$HMS_TOKEN" "HMS_ROLLBACK_SUCCESS"|| {
            loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH after OOB Rollback succeeded."
        }

        # Restart HMS Aggregator, as it is in MAINTENANCE mode.
        restartHmsIB || {
            loginfo "Failed to restart HMS Aggregator."
        }

        # Delete HMS Upgrade Bundle and Scripts Directory
        delete_ib_upgrade_directory
        #NOTE:
        # 1. IB backup is not done yet. Hence nothing to delete.
        # 2. OOB backup does not exist as OOB upgrade is rollbacked.
        ;;
    RESTORE_IB_OOB)
        hms_oob_rollback || {
            save_upgrade_status "$HMS_TOKEN" "HMS_ROLLBACK_FAILED"|| {
                loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH after OOB Rollback failed."
            }
            loginfo "HMS OOB RollBack : Failed" $?;
        }
        hms_ib_rollback || {
            save_upgrade_status "$HMS_TOKEN" "HMS_ROLLBACK_FAILED"|| {
                loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH after IB Rollback failed."
            }
            loginfo "HMS IB RollBack : Failed" $?;
        }
        save_upgrade_status "$HMS_TOKEN" "HMS_ROLLBACK_SUCCESS"|| {
            loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH after IB and OOB Rollback succeeded."
        }
        # Delete HMS Upgrade Bundle and Scripts Directory
        delete_ib_upgrade_directory

        #NOTE:
        # 1. IB and OOB backups have been rollbacked.
        ;;
    esac
    loginfo "$1 Exit Code: $2"

    echo "***************************************************************"
    echo "HMS Upgrade: Failed @ `date`"
    echo "***************************************************************"
    exit $2
}

# Gets Last Upgrade Status of HMS OOB.
# This assumes that an empty file with the passed HMS Token should be present in remote hms directory, if it was successfully upgraded.
function getLastUpgradeStatus_HmsOOB()
{
    about_http_url="https://${HMS_OOB_HOST}:8450/api/1.0/hms/about"
    for (( counter=1; counter<=$HMS_OOB_ABOUT_CALL_RETRY_LIMIT; counter++ ))
    do
        about_response=`curl ${CURL_OPTIONS} ${about_http_url}`

        loginfo "Checking for HMS OOB Response";
        # Checking if the HMS OOB is responsive, by hitting its /about endpoint.
        # If it is responsive, the following will execute normally, otherwise, it will exit with returncode
        echo ${about_response} | grep -iq "buildVersion"
        if [ $? -eq 0 ]; then
            loginfo "HMS OOB responded successfully"
            return 0;
        else
           loginfo "Hms oob about call failed, will go for retry, retry count: $counter"
           # Wait before querying if the HMS OOB is up and responsive.
           loginfo "Waiting for $HMS_OOB_QUERY_WAIT_TIME seconds, before querying HMS OOB response"
           sleep $HMS_OOB_QUERY_WAIT_TIME
        fi
    done
    return 134;
}

# Gets Last updated status of HMS IB
function getLastUpgradeStatus_HmsIB()
{
    # Wait before querying if the HMS IB is up and responsive.
    loginfo "Waiting for $HMS_QUERY_WAIT seconds, before querying HMS IB response"
    sleep $HMS_QUERY_WAIT
    about_http_url="http://127.0.0.1:8080/hms-aggregator/api/1.0/hms/about"
    about_response=`curl ${CURL_OPTIONS} -s -i ${about_http_url}`
    loginfo "$about_response"

    loginfo "Checking for HMS IB Response";
    # Checking if the HMS IB is responsive, by hitting its /about endpoint.
    # If it is responsive, the following will execute normally, otherwise, it will exit with returncode
    echo ${about_response} | grep -iq "buildVersion" || {
        return 135;
    }
    return 0;
}

# Function saves the upgrade status as upgrade_status.json
# It expects below arguments.
#   1. HMS_TOKEN
#   2. UpgradeStatusCode
#           HMS_UPGRADE_INVALID_REQUEST
#           HMS_UPGRADE_FORBIDDEN
#           HMS_UPGRADE_INTERNAL_ERROR
#           HMS_UPGRADE_INITIATED
#           HMS_UPGRADE_SCRIPT_INVALIDARGUMENTS
#           HMS_UPGRADE_SCRIPT_VALIDATION_FAILED
#           HMS_UPGRADE_SUCCESS
#           HMS_UPGRADE_FAILED
#           HMS_BACKUP_FAILED
#           HMS_ROLLBACK_FAILED
#           HMS_ROLLBACK_SUCCESS
function save_upgrade_status()
{
    id=$1
    status_code=$2
    upgrade_status_json="{\"id\":\"$id\",\"statusCode\":\"$status_code\"}"
    echo $upgrade_status_json > $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH
}

function delete_directory()
{
    dir=$1
    dir_type=$2
    if [ -d $dir ]; then
        rm -rf $dir
        if [ $? -eq 0 ]; then
            loginfo "HMS $dir_type directory - $dir deleted."
        else
            loginfo "Failed to delete HMS $dir_type directory - $dir"
        fi
    else
        loginfo "Failed to delete HMS $dir_type directory - $dir"
    fi
}

function delete_ib_upgrade_directory()
{
    if [ ! -z $HMS_IB_UPGRADE_DIR -a -d $HMS_IB_UPGRADE_DIR ]; then
        delete_directory $HMS_IB_UPGRADE_DIR upgrade
    else
        loginfo "HMS_IB_UPGRADE_DIR is either not set or not a directory."
    fi
    if [ ! -z $HMS_SCRIPT_DIR -a -d $HMS_SCRIPT_DIR ]; then
        delete_directory $HMS_SCRIPT_DIR script
    else
        loginfo "HMS_SCRIPT_DIR is either not set or not a directory."
    fi
}

function delete_ib_backup_directory()
{
    if [ ! -z $HMS_IB_BACKUP_DIR_FULLPATH -a -d $HMS_IB_BACKUP_DIR_FULLPATH ]; then
        delete_directory $HMS_IB_BACKUP_DIR_FULLPATH backup
    else
        loginfo "HMS_IB_BACKUP_DIR_FULLPATH is either not set or not a directory."
    fi
}

function delete_oob_backup()
{
    loginfo "###################################";
    loginfo "### HMS OOB Backup Delete Phase ###";
    loginfo "###################################";

    backup_deletehttp_url="https://${HMS_OOB_HOST}:8450/api/1.0/hms/upgrade/backup/$HMS_TOKEN"
    backup_deleteresponse=`curl ${CURL_OPTIONS} -i -w "\n" --request DELETE ${backup_deletehttp_url}`
    backup_deletehttp_code=`echo $backup_deleteresponse | head -n 1 | cut -d$' ' -f2`
    loginfo "\n$backup_deleteresponse"

    # check that oob agent has accepted the request for rollback of upgrade
    if [ $backup_deletehttp_code -eq 200 ]; then
        loginfo "HMS OOB Backup deleted."
    else
        loginfo "Failed to delete HMS OOB Backup."
    fi
}

#
# Converts Java Truststore in JKS format to PEM format.
#
# Note: Truststore will be in JKS format and CURL doesn't know anything about JKS files.
# Hence, ROOT CA cert is extracted from JKS and saved in PEM format which CURL needs.
#
function convert_truststore()
{
    psc_ca_1=1
    psc_ca_2=1
    $JAVA_KEYSTORE_FULLPATH -exportcert \
                            -rfc \
                            -keystore $VRM_TRUSTSTORE_FILE \
                            -storepass $VRM_TRUSTSTORE_PASSWORD \
                            -alias $PSC_CA_1_ALIAS \
                            -file $PSC_CA_1_PEM
    if [ $? -ne 0 ]; then
        loginfo "Unable to export PSC CA 1 Certificate in PEM format from VRM Truststore: $VRM_TRUSTSTORE_FILE"
    else
        cat $PSC_CA_1_PEM > $VRM_TRUSTSTORE_PEM
        psc_ca_1=0
    fi

    $JAVA_KEYSTORE_FULLPATH -exportcert \
                            -rfc \
                            -keystore $VRM_TRUSTSTORE_FILE \
                            -storepass $VRM_TRUSTSTORE_PASSWORD \
                            -alias $PSC_CA_2_ALIAS \
                            -file $PSC_CA_2_PEM
    if [ $? -ne 0 ]; then
        loginfo "Unable to export PSC CA 2 Certificate in PEM format from VRM Truststore: $VRM_TRUSTSTORE_FILE"
    else
        cat $PSC_CA_2_PEM >> $VRM_TRUSTSTORE_PEM
        psc_ca_2=0
    fi

    # Return 0(SUCCESS) if either PSC CA 1 or PSC CA 2 certificate export is successful.
    if [ $psc_ca_1 -eq 0 -o $psc_ca_2 -eq 0 ]; then
        return 0;
    else
        return 1;
    fi
}

#Method to restart lighttpd after hms handshake is completed
function restart_lighttpd()
{
    restart_lighttpd_url="https://${HMS_OOB_HOST}:8450/api/1.0/hms/upgrade/proxy/restart/${HMS_TOKEN}"
    restart_lighttpd_response=`curl ${CURL_OPTIONS} -s -i -w "\n" --request POST --header "Content-Length: 0" ${restart_lighttpd_url}`
    loginfo "$restart_lighttpd_response"

    restart_lighttpd_http_code=`echo $restart_lighttpd_response | head -n 1 | cut -d$' ' -f2`
    loginfo "Restart lighttpd API returned HTTP Status: $restart_lighttpd_http_code"

    # check the restart lighttpd status
    if [ $restart_lighttpd_http_code -eq 202 -o $restart_lighttpd_http_code -eq 200 ]; then
        loginfo "lighttpd restart is successful"
        return 0;
    else
        loginfo "Unable to restart lighttpd !!!"
        return 304;
    fi
}

#Validates if the ip extracted is the right one or not
function validate_ip()
{
    local  ip=$1
    local  stat=1

    if [[ $ip =~ ^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$ ]]; then
        OIFS=$IFS
        IFS='.'
        ip=($ip)
        IFS=$OIFS
        [[ ${ip[0]} -le 255 && ${ip[1]} -le 255 \
            && ${ip[2]} -le 255 && ${ip[3]} -le 255 ]]
        stat=$?
    fi
    return $stat
}

# Perform Handshake with Out of band
# This is mainly required to share the aggregator ip with oob, which will enable
# Hms oob to accept the requests only incoming from the above Ip.
function hms_oob_handshake_using_aggr_ip()
{
    AGGREGATOR_IP=$(/sbin/ip route | awk '/192.168.100.0\/22/ { print $NF }')
    loginfo "AggregatorIp: $AGGREGATOR_IP"

    # check if the IP is valid
    if validate_ip $AGGREGATOR_IP; then
        handshake_url="https://${HMS_OOB_HOST}:8450/api/1.0/hms/handshake/$AGGREGATOR_IP/HMS_UPGRADE"
        handshake_response=`curl ${CURL_OPTIONS} -s -i -w "\n" --request POST --header "Content-Length: 0" ${handshake_url}`
        loginfo "$handshake_response"

        handshake_http_code=`echo $handshake_response | head -n 1 | cut -d$' ' -f2`
        loginfo "Out of band handshake has returned HTTP Status: $handshake_http_code"

        # check the handshake status
        if [ $handshake_http_code -eq 202 -o $handshake_http_code -eq 200 ]; then
            loginfo "Handshake is completed"
            return 0;
        else
            loginfo "Handshake is NOT completed !!!"
            return 304;
        fi
    else
        loginfo "Invalid Ip is retrieved"
        return 305;
    fi
}

function is_the_current_upgrade_from_rtp2_to_rtp3() {
    HMS_MAINIFEST_LOCATION="$HMS_IB_BACKUP_DIR_FULLPATH/hms-aggregator/META-INF/MANIFEST.MF"
    loginfo "mainifest location: $HMS_MAINIFEST_LOCATION"
    if [ -f $HMS_MAINIFEST_LOCATION ]; then
        HMS_BUILD_VERSION=`sed '/^\#/d' $HMS_MAINIFEST_LOCATION | grep 'Build-Version'  | tail -n 1 | cut -d ":" -f2- | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'`
        loginfo "HMS_BUILD_VERSION: $HMS_BUILD_VERSION :: HMS_BUILD_TO_MATCH: $HMS_BUILD_TO_MATCH"

        if [[ $HMS_BUILD_VERSION == $HMS_BUILD_TO_MATCH ]]; then
            loginfo "Build matches"
            return 0;
        else
            loginfo "Build match is not satisfied, so proceeding without inventory file download"
        fi
    fi
    return -1;
}

#Method responsible for downloading the inventory from Out of band. TODO - remove this later RTP3
function download_hms_oob_inventory() {
    is_the_current_upgrade_from_rtp2_to_rtp3
    if [ $? -eq 0 ]; then
        download_oob_inventory_url="https://${HMS_OOB_HOST}:8450/api/1.0/hms/upgrade/download/inventory/${HMS_TOKEN}"
        hms_oob_inventory=`curl ${CURL_OPTIONS} ${download_oob_inventory_url}`

        loginfo "Checking for HMS OOB Response";
        # Checking if the HMS OOB is responsive, by hitting its /download inventory endpoint.
        # If it is responsive, the following will execute normally, otherwise, it will exit with returncode
        echo ${hms_oob_inventory} | grep -iq "servers"
        if [ $? -eq 0 ]; then
            loginfo "HMS OOB responded successfully"
            echo ${hms_oob_inventory}  > $HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION;
            if [ -f $HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION ]; then
                loginfo "$HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION file created successfully"
                return 0;
            else
                loginfo "$HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION file creation failed"
                return 306;
            fi
        else
            loginfo "Hms oob download inventory call failed"
            return 307;
        fi
    fi
}

#Method responsible for building the inventory from Out of band. TODO - remove this later RTP3
function build_hms_ib_inventory() {
    is_the_current_upgrade_from_rtp2_to_rtp3
    if [ $? -eq 0 ]; then

        HMS_LOCAL_EXTRACT_DIR=$HMS_UPGRADE_DIR/hms-aggregator-war-extract;
        mkdir -p $HMS_LOCAL_EXTRACT_DIR || {
            echo "Failed to create dir: $HMS_LOCAL_EXTRACT_DIR"; return 308;
        }

        HMS_LOCAL_WAR_ABS_PATH=`find $HMS_UPGRADE_DIR/$HMS_TOKEN -name hms-aggregator.war`
        echo "hms_local.war absolute path: $HMS_LOCAL_WAR_ABS_PATH";

        unzip -q -o $HMS_LOCAL_WAR_ABS_PATH -d $HMS_LOCAL_EXTRACT_DIR || {
            echo "Failed to extract the war: $HMS_LOCAL_WAR_ABS_PATH"; return 310;
        }
        `$JAVA_FULLPATH -jar $HMS_LOCAL_EXTRACT_DIR/WEB-INF/lib/hms-upgrade-util*.jar $HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION $HMS_IB_INVENTORY_CONFIG_FILE_LOCATION`
        if [ $? -eq 0 ]; then
            loginfo "HMS IB inventory is built successfully"
            rm -Rf $HMS_LOCAL_EXTRACT_DIR || {
                echo "cannot delete $HMS_LOCAL_EXTRACT_DIR"; return -1;
            }
            rm -f $HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION || {
                echo "cannot delete $HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION"; return -1;
            }
            return 0;
        else
            loginfo "HMS IB inventory build failed"
            rm -Rf $HMS_LOCAL_EXTRACT_DIR || {
                echo "cannot delete $HMS_LOCAL_EXTRACT_DIR"; return -1;
            }
            rm -f $HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION || {
                echo "cannot delete $HMS_OOB_INVENTORY_CONFIG_FILE_LOCATION"; return -1;
            }
            return 310;
        fi
    fi
}

##ENTRY POINT
echo "***********************************************************************"
echo "HMS Upgrade: Starting HMS Aggregator update - `date`"
echo "***********************************************************************"

# log PID
loginfo "PID: $$"

# 0. Check Arguments first to see if any HMS Token has been provided.
check_arguments "$@" || {

    # when check_arguments fails, declared variables are not initialized.
    # use first argument if it is passed to save upgrade status
    if [[ ! -z "$1" ]]; then
        HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH="$HMS_UPGRADE_DIR/$1.json"
        save_upgrade_status "$1" "HMS_UPGRADE_SCRIPT_INVALIDARGUMENTS" || {
            loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
        }
    fi

    fatal_exit "HMS Arguments Check: Failed" $? RESTORE_OOB;
}

# 2. convert vrm truststore from jks to pem
convert_truststore || {

    save_upgrade_status "$HMS_TOKEN" "HMS_UPGRADE_FORBIDDEN" || {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }

    fatal_exit "Converting VRM Truststore from JKS to PEM failed." $? RESTORE_OOB;
}

# 3. Validate HMS IB
validate || {

    save_upgrade_status "$HMS_TOKEN" "HMS_UPGRADE_SCRIPT_VALIDATION_FAILED" || {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }

    fatal_exit "HMS Validation Check: Failed" $? RESTORE_OOB;
}

# 4. Perform handshake with oob on aggregator ip
hms_oob_handshake_using_aggr_ip || {

    save_upgrade_status "$HMS_TOKEN" "HMS_OOB_HANDSHAKE_USING_AGGR_IP_FAILED"|| {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    loginfo "HMS OOB handshake is failed"
    fatal_exit "HMS OOB handshake using aggregator ip: Failed" $? RESTORE_OOB;
}

# 5. Restart Lighttpd.
restart_lighttpd || {

    save_upgrade_status "$HMS_TOKEN" "RESTART_LIGHTTPD_INITIALIZATION_FAILED"|| {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    loginfo "Restart Lighttpd initialization failed, might need to go for *** manual intervention ***, but still will try for restore"
    fatal_exit "Restart Lighttpd initialization: Failed" $? RESTORE_OOB;
}

# 6. Check if HMS oob is accessible after Lighttpd restart.
loginfo "Waiting for $LIGHTTPD_RESTART_WAIT_TIME seconds before lighttpd restarts."
sleep $LIGHTTPD_RESTART_WAIT_TIME

getLastUpgradeStatus_HmsOOB || {

    save_upgrade_status "$HMS_TOKEN" "HMS_UPGRADE_FAILED_AFTER_LIGHTTPD_RESTART"|| {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    fatal_exit "HMS oob reachability status after restart of lighttpd, might need to go for *** manual intervention ***, but still will try for restore: Failed" $? RESTORE_OOB;
}
loginfo "Restart of Lighttpd is successful and services are accessible"

save_upgrade_status "$HMS_TOKEN" "HMS_UPGRADE_SUCCESS"|| {
    loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
}

# Wait for HMS_WAIT_BEFORE_UPGRADE seconds. This is to ensure that running HMS to respond to the caller
# that upgrade has been initiated. (Before it gets killed during upgrade process.)
loginfo "Waiting for $HMS_WAIT_BEFORE_UPGRADE seconds before starting upgrade."
sleep $HMS_WAIT_BEFORE_UPGRADE

# 7. HMS IB Backup Phase
hms_ib_backup || {

    save_upgrade_status "$HMS_TOKEN" "HMS_BACKUP_FAILED" || {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }

    fatal_exit "HMS IB Backup Phase: Failed" $? RESTORE_OOB;
}

hms_inventory_backup  || {

    save_upgrade_status "$HMS_TOKEN" "HMS_INVENTORY_BACKUP_FAILED" || {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }

    fatal_exit "HMS IB Inventory Backup Phase: Failed" $? RESTORE_IB_OOB;
}

# 8. Download the hms out of band inventory file
download_hms_oob_inventory  || {

    save_upgrade_status "$HMS_TOKEN" "HMS_OOB_DOWNLOAD_INVENTORY_FAILED" || {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }

    fatal_exit "HMS IB download oob inventory Phase: Failed" $? RESTORE_IB_OOB;
}

# 9. Build Hms Inband inventory file (Merge data from Hms out of band inventory file to hms ib inventory file
build_hms_ib_inventory  || {

    save_upgrade_status "$HMS_TOKEN" "BUILD_HMS_IB_INVENTORY_FAILED" || {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }

    fatal_exit "HMS IB inventory build Phase: Failed" $? RESTORE_IB_OOB;
}

# 10. HMS IB Upgrade Phase
hms_ib_upgrade || {

    save_upgrade_status "$HMS_TOKEN" "HMS_UPGRADE_FAILED"|| {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    fatal_exit "HMS IB Upgrade Phase: Failed" $? RESTORE_IB_OOB;
}

# 11. Check if HMS IB was able to boot up successfully.
getLastUpgradeStatus_HmsIB || {

    save_upgrade_status "$HMS_TOKEN" "HMS_UPGRADE_FAILED"|| {
        loginfo "Unable to save upgrade status to - $HMS_IB_UPGRADE_STATUS_JSON_FILENAME_FULLPATH"
    }
    fatal_exit "HMS IB upgrade status: Failed" $? RESTORE_IB_OOB;
}

# 12. Delete the backup/configuration folders

# Delete HMS OOB Backup
delete_oob_backup

# Delete HMS Aggregator backup
delete_ib_backup_directory

# Delete HMS Upgrade Bundle and Scripts Directory
delete_ib_upgrade_directory

echo
echo "***********************************************************************"
echo "HMS Upgrade: HMS Aggregator upgraded successfully - `date`"
echo "***********************************************************************"

exit 0;
