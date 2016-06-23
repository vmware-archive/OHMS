#!/bin/bash

# Directory where all shell scripts will reside e.g hms_oob_upgrade.sh, hms_ib_upgrade.sh
HMS_SCRIPT_DIR="/home/vrack/vrm/webapps/hms-local/WEB-INF/classes"

# **** HMS IB Related Vars **** #

# Hms ib upgrade binary
HMS_IB_BINARY_FILENAME="hms-local.war"
# Directory which holds hms ib upgrade binary
HMS_IB_BINARY_LOCATION="/home/vrack/upgrade/hms-ib"
# Calculated absolute path of hms ib upgrade binary
HMS_IB_BINARY_FULLPATH="${HMS_IB_BINARY_LOCATION}/${HMS_IB_BINARY_FILENAME}"
# Provided checksum file for hms ib upgrade binary. Should be exactly side by side to the binary
HMS_IB_BINARY_CHECKSUM="${HMS_IB_BINARY_FILENAME}.md5"
# Calculated absolute path to the ib checksum file
HMS_IB_BINARY_CHECKSUM_FULLPATH="$HMS_IB_BINARY_LOCATION/$HMS_IB_BINARY_CHECKSUM"


# Hms ib Backup script file
HMS_IB_BACKUP_SCRIPT="hms_ib_backup.sh"
#Hms ib Service Script file that will start stop and query running HMS IB
HMS_IB_SERVICE_SCRIPT="hms_ib_service.sh"
#Hms ib Upgrade Script
HMS_IB_UPGRADE_SCRIPT="hms_ib_upgrade.sh"
#Hms ib recover script that will recover from the previous running build.
HMS_IB_RECOVER_SCRIPT="hms_ib_recover.sh"

# HMS IB absolute path to Backup Script
HMS_IB_BACKUP_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_BACKUP_SCRIPT}"
# HMS IB absolute path to Service Script
HMS_IB_SERVICE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_SERVICE_SCRIPT}"
# HMS IB absolute path to Upgrade Script
HMS_IB_UPGRADE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_UPGRADE_SCRIPT}"
# HMS IB absolute path to Recover Script
HMS_IB_RECOVER_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_RECOVER_SCRIPT}"

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
	loginfo "#######################################################";
	loginfo "### HMS_IB_VALIDATE : 0. Configuring variables ###";
	loginfo "#######################################################";

	if [ "$#" -ge 8 ]
	then
		HMS_SCRIPT_DIR="$1";

		HMS_IB_BACKUP_SCRIPT="$2"
		HMS_IB_SERVICE_SCRIPT="$3"
		HMS_IB_UPGRADE_SCRIPT="$4"
		HMS_IB_RECOVER_SCRIPT="$5"

		HMS_IB_BACKUP_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_BACKUP_SCRIPT}"
		HMS_IB_SERVICE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_SERVICE_SCRIPT}"
		HMS_IB_UPGRADE_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_UPGRADE_SCRIPT}"
		HMS_IB_RECOVER_SCRIPT_FULLPATH="${HMS_SCRIPT_DIR}/${HMS_IB_RECOVER_SCRIPT}"

		HMS_IB_BINARY_FILENAME="$6";
		HMS_IB_BINARY_LOCATION="$7";
		HMS_IB_BINARY_FULLPATH="${HMS_IB_BINARY_LOCATION}/${HMS_IB_BINARY_FILENAME}";

		HMS_IB_BINARY_CHECKSUM="$8";
		HMS_IB_BINARY_CHECKSUM_FULLPATH="$HMS_IB_BINARY_LOCATION/$HMS_IB_BINARY_CHECKSUM";
	else
		loginfo "FAILED: Required number of arguments must be passed before continuing.";
		return 20;
	fi
}

# Validation Entry Point
function validate()
{
	loginfo "###########################################";
	loginfo "### HMS_VALIDATE : 1. Validation Phase ###";
	loginfo "############################################";


	echo
	loginfo "###################################################";
	loginfo "### HMS_VALIDATE : 1.2 HMS IB Validation Phase ###";
	loginfo "###################################################";
	validate_hms_ib || {
		loginfo "Unable to validate HMS_IB files";
		return 20;
	}
	loginfo "HMS IB Validation completed Successfully";
}

# Validation of HMS IB files
function validate_hms_ib()
{
	loginfo "1.2.1 Validating HMS-IB Script file"
	validate_hms_ib_script || {
		loginfo "HMS_IB Script [ $HMS_IB_SCRIPT_FULLPATH ] not found";
		return 21;
	}

	loginfo "1.2.2 Validating HMS-IB Binary"
	validate_hms_ib_binary || {
		loginfo "HMS_IB Binary [ $HMS_IB_BINARY_FULLPATH ] not found";
		return 22;
	}

	#loginfo "1.2.3 Validating HMS-IB Binary against checksum"
	#validate_hms_ib_binary_checksum || {
	#	loginfo "Unable to validate Checksum for HMS IB Binary";
	#	return 23;
	#}
}


