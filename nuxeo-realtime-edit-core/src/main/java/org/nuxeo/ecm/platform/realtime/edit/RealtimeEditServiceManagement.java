/**
 *
 */

package org.nuxeo.ecm.platform.realtime.edit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.api.local.ClientLoginModule;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.schema.types.primitives.StringType;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.model.Extension;


/**
 * @author nfgs
 */
public class RealtimeEditServiceManagement extends DefaultComponent implements RealtimeEditServiceManager {

    private static final long serialVersionUID = 1L;

    static final String REALTIME_EDITABLE_FACET = "RealtimeEditable";

    static final String SCHEMA = "realtime_edit";
    static final String SESSION_ID_PROPERTY = "sessionId";
    static final String SERVICE_NAME_PROPERTY = "serviceName";

    public static String REALTIME_EDIT_DOCTYPES_XP_NAME = "doctypes";

    public static String REALTIME_EDIT_SERVICES_XP_NAME = "services";

    private static final Log log = LogFactory.getLog(RealtimeEditServiceManagement.class);

    private Map<String, RealtimeEditService> services = new HashMap<String, RealtimeEditService>();

    private Map<String, RealtimeEditableDocTypeDescriptor> doctypeProperties = new HashMap<String, RealtimeEditableDocTypeDescriptor>();

    @Override
    public void applicationStarted(ComponentContext context) throws Exception {
        // do nothing by default. You can remove this method if not used.
    }

    @Override
    public void registerExtension(Extension extension) throws Exception {
        if (extension.getExtensionPoint().equals(REALTIME_EDIT_SERVICES_XP_NAME)) {
            Object[] contribs = extension.getContributions();
            for (Object contrib : contribs) {
                if (contrib instanceof RealtimeEditServiceDescriptor) {
                    RealtimeEditServiceDescriptor descriptor = (RealtimeEditServiceDescriptor) contrib;
                    registerRealtimeEditService(descriptor);
                }
            }
        }

        if (extension.getExtensionPoint().equals(REALTIME_EDIT_DOCTYPES_XP_NAME)) {
            Object[] contribs = extension.getContributions();
            for (Object contrib : contribs) {
                if (contrib instanceof RealtimeEditableDocTypeDescriptor) {
                    RealtimeEditableDocTypeDescriptor descriptor = (RealtimeEditableDocTypeDescriptor) contrib;
                    registerRealtimeEditableDocType(descriptor);
                }
            }
        }

    }

    private void registerRealtimeEditableDocType(
            RealtimeEditableDocTypeDescriptor descriptor) {
        doctypeProperties.put(descriptor.getName(), descriptor);
    }

    private void registerRealtimeEditService(RealtimeEditServiceDescriptor desc) {

        if (desc.getName() != null) {
            String name = desc.getName();

            if(!services.containsKey(name)){
                RealtimeEditService service = constructBackend(desc);
                services.put(name, service);
            }

        } else {
            log.error("No name supplied for realtime editing service plugin ");
        }
    }

    private static RealtimeEditService constructBackend(
            RealtimeEditServiceDescriptor desc) {

        RealtimeEditService service = null;

        try {
            service = Framework.getLocalService(desc.getKlass());
            service.setName(desc.getName());
            service.setMimeTypeSupport(desc.getMimeTypes());
        } catch (Exception e) {
            log.error("Exception in nxruntime's service lookup: "
                    + e.getMessage());
        }

        return service;
    }

    @Override
    public RealtimeEditService getService(String name) {
        return services.get(name);
    }

    public final Map<String, RealtimeEditService> getServices() {
        return services;
    }

    @Override
    public boolean isRealtimeEditable(DocumentModel documentModel) {

        boolean isEditable = false;

        try {

            // Check if there is any realtime editing service available
            if (!isRealtimeEditServiceAvailable()) {
                return false;
            }

            if (documentModel.isImmutable()) {
                return false;
            }

            Blob blob  = getRealtimeEditableBlob(documentModel);

            return isRealtimeEditable(blob);

        } catch (Exception e) {
            log.error(e.getMessage());
            isEditable = false;
        }

        return isEditable;
    }


