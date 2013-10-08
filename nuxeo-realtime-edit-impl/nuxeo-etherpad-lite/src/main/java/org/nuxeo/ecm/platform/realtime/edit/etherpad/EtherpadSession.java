package org.nuxeo.ecm.platform.realtime.edit.etherpad;

import javax.servlet.http.Cookie;
import org.nuxeo.ecm.platform.realtime.edit.RealtimeEditSession;

public class EtherpadSession implements RealtimeEditSession {

	private Pad pad = null;
	private String userId;
	private String userFullName;
	private String etherpadAuthorId;
	private String etherpadSessionId;
	private int	   etherpadSessionlength;
	
	public EtherpadSession(
			Pad pad,
			String userId, 
			String userFullName,
			String etherpadAuthorId, 
			String etherpadSessionId,
			int    etherpadSessionlength) {
		
		this.pad = pad;
		this.userId = userId;
		this.userFullName = userFullName;
		this.etherpadAuthorId = etherpadAuthorId;
		this.etherpadSessionId = etherpadSessionId;
		this.etherpadSessionlength = etherpadSessionlength;
	}

	@Override
	public String getRealtimeEditSessionURL() {
		return pad.etherpadPadURL + 
				"?showControls=true&showChat=true&showLineNumbers=false&useMonospaceFont=false&userName=" + userFullName + "&noColors=false&userColor=false&hideQRCode=false&alwaysShowChat=false";
	}

	public Pad getPad() {
		return pad;
	}

	public void setPad(Pad pad) {
		this.pad = pad;
	}

	@Override
	public boolean hasRealtimeEditCookies() {
		return true;
	}

	@Override
	public Cookie[] getRealtimeEditSessionCookies() {
		Cookie[] cookies = new Cookie[1];
		cookies[0] = new Cookie("sessionID", etherpadSessionId);
		return cookies;
	}

	@Override
	public String getRealtimeSessionID() {
		return pad.etherpadPadURL;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public String getEtherpadAuthorId() {
		return etherpadAuthorId;
	}

	public String getEtherpadSessionId() {
		return etherpadSessionId;
	}

	public int getEtherpadSessionlength() {
		return etherpadSessionlength;
	}
}
