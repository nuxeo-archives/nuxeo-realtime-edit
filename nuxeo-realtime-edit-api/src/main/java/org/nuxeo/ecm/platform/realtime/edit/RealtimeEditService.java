package org.nuxeo.ecm.platform.realtime.edit;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.runtime.model.Component;

public interface RealtimeEditService extends Component{

    public String getName();
    public String getIcon();

    void setName(String name);

    void setMimeTypeSupport(String[] mimeTypes);

    public boolean isRealtimeEditable(Blob blob);

    public String createSession(String username, String title, Blob blob) throws ClientException;

    public void updateSession(String sessionId, String username, Blob blob) throws ClientException;

    public String getURL(String sessionId);

    public String getEmbeddedURL(String sessionId, String username);

    public Blob getSessionBlob(String sessionId, String mimeType);

    public void deleteSession(String sessionId);

    public boolean existsSession(String sessionId);
}