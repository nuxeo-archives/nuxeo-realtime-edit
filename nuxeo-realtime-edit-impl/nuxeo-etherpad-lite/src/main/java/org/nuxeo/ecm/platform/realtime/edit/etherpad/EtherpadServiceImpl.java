/**
 *
 */

package org.nuxeo.ecm.platform.realtime.edit.etherpad;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Map;

import org.etherpad_lite_client.EPLiteClient;
import org.etherpad_lite_client.EPLiteException;
import org.json.simple.JSONArray;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.platform.realtime.edit.AbstractRealtimeEditService;
import org.nuxeo.ecm.platform.realtime.edit.RealtimeEditSession;
import org.nuxeo.runtime.model.ComponentContext;

/**
 * @author nfgs
 */
public class EtherpadServiceImpl extends AbstractRealtimeEditService implements EtherpadService {

    private EPLiteClient client = null;

    // Etherpad service
     private String etherpadServerURL = "http://localhost:9001";
     private String etherpadApiKey = "eHpiOkAIK1ucxZJsCcFEws3pdlty72ab";
     private String etherpadPrefixURL = "/p/";
     private String etherpadBaseURL = etherpadServerURL + etherpadPrefixURL;
     private int    etherpadSessionLength = 1; // in hours

     // Opened docs & user sessions
     Map<String, Pad> pads;
     Map<String, EtherpadSession> usersSessions;

    @Override
    public void applicationStarted(ComponentContext context) throws Exception {

    }

    @Override
    public void activate(ComponentContext context) throws Exception {
        super.activate(context);
        client = new EPLiteClient(etherpadBaseURL, etherpadApiKey);
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

    private void deletePad(String sessionId) throws EPLiteException {

        if(pads.containsKey(sessionId)){
            Pad pad = pads.get(sessionId);
            client.deletePad(pad.padId);
            pads.remove(sessionId);
        }
    }

    private void createPad(String sessionId) throws EPLiteException {

        Pad pad = getPad(sessionId);

        // Create & initialize pad if not exists in therpad server
        if (!groupPadExists(pad)) {
            client.createGroupPad(pad.groupId, sessionId);
        }
    }

    private String getPadContent(String padId, String mimeType) throws EPLiteException {

        if (mimeType.equals("text/html")) {
            return (String) client.getHTML(padId).get("html");
        }

        return (String) client.getText(padId).get("text");
    }

    private void setPadContent(String padId, Blob blob) throws IOException {

        String mimeType = blob.getMimeType();
        String content = URLEncoder.encode("<html><body>" + blob.getString() + "</body></html>", "UTF-8");

        if (mimeType.equals("text/html")) {
            client.setHTML(padId, content);
        } else {
            client.setText(padId, content);
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
    public boolean existsSession(RealtimeEditSession session) { // sessionId = doc id
        return pads.containsKey(session.getRealtimeSessionID());
    }

    public int getActualEditorCount(String nuxeoDocId) {

        Pad pad = getPad(nuxeoDocId);
        if ( ! groupPadExists(pad)) {
            return 0;
        }

        int count = client.padUsersCount(pad.padId).intValue();
        if (count == 0) { // destroy non actually edited pad on etherpad server
            client.deletePad(pad.padId);
        }
        return count;
    }

    /**
     * Check if a pad exists on Etherpad server
     *
     * @param pad
     *            Internal pad
     * @return true if exists, false otherwise
     */
    private boolean groupPadExists(Pad pad) {
        return ((JSONArray) client.listPads(pad.groupId).get("padIDs")).contains(pad.padId);
    }

    /**
     * Get internal Pad informations
     *
     * @param nuxeoDocId
     *            Nuxeo document ID
     * @return internal pad
     */
    private Pad getPad(String nuxeoDocId) {

        if (pads.containsKey(nuxeoDocId)) {
            return pads.get(nuxeoDocId);
        } else {

            String etherpadGroupId = client.createGroupIfNotExistsFor(nuxeoDocId).get("groupID").toString();
            String etherpadPadId   = etherpadGroupId + "$" + nuxeoDocId;
            String etherpadPadURL  = etherpadBaseURL + etherpadPadId;

            Pad pad = new Pad(etherpadGroupId, etherpadPadId, etherpadPadURL);
            pads.put(nuxeoDocId, pad);

            return pad;
        }
    }

    private EtherpadSession getNewSession(RealtimeEditSession oldSession, String userFullName) {

        if (usersSessions.containsKey(userFullName)) {
            return usersSessions.get(userFullName);
        } else {

            EtherpadSession es = (EtherpadSession) oldSession;
            Pad pad = es.getPad();

            String etherpadAuthorId  = client.createAuthorIfNotExistsFor(userFullName, userFullName).get("authorID").toString();
            String etherpadSessionId = client.createSession(pad.groupId, etherpadAuthorId,etherpadSessionLength).get("sessionID").toString();

            EtherpadSession etherpadSession = new EtherpadSession(pad, userFullName, userFullName, etherpadAuthorId, etherpadSessionId, etherpadSessionLength);
            usersSessions.put(userFullName, etherpadSession);

            return etherpadSession;
        }
    }

    @Override
    public void updateSession(RealtimeEditSession session, String username, Blob blob) throws ClientException {
        // TODO Auto-generated method stub

    }

    @Override
    public RealtimeEditSession joinSession(RealtimeEditSession session, String userFullName) {

        return getNewSession(session, userFullName);
    }

    @Override
    public Blob getSessionBlob(RealtimeEditSession session, String mimeType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteSession(RealtimeEditSession session) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean existsSession(RealtimeEditSession session) {
        // TODO Auto-generated method stub
        return false;
    }
}
