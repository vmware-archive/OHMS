/* ********************************************************************************
 * HmsUpgradeTaskBuilder.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/
//
// package com.vmware.vrack.hms.aggregator.util;
//
// import com.vmware.evo.sddc.lcm.primitive.rest.api.model.upgrade.PrimitiveUpgradeSpec;
//
/// **
// * <code>HmsUpgradeTaskBuilder</code><br>
// *
// * @author VMware, Inc.
// */
// public class HmsUpgradeTaskBuilder {
//
// /** The bundle extracted dir abs path. */
// private String bundleExtractedDirAbsPath;
//
// /** The hms upgrade dir. */
// private String hmsUpgradeDir;
//
// /** The hms backup dir. */
// private String hmsBackupDir;
//
// /** The hms ib inventory location. */
// private String hmsIbInventoryLocation;
//
// /** The hms upgrade script. */
// private String hmsUpgradeScript;
//
// /** The oob agent host. */
// private String oobAgentHost;
//
// /** The hms local upgrade bundle abs path. */
// private String hmsLocalUpgradeBundleAbsPath;
//
// /** The oob agent upgrade bundle abs path. */
// private String oobAgentUpgradeBundleAbsPath;
//
// /** The oob nodes endpoint. */
// private String oobNodesEndpoint;
//
// /** The oob upgrade max timeout. */
// private int oobUpgradeMaxTimeout;
//
// /** The oob upgrade retry interval. */
// private int oobUpgradeRetryInterval;
//
// /** The service maintenance max waittime. */
// private int serviceMaintenanceMaxWaittime;
//
// /** The service maintenance retry interval. */
// private int serviceMaintenanceRetryInterval;
//
// /** The shutdown monitoring max waittime. */
// private Long shutdownMonitoringMaxWaittime;
//
// /** The upgrade spec. */
// private PrimitiveUpgradeSpec upgradeSpec;
//
// /** The prm host. */
// private String prmHost;
//
// /** The prm user name. */
// private String prmUserName;
//
// /** The prm password. */
// private String prmPassword;
//
// /** The vrm truststore file. */
// private String vrmTruststoreFile;
//
// /** The vrm truststore password. */
// private String vrmTruststorePassword;
//
// /** The psc ca1 alias. */
// private String pscCa1Alias;
//
// /** The psc ca2 alias. */
// private String pscCa2Alias;
//
// /**
// * Instantiates a new hms upgrade task builder.
// */
// public HmsUpgradeTaskBuilder() {
// }
//
// /**
// * Bundle extracted dir abs path.
// *
// * @param bundleExtractedDirAbsPath the bundle extracted dir abs path
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder bundleExtractedDirAbsPath(String bundleExtractedDirAbsPath) {
// this.bundleExtractedDirAbsPath = bundleExtractedDirAbsPath;
// return this;
// }
//
// /**
// * Hms upgrade dir.
// *
// * @param hmsUpgradeDir the hms upgrade dir
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder hmsUpgradeDir(String hmsUpgradeDir) {
// this.hmsUpgradeDir = hmsUpgradeDir;
// return this;
// }
//
// /**
// * Hms backup dir.
// *
// * @param hmsBackupDir the hms backup dir
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder hmsBackupDir(String hmsBackupDir) {
// this.hmsBackupDir = hmsBackupDir;
// return this;
// }
//
// /**
// * Hms ib inventory location.
// *
// * @param hmsIbInventoryLocation the hms ib inventory location
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder hmsIbInventoryLocation(String hmsIbInventoryLocation) {
// this.hmsIbInventoryLocation = hmsIbInventoryLocation;
// return this;
// }
//
// /**
// * Hms upgrade script.
// *
// * @param hmsUpgradeScript the hms upgrade script
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder hmsUpgradeScript(String hmsUpgradeScript) {
// this.hmsUpgradeScript = hmsUpgradeScript;
// return this;
// }
//
// /**
// * Oob agent host.
// *
// * @param oobAgentHost the oob agent host
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder oobAgentHost(String oobAgentHost) {
// this.oobAgentHost = oobAgentHost;
// return this;
// }
//
// /**
// * Oob agent upgrade bundle abs path.
// *
// * @param oobAgentUpgradeBundleAbsPath the oob agent upgrade bundle abs path
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder oobAgentUpgradeBundleAbsPath(String oobAgentUpgradeBundleAbsPath) {
// this.oobAgentUpgradeBundleAbsPath = oobAgentUpgradeBundleAbsPath;
// return this;
// }
//
// /**
// * Oob nodes endpoint.
// *
// * @param oobNodesEndpoint the oob nodes endpoint
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder oobNodesEndpoint(String oobNodesEndpoint) {
// this.oobNodesEndpoint = oobNodesEndpoint;
// return this;
// }
//
// /**
// * Oob upgrade max timeout.
// *
// * @param oobUpgradeMaxTimeout the oob upgrade max timeout
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder oobUpgradeMaxTimeout(int oobUpgradeMaxTimeout) {
// this.oobUpgradeMaxTimeout = oobUpgradeMaxTimeout;
// return this;
// }
//
// /**
// * Oob upgrade retry interval.
// *
// * @param oobUpgradeRetryInterval the oob upgrade retry interval
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder oobUpgradeRetryInterval(int oobUpgradeRetryInterval) {
// this.oobUpgradeRetryInterval = oobUpgradeRetryInterval;
// return this;
// }
//
// /**
// * Service maintenance max waittime.
// *
// * @param serviceMaintenanceMaxWaittime the service maintenance max waittime
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder serviceMaintenanceMaxWaittime(int serviceMaintenanceMaxWaittime) {
// this.serviceMaintenanceMaxWaittime = serviceMaintenanceMaxWaittime;
// return this;
// }
//
// /**
// * Service maintenance retry interval.
// *
// * @param serviceMaintenanceRetryInterval the service maintenance retry
// * interval
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder serviceMaintenanceRetryInterval(int serviceMaintenanceRetryInterval) {
// this.serviceMaintenanceRetryInterval = serviceMaintenanceRetryInterval;
// return this;
// }
//
// /**
// * Shutdown monitoring max waittime.
// *
// * @param shutdownMonitoringMaxWaittime the shutdown monitoring max waittime
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder shutdownMonitoringMaxWaittime(Long shutdownMonitoringMaxWaittime) {
// this.shutdownMonitoringMaxWaittime = shutdownMonitoringMaxWaittime;
// return this;
// }
//
// /**
// * Upgrade spec.
// *
// * @param upgradeSpec the upgrade spec
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder upgradeSpec(PrimitiveUpgradeSpec upgradeSpec) {
// this.upgradeSpec = upgradeSpec;
// return this;
// }
//
// /**
// * Prm host.
// *
// * @param prmHost the prm host
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder prmHost(String prmHost) {
// this.prmHost = prmHost;
// return this;
// }
//
// /**
// * Prm user name.
// *
// * @param prmUserName the prm user name
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder prmUserName(String prmUserName) {
// this.prmUserName = prmUserName;
// return this;
// }
//
// /**
// * Prm password.
// *
// * @param prmPassword the prm password
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder prmPassword(String prmPassword) {
// this.prmPassword = prmPassword;
// return this;
// }
//
// /**
// * Hms local upgrade bundle abs path.
// *
// * @param hmsLocalUpgradeBundleAbsPath the hms local upgrade bundle abs path
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder hmsLocalUpgradeBundleAbsPath(String hmsLocalUpgradeBundleAbsPath) {
// this.hmsLocalUpgradeBundleAbsPath = hmsLocalUpgradeBundleAbsPath;
// return this;
// }
//
// /**
// * Vrm truststore file.
// *
// * @param vrmTruststoreFile the vrm truststore file
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder vrmTruststoreFile(String vrmTruststoreFile) {
// this.vrmTruststoreFile = vrmTruststoreFile;
// return this;
// }
//
// /**
// * Vrm truststore password.
// *
// * @param vrmTruststorePassword the vrm truststore password
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder vrmTruststorePassword(String vrmTruststorePassword) {
// this.vrmTruststorePassword = vrmTruststorePassword;
// return this;
// }
//
// /**
// * Psc ca1 alias.
// *
// * @param pscCa1Alias the psc ca1 alias
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder pscCa1Alias(String pscCa1Alias) {
// this.pscCa1Alias = pscCa1Alias;
// return this;
// }
//
// /**
// * Psc ca2 alias.
// *
// * @param pscCa2Alias the psc ca2 alias
// * @return the hms upgrade task builder
// */
// public HmsUpgradeTaskBuilder pscCa2Alias(String pscCa2Alias) {
// this.pscCa2Alias = pscCa2Alias;
// return this;
// }
//
// /**
// * Builds the.
// *
// * @return the hms upgrade task
// */
// public HmsUpgradeTask build() {
//
// return new HmsUpgradeTask(upgradeSpec, hmsUpgradeDir, bundleExtractedDirAbsPath, hmsUpgradeScript, hmsBackupDir,
// shutdownMonitoringMaxWaittime, serviceMaintenanceMaxWaittime, serviceMaintenanceRetryInterval,
// hmsIbInventoryLocation, oobAgentUpgradeBundleAbsPath, oobAgentHost, oobUpgradeMaxTimeout,
// oobUpgradeRetryInterval, oobNodesEndpoint, prmHost, prmUserName, prmPassword,
// hmsLocalUpgradeBundleAbsPath, vrmTruststoreFile, vrmTruststorePassword, pscCa1Alias, pscCa2Alias);
// }
// }
