package org.nuxeo.ecm.platform.realtime.edit;

import javax.servlet.http.Cookie;

public interface RealtimeEditSession {
	
	public String   getRealtimeSessionID();
	public String   getRealtimeEditSessionURL();
	public boolean  hasRealtimeEditCookies();
	public Cookie[] getRealtimeEditSessionCookies();
}