    protected void setRealtimeEditableBlob(DocumentModel document, Blob blob) {
        String doctype = document.getDocumentType().getName();
        String propertyName = doctypeProperties.get(doctype).getBlobProperty();

        if (propertyName == null) {
            BlobHolder docBlobHolder = document.getAdapter(BlobHolder.class);
            try {
                docBlobHolder.setBlob(blob);
            } catch (ClientException e) {
            }
        } else {
            try {
                document.setPropertyValue(propertyName, (Serializable) blob);
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }

    }

    protected Blob getRealtimeEditableBlob(DocumentModel document) {
        String doctype = document.getDocumentType().getName();
        RealtimeEditableDocTypeDescriptor doctypeProps = doctypeProperties.get(doctype);
        String propertyName = doctypeProps.getBlobProperty();
        String mt = doctypeProps.getMimeType();

        Blob blob = null;

        if (propertyName == null) {
            BlobHolder docBlobHolder = document.getAdapter(BlobHolder.class);
            try {
                blob = docBlobHolder.getBlob();
            } catch (ClientException e) {
            }
        } else {
            try {
                Property prop = document.getProperty(propertyName);

                if (prop.getType().equals(StringType.INSTANCE)) {
                    if (mt == null) { // Let's use the mimeTypeProperty
                        String mtName = doctypeProps.getMimeTypeProperty();
                        mt = (String) document.getPropertyValue(mtName);
                    }
                    blob = new StringBlob(prop.getValue(String.class), mt);
                } else {
                    blob = prop.getValue(Blob.class);
                }

            } catch (ClientException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return blob;
    }

    public boolean isRealtimeEditable(Blob blob)
            throws ClientException {
        if (blob == null) {
            return false;
        }

        for (RealtimeEditService service : services.values()) {
            if (service.isRealtimeEditable(blob)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public RealtimeEditSession getOrCreateEditingSession(String serviceName, DocumentModel document) throws ClientException {
    	
        RealtimeEditSession session = getCurrentEditingSession(serviceName, document);

        if (session != null) {
        	
            RealtimeEditService service = getService(serviceName);
            if (service.existsSession(session)) { // If the session exists
                String username = ClientLoginModule.getCurrentPrincipal().getName();
                Blob blob = getRealtimeEditableBlob(document);
                service.updateSession(session, username, blob);
                return session;
            }
        }

        return createEditingSession(serviceName, document);
    }

    @Override
    public RealtimeEditSession createEditingSession(String serviceName, DocumentModel document) throws ClientException {
    	
        RealtimeEditService service = getService(serviceName);
        Blob blob = getRealtimeEditableBlob(document);

        if (!service.isRealtimeEditable(blob)) {
            throw new ClientException("Document is not editable by " + serviceName);
        }

        String title = document.getId();
        String username = ClientLoginModule.getCurrentPrincipal().getName();

        RealtimeEditSession session = service.createSession(username, title, blob);

        if (!document.hasFacet(REALTIME_EDITABLE_FACET)) {
            document.addFacet(REALTIME_EDITABLE_FACET);
        }

        document.setProperty(SCHEMA, SESSION_ID_PROPERTY, session.getRealtimeSessionID());
        document.setProperty(SCHEMA, SERVICE_NAME_PROPERTY, serviceName);

        Lock lock = document.setLock();

        return session;

    }

    @Override
    public void saveCurrentEditingSession(DocumentModel document) throws ClientException {
    	
        String serviceName = getCurrentEditingService(document);
        RealtimeEditSession session = getCurrentEditingSession(serviceName, document);

        RealtimeEditService service = getService(serviceName);

        Blob curBlob = getRealtimeEditableBlob(document);
        String mimeType = curBlob.getMimeType();

        Blob blob = service.getSessionBlob(session, mimeType);

        setRealtimeEditableBlob(document, blob);

    }

    @Override
    public void cancelCurrentEditingSession(DocumentModel document) throws ClientException {
    	
        String serviceName = getCurrentEditingService(document);
        RealtimeEditSession session = getCurrentEditingSession(serviceName, document);

        RealtimeEditService service = getService(serviceName);
        service.deleteSession(session);

        document.removeFacet(REALTIME_EDITABLE_FACET);

        document.removeLock();

    }

    @Override
    public String getCurrentEditingService(DocumentModel document) {
        if (!document.hasFacet(REALTIME_EDITABLE_FACET)) {
            return null;
        }

        try {
            return (String) document.getProperty(SCHEMA, SERVICE_NAME_PROPERTY);
        } catch (ClientException e) {

        }

        return null;
    }

    @Override
    public RealtimeEditSession getCurrentEditingSession(String serviceName, DocumentModel document) {
    	
        if (!document.hasFacet(REALTIME_EDITABLE_FACET)) {
            return null;
        }

        try {
            String sessionID = (String) document.getProperty(SCHEMA, SESSION_ID_PROPERTY);
            return null; // TODO !
        } catch (ClientException e) {
        	return null;
        }
    }

    @Override
    public void unregisterExtension(Extension extension) throws Exception {
        if (extension.getExtensionPoint().equals(REALTIME_EDIT_SERVICES_XP_NAME)) {
            Object[] contribs = extension.getContributions();
            for (Object contrib : contribs) {
                if (contrib instanceof RealtimeEditServiceDescriptor) {
                    unregisterRealtimeEditService(
                            (RealtimeEditServiceDescriptor) contrib);
                }
            }
        }
        if (extension.getExtensionPoint().equals(REALTIME_EDIT_DOCTYPES_XP_NAME)) {
            Object[] contribs = extension.getContributions();
            for (Object contrib : contribs) {
                if (contrib instanceof RealtimeEditableDocTypeDescriptor) {
                    unregisterRealtimeEditableDocType(
                            (RealtimeEditableDocTypeDescriptor) contrib);
                }
            }
        }
    }

    private void unregisterRealtimeEditableDocType(
            RealtimeEditableDocTypeDescriptor contrib) {
        // TODO Auto-generated method stub

    }


    private void unregisterRealtimeEditService(RealtimeEditServiceDescriptor desc) {
        services.remove(desc.getName());
    }


    @Override
    public boolean isRealtimeEditServiceAvailable() {
        return !services.isEmpty();
    }

}
