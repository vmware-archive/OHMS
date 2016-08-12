/* ********************************************************************************
 * SwitchOspfNetworkConfig.java
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

package com.vmware.vrack.hms.common.switches.api;

public class SwitchOspfNetworkConfig {
	
	public String getArea() { return area; }
	public String getNetwork() { return network; }
	
	public void setArea(String area) { this.area = area; }
	public void setNetwork(String network) { this.network = network; }
	
	private String area;
	private String network;
}
