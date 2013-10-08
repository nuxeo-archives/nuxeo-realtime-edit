/*
 * (C) Copyright 2013 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     dmetzler
 */
package org.nuxeo.ecm.platform.realtime.edit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 *
 * @since 5.8
 */
public class RealtimeEditedDocAdapter implements RealtimeEditedDoc {

    /**
     *
     */

    /**
     *
     */
    private static final String PARAMS_VALUE_PROP = "value";

    /**
     *
     */
    private static final String PARAMS_NAME_PROP = "name";

    static final String SCHEMA = "rtedit";

    static final String SESSION_ID_PROPERTY = SCHEMA + ":sessionId";

    static final String SERVICE_NAME_PROPERTY = SCHEMA + ":serviceName";

    private static final String PARAMS_PROPERTY = SCHEMA + ":params";

    private DocumentModel doc;

    /**
     * @param doc
     */
    public RealtimeEditedDocAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    @Override
    public String getSessionId() throws ClientException {
        return doc.getProperty(SESSION_ID_PROPERTY).getValue(String.class);
    }

    @Override
    public String getServiceName() throws ClientException {
        return doc.getProperty(SERVICE_NAME_PROPERTY).getValue(String.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getParams() throws ClientException {

        List<Map<String, String>> paramsProps = (List<Map<String, String>>) doc.getPropertyValue(PARAMS_PROPERTY);

        Map<String, String> result = new HashMap<String, String>();
        for (Map<String, String> item : paramsProps) {
            result.put(item.get(PARAMS_NAME_PROP), item.get(PARAMS_VALUE_PROP));
        }
        return result;
    }

    @Override
    public void setSessionId(String sessionId) throws ClientException {
        doc.setPropertyValue(SESSION_ID_PROPERTY, sessionId);
    }

    @Override
    public void setServiceName(String serviceName) throws ClientException {
        doc.setPropertyValue(SERVICE_NAME_PROPERTY, serviceName);
    }

    @Override
    public void putParam(String key, String value) throws ClientException {
        Map<String, String> params = getParams();
        params.put(key, value);
        setParams(params);

    }

    /**
     * @param params
     * @throws ClientException
     *
     */
    private void setParams(Map<String, String> params) throws ClientException {
        List<Map<String, String>> prop = new ArrayList<Map<String, String>>(
                params.size());
        for (Entry<String, String> entry : params.entrySet()) {
            Map<String, String> item = new HashMap<String, String>();
            item.put(PARAMS_NAME_PROP, entry.getKey());
            item.put(PARAMS_VALUE_PROP, entry.getValue());
            prop.add(item);
        }
        doc.setPropertyValue(PARAMS_PROPERTY, (Serializable) prop);
    }

}
