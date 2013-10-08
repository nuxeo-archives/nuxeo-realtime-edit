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

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;

/**
 *
 *
 * @since 5.8
 */
public class FakeRealtimeEditService extends AbstractRealtimeEditService {

    @Override
    public RealtimeEditSession createSession(String username, String title,
            Blob blob) throws ClientException {
        return new FakeSession();
    }

    @Override
    public boolean isRealtimeEditable(Blob blob) {
        return true;
    }

    @Override
    public void updateSession(RealtimeEditSession session, String username,
            Blob blob) throws ClientException {

    }

    @Override
    public RealtimeEditSession joinSession(RealtimeEditSession session,
            String userFullName) {
        return null;
    }

    @Override
    public Blob getSessionBlob(RealtimeEditSession session, String mimeType) {
        return null;
    }

    @Override
    public void deleteSession(RealtimeEditSession session) {
    }

    @Override
    public boolean existsSession(RealtimeEditSession session) {
        return false;
    }

}
