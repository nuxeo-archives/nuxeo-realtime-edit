package org.nuxeo.ecm.platform.realtime.edit.etherpad;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.realtime.edit.RealtimeEditService;

public interface EtherpadService extends RealtimeEditService {

	public EtherpadServerDescriptor getDescriptor() throws ClientException;
	public String getServerUrl();
	public String getServerApiKey();
}
