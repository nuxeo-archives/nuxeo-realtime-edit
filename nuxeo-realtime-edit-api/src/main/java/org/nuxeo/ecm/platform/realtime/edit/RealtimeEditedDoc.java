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

import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;

/**
 *
 *
 * @since 5.8
 */
public interface RealtimeEditedDoc {
    public String getSessionId() throws ClientException;

    public String getServiceName() throws ClientException;

    public Map<String, String> getParams() throws ClientException;

    /**
     * @param realtimeSessionID
     * @throws ClientException
     *
     */
    public void setSessionId(String realtimeSessionID) throws ClientException;

    /**
     * @param serviceName
     * @throws ClientException
     *
     */
    public void setServiceName(String serviceName) throws ClientException;

    /**
     * @param string
     * @param string2
     * @throws ClientException
     *
     */
    public void putParam(String key, String value) throws ClientException;
}
