package org.nuxeo.ecm.platform.realtime.edit;

import java.util.Arrays;
import java.util.List;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.platform.mimetype.interfaces.MimetypeEntry;
import org.nuxeo.ecm.platform.mimetype.interfaces.MimetypeRegistry;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

public abstract class AbstractRealtimeEditService extends DefaultComponent implements
RealtimeEditService{

    private String name;
    private String icon;
    private List<String> supportedMimeTypes;

    private MimetypeRegistry mimetypeRegistry;

    @Override
    public void setMimeTypeSupport(String[] mimeTypes) {
        supportedMimeTypes = Arrays.asList(mimeTypes);
    }

    @Override
    public boolean isRealtimeEditable(Blob blob) {
        if (blob == null) {
            return false;
        }
        String mimetype = blob.getMimeType();
        return isRealtimeEditable(mimetype);
    }

    public boolean isRealtimeEditable(String mimetype) {

        boolean isEditable = Boolean.FALSE;

        try {
            MimetypeEntry mimetypeEntry = getMimetypeRegistry().getMimetypeEntryByMimeType(
                    mimetype);
            if (mimetypeEntry != null) {

                List<String> mimetypes = mimetypeEntry.getMimetypes();

                // check all the variants of the mimetype
                for (String mtype : mimetypes) {
                    if (supportedMimeTypes.contains(mtype)) {
                        isEditable = true;
                        break;
                    }
                }

            }
        } catch (Throwable t) {

        }

        return isEditable;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    protected MimetypeRegistry getMimetypeRegistry() throws Exception {
        if (mimetypeRegistry == null) {
            mimetypeRegistry = Framework.getService(MimetypeRegistry.class);
        }
        return mimetypeRegistry;
    }
}
