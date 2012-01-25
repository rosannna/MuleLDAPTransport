/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.ldapx.config;

import org.mule.tck.FunctionalTestCase;
import org.mule.transport.ldapx.LdapxConnector;

/**
 * TODO
 */
public class LdapxNamespaceHandlerTestCase extends FunctionalTestCase {

	@Override
	protected String getConfigResources() {
		//TODO You'll need to edit this file to configure the properties specific to your transport
		return "ldapx-namespace-config.xml";
	}

	public void testLdapxConfig() throws Exception {
		LdapxConnector c = (LdapxConnector) muleContext.getRegistry().lookupConnector("ldapxConnector");
		assertNotNull(c);
		assertTrue(c.isConnected());
		assertTrue(c.isStarted());

		//TODO Assert specific properties are configured correctly
	}
}
