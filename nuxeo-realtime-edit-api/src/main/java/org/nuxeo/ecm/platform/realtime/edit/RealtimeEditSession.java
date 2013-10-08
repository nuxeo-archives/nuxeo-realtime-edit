package org.nuxeo.ecm.platform.realtime.edit;

import javax.servlet.http.Cookie;

import org.nuxeo.ecm.core.api.ClientException;

public interface RealtimeEditSession {

    public String getRealtimeSessionID();

    public String getRealtimeEditSessionURL();

    public boolean hasRealtimeEditCookies();

    public String getRealtimeEditCookieName();

    public Cookie[] getRealtimeEditSessionCookies();

    public void updateState(RealtimeEditedDoc red) throws ClientException;
}
