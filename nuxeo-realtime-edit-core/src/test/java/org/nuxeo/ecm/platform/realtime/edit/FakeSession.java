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

import javax.servlet.http.Cookie;

import org.nuxeo.ecm.core.api.ClientException;

/**
 *
 *
 * @since TODO
 */
public class FakeSession implements RealtimeEditSession {

    @Override
    public String getRealtimeSessionID() {
        return "fake";
    }

    @Override
    public String getRealtimeEditSessionURL() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasRealtimeEditCookies() {
        // TODO Auto-generated method stub
        // return false;
        throw new UnsupportedOperationException();
    }

    @Override
    public Cookie[] getRealtimeEditSessionCookies() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRealtimeEditCookieName() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateState(RealtimeEditedDoc red) throws ClientException {
        red.putParam("fakeParam","totallyFake");
    }

}
