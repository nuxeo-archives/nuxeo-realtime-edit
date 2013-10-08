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

    public RealtimeEditSession createSession(String username, String title, Blob blob) throws ClientException;

    public void updateSession(RealtimeEditSession session, String username, Blob blob) throws ClientException;

    public RealtimeEditSession joinSession(RealtimeEditSession session, String userFullName);

    public Blob getSessionBlob(RealtimeEditSession session, String mimeType);

    public void deleteSession(RealtimeEditSession session);

    public boolean existsSession(RealtimeEditSession session);
}