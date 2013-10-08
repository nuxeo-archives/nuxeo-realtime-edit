package org.nuxeo.ecm.platform.realtime.edit.etherpad;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.runtime.test.runner.RuntimeFeature;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(RuntimeFeature.class)
@Deploy("nuxeo-etherpad-lite-core")
@LocalDeploy("nuxeo-etherpad-lite-core:test-contrib.xml")
public class EtherpadServerDescriptorTest {

	@Inject
	EtherpadService service;

	@Test
	public void itDeploysContribComponent() throws Exception {
		ComponentInstance component = (ComponentInstance) Framework
				.getRuntime().getComponent("test.realtime.server.contrib");
		assertNotNull(component);
	}

	@Test
	public void itContributesADescriptor() throws Exception {
		EtherpadServerDescriptor desc = service.getDescriptor();
		assertNotNull(desc);		
		assertThat(desc.isEnabled(), is(true));
		String name = desc.getName();
		assertEquals("server_1", name);

		String displayType = desc.getDisplayType();
		assertEquals("display_type", displayType);

		Map<String, String> params = desc.getParams();
		assertEquals(2, params.size());

		String p1 = params.get("param1");
		assertEquals("value1", p1);

		String p2 = params.get("param2");
		assertEquals("value2", p2);
	}
	
	@Test
	public void itCanGetTheEtherpadServerUrl() throws Exception {
		String fakeUrl = "http://127.0.0.1/";
		String fakeApiKey = "azerty";
		
		Framework.getProperties().put("nuxeo.realtime-editor.etherpad.url", fakeUrl);
		Framework.getProperties().put("nuxeo.realtime-editor.etherpad.apiKey", fakeApiKey);
		
		assertThat(service.getServerUrl(),is(fakeUrl));
		assertThat(service.getServerApiKey(),is(fakeApiKey));
	}

}
