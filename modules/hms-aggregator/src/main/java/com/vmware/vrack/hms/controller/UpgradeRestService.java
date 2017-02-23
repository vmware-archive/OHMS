/* ********************************************************************************
 * UpgradeRestService.java
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
// package com.vmware.vrack.hms.controller;
//
// import java.io.File;
// import java.net.URI;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
//
// import javax.servlet.http.HttpServletRequest;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.ResponseBody;
//
// import com.vmware.evo.sddc.lcm.primitive.rest.api.model.upgrade.PrimitiveUpgradeSpec;
// import com.vmware.evo.sddc.lcm.primitive.rest.api.model.upgrade.PrimitiveUpgradeStatus;
// import com.vmware.evo.sddc.lcm.primitive.rest.api.model.upgrade.PrimitiveUpgradeStatusCodes;
// import com.vmware.vrack.hms.aggregator.util.HmsUpgradeTask;
// import com.vmware.vrack.hms.aggregator.util.HmsUpgradeTaskBuilder;
// import com.vmware.vrack.hms.aggregator.util.UpgradeUtil;
// import com.vmware.vrack.hms.common.resource.UpgradeStatusCode;
// import com.vmware.vrack.hms.common.rest.model.UpgradeStatus;
// import com.vmware.vrack.hms.common.service.ServiceState;
// import com.vmware.vrack.hms.common.upgrade.api.ChecksumMethod;
// import com.vmware.vrack.hms.common.util.FileUtil;
// import com.vmware.vrack.hms.common.util.HmsUpgradeUtil;
//
/// **
// * <code>UpgradeRestService</code><br>
// *
// * @author VMware, Inc.
// */
// @Controller
// @RequestMapping(value = { "/upgrade" },
// produces = { MediaType.APPLICATION_JSON_VALUE })
// public class UpgradeRestService {
//
// /** The logger. */
// private Logger logger = LoggerFactory.getLogger(UpgradeRestService.class);
//
// @Value("${hms.upgrade.dir:/home/vrack/upgrade}")
// private String hmsUpgradeDir;
//
// @Value("${hms.upgrade.enable-versioncheck:true}")
// private boolean versionCheck;
//
// @Value("${hms.upgrade.script:hms_ib_upgrade_wrapper.sh}")
// private String hmsUpgradeScript;
//
// @Value("${hms.switch.host}")
// private String oobAgentHost;
//
// @Value("${hms.oob.upgrade.max-wait-time:300000}")
// private int oobUpgradeMaxTimeout;
//
// @Value("${hms.oob.upgrade.retry-interval:30000}")
// private int oobUpgradeRetryInterval;
//
// @Value("${hms.backup.dir:/home/vrack/backup}")
// private String hmsBackupDir;
//
// @Value("${prm.host}")
// private String prmHost;
//
// @Value("${prm.basic.username}")
// private String prmUserName;
//
// @Value("${prm.basic.password}")
// private String prmPassword;
//
// @Value("${monitor.frequency:600000}")
// private Long monitorFrequency;
//
// @Value("${monitor.shutdown.additional.waittime:60000}")
// private Long shutdownMonitoringAdditionalWaitTime;
//
// @Value("${hms.service.maintenance.max-wait-time:300000}")
// private int serviceMaintenanceMaxWaittime;
//
// @Value("${hms.service.maintenance.retry-interval:30000}")
// private int serviceMaintenanceRetryInterval;
//
// @Value("${hms.ib.inventory.location}")
// private String hmsIbInventoryLocation;
//
// @Value("${hms.oob.nodes.pathinfo}")
// private String oobNodesEndpoint;
//
// @Value("${vrm.truststore.file}")
// private String vrmTruststoreFile;
//
// @Value("${vrm.truststore.password}")
// private String vrmTruststorePassword;
//
// @Value("${psc.ca.1.alias}")
// private String pscCa1Alias;
//
// @Value("${psc.ca.2.alias}")
// private String pscCa2Alias;
//
// /**
// * Upgrade.
// *
// * @param upgradeSpec the upgrade parameters
// * @param request the request
// * @return the upgrade status
// */
// @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
// @ResponseBody
// public ResponseEntity<PrimitiveUpgradeStatus> upgrade(
// @RequestBody(required = false) PrimitiveUpgradeSpec upgradeSpec,
// HttpServletRequest request) {
//
// PrimitiveUpgradeStatus primitiveUpgradeStatus = null;
// String message = null;
// String upgradeId = null;
//
// if (!UpgradeUtil.validateUpgradeParameters(upgradeSpec)) {
//
// message = "Upgrade parameters are either null or blank. "
// + "All upgrade parameters are mandatory and they can't be either null or blank.";
// logger.error(message);
//
// if (upgradeSpec != null) {
// upgradeId = upgradeSpec.getUpgradeId();
// }
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_INVALID_REQUEST, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return BAD_REQUEST
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.BAD_REQUEST);
// }
// upgradeId = upgradeSpec.getUpgradeId();
//
// // validate version
// if (versionCheck
// && !UpgradeUtil.validateVersion(upgradeSpec.getPreviousVersion(), upgradeSpec.getPatchFile())) {
//
// logger.error("HMS upgrade version validation failed.");
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_FORBIDDEN, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return FORBIDDEN
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.FORBIDDEN);
// }
//
// /*
// * LCM will send the SHA-256 checksum of the bundle. Check that the bundle exists and validate its checksum.
// * Please note that the PrmitiveUpgradeSpec still says sha1checksum. This should be fixed
// */
// String bundleChecksum = FileUtil.getFileChecksum(upgradeSpec.getPatchFile(), ChecksumMethod.SHA256);
// if (bundleChecksum == null) {
//
// message = String.format("Unable to compute checksum of the upgrade bundle - '%s' using algoritham - '%s'.",
// upgradeSpec.getPatchFile(), ChecksumMethod.SHA256.toString());
// logger.error(message);
//
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_INTERNAL_ERROR, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return INTERNAL_SERVER_ERROR
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.INTERNAL_SERVER_ERROR);
//
// } else {
//
// if (!bundleChecksum.equalsIgnoreCase(upgradeSpec.getSha1CheckSum())) {
//
// message = String.format("Upgrade bundle checkusm validation failed. "
// + "[ Upgrade bundle: %s, Computed checksum: %s, Upgrade paramaters checksum: %s.",
// upgradeSpec.getPatchFile(), bundleChecksum, upgradeSpec.getSha1CheckSum());
// logger.error(message);
//
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_FORBIDDEN, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return FORBIDDEN
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.FORBIDDEN);
// }
// }
//
// /*
// * Extract bundle to hmsUpgradeDir + hmsUpgradeSpec.getId()
// */
// String bundleExtractedDirAbsPath = String.format("%1$s/%2$s", hmsUpgradeDir, upgradeId);
// boolean bundleExtracted = FileUtil.extractArchive(upgradeSpec.getPatchFile(), bundleExtractedDirAbsPath);
// if (!bundleExtracted) {
//
// message = String.format("Unable to extract upgrade bundle - '%s' to '%s'", upgradeSpec.getPatchFile(),
// bundleExtractedDirAbsPath);
// logger.error(message);
//
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_INTERNAL_ERROR, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return INTERNAL_SERVER_ERROR
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.INTERNAL_SERVER_ERROR);
// }
//
// // find aggregator bundle
// File[] files = FileUtil.findFiles(bundleExtractedDirAbsPath, "hms-local.*.war", true);
// if (files == null) {
//
// // delete bundleExtractedDirAbsPath
// if (!FileUtil.deleteDirectory(bundleExtractedDirAbsPath)) {
// logger.warn("Failed to delete HMS upgrade bundle extracted directory -'{}'.",
// bundleExtractedDirAbsPath);
// }
//
// message = String.format("HMS upgrade bundle not found after extracting upgrade bundle - '%s'.",
// upgradeSpec.getPatchFile());
// logger.error(message);
//
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_FORBIDDEN, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return FORBIDDEN
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.FORBIDDEN);
//
// } else if (files != null && files.length > 1) {
//
// // delete bundleExtractedDirAbsPath
// if (!FileUtil.deleteDirectory(bundleExtractedDirAbsPath)) {
// logger.warn("Failed to delete HMS upgrade bundle extracted directory -'{}'.",
// bundleExtractedDirAbsPath);
// }
//
// message = String.format("Multiple HMS Aggregator upgrade bundles found after extracting "
// + "upgrade bundle - '%s'.", upgradeSpec.getPatchFile());
// logger.error(message);
//
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_FORBIDDEN, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return FORBIDDEN
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.FORBIDDEN);
// }
//
// // Absolute path of the hms-local.war of the upgrade bundle.
// String hmsLocalUpgradeBundleAbsPath = files[0].getAbsolutePath();
//
// // find out-of-band agent upgrade bundle
// files = FileUtil.findFiles(bundleExtractedDirAbsPath, "hms.*.tar.gz", true);
// if (files == null) {
//
// // delete bundleExtractedDirAbsPath
// if (!FileUtil.deleteDirectory(bundleExtractedDirAbsPath)) {
// logger.warn("Failed to delete HMS upgrade bundle extracted directory -'{}'.",
// bundleExtractedDirAbsPath);
// }
//
// message = String.format("HMS Out-of-band agent upgrade bundle not found after "
// + "extracting upgrade bundle - '%s'.", upgradeSpec.getPatchFile());
// logger.error(message);
//
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_FORBIDDEN, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return FORBIDDEN
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.FORBIDDEN);
//
// } else if (files != null && files.length > 1) {
//
// // delete bundleExtractedDirAbsPath
// if (!FileUtil.deleteDirectory(bundleExtractedDirAbsPath)) {
// logger.warn("Failed to delete HMS upgrade bundle extracted directory -'{}'.",
// bundleExtractedDirAbsPath);
// }
//
// message = String.format("Multiple Out-of-band agent upgrade bundles found after "
// + "extracting upgrade bundle - '%s'.", upgradeSpec.getPatchFile());
// logger.error(message);
//
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_FORBIDDEN, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return FORBIDDEN
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.FORBIDDEN);
// }
// String oobAgentUpgradeBundleAbsPath = files[0].getAbsolutePath();
//
// // Notify PRM about HMS in MAINTENANCE
// if (!UpgradeUtil.notifyPRMService(ServiceState.NORMAL_MAINTENANCE, prmHost, prmUserName, prmPassword)) {
//
// // delete bundleExtractedDirAbsPath
// if (!FileUtil.deleteDirectory(bundleExtractedDirAbsPath)) {
// logger.warn("Failed to delete HMS upgrade bundle extracted directory -'{}'.",
// bundleExtractedDirAbsPath);
// }
//
// logger.error("Unable to notify PRM about HMS in Maintenance state.");
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_FORBIDDEN, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.INTERNAL_SERVER_ERROR);
// }
//
// URI upgadeStatusURI = UpgradeUtil.getUpgradeStatusURI(request, upgradeId);
// if (upgadeStatusURI == null) {
//
// // delete bundleExtractedDirAbsPath
// if (!FileUtil.deleteDirectory(bundleExtractedDirAbsPath)) {
// logger.warn("Failed to delete HMS upgrade bundle extracted directory -'{}'.",
// bundleExtractedDirAbsPath);
// }
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_INTERNAL_ERROR, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.INTERNAL_SERVER_ERROR);
// }
//
// // initiate upgrade
// ExecutorService executorService = Executors.newSingleThreadExecutor();
// Long shutdownMonitoringMaxWaittime = monitorFrequency + shutdownMonitoringAdditionalWaitTime;
//
// HmsUpgradeTaskBuilder hmsUpgradeTaskBuilder = new HmsUpgradeTaskBuilder();
// HmsUpgradeTask hmsUpgradeTask = hmsUpgradeTaskBuilder.upgradeSpec(upgradeSpec).hmsUpgradeDir(hmsUpgradeDir)
// .bundleExtractedDirAbsPath(bundleExtractedDirAbsPath).hmsUpgradeScript(hmsUpgradeScript)
// .hmsBackupDir(hmsBackupDir).shutdownMonitoringMaxWaittime(shutdownMonitoringMaxWaittime)
// .serviceMaintenanceMaxWaittime(serviceMaintenanceMaxWaittime)
// .serviceMaintenanceRetryInterval(serviceMaintenanceRetryInterval)
// .hmsIbInventoryLocation(hmsIbInventoryLocation)
// .oobAgentUpgradeBundleAbsPath(oobAgentUpgradeBundleAbsPath).oobAgentHost(oobAgentHost)
// .oobUpgradeMaxTimeout(oobUpgradeMaxTimeout).oobUpgradeRetryInterval(oobUpgradeRetryInterval)
// .oobNodesEndpoint(oobNodesEndpoint).prmHost(prmHost).prmPassword(prmPassword).prmUserName(prmUserName)
// .hmsLocalUpgradeBundleAbsPath(hmsLocalUpgradeBundleAbsPath).vrmTruststoreFile(vrmTruststoreFile)
// .vrmTruststorePassword(vrmTruststorePassword).pscCa1Alias(pscCa1Alias).pscCa2Alias(pscCa2Alias).build();
//
// executorService.submit(hmsUpgradeTask);
// executorService.shutdown();
//
// // save upgrade status
// UpgradeStatus upgradeStatus = new UpgradeStatus();
// upgradeStatus.setStatusCode(UpgradeStatusCode.HMS_UPGRADE_INITIATED);
// upgradeStatus.setId(upgradeId);
// String upgradeStatusFileNameAbsPath = String.format("%1$s/%2$s.json", hmsUpgradeDir, upgradeId);
// boolean upgradeStatusFileSaved = HmsUpgradeUtil.saveUpgradeStatus(upgradeStatusFileNameAbsPath, upgradeStatus);
// if (!upgradeStatusFileSaved) {
// logger.warn("Unable to save UpgradeStatus to file '{}', after initiating upgrade.",
// upgradeStatusFileNameAbsPath);
// }
//
// logger.info("HMS upgrade initiated. Upgrade ID: {}", upgradeId);
//
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_INITIATED, PrimitiveUpgradeStatusCodes.IN_PROGRESS, upgadeStatusURI);
//
// // return ACCEPTED
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.ACCEPTED);
// }
//
// /**
// * Gets the upgrade status.
// *
// * @param upgradeId the upgrade id
// * @return the upgrade status
// */
// @RequestMapping(method = RequestMethod.GET, value = { "/monitor/{upgradeId}" })
// @ResponseBody
// public ResponseEntity<PrimitiveUpgradeStatus> getUpgradeStatus(@PathVariable("upgradeId") String upgradeId) {
//
// PrimitiveUpgradeStatus primitiveUpgradeStatus = null;
//
// String upgradeStatusFileName = upgradeId + ".json";
// logger.debug("Looking for upgrade status file '{}' at '{}' directory and its sub-directories.",
// upgradeStatusFileName, hmsUpgradeDir);
// File[] upgradeSatusFiles = FileUtil.findFiles(hmsUpgradeDir, upgradeStatusFileName, true);
// if (upgradeSatusFiles == null || upgradeSatusFiles.length != 1) {
//
// logger.error("No upgrade status file '{}' found for the given upgrade id '{}' at '{}'.",
// upgradeStatusFileName, upgradeId, hmsUpgradeDir);
//
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_INVALID_REQUEST, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return BAD_REQUEST
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.BAD_REQUEST);
// }
//
// File upgradeSatusFile = upgradeSatusFiles[0];
// String upgradeStatusFileNameAbsPath = upgradeSatusFile.getAbsolutePath();
// logger.debug("Found upgrade status file - '{}'. ", upgradeStatusFileNameAbsPath);
// UpgradeStatus upgradeStatus = HmsUpgradeUtil.loadUpgradeStatus(upgradeSatusFile);
// if (upgradeStatus == null) {
//
// logger.error("Unable to load UpgradeStatus from the file - '{}'", upgradeStatusFileNameAbsPath);
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId,
// UpgradeStatusCode.HMS_UPGRADE_INTERNAL_ERROR, PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE);
//
// // return INTERNAL_SERVER_ERROR
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.INTERNAL_SERVER_ERROR);
// }
//
// // additional check of upgrade id
// if (upgradeStatus.getId().equals(upgradeId)) {
//
// String primitiveUpgradeStatusCode = PrimitiveUpgradeStatusCodes.IN_PROGRESS;
// switch (upgradeStatus.getStatusCode()) {
//
// case HMS_UPGRADE_SUCCESS:
//
// primitiveUpgradeStatusCode = PrimitiveUpgradeStatusCodes.COMPLETED_WITH_SUCCESS;
// break;
//
// case HMS_UPGRADE_INITIATED:
//
// primitiveUpgradeStatusCode = PrimitiveUpgradeStatusCodes.IN_PROGRESS;
// break;
//
// default:
//
// /*
// * HMS Upgrade Status anything other than INITIATED or SUCCESS
// * is an ERROR
// */
// primitiveUpgradeStatusCode = PrimitiveUpgradeStatusCodes.COMPLETED_WITH_FAILURE;
// break;
// }
//
// primitiveUpgradeStatus = UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId, upgradeStatus.getStatusCode(),
// primitiveUpgradeStatusCode);
//
// logger.info("Returning UpgradeStatus - {} for the Upgrade ID: {}", upgradeStatus.toString(), upgradeId);
// return new ResponseEntity<PrimitiveUpgradeStatus>(primitiveUpgradeStatus, HttpStatus.OK);
// }
// return null;
// }
// }
