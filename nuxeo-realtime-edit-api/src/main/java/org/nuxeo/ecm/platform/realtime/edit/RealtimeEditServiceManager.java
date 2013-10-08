/**
 *
 */

package org.nuxeo.ecm.platform.realtime.edit;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;


/**
 * @author nfgs
 */
public interface RealtimeEditServiceManager extends Serializable {

    boolean isRealtimeEditServiceAvailable();

    RealtimeEditService getService(String serviceName);

    boolean isRealtimeEditable(DocumentModel document);

    String createEditingSession(String serviceName, DocumentModel document) throws ClientException;

    String getOrCreateEditingSession(String serviceName, DocumentModel document) throws ClientException;

    String getCurrentEditingSession(String serviceName, DocumentModel document);

    String getCurrentEditingService(DocumentModel documentModel);

    void saveCurrentEditingSession(DocumentModel document) throws ClientException;

    void cancelCurrentEditingSession(DocumentModel document) throws ClientException;

}
