package org.nuxeo.ecm.platform.realtime.edit;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("doctype")
public class RealtimeEditableDocTypeDescriptor {

    @XNode("@name")
    protected String name;

    @XNode("@blobProperty")
    protected String blobProperty;

    @XNode("@mimeType")
    protected String mimeType;

    @XNode("@mimeTypeProperty")
    protected String mimeTypeProperty;

    public String getName() {
        return name;
    }

    public String getBlobProperty() {
        return blobProperty;
    }

    public String getMimeTypeProperty() {
        return mimeTypeProperty;
    }

    public String getMimeType() {
        return mimeType;
    }
}
