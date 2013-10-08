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

    RealtimeEditSession createEditingSession(String serviceName, DocumentModel document) throws ClientException;

    RealtimeEditSession getOrCreateEditingSession(String serviceName, DocumentModel document) throws ClientException;

    RealtimeEditSession getCurrentEditingSession(String serviceName, DocumentModel document);

    String getCurrentEditingService(DocumentModel documentModel);

    void saveCurrentEditingSession(DocumentModel document) throws ClientException;

    void cancelCurrentEditingSession(DocumentModel document) throws ClientException;

}
