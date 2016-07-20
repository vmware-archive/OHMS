package com.vmware.vrack.hms.common.switches.model.bulk;

import java.util.List;

public class PluginSwitchBulkConfig {
	   private PluginSwitchBulkConfigEnum type;
	    private List<String> values;
	    private List<String> filters;
	    
		public PluginSwitchBulkConfigEnum getType() {
			return type;
		}
		public void setType(PluginSwitchBulkConfigEnum type) {
			this.type = type;
		}
		public List<String> getValues() {
			return values;
		}
		public void setValues(List<String> values) {
			this.values = values;
		}
		public List<String> getFilters() {
			return filters;
		}
		public void setFilters(List<String> filters) {
			this.filters = filters;
		}
}
