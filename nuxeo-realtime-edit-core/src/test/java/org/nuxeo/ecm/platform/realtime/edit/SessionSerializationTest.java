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

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

/**
 *
 *
 * @since 5.8
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({ "org.nuxeo.platform.realtime.edit.core",
        "org.nuxeo.platform.realtime.edit.api" })
@LocalDeploy("org.nuxeo.platform.realtime.edit.core:fake-res-contrib.xml")
public class SessionSerializationTest {

    @Inject
    CoreSession session;

    @Inject
    RealtimeEditServiceManager resm;

    @Test
    public void itCanAddRTEditFacetOnADoc() throws Exception {
        DocumentModel doc = session.createDocumentModel("/", "test", "Note");
        doc.setPropertyValue("dc:title", "Test");

        doc.setPropertyValue("note:note", "<h1>Hello World</h1>");
        doc = session.createDocument(doc);
        session.save();

        assertTrue(resm.isRealtimeEditable(doc));


        RealtimeEditService service = resm.getService("etherpad");
        RealtimeEditSession reSession = resm.getOrCreateEditingSession(service.getName(), doc);
        assertNotNull(reSession);

        session.saveDocument(doc);



        doc = session.getDocument(new PathRef("/test"));

        assertTrue(doc.hasFacet(RealtimeEditServiceManagement.REALTIME_EDITABLE_FACET));



        RealtimeEditedDoc red = doc.getAdapter(RealtimeEditedDoc.class);
        assertNotNull(red);


        assertEquals("fake", red.getSessionId());
        assertEquals("etherpad", red.getServiceName());


        assertEquals("totallyFake",red.getParams().get("fakeParam"));




    }
}
