package org.nuxeo.ecm.platform.realtime.edit.etherpad;

public class Pad {

	public String groupId;
	public String padId;
	public String etherpadPadURL;
	
	public Pad(
			String groupId, 
			String padId, 
			String etherpadPadURL) {
		
		this.groupId = groupId;
		this.padId = padId;
		this.etherpadPadURL = etherpadPadURL;
	}
}
