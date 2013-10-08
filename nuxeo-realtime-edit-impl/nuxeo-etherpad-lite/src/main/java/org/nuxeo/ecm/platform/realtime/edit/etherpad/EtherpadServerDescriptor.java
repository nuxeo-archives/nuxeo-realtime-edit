package org.nuxeo.ecm.platform.realtime.edit.etherpad;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject(value = "EtherpadServer")
public class EtherpadServerDescriptor {
		@XNode("@name")
		private String name;

		@XNode("@enabled")
		private boolean enabled;

		@XNode("displayType")
		private String displayType;

	    @XNodeMap(value = "params/param", key = "@name", type = HashMap.class, componentType = String.class)
	    public Map<String, String> params;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getDisplayType() {
			return displayType;
		}

		public void setDisplayType(String displayType) {
			this.displayType = displayType;
		}

		public Map<String, String> getParams() {
			return params;
		}

		public void setParams(Map<String, String> params) {
			this.params = params;
		}
	    
}
