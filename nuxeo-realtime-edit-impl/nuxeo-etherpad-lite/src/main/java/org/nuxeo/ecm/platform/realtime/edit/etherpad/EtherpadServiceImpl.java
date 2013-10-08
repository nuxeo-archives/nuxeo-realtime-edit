/**
 *
 */

package org.nuxeo.ecm.platform.realtime.edit.etherpad;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etherpad_lite_client.EPLiteClient;
import org.etherpad_lite_client.EPLiteException;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.platform.realtime.edit.AbstractRealtimeEditService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;

/**
 * @author nfgs
 */
public class EtherpadServiceImpl extends AbstractRealtimeEditService implements
        EtherpadService {

	private static final Log log = LogFactory.getLog(EtherpadServiceImpl.class);
	private static final String ETHERPAD_EXTENSION_POINT_SERVER = "EtherpadServers";
	private static final String ETHERPAD_PROPERTY_URL = "nuxeo.realtime-editor.etherpad.url";
	private static final String ETHERPAD_PROPERTY_API_KEY = "nuxeo.realtime-editor.etherpad.apiKey";
	
    private EPLiteClient client;
    private Map<String, EtherpadServerDescriptor> descriptors;
    private String defaultDescriptorName;
    
    @Override
    public void applicationStarted(ComponentContext context) throws Exception {
    }

	@Override
	public void activate(ComponentContext context) throws Exception {
		super.activate(context);
		this.defaultDescriptorName = null;
		if (null == this.descriptors) {
			this.descriptors = new HashMap<String, EtherpadServerDescriptor>();
		}
	}
	
	@Override
	public void deactivate(ComponentContext context) throws Exception {
		super.deactivate(context);
		if (null != this.descriptors) {
			this.descriptors.clear();
		}
	}

	@Override
	public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor)
			throws Exception {
		if (ETHERPAD_EXTENSION_POINT_SERVER.equals(extensionPoint)) {
			EtherpadServerDescriptor desc = (EtherpadServerDescriptor) contribution;
			if (desc.isEnabled()) {
				this.defaultDescriptorName = desc.getName();
				this.descriptors.put(desc.getName(), desc);
			}
		}
	}

	@Override
	public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor)
			throws Exception {
		if (ETHERPAD_EXTENSION_POINT_SERVER.equals(extensionPoint)) {
			EtherpadServerDescriptor desc = (EtherpadServerDescriptor) contribution;
			this.descriptors.remove(desc.getName());
		}
	}

	@Override
	public EtherpadServerDescriptor getDescriptor() throws ClientException {
		if (StringUtils.isNotBlank(this.defaultDescriptorName)) {
			return this.descriptors.get(this.defaultDescriptorName);
		} else {
			log.error("No default Etherpad configuration found");
			throw new ClientException("No default Etherpad configuration found");
		}
	}
	
    @Override
	public String getServerUrl() {
    	return Framework.getProperty(ETHERPAD_PROPERTY_URL);    		
	}

	@Override
	public String getServerApiKey() {
		return Framework.getProperty(ETHERPAD_PROPERTY_API_KEY);
	}

	EPLiteClient getClient() {
        if (client == null) {
            client = new EPLiteClient(getServerUrl(), getServerApiKey());
        }
        return client;
    }

    @Override
    public String createSession(String username, String title, Blob blob)
            throws ClientException {
        String padId = title;
        try {
            deletePad(padId);
        } catch (EPLiteException e) {
        }

        try {
            createPad(padId);
            setPadContent(padId, blob);
        } catch (IOException e) {
            throw ClientException.wrap(e);
        }

        return title;
    }

    @Override
    public void updateSession(String sessionId, String username, Blob blob)
            throws ClientException {
        try {
            setPadContent(sessionId, blob);
        } catch (IOException e) {
            throw ClientException.wrap(e);
        }
    }

    @Override
    public String getEmbeddedURL(String sessionId, String username) {
        String url = getURL(sessionId);
        
        try {
        	url += "?userName=" + username;
        	Map<String, String> params = getDescriptor().getParams();
        	for (Map.Entry<String, String> entry : params.entrySet()) { 
        		url += "&" + entry.getKey() + "=" + entry.getValue();
        	}
        } catch (ClientException e) {
        	log.error("Error while building the embedded URL, error:" + e.getMessage());
        }
        
        return url;
    }

    @Override
    public String getURL(String sessionId) {
        return getServerUrl() + "/p/" + sessionId;
    }
    
    private void deletePad(String padId) throws EPLiteException {
        getClient().deletePad(padId);
    }

    private void createPad(String padId) throws EPLiteException {
        getClient().createPad(padId);
    }

    private String getPadContent(String padId, String mimeType)
            throws EPLiteException {
        if (mimeType.equals("text/html")) {
            return (String) getClient().getHTML(padId).get("html");
        }

        return (String) getClient().getText(padId).get("text");
    }

    private void setPadContent(String padId, Blob blob) throws IOException {
        String mimeType = blob.getMimeType();
        String content = URLEncoder.encode("<html><body>" + blob.getString()
                + "</body></html>", "UTF-8");

        if (mimeType.equals("text/html")) {
            getClient().setHTML(padId, content);
        } else {
            getClient().setText(padId, content);
        }
    }

    @Override
    public Blob getSessionBlob(String sessionId, String mimeType) {
        String content = getPadContent(sessionId, mimeType);
        Blob blob = new StringBlob(content, mimeType);
        return blob;
    }

    @Override
    public void deleteSession(String sessionId) {
        deletePad(sessionId);
    }

    @Override
    public boolean existsSession(String sessionId) {
        boolean exists = true;
        try {
            getClient().getRevisionsCount(sessionId);
        } catch (Exception e) {
            exists = false;
        }
        return exists;
    }

}
