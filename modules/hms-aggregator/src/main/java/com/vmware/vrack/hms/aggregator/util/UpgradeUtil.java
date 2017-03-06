/* ********************************************************************************
 * UpgradeUtil.java
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
// import java.io.File;
// import java.io.IOException;
// import java.net.URI;
// import java.net.URISyntaxException;
// import java.security.KeyManagementException;
// import java.security.KeyStoreException;
// import java.security.NoSuchAlgorithmException;
// import java.security.cert.CertificateException;
// import java.security.cert.X509Certificate;
// import java.util.Map;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
//
// import javax.net.ssl.SSLContext;
// import javax.servlet.http.HttpServletRequest;
//
// import org.apache.commons.io.FileUtils;
// import org.apache.commons.io.FilenameUtils;
// import org.apache.commons.lang3.StringUtils;
// import org.apache.http.config.Registry;
// import org.apache.http.config.RegistryBuilder;
// import org.apache.http.conn.socket.ConnectionSocketFactory;
// import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
// import org.apache.http.conn.ssl.SSLContextBuilder;
// import org.apache.http.conn.ssl.SSLContexts;
// import org.apache.http.conn.ssl.TrustStrategy;
// import org.apache.http.impl.client.CloseableHttpClient;
// import org.apache.http.impl.client.HttpClients;
// import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.core.io.FileSystemResource;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.client.HttpClientErrorException;
// import org.springframework.web.client.HttpServerErrorException;
// import org.springframework.web.client.RestTemplate;
//
// import com.vmware.evo.sddc.lcm.primitive.rest.api.model.upgrade.PrimitiveUpgradeSpec;
// import com.vmware.evo.sddc.lcm.primitive.rest.api.model.upgrade.PrimitiveUpgradeStatus;
// import com.vmware.evo.sddc.lcm.primitive.rest.api.model.upgrade.PrimitiveUpgradeStatusBuilder;
// import com.vmware.vrack.hms.aggregator.HMSAboutResponseAggregator;
// import com.vmware.vrack.hms.common.exception.HmsException;
// import com.vmware.vrack.hms.common.notification.BaseResponse;
// import com.vmware.vrack.hms.common.resource.AboutResponse;
// import com.vmware.vrack.hms.common.resource.UpgradeStatusCode;
// import com.vmware.vrack.hms.common.rest.model.HmsServiceState;
// import com.vmware.vrack.hms.common.rest.model.OobUpgradeSpec;
// import com.vmware.vrack.hms.common.rest.model.RollbackSpec;
// import com.vmware.vrack.hms.common.rest.model.UpgradeStatus;
// import com.vmware.vrack.hms.common.service.ServiceState;
// import com.vmware.vrack.hms.common.util.ComparableVersion;
// import com.vmware.vrack.hms.common.util.Constants;
// import com.vmware.vrack.hms.common.util.FileUtil;
// import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;
//
/// **
// * <code>UpgradeUtil</code> is ... <br>
// *
// * @author VMware, Inc.
// */
// @SuppressWarnings("deprecation")
// public class UpgradeUtil {
//
// /** The logger. */
// private static Logger logger = LoggerFactory.getLogger(UpgradeUtil.class);
//
// /** The Constant CONNECTION_TIMEOUT. */
// private static final int CONNECTION_TIMEOUT = 60000;
//
// /** The Constant READ_TIMEOUT. */
// private static final int READ_TIMEOUT = 30000;
//
// /** The Constant CONCURRENT_HTTP_REQUESTS. */
// private static final int CONCURRENT_HTTP_REQUESTS = 1;
//
// /** The Constant SEMANTIC_VERSION_REGEX. */
// private static final String SEMANTIC_VERSION_REGEX = "(\\d+.){0,2}\\d+";
//
// /** The Constant HMS_SNAPSHOT_VERSION_REGEX. */
// private static final String HMS_SNAPSHOT_VERSION_REGEX = SEMANTIC_VERSION_REGEX + "-SNAPSHOT-\\d+";
//
// /** The Constant HMS_RELEASE_VERSION_REGEX. */
// private static final String HMS_RELEASE_VERSION_REGEX = SEMANTIC_VERSION_REGEX + "-RELEASE-\\d+";
//
// /** The Constant HMS_PATCHFILE_RELEASE_VERSION_PATTERN. */
// private static final String HMS_PATCHFILE_RELEASE_VERSION_PATTERN = "^.*?(" + HMS_RELEASE_VERSION_REGEX + ").*$";
//
// /** The Constant HMS_PATCHFILE_SNAPSHOT_VERSION_PATTERN. */
// private static final String HMS_PATCHFILE_SNAPSHOT_VERSION_PATTERN = "^.*?(" + HMS_SNAPSHOT_VERSION_REGEX + ").*$";
//
// /**
// * Validate upgrade parameters.
// *
// * @param upgradeSpec the upgrade parameters
// * @return true, if successful
// */
// public static boolean validateUpgradeParameters(PrimitiveUpgradeSpec upgradeSpec) {
//
// if (upgradeSpec == null) {
// logger.info("UpgradeSpec is null");
// return false;
// }
//
// if (StringUtils.isBlank(upgradeSpec.getPatchFile())) {
// logger.info("Upgrade patch file is either null or blank.");
// return false;
// }
//
// if (StringUtils.isBlank(upgradeSpec.getSha1CheckSum())) {
// logger.info("Upgrade patch file SHA-1 checksum is either null or blank.");
// return false;
// }
//
// if (StringUtils.isBlank(upgradeSpec.getUpgradeId())) {
// logger.info("Upgrade ID is either null or blank.");
// return false;
// }
// return true;
// }
//
// /**
// * Gets the oob version.
// *
// * @return the oob version
// */
// public static String getOobBuildVersion() {
//
// try {
//
// AboutResponse response = MonitoringUtil.getServerEndpointAboutResponseOOB(Constants.HMS_OOB_ABOUT_ENDPOINT);
// if (response != null) {
// return response.getBuildVersion();
// }
// } catch (Exception e) {
//
// logger.debug("Unable to get HMS Out-of-band agent version.", e);
// }
// return null;
// }
//
// /**
// * Gets the oob build date.
// *
// * @return the oob build date
// */
// public static String getOobBuildDate() {
//
// try {
//
// AboutResponse response = MonitoringUtil.getServerEndpointAboutResponseOOB(Constants.HMS_OOB_ABOUT_ENDPOINT);
// if (response != null) {
// return response.getBuildDate();
// }
// } catch (Exception e) {
//
// logger.debug("Unable to get HMS Out-of-band agent build date.", e);
// }
// return null;
// }
//
// /**
// * Initiate oob upgrade.
// *
// * @param upgradeSpec the upgrade spec
// * @return true, if successful
// */
// public static boolean initiateOobUpgrade(OobUpgradeSpec upgradeSpec) {
//
// try {
// String path = Constants.HMS_OOB_UPGRADE_ENDPOINT;
// HttpHeaders headers = getHttpHeaders();
// HmsOobAgentRestTemplate<OobUpgradeSpec> restTemplate = new HmsOobAgentRestTemplate<OobUpgradeSpec>(upgradeSpec,
/// headers);
//
// ResponseEntity<UpgradeStatus> upgradeResponse = restTemplate.exchange(HttpMethod.POST, path,
// UpgradeStatus.class);
//
// if (upgradeResponse != null) {
// UpgradeStatus upgradeStatus = upgradeResponse.getBody();
// if (upgradeResponse.getStatusCode() == HttpStatus.ACCEPTED) {
// logger.info("Initiated HMS Out-of-band agent upgrade successfully.");
// return true;
// } else {
// UpgradeStatusCode statusCode = upgradeStatus.getStatusCode();
// logger.error(
// "Initiating HMS Out-of-band agent upgrade failed. "
// + "ID: {}, UpgradeStatusCode: {}, StatusCodeMessage: {}, MoreInfo: {}",
// upgradeStatus.getId(), statusCode, statusCode.getStatusMessage(),
// upgradeStatus.getMoreInfo());
// }
// } else {
// logger.error("Initiating HMS Out-of-band agent upgrade failed.");
// }
// } catch (Exception e) {
// logger.error("Error while initiating HMS Out-of-band agent upgrade.", e);
// }
// return false;
// }
//
// private static HttpHeaders getHttpHeaders() {
// HttpHeaders headers = new HttpHeaders();
// headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
// headers.add("Accept", MediaType.APPLICATION_JSON.toString());
// return headers;
// }
//
// /**
// * Upload file to management switch.
// *
// * @param srcFileNameAbsPath the src file name abs path
// * @param destDirAbsPath the dest dir abs path
// * @return true, if successful
// */
// public static boolean uploadFileToManagementSwitch(final String srcFileNameAbsPath, final String upgradeId) {
//
// try {
// String srcFileName = null;
// String path = Constants.HMS_OOB_UPLOAD_ENDPOINT;
// File srcFile = new File(srcFileNameAbsPath);
//
// if (srcFile.exists() && srcFile.isFile()) {
// srcFileName = srcFile.getName();
// }
//
// MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
// map.add("upgradeId", upgradeId);
// map.add("fileName", srcFileName);
// map.add("fileContent", new FileSystemResource(srcFile));
//
// HttpHeaders headers = new HttpHeaders();
// headers.add("Accept-Encoding", MediaType.MULTIPART_FORM_DATA.toString());
//
// HmsOobAgentRestTemplate<MultiValueMap<String, Object>> restTemplate = new
/// HmsOobAgentRestTemplate<MultiValueMap<String, Object>>(
// map, headers);
//
// ResponseEntity<Object> oobResponse = restTemplate.exchange(HttpMethod.POST, path, Object.class);
//
// if (oobResponse.getStatusCode() == HttpStatus.OK) {
// logger.debug("File - '{}' sucessfully uploaded to Management Switch.", srcFileNameAbsPath);
// return true;
//
// } else {
// logger.debug("Uploading file - '{}' to Management Switch failed. Status Code: {}, Response Body: {}",
// srcFileNameAbsPath, oobResponse.getStatusCode(), oobResponse.getBody().toString());
// }
// } catch (Exception e) {
// logger.debug("Error while uploading file - '{}' to Management Switch", srcFileNameAbsPath, e);
// }
// return false;
// }
//
// /**
// * Upload oob agent upgrade scripts to oob agent host.
// *
// * @param upgradeScriptsLocation the upgrade scripts location
// * @param oobAgentHost the oobagent host
// * @param oobAgentPort the oob agent port
// * @param upgradeId the upgrade id
// * @return true, if successful
// */
// public static boolean uploadOobAgentUpgradeScriptsToOobAgentHost(final String upgradeScriptsLocation,
// final String oobAgentHost, final int oobAgentPort, final String upgradeId) {
//
// File[] oobUpgradeScripts = FileUtil.findFiles(upgradeScriptsLocation, "hms_oob_.*.sh", false);
// if (oobUpgradeScripts == null) {
//
// logger.error("OOB Agent upgrade scripts not found at '{}'.", upgradeScriptsLocation);
// }
//
// boolean uploaded = false;
// for (File oobUpgradeScript : oobUpgradeScripts) {
//
// uploaded = UpgradeUtil.uploadFileToManagementSwitch(oobUpgradeScript.getAbsolutePath(), upgradeId);
// if (!uploaded) {
//
// logger.error("Error uploading OOB Agent upgrade script - '{}' to OOB Agent Host - '{}'.",
// oobUpgradeScript.getAbsoluteFile(), oobAgentHost);
// break;
// }
// }
//
// if (uploaded) {
// return true;
// }
// return false;
// }
//
// /**
// * Gets the upgrade scripts location.
// *
// * @return the upgrade scripts location
// */
// public static String getUpgradeScriptsLocation() {
//
// return UpgradeUtil.class.getResource("/").getPath();
// }
//
// /**
// * Copy hms upgrade scripts.
// *
// * @param destDirAbsPath the dest dir abs path
// * @return true, if successful
// */
// public static boolean copyHmsUpgradeScripts(final String upgradeScriptsLocation, final String destDirAbsPath) {
//
// File[] hmsUpgradeScripts = FileUtil.findFiles(upgradeScriptsLocation, "hms_.*.sh", true);
// if (hmsUpgradeScripts == null) {
// logger.error("Unable to find HMS Aggregator upgrade scripts at - '{}'.", upgradeScriptsLocation);
// return false;
// }
//
// String srcFileName = null;
// String destFileName = null;
// File destFile = null;
// boolean copied = false;
// for (File srcFile : hmsUpgradeScripts) {
//
// srcFileName = srcFile.getName();
// destFileName = destDirAbsPath + File.separator + srcFileName;
// destFile = new File(destFileName);
//
// try {
//
// FileUtils.copyFile(srcFile, destFile);
// if (destFile.exists()) {
// copied = true;
// }
// } catch (IOException e) {
//
// logger.error("Error while copying '{}' as '{}'", srcFileName, destFileName);
// return false;
// }
// }
// if (copied) {
// return true;
// }
// return false;
// }
//
// /**
// * Stage upgrade scripts. Utility method will deflate the hms-local.war
// * bundle and copies the upgrade scripts to destDirAbsPath.
// *
// * @param hmsLocalWarAbsPath the hms local war abs path
// * @param destDirAbsPath the dest dir abs path
// */
// public static boolean stageUpgradeScripts(final String hmsLocalWarAbsPath, final String destDirAbsPath) {
//
// if (StringUtils.isBlank(hmsLocalWarAbsPath) || StringUtils.isBlank(destDirAbsPath)) {
// logger.debug("Either hms local war '{}' or hms upgrade directory '{}' is blank.", hmsLocalWarAbsPath,
// destDirAbsPath);
// return false;
// }
// if (!FileUtil.isFileExists(hmsLocalWarAbsPath)) {
// logger.debug("HMS local war '{}' does not exist.", hmsLocalWarAbsPath);
// return false;
// }
//
// /*
// * HMS Aggregator WAR is actually a ZIP bundle. Decompress the WAR for
// * HMS upgrade scripts.
// */
// final String hmsLocalWarBaseName = FileUtil.getFileBaseName(hmsLocalWarAbsPath);
// final String outputDir = FilenameUtils.concat(destDirAbsPath, hmsLocalWarBaseName);
// boolean hmsLocalWarUnzipped = FileUtil.unzipFile(hmsLocalWarAbsPath, outputDir);
// if (!hmsLocalWarUnzipped) {
// logger.error("Unable to unzip HMS Aggregator WAR bundle - '{}'. ", hmsLocalWarAbsPath);
// return false;
// }
// boolean upgradeScriptsStaged = UpgradeUtil.copyHmsUpgradeScripts(outputDir, destDirAbsPath);
//
// /*
// * Irrespective of Upgrade Scripts copy status, delete the folder to
// * which HMS Aggregator WAR has been decompressed.
// */
// FileUtil.deleteDirectory(outputDir);
// return upgradeScriptsStaged;
// }
//
// /**
// * Notify PRM service.
// *
// * @param serviceState the service state
// * @param prmUserName the prm user name
// * @param prmPassword the prm password
// * @return true, if successful
// */
// public static boolean notifyPRMService(final ServiceState serviceState, final String prmHost,
// final String prmUserName, final String prmPassword) {
//
// HmsServiceState hmsServiceState = new HmsServiceState();
// hmsServiceState.setHmsServiceState(serviceState);
//
// String plainCreds = String.format("%s:%s", prmUserName, prmPassword);
// String base64Creds = javax.xml.bind.DatatypeConverter.printBase64Binary(plainCreds.getBytes());
// HttpHeaders headers = new HttpHeaders();
// headers.add("Authorization", "Basic " + base64Creds);
// headers.setContentType(MediaType.APPLICATION_JSON);
//
// HttpEntity<HmsServiceState> httpEntity = new HttpEntity<HmsServiceState>(hmsServiceState, headers);
//
// URI uri = null;
// try {
// uri = new URI("http", null, prmHost, 8080, Constants.PRM_HMS_SERVICESTATE_ENDPOINT, null, null);
// } catch (URISyntaxException e) {
// logger.error("Error while building URI for PRM Notification.", e);
// }
// if (uri != null) {
//
// logger.info("Notifying PRM about HMS Service State - {}.", serviceState.toString());
// try {
//
// RestTemplate restTemplate = new RestTemplate();
// logger.debug("Invoking POST on {} for notifying PRM.", uri.toString());
// ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Void.class);
//
// if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
//
// logger.debug("PRM Response - {} for POST on {}.", response.getStatusCode().toString(),
// uri.toString());
// return true;
// }
//
// } catch (Exception e) {
//
// if (e instanceof HttpClientErrorException) {
//
// HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
// if (httpClientErrorException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
//
// logger.info("PRM service is not running. [ Host: localhost; Port: 8080; URI: {}].",
// uri.toString());
// }
// }
// logger.error("Error while notifying PRM about HMS Service State - {}.", serviceState, e);
// }
// }
// return false;
// }
//
// /**
// * Rollback oob upgrade.
// *
// * @param rollbackSpec the rollback spec
// * @return true, if successful
// */
// public static boolean rollbackOobUpgrade(RollbackSpec rollbackSpec) {
//
// try {
// String path = Constants.HMS_OOB_UPGRADE_ENDPOINT;
// HttpHeaders headers = getHttpHeaders();
// HmsOobAgentRestTemplate<RollbackSpec> restTemplate = new HmsOobAgentRestTemplate<RollbackSpec>(rollbackSpec,
/// headers);
// ResponseEntity<BaseResponse> rollbackResponse = restTemplate.exchange(HttpMethod.POST, path,
// BaseResponse.class);
//
// if (rollbackResponse != null) {
// if (rollbackResponse.getStatusCode() == HttpStatus.ACCEPTED) {
// logger.info("Initiated HMS Out-of-band agent rollback successfully.");
// return true;
// } else {
// logger.error("Initiating HMS Out-of-band agent rollback failed. Reason: {}",
// rollbackResponse.getBody().getErrorMessage());
// }
// } else {
//
// logger.error("Initiating HMS Out-of-band agent rollback failed.");
// }
// } catch (Exception e) {
//
// logger.error("Error while initiating HMS Out-of-band agent rollback.", e);
// }
// return false;
// }
//
// /**
// * Sleep.
// *
// * @param timeInMilliSeconds the time in milli seconds
// */
// public static void sleep(int timeInMilliSeconds) {
//
// try {
//
// Thread.sleep(timeInMilliSeconds);
//
// } catch (InterruptedException e) {
//
// logger.debug("Error while waiting for {} seconds.", timeInMilliSeconds / 1000, e);
// }
// }
//
// /**
// * Gets the oob upgrade status.
// *
// * @param upgradeId the upgrade id
// * @return the oob upgrade status
// */
// public static UpgradeStatus getOobUpgradeStatus(final String upgradeId) {
//
// String oobUpgradeMonitoringUrl = Constants.HMS_OOB_UPGRADE_MONITOR_ENDPOINT + "/" + upgradeId;
// String responseBody = null;
// String httpReasonPhrase = null;
// int httpStatusValue = 0;
// try {
// HttpHeaders headers = getHttpHeaders();
// HmsOobAgentRestTemplate<Object> restTemplate = new HmsOobAgentRestTemplate<Object>(headers);
// ResponseEntity<UpgradeStatus> upgradeStatusResponse = restTemplate.exchange(HttpMethod.GET,
// oobUpgradeMonitoringUrl, UpgradeStatus.class);
// httpStatusValue = upgradeStatusResponse.getStatusCode().value();
// httpReasonPhrase = upgradeStatusResponse.getStatusCode().getReasonPhrase();
// if (upgradeStatusResponse.getBody() != null) {
// responseBody = upgradeStatusResponse.getBody().toString();
// }
// logger.debug("In getOobUpgradeStatus, OOB Agent responded with '{} ({})' for GET on {}. Response Body: {}",
// httpStatusValue, httpReasonPhrase, oobUpgradeMonitoringUrl, responseBody);
// if (httpStatusValue == HttpStatus.OK.value()) {
// UpgradeStatus upgradeStatus = upgradeStatusResponse.getBody();
// return upgradeStatus;
// }
// } catch (Exception e) {
// logger.error("Error while invoking GET on {}.", oobUpgradeMonitoringUrl, e);
// if (e instanceof HttpClientErrorException) {
// HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
// responseBody = httpClientErrorException.getResponseBodyAsString();
// httpStatusValue = httpClientErrorException.getStatusCode().value();
// httpReasonPhrase = httpClientErrorException.getStatusCode().getReasonPhrase();
// logger.debug("In getOobUpgradeStatus, HttpClientErrorException occurred.", e);
// } else if (e instanceof HttpServerErrorException) {
// HttpServerErrorException httpServerErrorException = (HttpServerErrorException) e;
// responseBody = httpServerErrorException.getResponseBodyAsString();
// httpStatusValue = httpServerErrorException.getStatusCode().value();
// httpReasonPhrase = httpServerErrorException.getStatusCode().getReasonPhrase();
// logger.debug("In getOobUpgradeStatus, HttpServerErrorException occurred.", e);
// }
// logger.debug("In getOobUpgradeStatus, OOB Agent responded with '{} ({})' for GET on {}. Response Body: {}.",
// httpStatusValue, httpReasonPhrase, oobUpgradeMonitoringUrl, responseBody);
// }
// return null;
// }
//
// /**
// * Gets the oob upgrade status.
// *
// * @param upgradeId the upgrade id
// * @param oobAgentUpgradeMaxTimeout the oob agent upgrade max timeout
// * @param retryInterval the retry interval
// * @return the oob upgrade status
// */
// public static UpgradeStatus getOobUpgradeStatus(final String upgradeId, final int oobAgentUpgradeMaxTimeout,
// final int retryInterval) {
//
// int maxRetries = (oobAgentUpgradeMaxTimeout / retryInterval);
// int retryCount = 1;
// logger.debug("In getOobUpgradeStatus, No of retires: {}, Retry Interval: {} Seconds.", maxRetries,
// (retryInterval / 1000));
//
// UpgradeStatus upgradeStatus = null;
// while (retryCount <= maxRetries) {
//
// upgradeStatus = UpgradeUtil.getOobUpgradeStatus(upgradeId);
//
// /*
// * if upgradeStatus is null, that means OOB might either be in
// * MAINTENANCE or not available for monitoring call.
// *
// * If we got a valid response, check that upgrade is still not in
// * progress. Any state other than HMS_OOB_UPGRADE_INITIATED is
// * either upgrade succeeded or failed.
// */
// if (upgradeStatus == null
// || upgradeStatus.getStatusCode().equals(UpgradeStatusCode.HMS_OOB_UPGRADE_INITIATED)) {
//
// logger.debug("In getOobUpgradeStatus, OOB UpgradeStatus is either null or still {}. Retry Attempt: {}",
// UpgradeStatusCode.HMS_OOB_UPGRADE_INITIATED.toString(), retryCount);
// UpgradeUtil.sleep(retryInterval);
// retryCount++;
//
// } else {
//
// logger.debug("In getOobUpgradeStatus, OOB UpgradeStatus is other than {}. Retry Attempt: {}",
// UpgradeStatusCode.HMS_OOB_UPGRADE_INITIATED.toString(), retryCount);
// return upgradeStatus;
// }
// }
// return upgradeStatus;
// }
//
// /**
// * Gets the upgrade status uri.
// *
// * @param upgradeRequest the upgrade request
// * @param upgradeId the upgrade id
// * @return the upgrade status uri
// */
// public static URI getUpgradeStatusURI(final HttpServletRequest upgradeRequest, final String upgradeId) {
//
// if (upgradeRequest == null || upgradeId == null) {
// return null;
// }
// try {
// return new URI(upgradeRequest.getScheme(), null, upgradeRequest.getServerName(),
// upgradeRequest.getServerPort(), upgradeRequest.getRequestURI() + "/monitor/" + upgradeId, null,
// null);
// } catch (URISyntaxException e) {
//
// logger.error("Error while building HMS Upgrade Status URI.", e);
// }
// return null;
// }
//
// /**
// * Gets the primitive upgrade status.
// *
// * @param upgradeId the upgrade id
// * @param hmsUpgradeStatusCode the hms upgrade status code
// * @param primitiveUpgradeStatusCode the primitive upgrade status code
// * @return the primitive upgrade status
// */
// public static PrimitiveUpgradeStatus getPrimitiveUpgradeStatus(final String upgradeId,
// final UpgradeStatusCode hmsUpgradeStatusCode, final String primitiveUpgradeStatusCode) {
//
// return UpgradeUtil.getPrimitiveUpgradeStatus(upgradeId, hmsUpgradeStatusCode, primitiveUpgradeStatusCode, null);
// }
//
// /**
// * Gets the primitive upgrade status.
// *
// * @param upgradeId the upgrade id
// * @param hmsUpgradeStatusCode the hms upgrade status code
// * @param primitiveUpgradeStatusCode the primitive upgrade status code
// * @param upgradeStatusMonitoringURI the upgrade status monitoring uri
// * @return the primitive upgrade status
// */
// public static PrimitiveUpgradeStatus getPrimitiveUpgradeStatus(final String upgradeId,
// final UpgradeStatusCode hmsUpgradeStatusCode, final String primitiveUpgradeStatusCode,
// final URI upgradeStatusMonitoringURI) {
//
// PrimitiveUpgradeStatusBuilder primitiveUpgradeStatusBuilder = new PrimitiveUpgradeStatusBuilder();
// primitiveUpgradeStatusBuilder.upgradeId(upgradeId);
// primitiveUpgradeStatusBuilder.upgradeStatus(primitiveUpgradeStatusCode.toString());
// primitiveUpgradeStatusBuilder.errorCode(hmsUpgradeStatusCode.toString());
// if (upgradeStatusMonitoringURI != null) {
// primitiveUpgradeStatusBuilder.upgradeStatusUri(upgradeStatusMonitoringURI);
// }
// return primitiveUpgradeStatusBuilder.build();
// }
//
// /**
// * Return a RestTemplate which ignores SSL certificates for every request.
// * Valid only host with with "https" protocol.
// *
// * @return this RestTemplate
// * @throws NoSuchAlgorithmException the no such algorithm exception
// * @throws KeyStoreException the key store exception
// * @throws KeyManagementException the key management exception
// */
// @Deprecated
// private static RestTemplate createRestTemplateWithVerifierStrategy()
// throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
// SSLContextBuilder builder = SSLContexts.custom();
// builder.loadTrustMaterial(null, new TrustStrategy() {
// @Override
// public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
// return true;
// }
// });
// SSLContext sslContext = builder.build();
// SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
// SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
// Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
// .register("https", sslsf).build();
//
// PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
// connManager.setMaxTotal(CONCURRENT_HTTP_REQUESTS);
//
// CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
//
// HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new
/// HttpComponentsClientHttpRequestFactory(
// httpClient);
// httpComponentsClientHttpRequestFactory.setConnectTimeout(CONNECTION_TIMEOUT);
// httpComponentsClientHttpRequestFactory.setReadTimeout(READ_TIMEOUT);
// RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
// return restTemplate;
// }
//
// /**
// * Validates that the previous version matches with the current running HMS
// * version and patchFile version is later than the current running HMS
// * version.
// *
// * @param previousVersion the previous version
// * @param patchFile the patch file
// * @return true, if validation is successful
// */
// public static boolean validateVersion(final String previousVersion, final String patchFile) {
//
// if (StringUtils.isBlank(previousVersion) || StringUtils.isBlank(patchFile)) {
//
// logger.error("Either HMS Previous Version is blank or patchFile is blank.");
// return false;
// }
//
// /*
// * Check if previousVersion matches HMS (Release/Snapshot) version
// * format.
// */
// if (!(previousVersion.matches(UpgradeUtil.HMS_RELEASE_VERSION_REGEX)
// || previousVersion.matches(UpgradeUtil.HMS_SNAPSHOT_VERSION_REGEX))) {
// logger.error(
// "HMS previousVersion '{}' does not match either release version format - '{}' or snapshot version format - '{}'.",
// previousVersion, UpgradeUtil.HMS_RELEASE_VERSION_REGEX, UpgradeUtil.HMS_SNAPSHOT_VERSION_REGEX);
// return false;
// }
//
// // get current running HMS version
// String hmsVersion = UpgradeUtil.getHmsVersion();
// if (StringUtils.isBlank(hmsVersion)) {
// logger.error("Failed to get current running HMS version.");
// return false;
// }
//
// // validate previousVersion
// if (!UpgradeUtil.validatePreviousVersion(previousVersion, hmsVersion)) {
// logger.error("Previous version validation failed.");
// return false;
// }
//
// String patchFileVersion = UpgradeUtil.getPatchFileVersion(patchFile);
// if (StringUtils.isBlank(patchFileVersion)) {
// logger.error("Failed to determine patchFile - '{}' version.", patchFile);
// return false;
// }
//
// // validate validate Patch File Version
// if (!UpgradeUtil.validatePatchFileVersion(patchFileVersion, hmsVersion)) {
// logger.error("Patch file version validation failed.");
// return false;
// } else {
// logger.info("Version validation succeeded. [ Previous Version: {}; Patch Version: {}; HMS Version: {} ].",
// previousVersion, patchFileVersion, hmsVersion);
// return true;
// }
// }
//
// /**
// * Validate patch file version.
// *
// * @param patchFileVersion the patch file version
// * @param hmsVersion the hms version
// * @return true, if successful
// */
// public static boolean validatePatchFileVersion(final String patchFileVersion, final String hmsVersion) {
//
// if (StringUtils.isBlank(patchFileVersion) || StringUtils.isBlank(hmsVersion)) {
// return false;
// }
//
// if (!(patchFileVersion.matches(UpgradeUtil.HMS_RELEASE_VERSION_REGEX)
// || patchFileVersion.matches(UpgradeUtil.HMS_SNAPSHOT_VERSION_REGEX))) {
// logger.debug(
// "HMS patchFileVersion '{}' does not match either release version format - '{}' or snapshot version format - '{}'.",
// patchFileVersion, UpgradeUtil.HMS_RELEASE_VERSION_REGEX, UpgradeUtil.HMS_SNAPSHOT_VERSION_REGEX);
// return false;
// }
//
// if (!(hmsVersion.matches(UpgradeUtil.HMS_RELEASE_VERSION_REGEX)
// || hmsVersion.matches(UpgradeUtil.HMS_SNAPSHOT_VERSION_REGEX))) {
// logger.debug(
// "HMS version '{}' does not match either release version format - '{}' or snapshot version format - '{}'.",
// hmsVersion, UpgradeUtil.HMS_RELEASE_VERSION_REGEX, UpgradeUtil.HMS_SNAPSHOT_VERSION_REGEX);
// return false;
// }
//
// ComparableVersion patchVersion = new ComparableVersion(patchFileVersion);
// ComparableVersion currVersion = new ComparableVersion(hmsVersion);
//
// // patchVersion should be of later version that the current HMS version
// if (patchVersion.compareTo(currVersion) == 1) {
// logger.info("Patch version - '{}' is later than the HMS version - '{}.'", patchFileVersion, hmsVersion);
// return true;
// } else {
// logger.info("Patch version - '{}' is not later than the HMS version - '{}.'", patchFileVersion,
// hmsVersion);
// return false;
// }
// }
//
// /**
// * Returns the patch file version.
// *
// * @param patchFile the patch file
// * @return the patch file version
// */
// public static String getPatchFileVersion(final String patchFile) {
//
// if (StringUtils.isBlank(patchFile)) {
// return null;
// }
// File f = new File(patchFile);
// String patchFileName = f.getName();
//
// // first check if the bundle is a release version one
// Pattern pattern = Pattern.compile(UpgradeUtil.HMS_PATCHFILE_RELEASE_VERSION_PATTERN);
// Matcher matcher = pattern.matcher(patchFileName);
// if (matcher.find()) {
// logger.debug("HMS patchFile is of release version - '{}'.", matcher.group(1));
// return matcher.group(1);
// } else {
//
// // Check if the bundle is a SNAPSHOT version one
// pattern = Pattern.compile(UpgradeUtil.HMS_PATCHFILE_SNAPSHOT_VERSION_PATTERN);
// matcher = pattern.matcher(patchFileName);
// if (matcher.find()) {
// logger.debug("HMS patchFile is of SNAPSHOT version - '{}'.", matcher.group(1));
// return matcher.group(1);
// }
// }
// logger.debug(
// "HMS patchFile '{}' does not match either release version pattern - '{}' or snapshot version pattern - '{}'.",
// patchFile, UpgradeUtil.HMS_PATCHFILE_RELEASE_VERSION_PATTERN,
// UpgradeUtil.HMS_PATCHFILE_SNAPSHOT_VERSION_PATTERN);
// return null;
// }
//
// /**
// * Validates that the previous version matches with the current running HMS
// * version.
// *
// * @param previousVersion the previous version
// * @param hmsVersion the hms version
// * @return true, if if validation is successful
// */
// public static boolean validatePreviousVersion(final String previousVersion, final String hmsVersion) {
//
// if (StringUtils.isBlank(previousVersion) || StringUtils.isBlank(hmsVersion)) {
// return false;
// }
//
// if (!(previousVersion.matches(UpgradeUtil.HMS_RELEASE_VERSION_REGEX)
// || previousVersion.matches(UpgradeUtil.HMS_SNAPSHOT_VERSION_REGEX))) {
// logger.debug(
// "HMS previousVersion '{}' does not match either release version format - '{}' or snapshot version format - '{}'.",
// previousVersion, UpgradeUtil.HMS_RELEASE_VERSION_REGEX, UpgradeUtil.HMS_SNAPSHOT_VERSION_REGEX);
// return false;
// }
//
// if (!(hmsVersion.matches(UpgradeUtil.HMS_RELEASE_VERSION_REGEX)
// || hmsVersion.matches(UpgradeUtil.HMS_SNAPSHOT_VERSION_REGEX))) {
// logger.debug(
// "HMS version '{}' does not match either release version format - '{}' or snapshot version format - '{}'.",
// hmsVersion, UpgradeUtil.HMS_RELEASE_VERSION_REGEX, UpgradeUtil.HMS_SNAPSHOT_VERSION_REGEX);
// return false;
// }
//
// ComparableVersion prvVersion = new ComparableVersion(previousVersion);
// ComparableVersion currVersion = new ComparableVersion(hmsVersion);
//
// // previousVersion should match with the current version
// if (prvVersion.compareTo(currVersion) == 0) {
// logger.info("Previous version and HMS version matched - '{}'.", hmsVersion);
// return true;
// } else {
// logger.info("Previous version - '{}' did not match with HMS version - '{}'.", previousVersion, hmsVersion);
// return false;
// }
// }
//
// /**
// * Returns current running HMS version.
// *
// * @return the hms version
// */
// public static String getHmsVersion() {
// HMSAboutResponseAggregator aggregator = new HMSAboutResponseAggregator();
// try {
// Map<String, AboutResponse> aboutResponseMap = aggregator.getHMSAboutResponse();
// if (aboutResponseMap != null && aboutResponseMap.containsKey("IBAgent")) {
// AboutResponse aboutResponse = aboutResponseMap.get("IBAgent");
// return aboutResponse.getBuildVersion();
//
// } else {
// logger.debug("HMSAboutResponseAggregator.getHMSAboutResponse did not return IBAgent AboutResponse.");
// return null;
// }
// } catch (HmsException e) {
// logger.error("Error while getting HMS version.", e);
// return null;
// }
// }
//
// /**
// * Notify PRM service.
// *
// * @param serviceState the service state
// * @param prmUserName the prm user name
// * @param prmPassword the prm password
// * @return true, if successful
// */
// public static String getPhysicalRackInfo(final String prmHost, final String prmUserName,
// final String prmPassword) {
//
// logger.info("In UpgradeUtil, getPhysicalRackInfo, PRM host is {}", prmHost);
//
// HttpHeaders headers = new HttpHeaders();
// headers.setContentType(MediaType.APPLICATION_JSON);
//
// URI uri = null;
// try {
// uri = new URI("http", null, prmHost, 8080, Constants.PRM_PHYSICALRACKINFO_ENDPOINT, null, null);
// } catch (URISyntaxException e) {
// logger.error("In UpgradeUtil, getPhysicalRackInfo, Error while building URI.", e);
// }
// if (uri != null) {
// try {
// RestTemplate restTemplate = new RestTemplate();
// ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
// if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
// logger.debug("physical rack info: {} ", response.getBody());
// return response.getBody();
// }
// } catch (Exception e) {
// if (e instanceof HttpClientErrorException) {
// HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
// if (httpClientErrorException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
// logger.info("PRM service is not running. [ Host: localhost; Port: 8080; URI: {}].",
// uri.toString());
// }
// }
// logger.error("In UpgradeUtil, getPhysicalRackInfo, exception while posting prm rest call ", e);
// }
// }
// return null;
// }
// }