# Check Hms ib script availability
function validate_hms_ib_script()
{
	# check if latest hms ib script file exists
	local ibscript="$HMS_IB_BACKUP_SCRIPT_FULLPATH"
	[[ -f "$ibscript" ]] || return 201

	# check if latest hms ib script file exists
	local ibscript="$HMS_IB_SERVICE_SCRIPT_FULLPATH"
	[[ -f "$ibscript" ]] || return 201

	# check if latest hms ib script file exists
	local ibscript="$HMS_IB_UPGRADE_SCRIPT_FULLPATH"
	[[ -f "$ibscript" ]] || return 201

	# check if latest hms ib script file exists
	local ibscript="$HMS_IB_RECOVER_SCRIPT_FULLPATH"
	[[ -f "$ibscript" ]] || return 201

}

# Check Hms ib binary availability
function validate_hms_ib_binary()
{
	loginfo "Checking IB binary [ $HMS_IB_BINARY_FILENAME ] in location [ $HMS_IB_BINARY_LOCATION ]"
	local ibbinary="$HMS_IB_BINARY_FULLPATH"
	[[ -f "$ibbinary" ]] || return 202
}

# Validate Hms ib binary against provided checksum
function validate_hms_ib_binary_checksum()
{
	loginfo "Checking md5 checksum file [ $HMS_IB_BINARY_CHECKSUM ] in $HMS_IB_BINARY_LOCATION"
	# check if latest hms ib binary checksum file exists
	local f="$HMS_IB_BINARY_CHECKSUM_FULLPATH"
	[[ -f "$f" ]] || {
		loginfo "HMS IB Binary Checksum file [ $HMS_IB_BINARY_CHECKSUM_FULLPATH ] not found";
		return 203;
	}

	validate_checksum $HMS_IB_BINARY_FULLPATH $HMS_IB_BINARY_CHECKSUM_FULLPATH || {
		return 204;
	}
}

# Validate Checksum for file with CheckSum Provided. Usage: validate_checksum <binary fullpath> <provided checksum file>
function validate_checksum()
{
	if [ "$#" -eq 2 ]
	then
		md5prog=$(get_md5sum_fullpath);
		binary_fullpath="$1";
		provided_checksum_file="$2";

		if [ -z $md5prog ]
		then
			loginfo "Unable to find md5sum program on this system.Can not continue."
			return 50;
		fi

		calc_md5=`${md5prog} ${binary_fullpath} | awk '{ print $1 }'`;
		if [ -z $calc_md5 ]
		then
			{ loginfo "Error in calculating MD5 Checksum for file [ $binary_fullpath ]"; return 51; }
		fi

		orig_md5=`cat ${provided_checksum_file} | awk '{ print $1 }'`;
		if [ -z $orig_md5 ]
		then
			{ loginfo "Error in reading provided MD5 Checksum file [ $provided_checksum_file ]"; return 52; }
		fi

		# removing trailing and heading whitespaces before comparison
		calculated_md5=`trim_trailing_whitespaces $calc_md5`
		original_md5=`trim_trailing_whitespaces $orig_md5`

		# for case insensitive md5 comparison
		if [ `echo "$calculated_md5" | tr [:upper:] [:lower:]` =  `echo "$original_md5" | tr [:upper:] [:lower:]` ];
		then
			loginfo "Checksum SUCCESS. calculated [ $calculated_md5 ] and provided [ $original_md5 ]"
			return 0;
		else
			loginfo "Checksum FAILED. calculated [ $calculated_md5 ] and provided [ $original_md5 ]"
			return 53;
		fi

	else
		loginfo "Incorrect arguments to use the function No. of arguments passed [ "$#" ]";
	fi
}

# Gets the absolute path of md5sum on the system
function get_md5sum_fullpath()
{
	echo `which md5sum`;
}

#Removes Trailing and heading whitespaces
function trim_trailing_whitespaces()
{
	#var1="\t\t Test String trimming   "
	var1=$1
	Var2=$(echo "${var1}" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
	echo $Var2
}

# Log & Update HMS Validation Status
function fatal_exit()
{
	loginfo "$1 Exit Code: $2"

	echo
	echo "***************************************************************"
	echo "HMS Validation Phase: Failed @ `date`"
	echo "***************************************************************"
	exit $2
}


# MAIN ENTRY POINT

# 0. Check Arguments first to see if any HMS Token has been provided.
check_arguments "$@" || {
fatal_exit "HMS IB validation phase :Arguments Check: Failed" $? NO_UNDO_ACTION;
}

validate || {
fatal_exit "HMS upgrade validation failed" $? NO_UNDO_ACTION;
}

echo
echo "***********************************************************************"
echo "HMS Validation completed successfully. All preconditions satisfied. @ `date`"
echo "***********************************************************************"

exit 0;
