package org.nuxeo.ecm.platform.realtime.edit;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("service")
public class RealtimeEditServiceDescriptor {

    @XNode("@name")
    protected String name;

    @XNode("@class")
    protected Class<RealtimeEditService> klass;

    @XNodeList(value = "supports/mimeType", type = String[].class, componentType = String.class)
    private String[] mimeTypes;

    public String getName() {
        return name;
    }

    public Class<RealtimeEditService> getKlass() {
        return klass;
    }

    public String[] getMimeTypes(){
        return mimeTypes;
    }

}
